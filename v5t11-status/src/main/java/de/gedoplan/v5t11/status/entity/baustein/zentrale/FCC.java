package de.gedoplan.v5t11.status.entity.baustein.zentrale;

import de.gedoplan.v5t11.status.entity.Kanal;
import de.gedoplan.v5t11.status.entity.SX2Kanal;
import de.gedoplan.v5t11.status.entity.SystemTyp;
import de.gedoplan.v5t11.status.entity.baustein.Zentrale;
import de.gedoplan.v5t11.status.entity.lok.Lok;
import de.gedoplan.v5t11.status.entity.lok.Lok.FunktionConfig;
import de.gedoplan.v5t11.util.misc.V5t11Exception;

import java.io.EOFException;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.Predicate;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

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
   * - Bit 0: derzeit ungenutzt (nur bei DCC relevant: 14 Fahrstufen statt 28 bzw. 126)
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
      this.portPhaser.awaitAdvanceInterruptibly(this.portPhaser.arriveAndDeregister(), 10, TimeUnit.SECONDS);
    }
    catch (InterruptedException | TimeoutException e) {
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
      }
      catch (Exception e) {
        this.log.error("Fehler in Verbindung zu Zentrale", e);

        try {
          closePort();
        }
        catch (Exception ignore) {
        }

        delay(1000);
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
    }
    catch (InterruptedException | ExecutionException | TimeoutException e) {
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

        // Blockabfrage starten und neue Daten eintragen
        byte[] blockDaten = blockAbfrage();
        byte[] blockDatenAlt = this.status.getAndSet(blockDaten);

        // Gleisspannung und Kurzschluss aus Blockdaten entnehmen
        boolean gleisspannungAlt = this.gleisspannung;
        this.gleisspannung = blockDaten[BLOCK_DATEN_LEN_SX2 + 112] != 0;

        boolean kurzschlussAlt = this.kurzschluss;
        this.kurzschluss = (blockDaten[BLOCK_DATEN_LEN_SX2 + 109] & 0b0001_0000) != 0;

        if (gleisspannungAlt != this.gleisspannung || kurzschlussAlt != this.kurzschluss) {
          this.eventFirer.fire(this);
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
        if (blockDaten[offset + BUSEXT_OFFSET_FORMAT] != SystemTyp.SX1.getFormatCode()) {
          this.eventFirer.fire(new SX2Kanal(
              idx,
              blockDaten[offset + BUSEXT_OFFSET_FORMAT],
              blockDaten[offset + BUSEXT_OFFSET_ADR_HIGH],
              blockDaten[offset + BUSEXT_OFFSET_ADR_LOW_LICHT],
              blockDaten[offset + BUSEXT_OFFSET_RUECKWAERTS_FAHRSTUFE],
              blockDaten[offset + BUSEXT_OFFSET_FUNKTION_1_8],
              blockDaten[offset + BUSEXT_OFFSET_FUNKTION_9_16]));
        }
      }
    }

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
    this.eventFirer.fire(new Kanal(adr, wert));
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

    send(new byte[] { (byte) bus, (byte) (localAdr | 0x80), (byte) wert }, null);
  }

  private int send(byte[] werte, Predicate<Integer> ackCheck) {
    synchronized (Zentrale.class) {

      if (this.log.isTraceEnabled()) {
        StringBuilder sb = new StringBuilder("Send: ");
        for (int i = 0; i < werte.length; ++i) {
          if (i != 0) {
            sb.append(",");
          }

          sb.append(String.format("0x%02x", werte[i]));
        }
        this.log.trace(sb);
      }

      try {
        this.out.write(werte);

        int ack = this.in.read();
        if (this.log.isTraceEnabled()) {
          this.log.trace(String.format("Ack: 0x%02x", ack));
        }

        if (ackCheck == null) {
          if (ack == 0) {
            return ack;
          }
        } else if (ackCheck.test(ack)) {
          return ack;
        }

        throw new V5t11Exception("Steuerungskommando fehlgeschlagen; ack=" + ack);
      }
      catch (IOException e) {
        throw new V5t11Exception("Steuerungskommando fehlgeschlagen", e);
      }
    }
  }

  private static void delay(long millis) {
    try {
      Thread.sleep(millis);
    }
    catch (InterruptedException ignore) {
    }
  }

  @Override
  public void awaitSync() {
    this.syncPhaser.register();
    try {
      this.syncPhaser.awaitAdvanceInterruptibly(this.syncPhaser.arriveAndDeregister(), SX_SYNC_MILLIS + 50L, TimeUnit.MILLISECONDS);
    }
    catch (InterruptedException | TimeoutException e) {
      // ignore;
    }
  }

  private AtomicReferenceArray<Lok> sx2BusSlot = new AtomicReferenceArray<>(BUSEXT_MAX_IDX + 1);

  @Override
  public void lokChanged(Lok lok) {
    if (lok.getSystemTyp() == SystemTyp.SX1) {
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

        for (int i = 1; i <= 16; ++i) {
          FunktionConfig funktionConfig = lok.getFunktionConfigs().get(i);
          if (funktionConfig != null && funktionConfig.isHorn() && lok.getFunktion(i)) {
            wert |= 0b1000_0000;
            break;
          }
        }
      }
      setSX1Kanal(lok.getAdresse(), wert);

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
      setSX2FahrstufeUndRichtung(idx, SX2Kanal.encodeFahrstufeUndRückwaerts(lok.getSystemTyp(), lok.getFahrstufe(), lok.isRueckwaerts()));
      setSX2Licht(idx, lok.isLicht());
      setSX2Funktionen(idx, lok.getFunktionStatus());

      return;
    }

    // Inaktive Lok: Fahrstufe und alle Funktionen löschen und Lok abmelden
    setSX2FahrstufeUndRichtung(idx, 0);
    setSX2Licht(idx, false);
    setSX2Funktionen(idx, 0);
    sx2Abmelden(idx);

    // Slot freigeben
    this.sx2BusSlot.set(idx, null);
  }

  private int sx2Anmelden(Lok lok) {
    synchronized (Zentrale.class) {
      if (this.log.isDebugEnabled()) {
        this.log.debug("Lok anmelden: " + lok);
      }

      byte[] wert = SX2Kanal.encodeAdresse(lok.getSystemTyp(), lok.getAdresse());
      int idx = send(new byte[] { 0x79, 0x01, wert[1], wert[0], (byte) lok.getSystemTyp().getFormatCode() }, ack -> ack >= 0 && ack <= BUSEXT_MAX_IDX);

      if (this.log.isDebugEnabled()) {
        this.log.debug("Lok " + lok + " hat Index " + idx);
      }

      return idx;
    }
  }

  private void sx2Abmelden(int idx) {
    synchronized (Zentrale.class) {
      if (this.log.isDebugEnabled()) {
        this.log.debug("Lok abmelden: idx=" + idx);
      }
      send(new byte[] { 0x79, 0x02, (byte) idx, 0x00, 0x00 }, null);
    }
  }

  private void setSX2Funktionen(int idx, int funktionStatus) {
    synchronized (Zentrale.class) {
      if (this.log.isDebugEnabled()) {
        this.log.debug(String.format("Lok-Funktionen setzen: idx=%d, f=0x%04x", idx, funktionStatus));
      }
      byte f1_8 = (byte) (funktionStatus & 0x00ff);
      byte f9_16 = (byte) ((funktionStatus >>> 8) & 0x00ff);
      send(new byte[] { 0x79, 0x16, (byte) idx, f1_8, f9_16 }, null);
    }
  }

  private void setSX2Licht(int idx, boolean licht) {
    synchronized (Zentrale.class) {
      if (this.log.isDebugEnabled()) {
        this.log.debug(String.format("Lok-Licht setzen: idx=%d, licht=%b", idx, licht));
      }
      byte wert = licht ? (byte) 0x02 : (byte) 0x00;
      send(new byte[] { 0x79, 0x05, (byte) idx, wert, 0x00 }, null);
    }
  }

  private void setSX2FahrstufeUndRichtung(int idx, int wert) {
    synchronized (Zentrale.class) {
      if (this.log.isDebugEnabled()) {
        this.log.debug(String.format("Lok-Fahrstufe und Richtung setzen: idx=%d, fahrstufe=%d, rückwärts=%b", idx, wert & 0x7f, (wert & 0x80) != 0));
      }
      send(new byte[] { 0x79, 0x13, (byte) idx, (byte) wert, 0x00 }, null);
    }
  }

  private int findSx2BusSlot(Lok lok) {
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

}
