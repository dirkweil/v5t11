package de.gedoplan.v5t11.status.entity.baustein.zentrale;

import de.gedoplan.v5t11.status.entity.Kanal;
import de.gedoplan.v5t11.status.entity.SX2Kanal;
import de.gedoplan.v5t11.status.entity.baustein.Zentrale;
import de.gedoplan.v5t11.status.entity.fahrzeug.Fahrzeug;
import de.gedoplan.v5t11.util.cdi.Changed;
import de.gedoplan.v5t11.util.domain.attribute.SystemTyp;
import de.gedoplan.v5t11.util.misc.V5t11Exception;

import java.io.EOFException;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;

import lombok.NoArgsConstructor;

/**
 * Future Central Control von D&H (oder Mini FCC von Joachim Havekost).
 *
 * @author dw
 */
@XmlAccessorType(XmlAccessType.NONE)
@NoArgsConstructor
public class FCC extends Zentrale {

  private static final int BUS_ANZAHL = 2;
  /*
   * Blockdatenlängen für SX-Bus-Infos.
   * Die Blockdaten sind so aufgebaut:
   * - Status der SX2-Buserweiterung (BLOCK_DATEN_LEN_SX2 Bytes)
   * - Status des SX1-Busses 0 (BLOCK_DATEN_LEN_SX1 Bytes)
   * - Status des SX1-Busses 1 (BLOCK_DATEN_LEN_SX1 Bytes)
   */
  private static final int BLOCK_DATEN_LEN_SX1 = 113;
  private static final int BLOCK_DATEN_LEN_SX2 = 192;
  private static final int BLOCK_DATEN_LEN_GESAMT = BLOCK_DATEN_LEN_SX2 + 2 * BLOCK_DATEN_LEN_SX1;

  // Maximale normal verwendbare SX1-Adresse
  private static final int MAX_SX1_ADR = 103;

  // private static final Map<SystemTyp, Byte> SYSTEMTYP_ANMELDECODE_MAP = new EnumMap<>(SystemTyp.class);
  // static {
  // SYSTEMTYP_ANMELDECODE_MAP.put(SystemTyp.SX1, (byte) 0x00);
  // SYSTEMTYP_ANMELDECODE_MAP.put(SystemTyp.SX2, (byte) 0x04);
  // SYSTEMTYP_ANMELDECODE_MAP.put(SystemTyp.DCC, (byte) 0x05);
  // SYSTEMTYP_ANMELDECODE_MAP.put(SystemTyp.DCC, (byte) 0x07);
  // }
  //
  // private static final Map<Byte, SystemTyp> MELDECODE_SYSTEMTYP_MAP = new HashMap<>();
  // static {
  // MELDECODE_SYSTEMTYP_MAP.put((byte) 0x04, SystemTyp.SX2);
  // MELDECODE_SYSTEMTYP_MAP.put((byte) 0x14, SystemTyp.SX2);
  // MELDECODE_SYSTEMTYP_MAP.put((byte) 0x05, SystemTyp.DCC);
  // MELDECODE_SYSTEMTYP_MAP.put((byte) 0x15, SystemTyp.DCC);
  // MELDECODE_SYSTEMTYP_MAP.put((byte) 0x07, SystemTyp.DCC);
  // MELDECODE_SYSTEMTYP_MAP.put((byte) 0x17, SystemTyp.DCC);
  // }

  private int syncCycleNo = 0;

  /*
   * Die Daten der SX2-Buserweiterung befinden sich am Anfang der Blockdaten in zwei Segmenten:
   * - Index 0..15 in den ersten 96 Bytes
   * - Index 16..32 in den zweiten 96 Bytes
   */
  private static int BUSEXT_OFFSET(int idx) {
    return idx < 16 ? idx : 96 + idx;
  }

  /*
   * Jeder einzelne Eintrag der SX2-Buserweiterung besteht aus 6 Bytes im Abstand von 16 Bytes.
   */
  /*
   * Lokformat: Typ des Buserweiterungseintrags. Es werden derzeit nur die u. a. Werte unterstützt
   */
  private static final int BUSEXT_OFFSET_FORMAT = 0;

