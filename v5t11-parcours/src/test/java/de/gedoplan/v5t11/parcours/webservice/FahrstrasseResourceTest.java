package de.gedoplan.v5t11.parcours.webservice;

import de.gedoplan.v5t11.parcours.entity.Parcours;
import de.gedoplan.v5t11.parcours.entity.fahrstrasse.Fahrstrasse;
import de.gedoplan.v5t11.parcours.testenvironment.profile.V5T11Test;
import de.gedoplan.v5t11.util.domain.attribute.BereichselementId;

import java.util.List;

import jakarta.inject.Inject;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@QuarkusTest
@TestProfile(V5T11Test.class)
@TestMethodOrder(MethodOrderer.MethodName.class)
public class FahrstrasseResourceTest {

  private static final String BEREICH = "show";
  private static final String FS_NAME = "11-W1-1-W3-S";
  private static final String START_NAME = "11";
  private static final String ENDE_NAME = "S";

  @Inject
  FahrstrasseResource fahrstrasseResource;

  @Inject
  Parcours parcours;

  @Inject
  Logger log;

  @Test
  public void test_01_getFahrstrasse() throws Exception {

    this.log.debug("----- test_01_getFahrstrasse -----");

    Fahrstrasse fahrstrasse = this.fahrstrasseResource.getFahrstrasse(new BereichselementId(BEREICH, FS_NAME));
    this.log.debug(fahrstrasse);
  }

  @Test
  public void test_02_getFahrstrassenIds() throws Exception {

    this.log.debug("----- test_02_getFahrstrassenIds -----");

    List<String> fahrstrassenIds = this.fahrstrasseResource.getFahrstrassenIds(null, null, null, null, null);
    assertThat("Anzahl Fahrstrassen", fahrstrassenIds.size(), is(194));

    fahrstrassenIds.forEach(this.log::debug);
  }

  @Test
  public void test_03_getFahrstrassenIdsFromStart() throws Exception {

    this.log.debug("----- test_03_getFahrstrassenIdsFromStart -----");

    List<String> fahrstrassenIds = this.fahrstrasseResource.getFahrstrassenIds(BEREICH, START_NAME, null, null, null);
    assertThat("Anzahl Fahrstrassen", fahrstrassenIds.size(), is(6));

    fahrstrassenIds.forEach(this.log::debug);
  }

  @Test
  public void test_04_getFahrstrassenIdsFromStartToEnde() throws Exception {

    this.log.debug("----- test_04_getFahrstrassenIdsFromStartToEnde -----");

    List<String> fahrstrassenIds = this.fahrstrasseResource.getFahrstrassenIds(BEREICH, START_NAME, null, ENDE_NAME, null);
    assertThat("Anzahl Fahrstrassen", fahrstrassenIds.size(), is(2));

    fahrstrassenIds.forEach(this.log::debug);
  }

}
