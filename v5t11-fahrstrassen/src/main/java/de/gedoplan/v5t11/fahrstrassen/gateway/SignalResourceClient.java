package de.gedoplan.v5t11.fahrstrassen.gateway;

import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Signal;
import de.gedoplan.v5t11.util.domain.SignalStellung;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

@ApplicationScoped
public class SignalResourceClient extends StatusResourceClientBase {

  private WebTarget signalTarget;

  @PostConstruct
  void createSignalTarget() {
    this.signalTarget = this.StatusBaseTarget.path("signal");
  }

  public Set<Signal> getSignale() {
    return this.signalTarget
        .request()
        .accept(MediaType.APPLICATION_JSON)
        .get(new GenericType<Set<Signal>>() {});
  }

  public void signalStellen(Signal signal, List<SignalStellung> stellungen) {
    String stellungenAsString = stellungen.stream().map(Object::toString).collect(Collectors.joining(","));
    this.signalTarget
        .path(signal.getBereich())
        .path(signal.getName())
        .request()
        .put(Entity.text(stellungenAsString));

  }
}
