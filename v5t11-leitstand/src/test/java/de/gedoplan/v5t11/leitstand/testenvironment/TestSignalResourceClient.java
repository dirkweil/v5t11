package de.gedoplan.v5t11.leitstand.testenvironment;

import de.gedoplan.v5t11.leitstand.entity.fahrweg.Signal;
import de.gedoplan.v5t11.leitstand.gateway.SignalResourceClient;
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
      createTestSignal("show", "F", "HauptsignalRtGnGe", SignalStellung.HALT, SignalStellung.FAHRT, SignalStellung.LANGSAMFAHRT),
      createTestSignal("show", "N1", "HauptsignalRtGe", SignalStellung.HALT, SignalStellung.LANGSAMFAHRT),
      createTestSignal("show", "N2", "HauptsignalRtGn", SignalStellung.HALT, SignalStellung.FAHRT)
  };

  @Override
  public Set<Signal> getSignale() {
    return Stream.of(TEST_SIGNALE).collect(Collectors.toSet());
  }

  private static Signal createTestSignal(String bereich, String name, String typ, SignalStellung... stellung) {
    try {
      Signal signal = new Signal(bereich, name);
      signal.setTyp(typ);
      signal.setErlaubteStellungen(Stream.of(stellung).collect(Collectors.toSet()));

      // Signal.stellung ist private; daher per Reflection setzen
      Field besetztField = AbstractSignal.class.getDeclaredField("stellung");
      besetztField.setAccessible(true);
      besetztField.set(signal, stellung[0]);
      return signal;
    } catch (Exception e) {
      throw new RuntimeException("Cannot create test data", e);
    }
  }

}
