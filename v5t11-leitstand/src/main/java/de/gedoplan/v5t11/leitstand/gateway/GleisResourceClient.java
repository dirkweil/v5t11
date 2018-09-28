package de.gedoplan.v5t11.leitstand.gateway;

import de.gedoplan.v5t11.leitstand.entity.fahrweg.Gleisabschnitt;
import de.gedoplan.v5t11.leitstand.service.ConfigService;
import de.gedoplan.v5t11.util.webservice.ResourceClientBase;

import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

//TODO Dies ist eine exakte Kopie aus v5t11-fahrstrassen; geht das eleganter?

@ApplicationScoped
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GleisResourceClient extends ResourceClientBase {

  @Inject
  public GleisResourceClient(ConfigService configService) {
    super(configService.getStatusRestUrl(), "gleis");
  }

  public Set<Gleisabschnitt> getGleisabschnitte() {
    Set<Gleisabschnitt> gleisabschnitte = this.baseTarget
        .request()
        .accept(MediaType.APPLICATION_JSON)
        .get(new GenericType<Set<Gleisabschnitt>>() {});
    return gleisabschnitte;
  }
}
