package de.gedoplan.v5t11.fahrstrassen.gateway;

import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Gleisabschnitt;

import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

@ApplicationScoped
public class GleisResourceClient extends StatusResourceClientBase {

  private WebTarget gleisTarget;

  @PostConstruct
  void createGleisabschnittTarget() {
    this.gleisTarget = this.StatusBaseTarget.path("gleis");
  }

  public Set<Gleisabschnitt> getGleisabschnitte() {
    return this.gleisTarget
        .request()
        .accept(MediaType.APPLICATION_JSON)
        .get(new GenericType<Set<Gleisabschnitt>>() {
        });
  }
}
