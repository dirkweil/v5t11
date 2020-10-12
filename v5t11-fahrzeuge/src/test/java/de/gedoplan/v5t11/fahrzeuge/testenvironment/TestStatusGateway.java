package de.gedoplan.v5t11.fahrzeuge.testenvironment;

import de.gedoplan.v5t11.fahrzeuge.entity.baustein.Zentrale;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrweg.Gleisabschnitt;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrweg.Weiche;
import de.gedoplan.v5t11.fahrzeuge.gateway.StatusGateway;
import de.gedoplan.v5t11.util.domain.attribute.WeichenStellung;
import de.gedoplan.v5t11.util.domain.entity.fahrweg.AbstractGleisabschnitt;
import de.gedoplan.v5t11.util.domain.entity.fahrweg.geraet.AbstractWeiche;

import java.lang.reflect.Field;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Priority;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Alternative;

import org.eclipse.microprofile.rest.client.inject.RestClient;

@Dependent
@RestClient
@Alternative
@Priority(1)
public class TestStatusGateway implements StatusGateway {

  private static final Gleisabschnitt[] TEST_GLEISABSCHNITTE = {
      createTestGleisabschnitt("show", "1", false),
      createTestGleisabschnitt("show", "2", true),
      createTestGleisabschnitt("show", "11", false),
      createTestGleisabschnitt("show", "12", false),
      createTestGleisabschnitt("show", "S", false),
      createTestGleisabschnitt("show", "W1", false),
      createTestGleisabschnitt("show", "W2", false),
      createTestGleisabschnitt("show", "W3", false)
  };

  @Override
  public Set<Gleisabschnitt> getGleisabschnitte() {
    return Stream.of(TEST_GLEISABSCHNITTE).collect(Collectors.toSet());
  }

  private static Gleisabschnitt createTestGleisabschnitt(String bereich, String name, boolean besetzt) {
    try {
      Gleisabschnitt gleisabschnitt = new Gleisabschnitt(bereich, name);

      // Gleisabschnitt.besetzt ist private; daher per Reflection setzen
      Field besetztField = AbstractGleisabschnitt.class.getDeclaredField("besetzt");
      besetztField.setAccessible(true);
      besetztField.set(gleisabschnitt, besetzt);
      return gleisabschnitt;
    } catch (Exception e) {
      throw new RuntimeException("Cannot create test data", e);
    }
  }

  private static final Weiche[] TEST_WEICHEN = {
      createTestWeiche("show", "1", WeichenStellung.GERADE),
      createTestWeiche("show", "2", WeichenStellung.ABZWEIGEND),
      createTestWeiche("show", "3", WeichenStellung.GERADE)
  };

  @Override
  public Set<Weiche> getWeichen() {
    return Stream.of(TEST_WEICHEN).collect(Collectors.toSet());
  }

  private static Weiche createTestWeiche(String bereich, String name, WeichenStellung stellung) {
    try {
      Weiche weiche = new Weiche(bereich, name);

      // Weiche.stellung ist private; daher per Reflection setzen
      Field besetztField = AbstractWeiche.class.getDeclaredField("stellung");
      besetztField.setAccessible(true);
      besetztField.set(weiche, stellung);
      return weiche;
    } catch (Exception e) {
      throw new RuntimeException("Cannot create test data", e);
    }
  }

  @Override
  public Zentrale getZentrale() {
    throw new UnsupportedOperationException();
  }

}
