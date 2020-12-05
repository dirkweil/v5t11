package de.gedoplan.v5t11.leitstand.testenvironment;

import de.gedoplan.v5t11.leitstand.entity.baustein.Zentrale;
import de.gedoplan.v5t11.leitstand.entity.fahrweg.OldGleisabschnitt;
import de.gedoplan.v5t11.leitstand.entity.fahrweg.OldSignal;
import de.gedoplan.v5t11.leitstand.entity.fahrweg.OldWeiche;
import de.gedoplan.v5t11.leitstand.gateway.StatusGateway;
import de.gedoplan.v5t11.util.domain.attribute.SignalStellung;
import de.gedoplan.v5t11.util.domain.attribute.WeichenStellung;
import de.gedoplan.v5t11.util.domain.entity.fahrweg.AbstractGleisabschnitt;
import de.gedoplan.v5t11.util.domain.entity.fahrweg.geraet.AbstractSignal;
import de.gedoplan.v5t11.util.domain.entity.fahrweg.geraet.AbstractWeiche;

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

  private static final OldGleisabschnitt[] TEST_GLEISABSCHNITTE = {
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
  public Set<OldGleisabschnitt> getGleisabschnitte() {
    return Stream.of(TEST_GLEISABSCHNITTE).collect(Collectors.toSet());
  }

  private static OldGleisabschnitt createTestGleisabschnitt(String bereich, String name, boolean besetzt) {
    try {
      OldGleisabschnitt gleisabschnitt = new OldGleisabschnitt(bereich, name);

      // Gleisabschnitt.besetzt ist private; daher per Reflection setzen
      Field besetztField = AbstractGleisabschnitt.class.getDeclaredField("besetzt");
      besetztField.setAccessible(true);
      besetztField.set(gleisabschnitt, besetzt);
      return gleisabschnitt;
    } catch (Exception e) {
      throw new RuntimeException("Cannot create test data", e);
    }
  }

  private static final OldSignal[] TEST_SIGNALE = {
      createTestSignal("show", "F", "HauptsignalRtGnGe", SignalStellung.HALT, SignalStellung.FAHRT, SignalStellung.LANGSAMFAHRT),
      createTestSignal("show", "N1", "HauptsignalRtGe", SignalStellung.HALT, SignalStellung.LANGSAMFAHRT),
      createTestSignal("show", "N2", "HauptsignalRtGn", SignalStellung.HALT, SignalStellung.FAHRT)
  };

  @Override
  public Set<OldSignal> getSignale() {
    return Stream.of(TEST_SIGNALE).collect(Collectors.toSet());
  }

  private static OldSignal createTestSignal(String bereich, String name, String typ, SignalStellung... stellung) {
    try {
      OldSignal signal = new OldSignal(bereich, name);
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

  @Override
  public void signalStellen(String bereich, String name, SignalStellung stellung) {
  }

  @Override
  public void signalStellen(String bereich, String name, Collection<SignalStellung> stellungen) {
  }

  @Override
  public void weicheStellen(String bereich, String name, WeichenStellung stellung) {
  }

  private static final OldWeiche[] TEST_WEICHEN = {
      createTestWeiche("show", "1", WeichenStellung.GERADE),
      createTestWeiche("show", "2", WeichenStellung.ABZWEIGEND),
      createTestWeiche("show", "3", WeichenStellung.GERADE)
  };

  @Override
  public Set<OldWeiche> getWeichen() {
    return Stream.of(TEST_WEICHEN).collect(Collectors.toSet());
  }

  private static OldWeiche createTestWeiche(String bereich, String name, WeichenStellung stellung) {
    try {
      OldWeiche weiche = new OldWeiche(bereich, name);

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

  @Override
  public void putGleisspannung(String on) {
    throw new UnsupportedOperationException();
  }

}
