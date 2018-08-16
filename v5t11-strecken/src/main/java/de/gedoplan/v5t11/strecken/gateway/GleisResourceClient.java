package de.gedoplan.v5t11.strecken.gateway;

import de.gedoplan.v5t11.strecken.entity.fahrweg.Gleisabschnitt;

import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

@RequestScoped
public class GleisResourceClient extends StatusResourceClientBase {

  private WebTarget gleisTarget;

  @PostConstruct
  void createGleisabschnittTarget() {
    this.gleisTarget = this.StatusBaseTarget.path("gleis");
  }

  public Gleisabschnitt getGleisabschnitt(String bereich, String name) {
    return this.gleisTarget
        .path(bereich)
        .path(name)
        .request()
        .accept(MediaType.APPLICATION_JSON)
        .get(Gleisabschnitt.class);
  }

  public Set<Gleisabschnitt> getGleisabschnitte() {
    return this.gleisTarget
        .request()
        .accept(MediaType.APPLICATION_JSON)
        .get(new GenericType<Set<Gleisabschnitt>>() {
        });
  }
}
