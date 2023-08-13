package de.gedoplan.v5t11.leitstand.testenvironment;

import de.gedoplan.v5t11.leitstand.entity.baustein.Zentrale;
import de.gedoplan.v5t11.leitstand.entity.fahrweg.Gleis;
import de.gedoplan.v5t11.leitstand.entity.fahrweg.Signal;
import de.gedoplan.v5t11.leitstand.entity.fahrweg.Weiche;
import de.gedoplan.v5t11.leitstand.gateway.StatusGateway;
import de.gedoplan.v5t11.util.domain.attribute.BereichselementId;
import de.gedoplan.v5t11.util.domain.attribute.SignalStellung;
import de.gedoplan.v5t11.util.domain.attribute.SignalTyp;
import de.gedoplan.v5t11.util.domain.attribute.WeichenStellung;
import de.gedoplan.v5t11.util.domain.entity.fahrweg.AbstractGleis;
import de.gedoplan.v5t11.util.domain.entity.fahrweg.geraet.AbstractWeiche;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Alternative;

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
      Gleis gleis = new Gleis(new BereichselementId(bereich, name));

      // Gleis.besetzt ist private; daher per Reflection setzen
      Field besetztField = AbstractGleis.class.getDeclaredField("besetzt");
      besetztField.setAccessible(true);
      besetztField.set(gleis, besetzt);
      return gleis;
    } catch (Exception e) {
      throw new RuntimeException("Cannot create test data", e);
    }
  }

  private static final Signal[] TEST_SIGNALE = {
    createTestSignal("show", "F", SignalTyp.HAUPTSIGNAL_RT_GE_GN),
    createTestSignal("show", "N1", SignalTyp.HAUPTSIGNAL_RT_GE),
    createTestSignal("show", "N2", SignalTyp.HAUPTSIGNAL_RT_GN)
  };

  @Override
  public Set<Signal> getSignale() {
    return Stream.of(TEST_SIGNALE).collect(Collectors.toSet());
  }

  private static Signal createTestSignal(String bereich, String name, SignalTyp typ) {
    Signal signal = new Signal(new BereichselementId(bereich, name));
    signal.setTyp(typ);
    signal.setStellung(SignalStellung.HALT);
    return signal;
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
      Weiche weiche = new Weiche(new BereichselementId(bereich, name));

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
