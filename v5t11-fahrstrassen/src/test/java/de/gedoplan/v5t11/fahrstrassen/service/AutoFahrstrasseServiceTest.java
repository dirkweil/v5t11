package de.gedoplan.v5t11.fahrstrassen.service;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.junit.jupiter.api.Assertions.*;

import de.gedoplan.v5t11.fahrstrassen.entity.Parcours;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse.Fahrstrasse;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Gleisabschnitt;
import de.gedoplan.v5t11.fahrstrassen.persistence.GleisabschnittRepository;
import de.gedoplan.v5t11.util.cdi.Changed;
import de.gedoplan.v5t11.util.cdi.EventFirer;
import de.gedoplan.v5t11.util.domain.attribute.FahrstrassenReservierungsTyp;
import de.gedoplan.v5t11.util.test.V5t11TestConfigDirExtension;

import java.util.List;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.jboss.logging.Logger;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;

import io.quarkus.test.junit.QuarkusTestExtension;

@ExtendWith({ V5t11TestConfigDirExtension.class, QuarkusTestExtension.class })
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
public class AutoFahrstrasseServiceTest {

  private static final String BEREICH = "show";

  private static final String TRIGGER_NAME = "S";

  private static final String FS_1_NAME = "S-W3-1-W1-11";
  private static final String FS_2_NAME = "S-W3-2-W2-W1-11";
  private static final String FS_3_NAME = "S-W3-2-W2-12";

  @Inject
  GleisabschnittRepository gleisabschnittRepository;

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

    Gleisabschnitt trigger = this.gleisabschnittRepository.findByBereichAndName(BEREICH, TRIGGER_NAME);
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
    Gleisabschnitt trigger = this.gleisabschnittRepository.findByBereichAndName(BEREICH, TRIGGER_NAME);
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
    Gleisabschnitt trigger = this.gleisabschnittRepository.findByBereichAndName(BEREICH, TRIGGER_NAME);
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
    Gleisabschnitt gleis1 = this.gleisabschnittRepository.findByBereichAndName(BEREICH, "1");
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
    Gleisabschnitt trigger = this.gleisabschnittRepository.findByBereichAndName(BEREICH, TRIGGER_NAME);
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
    Gleisabschnitt gleis11 = this.gleisabschnittRepository.findByBereichAndName(BEREICH, "11");
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
