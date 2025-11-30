package de.gedoplan.v5t11.status.entity;

import de.gedoplan.v5t11.status.entity.fahrweg.Gleis;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Schalter;
import de.gedoplan.v5t11.status.testenvironment.profile.V5T11Test;
import de.gedoplan.v5t11.util.domain.attribute.SchalterStellung;

import jakarta.inject.Inject;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@QuarkusTest
@TestProfile(V5T11Test.class)
@TestMethodOrder(MethodOrderer.MethodName.class)
public class KehrschleifenTest {

  @Inject
  Logger log;

  @Inject
  Steuerung steuerung;

  private static final int BM_ADR = 92;

  private static final int FD_ADR = 83;

  @BeforeEach
  public void beforeEach() {
    // Gleisspannung ein (sonst gibt es keine Gleis-Events)
    this.steuerung.getZentrale().setGleisspannung(true);

    // Alle Gleise an BM nicht besetzt
    this.steuerung.setSX1Kanal(BM_ADR, (byte) 0b1111_1111);
    int wert = 0;
    this.steuerung.setSX1Kanal(BM_ADR, (byte) wert);

    // Schalter an FD aus
    this.steuerung.setSX1Kanal(FD_ADR, (byte) 0b1111_1111);
    this.steuerung.setSX1Kanal(FD_ADR, (byte) 0);

  }

  @Test
  public void test_01_fahrt17linksNachRechts() {
    this.log.info("----- test_01_fahrt17linksNachRechts -----");

    Schalter schalterKS17 = this.steuerung.getSchalter("SBf", "KS17");
    assertThat(schalterKS17.getStellung(), is(SchalterStellung.AUS));

    // Einfahrt von links
    setBelegt("17d", true);
    assertThat(schalterKS17.getStellung(), is(SchalterStellung.EIN));

    // Weiterfahrt auf zweiten Einfahrt-Abschnitt
    setBelegt("17c", true);
    assertThat(schalterKS17.getStellung(), is(SchalterStellung.EIN));

    // Weiterfahrt auf mittleren Abschnitt
    setBelegt("17d", false);
    setBelegt("17b", true);
    assertThat(schalterKS17.getStellung(), is(SchalterStellung.EIN));

    // Weiterfahrt auf Sensor-Abschnitt für Ausfahrt nach rechts
    setBelegt("17c", false);
    setBelegt("17a", true);
    assertThat(schalterKS17.getStellung(), is(SchalterStellung.AUS));

    // Weiterfahrt auf letzten rechten Ausfahrt-Abschnitt
    setBelegt("17b", false);
    setBelegt("109", true);
    assertThat(schalterKS17.getStellung(), is(SchalterStellung.AUS));

    // Weiterfahrt über Kehrschleifengleis hinaus
    setBelegt("17a", false);
    setBelegt("109", false);
    assertThat(schalterKS17.getStellung(), is(SchalterStellung.AUS));
  }

  @Test
  public void test_02_fahrt17rechtsNachLinks() {
    this.log.info("----- test_02_fahrt17rechtsNachLinks -----");

    Schalter schalterKS17 = this.steuerung.getSchalter("SBf", "KS17");
    assertThat(schalterKS17.getStellung(), is(SchalterStellung.AUS));

    // Einfahrt von rechts
    setBelegt("109", true);
    assertThat(schalterKS17.getStellung(), is(SchalterStellung.AUS));

    // Weiterfahrt auf zweiten rechten Einfahrt-Abschnitt
    setBelegt("17a", true);
    assertThat(schalterKS17.getStellung(), is(SchalterStellung.AUS));

    // Weiterfahrt auf mittleren Abschnitt
    setBelegt("109", false);
    setBelegt("17b", true);
    assertThat(schalterKS17.getStellung(), is(SchalterStellung.AUS));

    // Weiterfahrt auf Sensor-Abschnitt für Ausfahrt nach links
    setBelegt("17a", false);
    setBelegt("17c", true);
    assertThat(schalterKS17.getStellung(), is(SchalterStellung.EIN));

    // Weiterfahrt auf letzten linken Ausfahrt-Abschnitt
    setBelegt("17b", false);
    setBelegt("17d", true);
    assertThat(schalterKS17.getStellung(), is(SchalterStellung.EIN));

    // Weiterfahrt über Kehrschleifengleis hinaus
    setBelegt("17c", false);
    setBelegt("17d", false);
    assertThat(schalterKS17.getStellung(), is(SchalterStellung.EIN));
  }

  @Test
  public void test_03_fahrt18linksNachRechts() {
    this.log.info("----- test_03_fahrt18linksNachRechts -----");

    Schalter schalterKS18 = this.steuerung.getSchalter("SBf", "KS18");
    assertThat(schalterKS18.getStellung(), is(SchalterStellung.AUS));

    // Einfahrt von links
    setBelegt("18c", true);
    assertThat(schalterKS18.getStellung(), is(SchalterStellung.EIN));

    // Weiterfahrt auf mittleren Abschnitt
    setBelegt("18b", true);
    assertThat(schalterKS18.getStellung(), is(SchalterStellung.EIN));

    // Weiterfahrt auf Sensor-Abschnitt für Ausfahrt nach rechts
    setBelegt("18c", false);
    setBelegt("18a", true);
    assertThat(schalterKS18.getStellung(), is(SchalterStellung.AUS));

    // Weiterfahrt über Kehrschleifengleis hinaus
    setBelegt("18b", false);
    setBelegt("18a", false);
    assertThat(schalterKS18.getStellung(), is(SchalterStellung.AUS));
  }

  private void setBelegt(String gleisName, boolean belegt) {
    Gleis gleis = this.steuerung.getGleis("SBf", gleisName);
    int bmAdr = gleis.getBesetztmelder().getAdresse();
    int anschluss = gleis.getAnschluss();
    int bit = 1 << anschluss;
    int wert = belegt ? bit : 0;
    this.steuerung.setSX1Kanal(bmAdr, (this.steuerung.getSX1Kanal(bmAdr) & ~bit) | wert);
  }

}
