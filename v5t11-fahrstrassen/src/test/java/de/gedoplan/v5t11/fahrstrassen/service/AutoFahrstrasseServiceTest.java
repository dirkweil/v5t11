package de.gedoplan.v5t11.fahrstrassen.service;

import de.gedoplan.v5t11.fahrstrassen.entity.Parcours;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse.Fahrstrasse;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Gleis;
import de.gedoplan.v5t11.fahrstrassen.persistence.GleisRepository;
import de.gedoplan.v5t11.util.cdi.Changed;
import de.gedoplan.v5t11.util.cdi.EventFirer;
import de.gedoplan.v5t11.util.domain.attribute.FahrstrassenReservierungsTyp;
import de.gedoplan.v5t11.util.test.V5t11TestConfigDirExtension;

import java.util.List;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import io.quarkus.test.junit.QuarkusTestExtension;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith({ V5t11TestConfigDirExtension.class, QuarkusTestExtension.class })
@TestMethodOrder(MethodOrderer.MethodName.class)
public class AutoFahrstrasseServiceTest {

  private static final String BEREICH = "show";

  private static final String TRIGGER_NAME = "S";

  private static final String FS_1_NAME = "S-W3-1-W1-11";
  private static final String FS_2_NAME = "S-W3-2-W2-W1-11";
  private static final String FS_3_NAME = "S-W3-2-W2-12";

  @Inject
  GleisRepository gleisRepository;

  @Inject
  Parcours parcours;

  @Inject
  AutoFahrstrassenService autoFahrstrassenService;

  @Inject
  EventFirer eventFirer;

  @Inject
  Logger logger;

  @Test
  public void test_01_triggerToFahrstrassenMap() throws Exception {

    Gleis trigger = this.gleisRepository.findByBereichAndName(BEREICH, TRIGGER_NAME).orElse(null);
    assertNotNull(trigger);

    List<Fahrstrasse> fahrstrassen = this.autoFahrstrassenService.getAutoFahrstrassen().get(trigger);
    assertNotNull(fahrstrassen);
    assertThat("Anzahl zugeordneter Fahrstrassen", fahrstrassen.size(), is(3));
    assertThat("Name der 1. zugeordneten Fahrstrasse", fahrstrassen.get(0).getName(), is(FS_1_NAME));
    assertThat("Name der 2. zugeordneten Fahrstrasse", fahrstrassen.get(1).getName(), is(FS_2_NAME));
    assertThat("Name der 3. zugeordneten Fahrstrasse", fahrstrassen.get(2).getName(), is(FS_3_NAME));
  }

  @Test
  @Transactional
  public void test_02_autoreserviere_allesFrei() throws Exception {

    // Beteiligte Objekte besorgen und Grundzustand sicherstellen
    Gleis trigger = this.gleisRepository.findByBereichAndName(BEREICH, TRIGGER_NAME).orElse(null);
    assertNotNull(trigger);
    assertFalse(trigger.isBesetzt());

    Fahrstrasse fahrstrasse1 = this.parcours.getFahrstrasse(BEREICH, FS_1_NAME);
    assertNotNull(fahrstrasse1);
    assertEquals(FahrstrassenReservierungsTyp.UNRESERVIERT, fahrstrasse1.getReservierungsTyp());

    Fahrstrasse fahrstrasse2 = this.parcours.getFahrstrasse(BEREICH, FS_2_NAME);
    assertNotNull(fahrstrasse2);
    assertEquals(FahrstrassenReservierungsTyp.UNRESERVIERT, fahrstrasse2.getReservierungsTyp());

    Fahrstrasse fahrstrasse3 = this.parcours.getFahrstrasse(BEREICH, FS_3_NAME);
    assertNotNull(fahrstrasse3);
    assertEquals(FahrstrassenReservierungsTyp.UNRESERVIERT, fahrstrasse3.getReservierungsTyp());

    // Trigger besetzen und dies melden
    trigger.setBesetzt(true);
    this.eventFirer.fire(trigger, Changed.Literal.INSTANCE);

    // Nun muss 1. Fahrstrasse reserviert sein, die beiden anderen nicht
    assertEquals(FahrstrassenReservierungsTyp.ZUGFAHRT, fahrstrasse1.getReservierungsTyp());
    assertEquals(FahrstrassenReservierungsTyp.UNRESERVIERT, fahrstrasse2.getReservierungsTyp());
    assertEquals(FahrstrassenReservierungsTyp.UNRESERVIERT, fahrstrasse3.getReservierungsTyp());

    // Grundzustand wieder herstellen
    trigger.setBesetzt(false);
    fahrstrasse1.freigeben(null);
  }

