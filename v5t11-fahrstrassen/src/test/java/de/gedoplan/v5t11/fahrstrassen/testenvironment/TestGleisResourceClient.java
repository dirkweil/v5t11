package de.gedoplan.v5t11.fahrstrassen.testenvironment;

import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Gleisabschnitt;
import de.gedoplan.v5t11.fahrstrassen.gateway.GleisResourceClient;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Specializes;

@Dependent
@Specializes
public class TestGleisResourceClient extends GleisResourceClient {

  private static final Gleisabschnitt[] TEST_GLEISABSCHNITTE = {
      createTestGleisabschnitt("Demoanlage", "1", false),
      createTestGleisabschnitt("Demoanlage", "2", true),
      createTestGleisabschnitt("Demoanlage", "11", false),
      createTestGleisabschnitt("Demoanlage", "12", false),
      createTestGleisabschnitt("Demoanlage", "S", false),
      createTestGleisabschnitt("Demoanlage", "W1", false),
      createTestGleisabschnitt("Demoanlage", "W2", false),
      createTestGleisabschnitt("Demoanlage", "W3", false)
  };

  @Override
  public Set<Gleisabschnitt> getGleisabschnitte() {
    return Stream.of(TEST_GLEISABSCHNITTE).collect(Collectors.toSet());
  }

  private static Gleisabschnitt createTestGleisabschnitt(String bereich, String name, boolean besetzt) {
    Gleisabschnitt gleisabschnitt = new Gleisabschnitt(bereich, name);
    gleisabschnitt.setBesetzt(besetzt);
    return gleisabschnitt;
  }

}