  /*
   * Höherwertiger Teil der Lokadresse
   */
  private static final int BUSEXT_OFFSET_ADR_HIGH = 16;

  /*
   * Niederwertiger Teil der Lokadresse, Licht, DCC-Zusatzinfo:
   * - Bits 2-7: Niederwertiger Teil der Lokadresse
   * - Bit 1: Licht
   * - Bit 0: nur bei DCC relevant: 14 Fahrstufen statt 28 bzw. 126
   */
  private static final int BUSEXT_OFFSET_ADR_LOW_LICHT = 32;

  /*
   * Richtung und Fahrstufe:
   * - Bit 7: Rückwärts
   * - Bits 0-6: Fahrstufe (bei DCC wird der Wert 1 ausgelassen, d. h. für Fahrstufe 1-126 muss hier der Wert 2-127 eingetragen werden)
   */
  private static final int BUSEXT_OFFSET_RUECKWAERTS_FAHRSTUFE = 48;

  // Funktionen 1-8
  private static final int BUSEXT_OFFSET_FUNKTION_1_8 = 64;

  // Funktionen 9-16
  private static final int BUSEXT_OFFSET_FUNKTION_9_16 = 80;

  // Maximaler Index in der SX2-Buserweiterung
  private static final int BUSEXT_MAX_IDX = 31;

  // Synchronisationsdauer der Businformationen (Refresh 13x pro Sekunde)
  private static final long SX_SYNC_MILLIS = 1012L / 13L;

  private volatile boolean terminationRequested;
  private Phaser portPhaser;
  private Future<?> backgroundTask;
  private Phaser syncPhaser = new Phaser(1);

  private AtomicReference<byte[]> status = new AtomicReference<>(new byte[BLOCK_DATEN_LEN_GESAMT]);

  @Override
  public void setGleisspannung(boolean gleisspannung) {
    setSX1Kanal(127, gleisspannung ? (byte) 0xff : 0);
  }

  @Override
  public void open(ExecutorService executorService) {

    if (this.log.isTraceEnabled()) {
      this.log.trace("Background-Task starten und auf offenen Port warten");
    }

    // this::open parallel ausführen und darauf warten, dass der Port geöffnet wurde
    this.portPhaser = new Phaser(2);
    this.backgroundTask = executorService.submit((Runnable) this::open);
    try {
      this.portPhaser.awaitAdvanceInterruptibly(this.portPhaser.arriveAndDeregister(), 100, TimeUnit.SECONDS);
    } catch (InterruptedException | TimeoutException e) {
      throw new V5t11Exception("Cannot open FCC", e);
    }

    if (this.log.isTraceEnabled()) {
      this.log.trace("Background-Task gestartet und Port offen");
    }
  }

  private void open() {
    if (this.log.isTraceEnabled()) {
      this.log.trace("Background-Task gestartet");
    }

    this.terminationRequested = false;

    while (!this.terminationRequested) {
      try {
        openPort();
        this.portPhaser.arriveAndDeregister();

        syncStatus();
      } catch (Exception e) {
        this.log.error("Fehler in Verbindung zu Zentrale", e);

        try {
          closePort();
        } catch (Exception ignore) {
        }

        delay(15000);
      }
    }

    if (this.log.isTraceEnabled()) {
      this.log.trace("Background-Task gestoppt");
    }
  }

  @Override
  public void close() {
    if (this.log.isTraceEnabled()) {
      this.log.trace("Background-Task stoppen");
    }

    this.terminationRequested = true;
    try {
      this.backgroundTask.get(30, TimeUnit.SECONDS);
    } catch (InterruptedException | ExecutionException | TimeoutException e) {
      // ignore
    }

    if (this.log.isTraceEnabled()) {
      this.log.trace("Background-Task ist gestoppt");
    }

    closePort();
  }

  @Override
  protected int getPortSpeed() {
    return 230400;
  }

