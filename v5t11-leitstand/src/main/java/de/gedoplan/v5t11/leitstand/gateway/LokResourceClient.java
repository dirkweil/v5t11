package de.gedoplan.v5t11.leitstand.gateway;

import de.gedoplan.v5t11.leitstand.entity.lok.Lok;
import de.gedoplan.v5t11.leitstand.service.ConfigService;
import de.gedoplan.v5t11.util.webservice.ResourceClientBase;

import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@ApplicationScoped
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LokResourceClient extends ResourceClientBase {

  @Inject
  public LokResourceClient(ConfigService configService) {
    super(configService.getStatusRestUrl(), "lok");
  }

  public Set<Lok> getLoks() {
    return this.baseTarget
        .request()
        .accept(MediaType.APPLICATION_JSON)
        .get(new GenericType<Set<Lok>>() {});
  }

}
