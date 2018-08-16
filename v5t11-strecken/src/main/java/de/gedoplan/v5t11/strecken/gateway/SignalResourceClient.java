package de.gedoplan.v5t11.strecken.gateway;

import de.gedoplan.v5t11.strecken.entity.fahrweg.Signal;

import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

@RequestScoped
public class SignalResourceClient extends StatusResourceClientBase {

  private WebTarget signalTarget;

  @PostConstruct
  void createSignalTarget() {
    this.signalTarget = this.StatusBaseTarget.path("signal");
  }

  public Signal getSignal(String bereich, String name) {
    return this.signalTarget
        .path(bereich)
        .path(name)
        .request()
        .accept(MediaType.APPLICATION_JSON)
        .get(Signal.class);
  }

  public Set<Signal> getSignale() {
    return this.signalTarget
        .request()
        .accept(MediaType.APPLICATION_JSON)
        .get(new GenericType<Set<Signal>>() {
        });
  }
}