  private void syncStatus() throws IOException {
    while (!this.terminationRequested) {

      long start = System.currentTimeMillis();

      synchronized (Zentrale.class) {

        ++this.syncCycleNo;

        // Blockabfrage starten und neue Daten eintragen
        byte[] blockDaten = blockAbfrage();
        byte[] blockDatenAlt = this.status.getAndSet(blockDaten);

        // Gleisspannung und Kurzschluss aus Blockdaten entnehmen
        boolean gleisspannungAlt = this.gleisspannung;
        this.gleisspannung = blockDaten[BLOCK_DATEN_LEN_SX2 + 112] != 0;

        boolean kurzschlussAlt = this.kurzschluss;
        this.kurzschluss = (blockDaten[BLOCK_DATEN_LEN_SX2 + 109] & 0b0001_0000) != 0;

        if (gleisspannungAlt != this.gleisspannung || kurzschlussAlt != this.kurzschluss) {
          this.eventFirer.fire(this, Changed.Literal.INSTANCE);
        }

        // SX2-Buserweiterungseinträge vergleichen
        fireSX2Changes(blockDaten, blockDatenAlt);

        // Normale Kanäle von SX1-Bus 0 vergleichen
        fireSX1Changes(blockDaten, blockDatenAlt, BLOCK_DATEN_LEN_SX2, 0);

        // Normale Kanäle von SX1-Bus 0 vergleichen
        fireSX1Changes(blockDaten, blockDatenAlt, BLOCK_DATEN_LEN_SX2 + BLOCK_DATEN_LEN_SX1, 1000);
      }

      this.syncPhaser.arrive();

      /*
       * Selectrix refresht die Bus-Informationen 13x pro Sekunde. Daher so lange warten,
       * bis ein solches Zeitfenster verstrichen ist.
       * Minimal wird für 10ms gewartet, um anderen Threads eine faire Chance zu geben.
       */
      long waitMillis = System.currentTimeMillis() - (start + SX_SYNC_MILLIS);
      delay(waitMillis > 10 ? waitMillis : 10);
    }

  }

  private void fireSX2Changes(byte[] blockDaten, byte[] blockDatenAlt) {
    for (int idx = 0; idx <= BUSEXT_MAX_IDX; ++idx) {
      int offset = BUSEXT_OFFSET(idx);
      if (blockDaten[offset + BUSEXT_OFFSET_FORMAT] != blockDatenAlt[offset + BUSEXT_OFFSET_FORMAT]
        || blockDaten[offset + BUSEXT_OFFSET_ADR_HIGH] != blockDatenAlt[offset + BUSEXT_OFFSET_ADR_HIGH]
        || blockDaten[offset + BUSEXT_OFFSET_ADR_LOW_LICHT] != blockDatenAlt[offset + BUSEXT_OFFSET_ADR_LOW_LICHT]
        || blockDaten[offset + BUSEXT_OFFSET_RUECKWAERTS_FAHRSTUFE] != blockDatenAlt[offset + BUSEXT_OFFSET_RUECKWAERTS_FAHRSTUFE]
        || blockDaten[offset + BUSEXT_OFFSET_FUNKTION_1_8] != blockDatenAlt[offset + BUSEXT_OFFSET_FUNKTION_1_8]
        || blockDaten[offset + BUSEXT_OFFSET_FUNKTION_9_16] != blockDatenAlt[offset + BUSEXT_OFFSET_FUNKTION_9_16]) {
        SystemTyp systemTyp = decodeSystemTyp(blockDaten[offset + BUSEXT_OFFSET_ADR_LOW_LICHT], blockDaten[offset + BUSEXT_OFFSET_FORMAT]);
        if (systemTyp != null) {
          this.eventFirer.fire(new SX2Kanal(
              idx,
              systemTyp,
              decodeAdresse(systemTyp, blockDaten[offset + BUSEXT_OFFSET_ADR_HIGH], blockDaten[offset + BUSEXT_OFFSET_ADR_LOW_LICHT]),
              decodeLicht(blockDaten[offset + BUSEXT_OFFSET_ADR_LOW_LICHT]),
              decodeRueckwaerts(blockDaten[offset + BUSEXT_OFFSET_RUECKWAERTS_FAHRSTUFE]),
              decodeFahrstufe(systemTyp, blockDaten[offset + BUSEXT_OFFSET_RUECKWAERTS_FAHRSTUFE]),
              decodeFunktionsstatus(blockDaten[offset + BUSEXT_OFFSET_FUNKTION_9_16], blockDaten[offset + BUSEXT_OFFSET_FUNKTION_1_8])),
            Changed.Literal.INSTANCE);
        }
      }
    }
  }

