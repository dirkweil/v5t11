package de.gedoplan.v5t11.status.entity;

import de.gedoplan.v5t11.status.CdiTestBase;
import de.gedoplan.v5t11.status.entity.lok.Lok;
import de.gedoplan.v5t11.status.entity.lok.Lok.FunktionConfig;
import de.gedoplan.v5t11.status.testenvironment.service.TestLokRepository;
import de.gedoplan.v5t11.util.jsonb.JsonbWithIncludeVisibility;

import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.junit.Ignore;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

public class LokTest extends CdiTestBase {

  @Inject
  Steuerung steuerung;

  @Test
  public void test_01_toShortJson() throws Exception {

    final String lokId = TestLokRepository.testLok103.getId();

    Lok lok = this.steuerung.getLok(lokId);

    // System.out.println("##### " + lok);
    // Method method = lok.getClass().getMethod("getId", null);
    // System.out.println(" ##### annotations: " + Stream.of(method.getAnnotations()).map(a -> a.toString()).collect(Collectors.joining(",")));

    String json = JsonbWithIncludeVisibility.SHORT.toJson(lok);

    this.log.debug("JSON string: " + json);

    JSONAssert.assertEquals(""
        + "{\"aktiv\":" + lok.isAktiv()
        + ",\"fahrstufe\":" + lok.getFahrstufe()
        + ",\"funktionStatus\":" + lok.getFunktionStatus()
        + ",\"id\":\"" + lokId + "\""
        + ",\"licht\":" + lok.isLicht()
        + ",\"rueckwaerts\":" + lok.isRueckwaerts()
        + "}",
        json,
        true);
  }

  @Test
  @Ignore
  public void test_02_toFullJson() throws Exception {

    final String lokId = TestLokRepository.testLok103.getId();

    Lok lok = this.steuerung.getLok(lokId);

    String json = JsonbWithIncludeVisibility.FULL.toJson(lok);

    this.log.debug("JSON string: " + json);

    JSONAssert.assertEquals(""
        + "{\"aktiv\":" + lok.isAktiv()
        + ",\"fahrstufe\":" + lok.getFahrstufe()
        + ",\"funktionStatus\":" + lok.getFunktionStatus()
        + ",\"id\":\"" + lokId + "\""
        + ",\"licht\":" + lok.isLicht()
        + ",\"maxFahrstufe\":" + lok.getMaxFahrstufe()
        + ",\"rueckwaerts\":" + lok.isRueckwaerts()
        // + ",\"funktionConfigs\":{\"2\":{\"beschreibung\":\"Motorgeräusch\",\"impuls\":false},\"7\":{\"beschreibung\":\"Pfiff\",\"impuls\":true}}"
        + ",\"funktionConfigs\":" + toJson(lok.getFunktionConfigs())
        + "}",
        json,
        true);
  }

  private String toJson(Map<@Min(1) @Max(16) Integer, @NotNull FunktionConfig> funktionConfigs) {

    return funktionConfigs.entrySet().stream()
        .map(entry -> "\"" + entry.getKey() + "\":{\"beschreibung\":\"" + entry.getValue().getBeschreibung() + "\",\"impuls\":" + entry.getValue().isImpuls() + "}")
        .collect(Collectors.joining(",", "{", "}"));

  }
}
