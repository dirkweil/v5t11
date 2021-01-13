package de.gedoplan.v5t11.fahrstrassen.testenvironment;

import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Gleis;
import de.gedoplan.v5t11.fahrstrassen.gateway.StatusGateway;
import de.gedoplan.v5t11.util.domain.attribute.SignalStellung;
import de.gedoplan.v5t11.util.domain.attribute.WeichenStellung;
import de.gedoplan.v5t11.util.domain.entity.fahrweg.AbstractGleis;

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

  private static final Gleis[] TEST_GLEISE = {
      createTestGleis("show", "1", false),
      createTestGleis("show", "2", true),
      createTestGleis("show", "11", false),
      createTestGleis("show", "12", false),
      createTestGleis("show", "S", false),
      createTestGleis("show", "W1", false),
      createTestGleis("show", "W2", false),
      createTestGleis("show", "W3", false)
  };

  @Override
  public Set<Gleis> getGleise() {
    return Stream.of(TEST_GLEISE).collect(Collectors.toSet());
  }

  private static Gleis createTestGleis(String bereich, String name, boolean besetzt) {
    try {
      Gleis gleis = new Gleis(bereich, name);

      // Gleis.besetzt ist private; daher per Reflection setzen
      Field besetztField = AbstractGleis.class.getDeclaredField("besetzt");
      besetztField.setAccessible(true);
      besetztField.set(gleis, besetzt);
      return gleis;
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