  private static SystemTyp decodeSystemTyp(byte codeHigh, byte codeLow) {
    int meldeCode = ((codeLow & 0x0f) | ((codeHigh & 0x01) << 4));
    return switch (meldeCode) {
      case 0x00 -> null;
      case 0x04, 0x14 -> SystemTyp.SX2;
      case 0x05, 0x07, 0x15, 0x17 -> SystemTyp.DCC;
      default -> throw new IllegalArgumentException(String.format("Ungültiger Meldecode: 0x%02x", meldeCode));
    };
  }

  /**
   * Adresse decodieren.
   * <p>
   * Die Adress-Bits kommen in zwei Bytes und werden zu einem <code>int</code>-Wert zusammengesetzt.
   * Achtung: Für SX2 sind die <code>H</code>s Tausender und Hunderter, die <code>L</code>s Zehner und Einer.
   *
   * @param adrHigh oberer Teil der Adresse <code>HHHH_HHHL</code>
   * @param adrLow unterer Teil der Adresse <code>LLLL_LLxx</code>
   * @return decodierte Adresse <code>00HH_HHHH_HLLL_LLLL</code>
   */
  private static int decodeAdresse(SystemTyp systemTyp, byte adrHigh, byte adrLow) {
    int adr = Byte.toUnsignedInt(adrHigh) << 6 | (Byte.toUnsignedInt(adrLow) & 0b1111_1100) >>> 2;
    return switch (systemTyp) {
      case SX1 -> throw new IllegalArgumentException("Ungültiger Systemtyp: " + systemTyp);
      case SX2 -> ((adr & 0b0011_1111_1000_0000) >>> 7) * 100 + (adr & 0b0111_1111);
      default -> adr;
    };

  }

  private static boolean decodeLicht(byte adrLowLicht) {
    return (adrLowLicht & 0b0000_0010) != 0;
  }

  private static boolean decodeRueckwaerts(byte rueckwaertsFahrstufe) {
    return (rueckwaertsFahrstufe & 0b1000_0000) != 0;
  }

  private static int decodeFahrstufe(SystemTyp systemTyp, byte rueckwaertsFahrstufe) {
    int fahrstufe = rueckwaertsFahrstufe & 0b0111_1111;
    if (systemTyp == SystemTyp.DCC) {
      fahrstufe = fahrstufe < 2 ? 0 : fahrstufe - 1;
    }
    return fahrstufe;
  }

  private static int decodeFunktionsstatus(byte funktion9_16, byte funktion1_8) {
    return Byte.toUnsignedInt(funktion9_16) << 8 | Byte.toUnsignedInt(funktion1_8);
  }

  private void fireSX1Changes(byte[] blockDaten, byte[] blockDatenAlt, int idx, int adr) {
    for (int i = 0; i <= MAX_SX1_ADR; ++i) {
      if (blockDaten[idx] != blockDatenAlt[idx]) {
        fireSX1Changed(adr, blockDaten[idx]);
      }
      ++idx;
      ++adr;
    }
  }

  private void fireSX1Changed(int adr, byte wert) {
    this.log.trace("syncCycleNo: " + this.syncCycleNo);
    this.eventFirer.fire(new Kanal(adr, wert), Changed.Literal.INSTANCE);
  }

  private byte[] blockAbfrage() throws IOException {
    synchronized (Zentrale.class) {

      this.out.write(new byte[] { 0x78, (byte) 0xc3 });

      byte[] result = new byte[BLOCK_DATEN_LEN_GESAMT];
      for (int i = 0; i < result.length; ++i) {
        int value = this.in.read();
        if (value < 0) {
          throw new EOFException("Blockabfrage liefert zu wenige Ergebnisbytes: " + i);
        }

        result[i] = (byte) value;
      }

      return result;
    }
  }

  @Override
  public String toString() {
    return "FCC{portName=" + this.portName + ", gleisspannung=" + this.gleisspannung + ", kurzschluss=" + this.kurzschluss + "}";
  }

