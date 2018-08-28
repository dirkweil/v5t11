package de.gedoplan.v5t11.fahrstrassen.testenvironment;

import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Weiche;
import de.gedoplan.v5t11.fahrstrassen.gateway.WeicheResourceClient;
import de.gedoplan.v5t11.util.domain.WeichenStellung;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Specializes;

@Dependent
@Specializes
public class TestWeicheResourceClient extends WeicheResourceClient {

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
    Weiche signal = new Weiche(bereich, name);
    signal.setStellung(stellung);
    return signal;
  }
}
