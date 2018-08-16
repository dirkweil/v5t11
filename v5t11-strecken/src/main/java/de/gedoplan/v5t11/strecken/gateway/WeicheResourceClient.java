package de.gedoplan.v5t11.strecken.gateway;

import de.gedoplan.v5t11.strecken.entity.fahrweg.Weiche;

import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

@RequestScoped
public class WeicheResourceClient extends StatusResourceClientBase {

  private WebTarget weicheTarget;

  @PostConstruct
  void createWeicheTarget() {
    this.weicheTarget = this.StatusBaseTarget.path("weiche");
  }

  public Weiche getWeiche(String bereich, String name) {
    return this.weicheTarget
        .path(bereich)
        .path(name)
        .request()
        .accept(MediaType.APPLICATION_JSON)
        .get(Weiche.class);
  }

  public Set<Weiche> getWeichen() {
    return this.weicheTarget
        .request()
        .accept(MediaType.APPLICATION_JSON)
        .get(new GenericType<Set<Weiche>>() {
        });
  }
}