  @Override
  public int getSX1Kanal(int adr) {
    int bus = Kanal.toBusNr(adr);
    if (bus < 0 || bus >= BUS_ANZAHL) {
      throw new IllegalArgumentException("Ungültige SX1-Adresse: " + adr);
    }

    int localAdr = Kanal.toLocalAdr(adr);

    return this.status.get()[BLOCK_DATEN_LEN_SX2 + bus * BLOCK_DATEN_LEN_SX1 + localAdr] & 0xff;
  }

  @Override
  public void setSX1Kanal(int adr, int wert) {
    int bus = Kanal.toBusNr(adr);
    if (bus < 0 || bus >= BUS_ANZAHL) {
      throw new IllegalArgumentException("Ungültige SX1-Adresse: " + adr);
    }

    int localAdr = Kanal.toLocalAdr(adr);

    send(new byte[] { (byte) bus, (byte) (localAdr | 0x80), (byte) wert }, new byte[1], ALL_ZEROES);
  }

  /**
   * Befehl senden, Antwort lesen und prüfen.
   * <p>
   * Die Befehls-Bytes werden gesendet und soviele Antwort-Bytes eingelesen, wie in <code>antwort</code> Platz haben.
   * Ist <code>antwortCheck</code> nicht <code>null</code>, wird die Antwort damit geprüft.
   *
   * @param befehl Befehl
   * @param antwort Antwort
   * @param antwortCheck Antwortprüfung oder <code>null</code>
   */
  private void send(byte[] befehl, byte[] antwort, Predicate<byte[]> antwortCheck) {
    synchronized (Zentrale.class) {

      if (this.log.isTraceEnabled()) {
        this.log.tracef("befehl: %s", toHexString(befehl));
      }

      try {
        this.out.write(befehl);

        int antwortSollLaenge = antwort.length;
        int antwortIstLaenge = this.in.read(antwort);
        if (antwortIstLaenge != antwortSollLaenge) {
          throw new V5t11Exception("Steuerungskommando fehlgeschlagen; antwort zu kurz (" + antwortIstLaenge + " statt " + antwortSollLaenge + ")");
        }

        if (this.log.isTraceEnabled()) {
          this.log.tracef("antwort: %s", toHexString(antwort));
        }

        if (antwortCheck == null || antwortCheck.test(antwort)) {
          return;
        }

        throw new V5t11Exception("Steuerungskommando fehlgeschlagen; antwort=" + toHexString(antwort));
      } catch (IOException e) {
        throw new V5t11Exception("Steuerungskommando fehlgeschlagen", e);
      }
    }
  }

  private static final Predicate<byte[]> ALL_ZEROES = ba -> {
    for (byte b : ba) {
      if (b != 0) {
        return false;
      }
    }

    return true;
  };

  private static CharSequence toHexString(byte[] werte) {
    return IntStream.range(0, werte.length).mapToObj(i -> String.format("0x%02x", werte[i])).collect(Collectors.joining(",", "[", "]"));
  }

