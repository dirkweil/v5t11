package de.gedoplan.v5t11.fahrzeuge.service;

import de.gedoplan.v5t11.fahrzeuge.entity.fahrstrasse.Fahrstrasse;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrstrasse.Fahrstrassenelement;
import de.gedoplan.v5t11.fahrzeuge.gateway.ParcoursGateway;
import de.gedoplan.v5t11.fahrzeuge.persistence.FahrstrasseRepository;
import de.gedoplan.v5t11.util.domain.attribute.FahrstrassenFilter;
import de.gedoplan.v5t11.util.domain.attribute.FahrstrassenelementTyp;

import java.util.Set;
import java.util.stream.Collectors;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

@Dependent
public class ParcoursInitService {

  @Inject
  @RestClient
  ParcoursGateway parcoursGateway;

  @Inject
  FahrstrasseRepository fahrstrasseRepository;

  @Inject
  Logger logger;

  @Transactional
  public void loadParcours() {
    try {
      Set<Fahrstrasse> fahrstrassen = fetchNonCombiFahrstrassen();

      fahrstrassen.forEach(this::removeUnusedElements);
      completeFahrstrassen(fahrstrassen);

      fahrstrassen.forEach(fs -> this.logger.debugf("Loaded %s: %s", fs.getKey(), fs.getElemente().stream().map(fe -> fe.getId() + "(" + fe.getTyp() + ")").toList()));

      fahrstrassen.forEach(Fahrstrasse::injectFields);

      fahrstrassen
        .stream()
        .flatMap(fs -> fs.getElemente().stream())
        .forEach(Fahrstrassenelement::associateFahrwegelement);

      fahrstrassen.forEach(this.fahrstrasseRepository::merge);
    } catch (Exception e) {
      this.logger.warn("Laden des Parcours fehlgeschlagen", e);
    }
  }

  private void completeFahrstrassen(Set<Fahrstrasse> fahrstrassen) {
    Set<Fahrstrasse> umkehrFahrstrassen = fahrstrassen.stream().map(Fahrstrasse::createUmkehrung).collect(Collectors.toSet());
    fahrstrassen.addAll(umkehrFahrstrassen);
  }

  @Timeout(value = 5000)
  Set<Fahrstrasse> fetchNonCombiFahrstrassen() {
    return this.parcoursGateway.getFahrstrassen(FahrstrassenFilter.NON_COMBI);
  }

  private void removeUnusedElements(Fahrstrasse fahrstrasse) {
    fahrstrasse.getElemente().removeIf(fe -> fe.getTyp() == FahrstrassenelementTyp.SIGNAL || fe.isSchutz());
  }

}
