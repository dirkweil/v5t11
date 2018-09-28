package de.gedoplan.v5t11.leitstand.gateway;

import de.gedoplan.v5t11.leitstand.entity.Leitstand;
import de.gedoplan.v5t11.leitstand.entity.fahrstrasse.Fahrstrasse;
import de.gedoplan.v5t11.leitstand.service.ConfigService;
import de.gedoplan.v5t11.util.webservice.ResourceClientBase;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.MediaType;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@ApplicationScoped
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FahrstrasseResourceClient extends ResourceClientBase {

  @Inject
  Leitstand leitstand;

  @Inject
  public FahrstrasseResourceClient(ConfigService configService) {
    super(configService.getFahrstrassenRestUrl(), "fahrstrasse");
  }

  public Fahrstrasse getFahrstrasse(String bereich, String name) {
    Fahrstrasse fahrstrasse = this.baseTarget
        .path(bereich)
        .path(name)
        .request()
        .accept(MediaType.APPLICATION_JSON)
        .get(Fahrstrasse.class);

    // Fahrstrasse mit Fahrwegelementen im Leitstand verknüpfen
    fahrstrasse.getElemente().forEach(fse -> fse.linkFahrwegelement(this.leitstand));

    return fahrstrasse;
  }
}
