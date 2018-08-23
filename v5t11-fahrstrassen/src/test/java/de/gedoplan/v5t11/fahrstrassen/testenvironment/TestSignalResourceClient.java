package de.gedoplan.v5t11.fahrstrassen.testenvironment;

import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Signal;
import de.gedoplan.v5t11.fahrstrassen.gateway.SignalResourceClient;
import de.gedoplan.v5t11.util.domain.SignalStellung;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Specializes;

@Dependent
@Specializes
public class TestSignalResourceClient extends SignalResourceClient {

  private static final Signal[] TEST_SIGNALE = {
      createTestSignal("Demoanlage", "F", "HauptsignalRtGnGe", SignalStellung.HALT, SignalStellung.FAHRT, SignalStellung.LANGSAMFAHRT),
      createTestSignal("Demoanlage", "N1", "HauptsignalRtGe", SignalStellung.HALT, SignalStellung.LANGSAMFAHRT),
      createTestSignal("Demoanlage", "N2", "HauptsignalRtGn", SignalStellung.HALT, SignalStellung.FAHRT)
  };

  @Override
  public Set<Signal> getSignale() {
    return Stream.of(TEST_SIGNALE).collect(Collectors.toSet());
  }

  private static Signal createTestSignal(String bereich, String name, String typ, SignalStellung... stellung) {
    Signal signal = new Signal(bereich, name);
    signal.setTyp(typ);
    signal.setStellung(stellung[0]);
    signal.setErlaubteStellungen(Stream.of(stellung).collect(Collectors.toSet()));
    return signal;
  }

}
