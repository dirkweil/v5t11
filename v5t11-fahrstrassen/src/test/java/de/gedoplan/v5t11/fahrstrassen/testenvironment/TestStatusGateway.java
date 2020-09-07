package de.gedoplan.v5t11.fahrstrassen.testenvironment;

import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Gleisabschnitt;
import de.gedoplan.v5t11.fahrstrassen.gateway.StatusGateway;
import de.gedoplan.v5t11.util.domain.attribute.SignalStellung;
import de.gedoplan.v5t11.util.domain.attribute.WeichenStellung;
import de.gedoplan.v5t11.util.domain.entity.fahrweg.AbstractGleisabschnitt;

import java.lang.reflect.Field;
import java.util.Collection;
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

  @Override
  public void signalStellen(String bereich, String name, SignalStellung stellung) {
  }

  @Override
  public void signalStellen(String bereich, String name, Collection<SignalStellung> stellungen) {
  }

  @Override
  public void weicheStellen(String bereich, String name, WeichenStellung stellung) {
  }

}
