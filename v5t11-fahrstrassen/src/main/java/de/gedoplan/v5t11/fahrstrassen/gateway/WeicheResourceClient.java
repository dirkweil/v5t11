package de.gedoplan.v5t11.fahrstrassen.gateway;

import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Weiche;
import de.gedoplan.v5t11.fahrstrassen.service.ConfigService;
import de.gedoplan.v5t11.util.domain.attribute.WeichenStellung;
import de.gedoplan.v5t11.util.webservice.ResourceClientBase;

import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@ApplicationScoped
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WeicheResourceClient extends ResourceClientBase {

  @Inject
  public WeicheResourceClient(ConfigService configService) {
    super(configService.getStatusRestUrl(), "weiche");
  }

  public Set<Weiche> getWeichen() {
    return this.baseTarget
        .request()
        .accept(MediaType.APPLICATION_JSON)
        .get(new GenericType<Set<Weiche>>() {});
  }

  public void weicheStellen(Weiche weiche, WeichenStellung stellung) {
    this.baseTarget
        .path(weiche.getBereich())
        .path(weiche.getName())
        .request()
        .put(Entity.text(stellung));
  }

}
