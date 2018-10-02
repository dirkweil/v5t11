package de.gedoplan.v5t11.leitstand.gateway;

import de.gedoplan.v5t11.leitstand.entity.baustein.LokController;
import de.gedoplan.v5t11.leitstand.service.ConfigService;
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
public class LokControllerResourceClient extends ResourceClientBase {

  @Inject
  public LokControllerResourceClient(ConfigService configService) {
    super(configService.getStatusRestUrl(), "lokcontroller");
  }

  public Set<LokController> getLokController() {
    return this.baseTarget
        .request()
        .accept(MediaType.APPLICATION_JSON)
        .get(new GenericType<Set<LokController>>() {});
  }

  public void setLok(String id, String lokId) {
    this.baseTarget
        .path(id)
        .request()
        .put(Entity.text(lokId != null ? lokId : "null"));
  }

}
