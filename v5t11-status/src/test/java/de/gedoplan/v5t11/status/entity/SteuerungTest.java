package de.gedoplan.v5t11.status.entity;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import de.gedoplan.v5t11.status.CdiTestBase;
import de.gedoplan.v5t11.status.StatusEventCollector;
import de.gedoplan.v5t11.status.entity.fahrweg.Gleisabschnitt;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Signal;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Weiche;
import de.gedoplan.v5t11.util.domain.attribute.SignalStellung;
import de.gedoplan.v5t11.util.domain.attribute.WeichenStellung;
import de.gedoplan.v5t11.util.domain.entity.fahrweg.Geraet;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SteuerungTest extends CdiTestBase {

  @Inject
  Steuerung steuerung;

  @Inject
  StatusEventCollector statusEventCollector;

  private static final int BM_ADR = 91;

  private static final int FD_ADR1 = 81;
  private static final int FD_ADR2 = 82;

  /**
   * Test: Kann Steuerung produziert (= aus XML eingelesen) werden?
   */
  @Test
  public void test_01_produce() {
    System.out.println(this.steuerung);
  }

  /**
   * Test: Entspricht der Zustand von Gleisabschnitten dem der zugehoerigen Adressen und werden Statuswechsel gemeldet?
   */
  @Test
  public void test_02_adjustGleisAbschnitt() {

    Gleisabschnitt[] gleisabschnitte = {
        this.steuerung.getGleisabschnitt("test", "1"),
        this.steuerung.getGleisabschnitt("test", "2"),
        this.steuerung.getGleisabschnitt("test", "3"),
        this.steuerung.getGleisabschnitt("test", "4"),
        this.steuerung.getGleisabschnitt("test", "5"),
        this.steuerung.getGleisabschnitt("test", "6"),
        this.steuerung.getGleisabschnitt("test", "7"),
        this.steuerung.getGleisabschnitt("test", "8")
    };

    // Grundzustand herstellen: Alle Gleise an BM-1 nicht besetzt
    this.steuerung.setSX1Kanal(BM_ADR, (byte) 0b1111_1111);
    int wert = 0;
    this.steuerung.setSX1Kanal(BM_ADR, (byte) wert);

    // Events kommen je nach Zentrale ggf. verzögert; abwarten
    this.steuerung.awaitSync();

    // Zufällige Kombinationen von Gleisbelegungen prüfen
    Random random = new Random(0);
    for (int count = 0; count < 100; ++count) {
      this.statusEventCollector.clear();

      int oldWert = wert;
      wert = random.nextInt(256);

      this.steuerung.setSX1Kanal(BM_ADR, (byte) wert);
      this.steuerung.awaitSync();
      ;

      for (int i = 0; i < gleisabschnitte.length; ++i) {
        // Ist der Gleiszustand korrekt?
        assertThat(gleisabschnitte[i].isBesetzt(), is((wert & (1 << i)) != 0));

        // Ist bei Zustandswechsel ein Event ausgelöst worden und sonst nicht?
        assertThat(this.statusEventCollector.getEvents().contains(gleisabschnitte[i]), is((wert & (1 << i)) != (oldWert & (1 << i))));
      }
    }

  }

  /**
   * Test: Entspricht der Zustand von Weichen und Signalen dem der zugehoerigen Adressen und werden Statuswechsel gemeldet?
   */
  @Test
  public void test_03_adjustGeraete() {

    Geraet[] geraete = {
        this.steuerung.getSignal("test", "P2"),
        this.steuerung.getSignal("test", "P3"),
        this.steuerung.getSignal("test", "P4"),
        this.steuerung.getWeiche("test", "10"),
        this.steuerung.getWeiche("test", "11"),
        this.steuerung.getSignal("test", "2a"),
        this.steuerung.getSignal("test", "2b"),
        this.steuerung.getSignal("test", "A"),
        this.steuerung.getSignal("test", "B"),
        this.steuerung.getSignal("test", "C")
    };

    // Grundzustand herstellen: Alle Geraete an FD-2 in Grundstellung
    this.steuerung.setSX1Kanal(FD_ADR1, (byte) 0b1111_1111);
    this.steuerung.setSX1Kanal(FD_ADR2, (byte) 0b1111_1111);
    long wert = 0;
    this.steuerung.setSX1Kanal(FD_ADR1, (byte) (wert & 0b1111_1111));
    this.steuerung.setSX1Kanal(FD_ADR2, (byte) ((wert >> 8) & 0b1111_1111));

    // Events kommen je nach Zentrale ggf. verzögert; abwarten
    this.steuerung.awaitSync();

    // Zufällige Kombinationen von Geraetestellungen prüfen
    Random random = new Random(0);
    for (int count = 0; count < 100; ++count) {
      this.statusEventCollector.clear();

      long oldWert = wert;
      wert = random.nextInt(256 * 256);

      this.steuerung.setSX1Kanal(FD_ADR1, (byte) (wert & 0b1111_1111));
      this.steuerung.setSX1Kanal(FD_ADR2, (byte) ((wert >> 8) & 0b1111_1111));
      this.steuerung.awaitSync();

      int anschluss = 0;
      for (int i = 0; i < geraete.length; ++i) {
        Geraet geraet = geraete[i];
        int bitCount;
        long mask;
        long geraeteWert;
        long oldGeraeteWert;

        if (geraet instanceof Weiche) {
          Weiche weiche = (Weiche) geraet;

          bitCount = weiche.getFunktionsdecoderZuordnung().getBitCount();
          mask = (1 << (weiche.getFunktionsdecoderZuordnung().getBitCount())) - 1;
          geraeteWert = (wert >>> anschluss) & mask;
          oldGeraeteWert = (oldWert >>> anschluss) & mask;

          // Ist die Weichenstellung korrekt?
          assertThat("Weichenstellung fuer " + weiche, weiche.getStellung(), is(weiche.getStellungForWert(geraeteWert)));
        } else if (geraet instanceof Signal) {
          Signal signal = (Signal) geraet;

          bitCount = signal.getFunktionsdecoderZuordnung().getBitCount();
          mask = (1 << (signal.getFunktionsdecoderZuordnung().getBitCount())) - 1;
          geraeteWert = (wert >>> anschluss) & mask;
          oldGeraeteWert = (oldWert >>> anschluss) & mask;

          // Ist die Signalstellung korrekt?
          SignalStellung stellungForWert = signal.getStellungForWert(geraeteWert);
          if (stellungForWert != null) {
            assertThat("Signalstellung fuer " + signal, signal.getStellung(), is(stellungForWert));
          } else {
            /*
             * Sondersituation: Der Geraetewert ist ungueltig, d. h. es gibt keine erlaubte Signalstellung dazu.
             * In diesem Fall Werte so manipulieren, als ob fuer dieses Signal keine Aenderung erfolgt waere.
             */
            geraeteWert = oldGeraeteWert;
            wert = (wert & ~(mask << anschluss)) | (geraeteWert << anschluss);
          }
        } else {
          throw new AssertionError("Unbekanntes Geraet: " + geraet);
        }

        // Ist bei Zustandswechsel ein Event ausgelöst worden und sonst nicht?
        assertThat("Statuswechselmeldung fuer " + geraet + " erfolgt", this.statusEventCollector.getEvents().contains(geraet), is(geraeteWert != oldGeraeteWert));

        anschluss += bitCount;
      }
    }

  }

  /**
   * Test: Entspricht nach Statusaenderungen von Weichen und Signalen der Zustand der zugehoerigen Adressen dem der Geraete und werden Statuswechsel gemeldet?
   */
  @Test
  public void test_04_setGeraete() {

    Geraet[] geraete = {
        this.steuerung.getSignal("test", "P2"),
        this.steuerung.getSignal("test", "P3"),
        this.steuerung.getSignal("test", "P4"),
        this.steuerung.getWeiche("test", "10"),
        this.steuerung.getWeiche("test", "11"),
        this.steuerung.getSignal("test", "2a"),
        this.steuerung.getSignal("test", "2b"),
        this.steuerung.getSignal("test", "A"),
        this.steuerung.getSignal("test", "B"),
        this.steuerung.getSignal("test", "C")
    };

    // Grundzustand herstellen: Alle Geraete an FD-2 in Grundstellung
    this.steuerung.setSX1Kanal(FD_ADR1, (byte) 0b1111_1111);
    this.steuerung.setSX1Kanal(FD_ADR2, (byte) 0b1111_1111);
    this.steuerung.setSX1Kanal(FD_ADR1, (byte) 0);
    this.steuerung.setSX1Kanal(FD_ADR2, (byte) 0);

    // Events kommen je nach Zentrale ggf. verzögert; abwarten
    this.steuerung.awaitSync();

    long wert = 0;

    // Zufällige Stellungsaenderungen prüfen
    Random random = new Random(0);
    for (int count = 0; count < 100; ++count) {
      this.statusEventCollector.clear();

      long newWert = 0;

      Geraet geraet = geraete[random.nextInt(geraete.length)];

      int bitCount;
      int anschluss;
      long mask;

      boolean changed = false;

      if (geraet instanceof Weiche) {
        Weiche weiche = (Weiche) geraet;

        bitCount = weiche.getFunktionsdecoderZuordnung().getBitCount();
        anschluss = weiche.getFunktionsdecoderZuordnung().getAnschluss();
        mask = ((1L << bitCount) - 1) << anschluss;

        WeichenStellung oldStellung = weiche.getStellung();
        WeichenStellung stellung = weiche.getStellungForWert(random.nextInt(2));

        weiche.setStellung(stellung);

        changed = stellung != oldStellung;

        newWert = (wert & ~mask) | (weiche.getWertForStellung(stellung) << anschluss);

      } else if (geraet instanceof Signal) {
        Signal signal = (Signal) geraet;

        bitCount = signal.getFunktionsdecoderZuordnung().getBitCount();
        anschluss = signal.getFunktionsdecoderZuordnung().getAnschluss();
        mask = ((1L << bitCount) - 1) << anschluss;

        SignalStellung oldStellung = signal.getStellung();
        List<SignalStellung> erlaubteStellungen = new ArrayList<>(signal.getErlaubteStellungen());
        SignalStellung stellung = erlaubteStellungen.get(random.nextInt(erlaubteStellungen.size()));

        signal.setStellung(stellung);

        changed = stellung != oldStellung;

        newWert = (wert & ~mask) | (signal.getWertForStellung(stellung) << anschluss);

      } else {
        throw new AssertionError("Unbekanntes Geraet: " + geraet);
      }

      // Events kommen je nach Zentrale ggf. verzögert; abwarten
      this.steuerung.awaitSync();

      // Ist der Kanalwert passend?
      wert = this.steuerung.getSX1Kanal(FD_ADR1) | (this.steuerung.getSX1Kanal(FD_ADR2) << 8);
      if (wert != newWert) {
        fail(String.format("Kanalwert nach Aenderung von %s: soll=0x%04x, ist=0x%04x", geraet, newWert, wert));
      }

      // Ist bei Zustandswechsel ein Event ausgelöst worden und sonst nicht?
      assertThat("Statuswechselmeldung fuer " + geraet + " erfolgt", this.statusEventCollector.getEvents().contains(geraet), is(changed));

      anschluss += bitCount;
    }
  }

  /**
   * Test: Entspricht der Zustand von Loks dem der zugehoerigen Adressen und werden Statuswechsel gemeldet?
   */
  // @Test
  // public void test_05_adjustLoks() {
  // Lok lok = this.steuerung.getLok("212 216-6");
  // int adr = lok.getLokdecoder().getAdresse();
  //
  // // Grundzustand herstellen: Lok steht vorwaerts ohne Licht
  // this.steuerung.setSX1Kanal(adr, 0b0111_1111);
  // int wert = 0;
  // this.steuerung.setSX1Kanal(adr, wert);
  //
  // // Zufällige Statusaenderungen prüfen
  // Random random = new Random(0);
  // for (int count = 0; count < 100; ++count) {
  // this.statusEventCollector.clear();
  //
  // int oldWert = wert;
  // wert = random.nextInt(100) < 10
  // ? oldWert
  // : random.nextInt(0b1000_0000);
  // this.steuerung.setSX1Kanal(adr, wert);
  //
  // // Stimmt der Lok-Status?
  // assertThat("Licht an?", lok.isLicht(), is((wert & 0b0100_0000) != 0));
  // assertThat("Rueckwaerts?", lok.isRueckwaerts(), is((wert & 0b0010_0000) != 0));
  // assertThat("Geschwindigkeit", lok.getGeschwindigkeit(), is((wert & 0b0001_1111)));
  //
  // // Ist bei Zustandswechsel ein Event ausgelöst worden und sonst nicht?
  // assertThat("Statuswechselmeldung fuer " + lok + " erfolgt", this.statusEventCollector.getEvents().contains(lok), is(wert != oldWert));
  // }
  //
  // }

  /**
   * Test: Entspricht nach Statusaenderungen von Loks der Zustand der zugehoerigen Adressen dem der Loks und werden Statuswechsel gemeldet?
   */
  // @Test
  // public void test_06_setLoks() {
  // Lok lok = this.steuerung.getLok("212 216-6");
  // int adr = lok.getLokdecoder().getAdresse();
  //
  // // Grundzustand herstellen: Lok steht vorwaerts ohne Licht
  // this.steuerung.setSX1Kanal(adr, 0b0111_1111);
  // this.steuerung.setSX1Kanal(adr, 0);
  //
  // // Zufällige Statusaenderungen prüfen
  // Random random = new Random(0);
  // for (int count = 0; count < 100; ++count) {
  // this.statusEventCollector.clear();
  //
  // int oldWert = lok.getGeschwindigkeit()
  // + (lok.isRueckwaerts() ? 0b0010_0000 : 0)
  // + (lok.isLicht() ? 0b0100_0000 : 0);
  //
  // int wert = random.nextInt(100) < 10
  // ? oldWert
  // : random.nextInt(0b1000_0000);
  //
  // lok.setLicht((wert & 0b0100_0000) != 0);
  // lok.setRueckwaerts((wert & 0b0010_0000) != 0);
  // lok.setGeschwindigkeit(wert & 0b0001_1111);
  //
  // // Stimmt der Kanalwert?
  // assertThat("Kanalwert", this.steuerung.getSX1Kanal(adr), is(wert));
  //
  // // Ist bei Zustandswechsel ein Event ausgelöst worden und sonst nicht?
  // assertThat("Statuswechselmeldung fuer " + lok + " erfolgt", this.statusEventCollector.getEvents().contains(lok), is(wert != oldWert));
  // }
  //
  // }
}
