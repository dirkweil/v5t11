package de.gedoplan.v5t11.fahrstrassen.gateway;

import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Weiche;
import de.gedoplan.v5t11.util.domain.attribute.WeichenStellung;

import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

@ApplicationScoped
public class WeicheResourceClient extends StatusResourceClientBase {

  private WebTarget weicheTarget;

  @PostConstruct
  void createWeicheTarget() {
    this.weicheTarget = this.StatusBaseTarget.path("weiche");
  }

  public Set<Weiche> getWeichen() {
    return this.weicheTarget
        .request()
        .accept(MediaType.APPLICATION_JSON)
        .get(new GenericType<Set<Weiche>>() {});
  }

  public void weicheStellen(Weiche weiche, WeichenStellung stellung) {
    this.weicheTarget
        .path(weiche.getBereich())
        .path(weiche.getName())
        .request()
        .put(Entity.text(stellung));
  }

}
