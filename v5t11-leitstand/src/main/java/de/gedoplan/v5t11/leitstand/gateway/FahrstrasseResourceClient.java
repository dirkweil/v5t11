package de.gedoplan.v5t11.leitstand.gateway;

import de.gedoplan.v5t11.leitstand.entity.Leitstand;
import de.gedoplan.v5t11.leitstand.entity.fahrstrasse.Fahrstrasse;
import de.gedoplan.v5t11.leitstand.service.ConfigService;
import de.gedoplan.v5t11.util.domain.attribute.FahrstrassenFilter;
import de.gedoplan.v5t11.util.domain.attribute.FahrstrassenReservierungsTyp;
import de.gedoplan.v5t11.util.webservice.ResourceClientBase;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
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

  public List<Fahrstrasse> getFahrstrassen(String startBereich, String startName, String endeBereich, String endeName, FahrstrassenFilter filter) {
    WebTarget target = this.baseTarget
        .queryParam("startBereich", startBereich)
        .queryParam("startName", startName)
        .queryParam("endeBereich", endeBereich)
        .queryParam("endeName", endeName);

    if (filter != null) {
      target = target
          .queryParam("filter", filter);
    }

    List<Fahrstrasse> fahrstrassen = target
        .request()
        .accept(MediaType.APPLICATION_JSON)
        .get(new GenericType<List<Fahrstrasse>>() {});

    // Fahrstrassen mit Fahrwegelementen im Leitstand verknüpfen
    fahrstrassen.stream()
        .flatMap(fs -> fs.getElemente().stream())
        .forEach(fse -> fse.linkFahrwegelement(this.leitstand));

    return fahrstrassen;
  }

  public void reserviereFahrstrasse(String bereich, String name, FahrstrassenReservierungsTyp reservierungsTyp) {
    this.baseTarget
        .path(bereich)
        .path(name)
        .path("reservierung")
        .request()
        .put(Entity.text(reservierungsTyp));
  }

}