  @Test
  @Transactional
  public void test_03_autoreserviere_1besetzt() throws Exception {

    // Beteiligte Objekte besorgen und Grundzustand sicherstellen
    Gleis trigger = this.gleisRepository.findByBereichAndName(BEREICH, TRIGGER_NAME).orElse(null);
    assertNotNull(trigger);
    assertFalse(trigger.isBesetzt());

    Fahrstrasse fahrstrasse1 = this.parcours.getFahrstrasse(BEREICH, FS_1_NAME);
    assertNotNull(fahrstrasse1);
    assertEquals(FahrstrassenReservierungsTyp.UNRESERVIERT, fahrstrasse1.getReservierungsTyp());

    Fahrstrasse fahrstrasse2 = this.parcours.getFahrstrasse(BEREICH, FS_2_NAME);
    assertNotNull(fahrstrasse2);
    assertEquals(FahrstrassenReservierungsTyp.UNRESERVIERT, fahrstrasse2.getReservierungsTyp());

    Fahrstrasse fahrstrasse3 = this.parcours.getFahrstrasse(BEREICH, FS_3_NAME);
    assertNotNull(fahrstrasse3);
    assertEquals(FahrstrassenReservierungsTyp.UNRESERVIERT, fahrstrasse3.getReservierungsTyp());

    // Gleis 1 besetzen (ist Teil der ersten zugeordneten Fahrstrasse)
    Gleis gleis1 = this.gleisRepository.findByBereichAndName(BEREICH, "1").get();
    gleis1.setBesetzt(true);

    // Trigger besetzen und dies melden
    trigger.setBesetzt(true);
    this.eventFirer.fire(trigger, Changed.Literal.INSTANCE);

    // Nun muss 2. Fahrstrasse reserviert sein, die anderen nicht
    assertEquals(FahrstrassenReservierungsTyp.UNRESERVIERT, fahrstrasse1.getReservierungsTyp());
    assertEquals(FahrstrassenReservierungsTyp.ZUGFAHRT, fahrstrasse2.getReservierungsTyp());
    assertEquals(FahrstrassenReservierungsTyp.UNRESERVIERT, fahrstrasse3.getReservierungsTyp());

    // Grundzustand wieder herstellen
    trigger.setBesetzt(false);
    gleis1.setBesetzt(false);
    fahrstrasse2.freigeben(null);
  }

  @Test
  @Transactional
  public void test_04_autoreserviere_11besetzt() throws Exception {

    // Beteiligte Objekte besorgen und Grundzustand sicherstellen
    Gleis trigger = this.gleisRepository.findByBereichAndName(BEREICH, TRIGGER_NAME).orElse(null);
    assertNotNull(trigger);
    assertFalse(trigger.isBesetzt());

    Fahrstrasse fahrstrasse1 = this.parcours.getFahrstrasse(BEREICH, FS_1_NAME);
    assertNotNull(fahrstrasse1);
    assertEquals(FahrstrassenReservierungsTyp.UNRESERVIERT, fahrstrasse1.getReservierungsTyp());

    Fahrstrasse fahrstrasse2 = this.parcours.getFahrstrasse(BEREICH, FS_2_NAME);
    assertNotNull(fahrstrasse2);
    assertEquals(FahrstrassenReservierungsTyp.UNRESERVIERT, fahrstrasse2.getReservierungsTyp());

    Fahrstrasse fahrstrasse3 = this.parcours.getFahrstrasse(BEREICH, FS_3_NAME);
    assertNotNull(fahrstrasse3);
    assertEquals(FahrstrassenReservierungsTyp.UNRESERVIERT, fahrstrasse3.getReservierungsTyp());

    // Gleis 11 besetzen (ist Teil der ersten beiden zugeordneten Fahrstrasse)
    Gleis gleis11 = this.gleisRepository.findByBereichAndName(BEREICH, "11").get();
    gleis11.setBesetzt(true);

    // Trigger besetzen und dies melden
    trigger.setBesetzt(true);
    this.eventFirer.fire(trigger, Changed.Literal.INSTANCE);

    // Nun muss 3. Fahrstrasse reserviert sein, die anderen nicht
    assertEquals(FahrstrassenReservierungsTyp.UNRESERVIERT, fahrstrasse1.getReservierungsTyp());
    assertEquals(FahrstrassenReservierungsTyp.UNRESERVIERT, fahrstrasse2.getReservierungsTyp());
    assertEquals(FahrstrassenReservierungsTyp.ZUGFAHRT, fahrstrasse3.getReservierungsTyp());

    // Grundzustand wieder herstellen
    trigger.setBesetzt(false);
    gleis11.setBesetzt(false);
    fahrstrasse3.freigeben(null);
  }

}
