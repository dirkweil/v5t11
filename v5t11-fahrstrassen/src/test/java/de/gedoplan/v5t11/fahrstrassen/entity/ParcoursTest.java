package de.gedoplan.v5t11.fahrstrassen.entity;

import static org.junit.jupiter.api.Assertions.*;

import de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse.Fahrstrasse;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Gleisabschnitt;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Signal;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Weiche;
import de.gedoplan.v5t11.util.test.V5t11TestConfigDirExtension;

import java.util.Collection;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.jboss.logging.Logger;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;

import io.quarkus.test.junit.QuarkusTestExtension;
import lombok.AllArgsConstructor;

@ExtendWith({ V5t11TestConfigDirExtension.class, QuarkusTestExtension.class })
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
public class ParcoursTest {

  @Inject
  Parcours parcours;

  @Inject
  Logger logger;

  /**
   * Test: Erwartete Fahrstrassen da?
   */
  @Test
  public void test_01_fahrstrassen() {

    @AllArgsConstructor
    class FSParm {
      String bereich;
      String name;
      boolean combi;
      int rank;
      boolean zaehlrichtung;
      String elementTypen;
    }

    FSParm[] fsParms = {
        new FSParm("show", "1-W1-11", false, 4, false, "GLEIS,WEICHE,GLEIS,WEICHE,GLEIS"),
        new FSParm("show", "1-W3-S", false, 4, true, "GLEIS,SIGNAL,WEICHE,GLEIS,SIGNAL,GLEIS"),
        new FSParm("show", "2-W2-12", false, 4, false, "GLEIS,WEICHE,GLEIS,WEICHE,GLEIS"),
        new FSParm("show", "2-W2-W1-11", false, 5, false, "GLEIS,WEICHE,GLEIS,WEICHE,GLEIS,GLEIS"),
        new FSParm("show", "2-W3-S", false, 3, true, "GLEIS,SIGNAL,WEICHE,GLEIS,SIGNAL,GLEIS"),
        new FSParm("show", "11-W1-1", false, 4, true, "GLEIS,WEICHE,GLEIS,WEICHE,GLEIS"),
        new FSParm("show", "11-W1-1-W3-S", true, 8, true, "GLEIS,WEICHE,GLEIS,WEICHE,GLEIS,SIGNAL,WEICHE,GLEIS,SIGNAL,GLEIS"),
        new FSParm("show", "11-W1-W2-2", false, 5, true, "GLEIS,WEICHE,GLEIS,WEICHE,GLEIS,GLEIS"),
        new FSParm("show", "11-W1-W2-2-W3-S", true, 8, true, "GLEIS,WEICHE,GLEIS,WEICHE,GLEIS,GLEIS,SIGNAL,WEICHE,GLEIS,SIGNAL,GLEIS"),
        new FSParm("show", "12-W2-2", false, 4, true, "GLEIS,WEICHE,GLEIS,WEICHE,GLEIS"),
        new FSParm("show", "12-W2-2-W3-S", true, 7, true, "GLEIS,WEICHE,GLEIS,WEICHE,GLEIS,SIGNAL,WEICHE,GLEIS,SIGNAL,GLEIS"),
        new FSParm("show", "S-W3-1", false, 4, false, "GLEIS,SIGNAL,WEICHE,GLEIS,SIGNAL,GLEIS"),
        new FSParm("show", "S-W3-1-W1-11", true, 8, false, "GLEIS,SIGNAL,WEICHE,GLEIS,SIGNAL,GLEIS,WEICHE,GLEIS,WEICHE,GLEIS"),
        new FSParm("show", "S-W3-2", false, 3, false, "GLEIS,SIGNAL,WEICHE,GLEIS,SIGNAL,GLEIS"),
        new FSParm("show", "S-W3-2-W2-12", true, 7, false, "GLEIS,SIGNAL,WEICHE,GLEIS,SIGNAL,GLEIS,WEICHE,GLEIS,WEICHE,GLEIS"),
        new FSParm("show", "S-W3-2-W2-W1-11", true, 8, false, "GLEIS,SIGNAL,WEICHE,GLEIS,SIGNAL,GLEIS,WEICHE,GLEIS,WEICHE,GLEIS,GLEIS"),
    };

    Collection<Fahrstrasse> fahrstrassen = this.parcours.getFahrstrassen();
    if (this.logger.isDebugEnabled()) {
      fahrstrassen.forEach(this.logger::debug);
    }

    // fahrstrassen.forEach(fs -> System.out.printf("new FSParm(\"%s\", \"%s\", %b, %d, %b, \"%s\"),\n",
    // fs.getBereich(), fs.getName(), fs.isCombi(), fs.getRank(), fs.isZaehlrichtung(), getElementTypen(fs)));

    assertEquals(fsParms.length, fahrstrassen.size(), "Anzahl Fahrstrassen falsch");

    for (FSParm fsParm : fsParms) {
      Fahrstrasse fahrstrasse = this.parcours.getFahrstrasse(fsParm.bereich, fsParm.name);
      String fsDesc = fsParm.bereich + "/" + fsParm.name;
      assertNotNull(fahrstrasse, "Fahrstrasse nicht gefunden: " + fsDesc);
      assertEquals(fsParm.combi, fahrstrasse.isCombi(), "Fahrstrasse.combi falsch: " + fsDesc);
      assertEquals(fsParm.rank, fahrstrasse.getRank(), "Fahrstrasse.rank falsch: " + fsDesc);
      assertEquals(fsParm.zaehlrichtung, fahrstrasse.isZaehlrichtung(), "Fahrstrasse.zaehlrichtung falsch: " + fsDesc);
      assertEquals(fsParm.elementTypen, getElementTypen(fahrstrasse), "Element-Typen falsch: " + fsDesc);
    }
  }

  private String getElementTypen(Fahrstrasse fahrstrasse) {
    return fahrstrasse.getElemente()
        .stream()
        .map(fe -> fe.getTyp())
        .collect(Collectors.joining(","));
  }

  /**
   * Test: Erwartete Gleisabschnitte da?
   */
  @Test
  public void test_02_gleisabschnitte() {
    Collection<Gleisabschnitt> gleisabschnitte = this.parcours.getGleisabschnitte();
    if (this.logger.isDebugEnabled()) {
      gleisabschnitte.forEach(this.logger::debug);
    }
    assertEquals(8, gleisabschnitte.size(), "Anzahl Gleisabschnitte falsch");
  }

  /**
   * Test: Erwartete Signale da?
   */
  @Test
  public void test_03_signale() {
    Collection<Signal> signale = this.parcours.getSignale();
    if (this.logger.isDebugEnabled()) {
      signale.forEach(this.logger::debug);
    }
    assertEquals(3, signale.size(), "Anzahl Signale falsch");
  }

  /**
   * Test: Erwartete Weichen da?
   */
  @Test
  public void test_04_weichen() {
    Collection<Weiche> weichen = this.parcours.getWeichen();
    if (this.logger.isDebugEnabled()) {
      weichen.forEach(this.logger::debug);
    }
    assertEquals(3, weichen.size(), "Anzahl Weichen falsch");
  }

  /**
   * Test: Erwartete AutoFahrstrassen da?
   */
  @Test
  public void test_05_autoFahrstrassen() {
    Collection<AutoFahrstrasse> autoFahrstrassen = this.parcours.getAutoFahrstrassen();
    if (this.logger.isDebugEnabled()) {
      autoFahrstrassen.forEach(this.logger::debug);
    }
    assertEquals(1, autoFahrstrassen.size(), "Anzahl AutoFahrstrassen falsch");
  }

}
