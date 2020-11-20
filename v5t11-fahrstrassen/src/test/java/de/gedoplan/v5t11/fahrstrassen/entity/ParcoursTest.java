package de.gedoplan.v5t11.fahrstrassen.entity;

import static org.junit.jupiter.api.Assertions.*;

import de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse.Fahrstrasse;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse.Fahrstrassenelement;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Gleisabschnitt;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Signal;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Weiche;
import de.gedoplan.v5t11.fahrstrassen.persistence.GleisabschnittRepository;
import de.gedoplan.v5t11.fahrstrassen.persistence.SignalRepository;
import de.gedoplan.v5t11.fahrstrassen.persistence.WeicheRepository;
import de.gedoplan.v5t11.util.domain.attribute.BereichselementId;
import de.gedoplan.v5t11.util.test.V5t11TestConfigDirExtension;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
  GleisabschnittRepository gleisabschnittRepository;

  @Inject
  SignalRepository signalRepository;

  @Inject
  WeicheRepository weicheRepository;

  @Inject
  Logger logger;

  @AllArgsConstructor
  private static class FSParm {
    String bereich;
    String name;
    boolean combi;
    int rank;
    boolean zaehlrichtung;
    String elementTypen;
  }

  private static final FSParm[] fsParms = {
      new FSParm("NBf", "1-W4-11", true, 4, false, "G-1,G-W4,w-1~,W-4,G-11"),
      new FSParm("NBf", "1-W4-W1-102", true, 6, false, "G-1,G-W4,W-4,W-1,G-W1,G-102"),
      new FSParm("NBf", "1-W7-202", true, 4, true, "G+1,W+7,G+W7,G+202"),
      new FSParm("NBf", "102-W1-W2-2", true, 5, true, "G+102,G+W1,W+1,w+4~,G+W2,W+2,w+3~,G+2"),
      new FSParm("NBf", "102-W1-W2-2-W6-W7-202", true, 9, true, "G+102,G+W1,W+1,w+4~,G+W2,W+2,w+3~,G+2,W+6,w+5b~,G+W6,W+7,G+W7,G+202"),
      new FSParm("NBf", "102-W1-W2-W3-3", true, 6, true, "G+102,G+W1,W+1,w+4~,G+W2,W+2,W+3,G+W3,G+3"),
      new FSParm("NBf", "102-W1-W2-W3-3-W5-23", true, 9, true, "G+102,G+W1,W+1,w+4~,G+W2,W+2,W+3,G+W3,G+3,W+5a,G+W5,W+5b,w+6~,G+23"),
      new FSParm("NBf", "102-W1-W2-W3-3-W5-W6-W7-202", true, 11, true, "G+102,G+W1,W+1,w+4~,G+W2,W+2,W+3,G+W3,G+3,W+5a,G+W5,W+5b,W+6,G+W6,W+7,G+W7,G+202"),
      new FSParm("NBf", "102-W1-W4-1", true, 6, true, "G+102,G+W1,W+1,W+4,G+W4,G+1"),
      new FSParm("NBf", "102-W1-W4-1-W7-202", true, 9, true, "G+102,G+W1,W+1,W+4,G+W4,G+1,W+7,G+W7,G+202"),
      new FSParm("NBf", "11-W4-1", true, 4, true, "G+11,W+4,w+1~,G+W4,G+1"),
      new FSParm("NBf", "11-W4-1-W7-202", true, 7, true, "G+11,W+4,w+1~,G+W4,G+1,W+7,G+W7,G+202"),
      new FSParm("NBf", "13-W3-3", true, 3, true, "G+13,W+3,w+2~,G+W3,G+3"),
      new FSParm("NBf", "13-W3-3-W5-23", true, 6, true, "G+13,W+3,w+2~,G+W3,G+3,W+5a,G+W5,W+5b,w+6~,G+23"),
      new FSParm("NBf", "13-W3-3-W5-W6-W7-202", true, 8, true, "G+13,W+3,w+2~,G+W3,G+3,W+5a,G+W5,W+5b,W+6,G+W6,W+7,G+W7,G+202"),
      new FSParm("NBf", "2-W2-W1-102", true, 5, false, "G-2,w-3~,W-2,G-W2,w-4~,W-1,G-W1,G-102"),
      new FSParm("NBf", "2-W6-W7-202", true, 5, true, "G+2,W+6,w+5b~,G+W6,W+7,G+W7,G+202"),
      new FSParm("NBf", "202-W7-1", true, 4, false, "G-202,G-W7,W-7,G-1"),
      new FSParm("NBf", "202-W7-1-W4-11", true, 7, false, "G-202,G-W7,W-7,G-1,G-W4,w-1~,W-4,G-11"),
      new FSParm("NBf", "202-W7-1-W4-W1-102", true, 9, false, "G-202,G-W7,W-7,G-1,G-W4,W-4,W-1,G-W1,G-102"),
      new FSParm("NBf", "202-W7-W6-2", true, 5, false, "G-202,G-W7,W-7,G-W6,w-5b~,W-6,G-2"),
      new FSParm("NBf", "202-W7-W6-2-W2-W1-102", true, 9, false, "G-202,G-W7,W-7,G-W6,w-5b~,W-6,G-2,w-3~,W-2,G-W2,w-4~,W-1,G-W1,G-102"),
      new FSParm("NBf", "202-W7-W6-W5-3", true, 6, false, "G-202,G-W7,W-7,G-W6,W-6,W-5b,G-W5,W-5a,G-3"),
      new FSParm("NBf", "202-W7-W6-W5-3-W3-13", true, 8, false, "G-202,G-W7,W-7,G-W6,W-6,W-5b,G-W5,W-5a,G-3,G-W3,w-2~,W-3,G-13"),
      new FSParm("NBf", "202-W7-W6-W5-3-W3-W2-W1-102", true, 11, false, "G-202,G-W7,W-7,G-W6,W-6,W-5b,G-W5,W-5a,G-3,G-W3,W-3,W-2,G-W2,w-4~,W-1,G-W1,G-102"),
      new FSParm("NBf", "202-W7-W6-W5-4", true, 7, false, "G-202,G-W7,W-7,G-W6,W-6,W-5b,G-W5,W-5a,G-4"),
      new FSParm("NBf", "23-W5-3", true, 4, false, "G-23,w-6~,W-5b,G-W5,W-5a,G-3"),
      new FSParm("NBf", "23-W5-3-W3-13", true, 6, false, "G-23,w-6~,W-5b,G-W5,W-5a,G-3,G-W3,w-2~,W-3,G-13"),
      new FSParm("NBf", "23-W5-3-W3-W2-W1-102", true, 9, false, "G-23,w-6~,W-5b,G-W5,W-5a,G-3,G-W3,W-3,W-2,G-W2,w-4~,W-1,G-W1,G-102"),
      new FSParm("NBf", "23-W5-4", true, 5, false, "G-23,w-6~,W-5b,G-W5,W-5a,G-4"),
      new FSParm("NBf", "3-W3-13", true, 3, false, "G-3,G-W3,w-2~,W-3,G-13"),
      new FSParm("NBf", "3-W3-W2-W1-102", true, 6, false, "G-3,G-W3,W-3,W-2,G-W2,w-4~,W-1,G-W1,G-102"),
      new FSParm("NBf", "3-W5-23", true, 4, true, "G+3,W+5a,G+W5,W+5b,w+6~,G+23"),
      new FSParm("NBf", "3-W5-W6-W7-202", true, 6, true, "G+3,W+5a,G+W5,W+5b,W+6,G+W6,W+7,G+W7,G+202"),
      new FSParm("NBf", "4-W5-23", true, 5, true, "G+4,W+5a,G+W5,W+5b,w+6~,G+23"),
      new FSParm("NBf", "4-W5-W6-W7-202", true, 7, true, "G+4,W+5a,G+W5,W+5b,W+6,G+W6,W+7,G+W7,G+202"),
      new FSParm("show", "1-W1-11", false, 4, false, "G-1,w-2~,G-W1,W-1,G-11"),
      new FSParm("show", "1-W3-S", true, 4, true, "G+1,S+N1,s+n2~,W+3,G+W3,s+f~,G+S"),
      new FSParm("show", "11-W1-1", false, 4, true, "G+11,W+1,G+W1,w+2~,G+1"),
      new FSParm("show", "11-W1-1-W3-S", true, 7, true, "G+11,W+1,G+W1,w+2~,G+1,S+N1,s+n2~,W+3,G+W3,s+f~,G+S"),
      new FSParm("show", "11-W1-W2-2", false, 5, true, "G+11,W+1,G+W1,W+2,G+W2,G+2"),
      new FSParm("show", "11-W1-W2-2-W3-S", true, 7, true, "G+11,W+1,G+W1,W+2,G+W2,G+2,S+N2,s+n1~,W+3,G+W3,s+f~,G+S"),
      new FSParm("show", "12-W2-2", false, 4, true, "G+12,W+2,G+W2,w+1~,G+2"),
      new FSParm("show", "12-W2-2-W3-S", true, 6, true, "G+12,W+2,G+W2,w+1~,G+2,S+N2,s+n1~,W+3,G+W3,s+f~,G+S"),
      new FSParm("show", "2-W2-12", false, 4, false, "G-2,w-1~,G-W2,W-2,G-12"),
      new FSParm("show", "2-W2-W1-11", false, 5, false, "G-2,G-W2,W-2,G-W1,W-1,G-11"),
      new FSParm("show", "2-W3-S", true, 3, true, "G+2,S+N2,s+n1~,W+3,G+W3,s+f~,G+S"),
      new FSParm("show", "S-W3-1", false, 4, false, "G-S,s-f~,G-W3,s-n1~,s-n2~,W-3,G-1"),
      new FSParm("show", "S-W3-1-W1-11", true, 7, false, "G-S,s-f~,G-W3,s-n1~,s-n2~,W-3,G-1,w-2~,G-W1,W-1,G-11"),
      new FSParm("show", "S-W3-2", false, 3, false, "G-S,s-f~,G-W3,s-n1~,s-n2~,W-3,G-2"),
      new FSParm("show", "S-W3-2-W2-12", true, 6, false, "G-S,s-f~,G-W3,s-n1~,s-n2~,W-3,G-2,w-1~,G-W2,W-2,G-12"),
      new FSParm("show", "S-W3-2-W2-W1-11", true, 7, false, "G-S,s-f~,G-W3,s-n1~,s-n2~,W-3,G-2,G-W2,W-2,G-W1,W-1,G-11"),
  };

  /**
   * Test: Erwartete Fahrstrassen da?
   */
  @Test
  public void test_01_fahrstrassen() {

    Collection<Fahrstrasse> fahrstrassen = this.parcours.getFahrstrassen();
    if (this.logger.isDebugEnabled()) {
      fahrstrassen.forEach(this.logger::debug);
    }

    fahrstrassen
        .stream()
        .sorted((a, b) -> a.getName().compareTo(b.getName()))
        .sorted((a, b) -> a.getBereich().compareTo(b.getBereich()))
        .forEach(fs -> System.out.printf("new FSParm(\"%s\", \"%s\", %b, %d, %b, \"%s\"),\n",
            fs.getBereich(), fs.getName(), fs.isCombi(), fs.getRank(), fs.isZaehlrichtung(), getElementCodes(fs)));

    assertEquals(fsParms.length, fahrstrassen.size(), "Anzahl Fahrstrassen falsch");

    for (FSParm fsParm : fsParms) {
      Fahrstrasse fahrstrasse = this.parcours.getFahrstrasse(fsParm.bereich, fsParm.name);
      String fsDesc = fsParm.bereich + "/" + fsParm.name;
      assertNotNull(fahrstrasse, "Fahrstrasse nicht gefunden: " + fsDesc);
      assertEquals(fsParm.combi, fahrstrasse.isCombi(), "Fahrstrasse.combi falsch: " + fsDesc);
      assertEquals(fsParm.rank, fahrstrasse.getRank(), "Fahrstrasse.rank falsch: " + fsDesc);
      assertEquals(fsParm.zaehlrichtung, fahrstrasse.isZaehlrichtung(), "Fahrstrasse.zaehlrichtung falsch: " + fsDesc);
      assertEquals(fsParm.elementTypen, getElementCodes(fahrstrasse), "Element-Typen falsch: " + fsDesc);
    }
  }

  /**
   * Test: Erwartete Fahrstrassen da?
   */
  @Test
  public void test_02_fahrstrassen() {

    Gleisabschnitt gleisabschnitt = this.gleisabschnittRepository.findById(new BereichselementId("show", "11"));
    assertNotNull(gleisabschnitt, "Gleisabschnitt show/11 nicht gefunden");
    Collection<Fahrstrasse> fahrstrassen = this.parcours.getFahrstrassenMitStart(gleisabschnitt);
    if (this.logger.isDebugEnabled()) {
      fahrstrassen.forEach(this.logger::debug);
    }

    List<FSParm> fs11Parms = Stream.of(fsParms)
        .filter(fsp -> fsp.bereich.equals("show"))
        .filter(fsp -> fsp.name.startsWith("11-"))
        .collect(Collectors.toList());

    assertEquals(fs11Parms.size(), fahrstrassen.size(), "Anzahl Fahrstrassen falsch");

    fs11Parms.forEach(fsParm -> {
      Fahrstrasse fahrstrasse = this.parcours.getFahrstrasse(fsParm.bereich, fsParm.name);
      String fsDesc = fsParm.bereich + "/" + fsParm.name;
      assertNotNull(fahrstrasse, "Fahrstrasse nicht gefunden: " + fsDesc);
      assertEquals(fsParm.combi, fahrstrasse.isCombi(), "Fahrstrasse.combi falsch: " + fsDesc);
      assertEquals(fsParm.rank, fahrstrasse.getRank(), "Fahrstrasse.rank falsch: " + fsDesc);
      assertEquals(fsParm.zaehlrichtung, fahrstrasse.isZaehlrichtung(), "Fahrstrasse.zaehlrichtung falsch: " + fsDesc);
      assertEquals(fsParm.elementTypen, getElementCodes(fahrstrasse), "Element-Typen falsch: " + fsDesc);
    });
  }

  private String getElementCodes(Fahrstrasse fahrstrasse) {
    return fahrstrasse.getElemente()
        .stream()
        .map(Fahrstrassenelement::getCode)
        .collect(Collectors.joining(","));
  }

  /**
   * Test: Erwartete Gleisabschnitte da?
   */
  @Test
  public void test_03_gleisabschnitte() {
    Collection<Gleisabschnitt> gleisabschnitte = this.gleisabschnittRepository.findAll();
    if (this.logger.isDebugEnabled()) {
      gleisabschnitte.forEach(this.logger::debug);
    }
    assertEquals(24, gleisabschnitte.size(), "Anzahl Gleisabschnitte falsch");
  }

  /**
   * Test: Erwartete Signale da?
   */
  @Test
  public void test_04_signale() {
    Collection<Signal> signale = this.signalRepository.findAll();
    if (this.logger.isDebugEnabled()) {
      signale.forEach(this.logger::debug);
    }
    assertEquals(3, signale.size(), "Anzahl Signale falsch");
  }

  /**
   * Test: Erwartete Weichen da?
   */
  @Test
  public void test_05_weichen() {
    Collection<Weiche> weichen = this.weicheRepository.findAll();
    if (this.logger.isDebugEnabled()) {
      weichen.forEach(this.logger::debug);
    }
    assertEquals(11, weichen.size(), "Anzahl Weichen falsch");
  }

  /**
   * Test: Erwartete AutoFahrstrassen da?
   */
  @Test
  public void test_06_autoFahrstrassen() {
    Collection<AutoFahrstrasse> autoFahrstrassen = this.parcours.getAutoFahrstrassen();
    if (this.logger.isDebugEnabled()) {
      autoFahrstrassen.forEach(this.logger::debug);
    }
    assertEquals(1, autoFahrstrassen.size(), "Anzahl AutoFahrstrassen falsch");
  }

}
