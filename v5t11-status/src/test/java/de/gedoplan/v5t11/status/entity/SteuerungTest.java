package de.gedoplan.v5t11.status.entity;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import de.gedoplan.v5t11.status.CdiTestBase;
import de.gedoplan.v5t11.status.StatusEventCollector;
import de.gedoplan.v5t11.status.entity.fahrweg.Geraet;
import de.gedoplan.v5t11.status.entity.fahrweg.Gleisabschnitt;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Signal;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Signal.Stellung;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Weiche;
import de.gedoplan.v5t11.status.service.SteuerungProducer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SteuerungTest extends CdiTestBase {

  @BeforeClass
  public static void setConfig() {
    System.setProperty(SteuerungProducer.V5T11_CONFIG, "test");
  }

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
    this.steuerung.setKanalWert(BM_ADR, 0b1111_1111);
    int wert = 0;
    this.steuerung.setKanalWert(BM_ADR, wert);

    // Zufällige Kombinationen von Gleisbelegungen prüfen
    Random random = new Random(0);
    for (int count = 0; count < 100; ++count) {
      this.statusEventCollector.clear();

      int oldWert = wert;
      wert = random.nextInt(256);

      this.steuerung.setKanalWert(BM_ADR, wert);

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
    this.steuerung.setKanalWert(FD_ADR1, 0b1111_1111);
    this.steuerung.setKanalWert(FD_ADR2, 0b1111_1111);
    long wert = 0;
    this.steuerung.setKanalWert(FD_ADR1, (int) (wert & 0b1111_1111));
    this.steuerung.setKanalWert(FD_ADR2, (int) ((wert >> 8) & 0b1111_1111));

    // Zufällige Kombinationen von Geraetestellungen prüfen
    Random random = new Random(0);
    for (int count = 0; count < 100; ++count) {
      this.statusEventCollector.clear();

      long oldWert = wert;
      wert = random.nextInt(256 * 256);

      this.steuerung.setKanalWert(FD_ADR1, (int) (wert & 0b1111_1111));
      this.steuerung.setKanalWert(FD_ADR2, (int) ((wert >> 8) & 0b1111_1111));

      int anschluss = 0;
      for (int i = 0; i < geraete.length; ++i) {
        Geraet geraet = geraete[i];
        long mask = (1 << (geraet.getBitCount())) - 1;
        long geraeteWert = (wert >>> anschluss) & mask;
        long oldGeraeteWert = (oldWert >>> anschluss) & mask;

        if (geraet instanceof Weiche) {
          Weiche weiche = (Weiche) geraet;

          // Ist die Weichenstellung korrekt?
          assertThat("Weichenstellung fuer " + weiche, weiche.getStellung(), is(Weiche.Stellung.getInstance(geraeteWert)));
        } else if (geraet instanceof Signal) {
          Signal signal = (Signal) geraet;

          // Ist die Signalstellung korrekt?
          Stellung stellungForWert = signal.getStellungForWert(geraeteWert);
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
          fail("Unbekanntes Geraet: " + geraet);
        }

        // Ist bei Zustandswechsel ein Event ausgelöst worden und sonst nicht?
        assertThat("Statuswechselmeldung fuer " + geraet + " erfolgt", this.statusEventCollector.getEvents().contains(geraet), is(geraeteWert != oldGeraeteWert));

        anschluss += geraet.getBitCount();
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
    this.steuerung.setKanalWert(FD_ADR1, 0b1111_1111);
    this.steuerung.setKanalWert(FD_ADR2, 0b1111_1111);
    this.steuerung.setKanalWert(FD_ADR1, 0);
    this.steuerung.setKanalWert(FD_ADR2, 0);

    long wert = 0;

    // Zufällige Stellungsaenderungen prüfen
    Random random = new Random(0);
    for (int count = 0; count < 100; ++count) {
      this.statusEventCollector.clear();

      long newWert = 0;

      Geraet geraet = geraete[random.nextInt(geraete.length)];
      int anschluss = geraet.getAnschluss();
      long mask = ((1L << (geraet.getBitCount())) - 1) << anschluss;

      boolean changed = false;

      if (geraet instanceof Weiche) {
        Weiche weiche = (Weiche) geraet;

        Weiche.Stellung oldStellung = weiche.getStellung();
        Weiche.Stellung stellung = Weiche.Stellung.getInstance(random.nextInt(2));

        weiche.setStellung(stellung);

        changed = stellung != oldStellung;

        newWert = (wert & ~mask) | (stellung.getStellungsWert() << anschluss);

      } else if (geraet instanceof Signal) {
        Signal signal = (Signal) geraet;

        Signal.Stellung oldStellung = signal.getStellung();
        List<Signal.Stellung> erlaubteStellungen = new ArrayList<>(signal.getErlaubteStellungen());
        Signal.Stellung stellung = erlaubteStellungen.get(random.nextInt(erlaubteStellungen.size()));

        signal.setStellung(stellung);

        changed = stellung != oldStellung;

        newWert = (wert & ~mask) | (signal.getWertForStellung(stellung) << anschluss);

      } else {
        fail("Unbekanntes Geraet: " + geraet);
      }

      // Ist der Kanalwert passend?
      wert = this.steuerung.getKanalWert(FD_ADR1) | (this.steuerung.getKanalWert(FD_ADR2) << 8);
      assertThat("Kanalwert nach Aenderung von " + geraet, wert, is(newWert));

      // Ist bei Zustandswechsel ein Event ausgelöst worden und sonst nicht?
      assertThat("Statuswechselmeldung fuer " + geraet + " erfolgt", this.statusEventCollector.getEvents().contains(geraet), is(changed));

      anschluss += geraet.getBitCount();
    }
  }

}
