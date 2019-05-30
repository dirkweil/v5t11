package de.gedoplan.v5t11.leitstand.gateway;

import de.gedoplan.v5t11.leitstand.entity.baustein.Zentrale;
import de.gedoplan.v5t11.leitstand.service.ConfigService;
import de.gedoplan.v5t11.util.webservice.ResourceClientBase;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@ApplicationScoped
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ZentraleResourceClient extends ResourceClientBase {

  @Inject
  public ZentraleResourceClient(ConfigService configService) {
    super(configService.getStatusRestUrl(), "zentrale");
  }

  public Zentrale getZentrale() {
    return this.baseTarget
        .request()
        .accept(MediaType.APPLICATION_JSON)
        .get(Zentrale.class);
  }

  public void putGleisspannung(boolean aktiv) {
    this.baseTarget
        .request()
        .put(Entity.text(aktiv));
  }

}
