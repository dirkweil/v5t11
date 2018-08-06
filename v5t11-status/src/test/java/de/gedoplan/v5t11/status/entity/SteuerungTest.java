package de.gedoplan.v5t11.status.entity;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import de.gedoplan.v5t11.status.CdiTestBase;
import de.gedoplan.v5t11.status.StatusEventCollector;
import de.gedoplan.v5t11.status.entity.fahrweg.Geraet;
import de.gedoplan.v5t11.status.entity.fahrweg.Gleisabschnitt;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Signal;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Weiche;

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
    final int ADR = 90;

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
    this.steuerung.setKanalWert(ADR, 0b1111_1111);
    int wert = 0;
    this.steuerung.setKanalWert(ADR, wert);

    // Zufällige Kombinationen von Gleisbelegungen prüfen
    Random random = new Random(0);
    for (int count = 0; count < 100; ++count) {
      this.statusEventCollector.clear();

      int oldWert = wert;
      wert = random.nextInt(256);

      this.steuerung.setKanalWert(ADR, wert);

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
    final int ADR = 81;

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

    // Grundzustand herstellen: Alle Gleise an BM-1 nicht besetzt
    this.steuerung.setKanalWert(ADR, 0b1111_1111);
    int wert = 0;
    this.steuerung.setKanalWert(ADR, wert);

    // Zufällige Kombinationen von Gleisbelegungen prüfen
    Random random = new Random(0);
    for (int count = 0; count < 100; ++count) {
      this.statusEventCollector.clear();

      int oldWert = wert;
      wert = random.nextInt(256);

      this.steuerung.setKanalWert(ADR, wert);

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
          assertThat("Signalstellung fuer " + signal, signal.getStellung(), is(signal.getStellungForWert(geraeteWert)));
        } else {
          fail("Unbekanntes Geraet: " + geraet);
        }

        // Ist bei Zustandswechsel ein Event ausgelöst worden und sonst nicht?
        assertThat("Statuswechselmeldung fuer " + geraet + " erfolgt", this.statusEventCollector.getEvents().contains(geraet), is(geraeteWert != oldGeraeteWert));

        anschluss += geraet.getBitCount();
      }
    }

  }
}
