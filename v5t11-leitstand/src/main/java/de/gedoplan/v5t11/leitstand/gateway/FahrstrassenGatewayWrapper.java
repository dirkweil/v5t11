package de.gedoplan.v5t11.leitstand.gateway;

import de.gedoplan.v5t11.leitstand.entity.Leitstand;
import de.gedoplan.v5t11.leitstand.entity.fahrstrasse.Fahrstrasse;
import de.gedoplan.v5t11.util.domain.attribute.FahrstrassenFilter;
import de.gedoplan.v5t11.util.domain.attribute.FahrstrassenReservierungsTyp;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;

// Dies könnte eleganter als Decorator formuliert werden, aber leider unterstützt Quarkus keine Decorators!
@ApplicationScoped
public class FahrstrassenGatewayWrapper {

  @Inject
  @RestClient
  FahrstrassenGateway delegate;

  @Inject
  Leitstand leitstand;

  public Fahrstrasse getFahrstrasse(String bereich, String name) {
    Fahrstrasse fahrstrasse = this.delegate.getFahrstrasse(bereich, name);

    // Fahrstrasse mit Fahrwegelementen im Leitstand verknüpfen
    fahrstrasse.getElemente().forEach(fse -> fse.linkFahrwegelement(this.leitstand));

    return fahrstrasse;
  }

  public List<Fahrstrasse> getFahrstrassen(String startBereich, String startName, String endeBereich, String endeName, FahrstrassenFilter filter) {
    List<Fahrstrasse> fahrstrassen = this.delegate.getFahrstrassen(startBereich, startName, endeBereich, endeName, filter);

    // Fahrstrassen mit Fahrwegelementen im Leitstand verknüpfen
    fahrstrassen.stream()
        .flatMap(fs -> fs.getElemente().stream())
        .forEach(fse -> fse.linkFahrwegelement(this.leitstand));

    return fahrstrassen;
  }

  public void reserviereFahrstrasse(String bereich, String name, FahrstrassenReservierungsTyp reservierungsTyp) {
    this.delegate.reserviereFahrstrasse(bereich, name, reservierungsTyp);
  }

}
