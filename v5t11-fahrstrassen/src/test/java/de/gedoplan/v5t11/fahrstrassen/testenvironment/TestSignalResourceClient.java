package de.gedoplan.v5t11.fahrstrassen.testenvironment;

import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Signal;
import de.gedoplan.v5t11.fahrstrassen.gateway.SignalResourceClient;
import de.gedoplan.v5t11.util.domain.attribute.SignalStellung;
import de.gedoplan.v5t11.util.domain.entity.fahrweg.geraet.AbstractSignal;

import java.lang.reflect.Field;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Specializes;

@Dependent
@Specializes
public class TestSignalResourceClient extends SignalResourceClient {

  private static final Signal[] TEST_SIGNALE = {
      createTestSignal("show", "F", SignalStellung.HALT),
      createTestSignal("show", "N1", SignalStellung.HALT),
      createTestSignal("show", "N2", SignalStellung.HALT)
  };

  @Override
  public Set<Signal> getSignale() {
    return Stream.of(TEST_SIGNALE).collect(Collectors.toSet());
  }

  private static Signal createTestSignal(String bereich, String name, SignalStellung stellung) {
    try {
      Signal signal = new Signal(bereich, name);

      // Signal.stellung ist private; daher per Reflection setzen
      Field besetztField = AbstractSignal.class.getDeclaredField("stellung");
      besetztField.setAccessible(true);
      besetztField.set(signal, stellung);
      return signal;
    } catch (Exception e) {
      throw new RuntimeException("Cannot create test data", e);
    }
  }

}