  private static void delay(long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException ignore) {
    }
  }

  @Override
  public void awaitSync() {
    this.syncPhaser.register();
    try {
      this.syncPhaser.awaitAdvanceInterruptibly(this.syncPhaser.arriveAndDeregister(), SX_SYNC_MILLIS + 50L, TimeUnit.MILLISECONDS);
    } catch (InterruptedException | TimeoutException e) {
      // ignore;
    }
  }

  private AtomicReferenceArray<Fahrzeug> sx2BusSlot = new AtomicReferenceArray<>(BUSEXT_MAX_IDX + 1);

  @Override
  public void lokChanged(Fahrzeug lok) {
    if (lok.getId().getSystemTyp() == SystemTyp.SX1) {
      /*
       * Lok ist eine SX(1)-Lok.
       * SX1-Kanalwert komponieren: 0bHLRFFFFF
       * - H = Horn
       * - L = Licht
       * - R = Rückwärts
       * - FFFFF = Fahrstufe
       */
      int wert = 0;
      if (lok.isAktiv()) {
        wert |= lok.getFahrstufe();

        if (lok.isRueckwaerts()) {
          wert |= 0b0010_0000;
        }

        if (lok.isLicht()) {
          wert |= 0b0100_0000;
        }

        // TODO Horn unterstützen? Dann müsste Fahrzeug eine entsprechende Maske für die Funktion enthalten.
        // for (LokFunktion funktion : lok.getFunktionen()) {
        // if (funktion != null && funktion.isHorn() && funktion.isAktiv()) {
        // wert |= 0b1000_0000;
        // break;
        // }
        // }
      }
      setSX1Kanal(lok.getId().getAdresse(), wert);

      return;
    }

    // Lok in SX2-Bus-Slots suchen
    int idx = findSx2BusSlot(lok);
    if (idx < 0) {
      // Lok ist noch nicht angemeldet

      // Falls Lok nicht aktiv ist, nichts zu tun
      if (!lok.isAktiv()) {
        return;
      }

      idx = sx2Anmelden(lok);
      this.sx2BusSlot.set(idx, lok);
    }

    // Lok ist nun angemeldet

    // Falls Lok aktiv, Fahrstufe und alle Funktionen setzen
    if (lok.isAktiv()) {
      setSX2Werte(idx, lok.getId().getSystemTyp(), lok.getFahrstufe(), lok.isRueckwaerts(), lok.isLicht(), lok.getFktBits());
      return;
    }

    // Inaktive Lok: Fahrstufe und alle Funktionen löschen und Lok abmelden
    setSX2Werte(idx, lok.getId().getSystemTyp(), 0, false, false, 0);
    sx2Abmelden(idx);

    // Slot freigeben
    this.sx2BusSlot.set(idx, null);
  }

  private int sx2Anmelden(Fahrzeug lok) {
    synchronized (Zentrale.class) {
      if (this.log.isDebugEnabled()) {
        this.log.debug("Fahrzeug anmelden: " + lok);
      }

      byte anmeldeCode = 0x04; // SX2
      if (lok.getId().getSystemTyp() == SystemTyp.DCC) {
        if (lok.getId().getAdresse() <= 127) {
          anmeldeCode = 0x05; // DCC, kurze Adresse
        } else {
          anmeldeCode = 0x07; // DCC, lange Adresse
        }
      }
      byte[] adressWert = encodeAdresse(lok.getId().getSystemTyp(), lok.getId().getAdresse());
      byte[] antwort = new byte[1];
      send(new byte[] { 0x79, 0x01, adressWert[1], adressWert[0], anmeldeCode }, antwort, a -> a[0] >= 0 && a[0] <= BUSEXT_MAX_IDX);

      if (this.log.isDebugEnabled()) {
        this.log.debug("Fahrzeug " + lok + " hat Index " + antwort[0]);
      }

      return antwort[0];
    }
  }

  private static byte[] encodeAdresse(SystemTyp systemTyp, int adresse) {
    return switch (systemTyp) {
      case SX1 -> throw new IllegalArgumentException("Ungültiger Systemtyp: " + systemTyp);

      case SX2 -> {
        int hunderter = adresse / 100;
        int einer = adresse % 100;
        int sx2Wert = (hunderter << 9) | (einer << 2);
        yield new byte[] { (byte) (sx2Wert & 0xff), (byte) ((sx2Wert >> 8) & 0xff) };
      }

      default -> {
        int dccWert = adresse << 2;
        yield new byte[] { (byte) (dccWert & 0xff), (byte) ((dccWert >> 8) & 0xff) };
      }
    };
  }

  private void sx2Abmelden(int idx) {
    synchronized (Zentrale.class) {
      if (this.log.isDebugEnabled()) {
        this.log.debug("Fahrzeug abmelden: idx=" + idx);
      }
      send(new byte[] { 0x79, 0x02, (byte) idx, 0x00, 0x00 }, new byte[1], ALL_ZEROES);
    }
  }

  private void setSX2Werte(int idx, SystemTyp systemTyp, int fahrstufe, boolean rueckwaerts, boolean licht, int funktionStatus) {
    synchronized (Zentrale.class) {
      int wertFahrstufeRichtung = fahrstufe & 0x7f;
      if (wertFahrstufeRichtung > 0 && systemTyp == SystemTyp.DCC) {
        wertFahrstufeRichtung++;
      }

      if (rueckwaerts) {
        wertFahrstufeRichtung |= 0x80;
      }

      byte wertLicht = licht ? (byte) 0x02 : (byte) 0x00;

      byte wertF1_8 = (byte) (funktionStatus & 0x00ff);
      byte wertF9_16 = (byte) ((funktionStatus >>> 8) & 0x00ff);

      if (this.log.isDebugEnabled()) {
        this.log.debug(String.format(
          "Fahrzeugdaten setzen: idx=%d, fahrstufe=%d, rückwärts=%b, licht=%b, f=0x%04x",
          idx,
          fahrstufe,
          rueckwaerts,
          licht,
          funktionStatus));
      }

      byte[] antwort = new byte[1];
      send(new byte[] { 0x79, 0x13, (byte) idx, (byte) wertFahrstufeRichtung, 0x00 }, antwort, ALL_ZEROES);
      send(new byte[] { 0x79, 0x05, (byte) idx, wertLicht, 0x00 }, antwort, ALL_ZEROES);
      send(new byte[] { 0x79, 0x16, (byte) idx, wertF1_8, wertF9_16 }, antwort, ALL_ZEROES);
    }
  }

  private int findSx2BusSlot(Fahrzeug lok) {
    for (int idx = 0; idx <= BUSEXT_MAX_IDX; ++idx) {
      if (lok.equals(this.sx2BusSlot.get(idx))) {
        return idx;
      }
    }
    return -1;
  }

  @Override
  public int getBusAnzahl() {
    return BUS_ANZAHL;
  }

  @Override
  public void setGleisProtokoll() {
    int soll = 0x04;
    while (true) {
      awaitSync();
      int ist = getSX1Kanal(110) & 0x0F;
      if (ist == soll) {
        return;
      }

      this.log.warnf("Gleisprotokoll 0x%02x ist falsch - schrittweise Umstellung auf 0x%02x", ist, soll);

      try {
        send(new byte[] { (byte) 0x83, (byte) 0xa0, 0x00, 0x00, 0x00 }, new byte[3], ALL_ZEROES);
      } catch (Exception e) {
        // ignore
      }

      delay(250);
    }
  }

  @Override
  public Map<Integer, Integer> readFahrzeugConfig(SystemTyp systemTyp, Collection<Integer> fahrzeugConfigParameterKeys) {
    setGleisspannung(false);
    try {
      return fahrzeugConfigParameterKeys
        .stream()
        .collect(Collectors.toMap(key -> key, key -> readFahrzeugConfig(systemTyp, key)));
    } finally {
      stopProgMode();
    }
  }

  private Integer readFahrzeugConfig(SystemTyp systemTyp, Integer key) {
    if (key == null) {
      return null;
    }

    return switch (systemTyp) {
      case SX1 -> readSX1FahrzeugConfig(key);
      case SX2 -> readSX2orDCCFahrzeugConfig(key, (byte) 0xc2);
      case DCC -> readSX2orDCCFahrzeugConfig(key, (byte) 0xc6);
      default -> throw new IllegalArgumentException("ungültiger Systemtyp");
    };
  }

  private Integer readSX1FahrzeugConfig(int key) {
    throw new UnsupportedOperationException("not yet implemented");
  }

  private Integer readSX2orDCCFahrzeugConfig(int key, byte systemTypDiscriminator) {
    var antwort = new byte[3];
    send(new byte[] { (byte) 0x83, systemTypDiscriminator, (byte) (key / 100), (byte) (key % 100), 0 }, antwort, null);
    return (antwort[0] == 1 && antwort[2] == 0) ? ((int) antwort[1]) & 0xff : -1;
  }

  private void stopProgMode() {
    try {
      send(new byte[] { (byte) 0x83, 0, 0, 0, 0 }, new byte[3], ALL_ZEROES);
    } catch (Exception e) {
      this.log.warn("Kann Programmiermodus nicht stoppen", e);
    }
  }

  @Override
  public void writeFahrzeugConfig(SystemTyp systemTyp, Map<Integer, Integer> fahrzeugConfigParameters) {
    throw new UnsupportedOperationException("not yet implemented");
  }

}
