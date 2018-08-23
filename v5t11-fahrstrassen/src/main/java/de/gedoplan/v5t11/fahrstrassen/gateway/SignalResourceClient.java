package de.gedoplan.v5t11.fahrstrassen.gateway;

import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Signal;

import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
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
        .get(new GenericType<Set<Signal>>() {
        });
  }
}
