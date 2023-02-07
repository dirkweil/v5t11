package de.gedoplan.v5t11.fahrstrassen.webservice;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import de.gedoplan.v5t11.fahrstrassen.entity.Parcours;
import de.gedoplan.v5t11.util.domain.attribute.BereichselementId;
import de.gedoplan.v5t11.util.test.V5t11TestConfigDirExtension;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.jboss.logging.Logger;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;

import io.quarkus.test.junit.QuarkusTestExtension;

@ExtendWith({ V5t11TestConfigDirExtension.class, QuarkusTestExtension.class })
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

    Response response = this.fahrstrasseResource.getFahrstrasse(new BereichselementId(BEREICH, FS_NAME));
    assertThat("Response-Code", response.getStatus(), is(Status.OK.getStatusCode()));

    this.log.debug(response.getEntity());
  }

  @Test
  public void test_02_getFahrstrassenIds() throws Exception {

    this.log.debug("----- test_02_getFahrstrassenIds -----");

    List<String> fahrstrassenIds = this.fahrstrasseResource.getFahrstrassenIds(null, null, null, null, null);
    assertThat("Anzahl Fahrstrassen", fahrstrassenIds.size(), is(52));

    fahrstrassenIds.forEach(this.log::debug);
  }

  @Test
  public void test_03_getFahrstrassenIdsFromStart() throws Exception {

    this.log.debug("----- test_03_getFahrstrassenIdsFromStart -----");

    List<String> fahrstrassenIds = this.fahrstrasseResource.getFahrstrassenIds(BEREICH, START_NAME, null, null, null);
    assertThat("Anzahl Fahrstrassen", fahrstrassenIds.size(), is(4));

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
