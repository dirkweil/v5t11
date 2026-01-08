package de.gedoplan.v5t11.fahrzeuge.service;

import de.gedoplan.v5t11.fahrzeuge.entity.fahrstrasse.Fahrstrasse;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrstrasse.Fahrstrassenelement;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrweg.Gleis;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrweg.Weiche;
import de.gedoplan.v5t11.fahrzeuge.gateway.ParcoursGateway;
import de.gedoplan.v5t11.fahrzeuge.persistence.FahrstrasseRepository;
import de.gedoplan.v5t11.fahrzeuge.persistence.GleisRepository;
import de.gedoplan.v5t11.fahrzeuge.persistence.WeicheRepository;
import de.gedoplan.v5t11.util.cdi.Changed;
import de.gedoplan.v5t11.util.cdi.EventFirer;
import de.gedoplan.v5t11.util.domain.attribute.BereichselementId;
import de.gedoplan.v5t11.util.domain.attribute.FahrstrassenFilter;
import de.gedoplan.v5t11.util.domain.attribute.FahrstrassenelementTyp;
import de.gedoplan.v5t11.util.domain.entity.Fahrwegelement;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

@ApplicationScoped
public class ParcoursService {

  @Inject
  @RestClient
  ParcoursGateway parcoursGateway;

  @Inject
  FahrstrasseRepository fahrstrasseRepository;

  @Inject
  GleisRepository gleisRepository;

  @Inject
  WeicheRepository weicheRepository;

  @Inject
  EventFirer eventFirer;

  @Inject
  Logger logger;

  private SortedMap<BereichselementId, Gleis> gleise = new TreeMap<>();
  private SortedMap<BereichselementId, Weiche> weichen = new TreeMap<>();
  private SortedMap<BereichselementId, Fahrstrasse> fahrstrassen = new TreeMap<>();

  @Transactional
  public void loadParcours() {
    try {
      Set<Fahrstrasse> tmpFs = fetchNonCombiFahrstrassen();

      tmpFs.forEach(this::removeUnusedElements);
      completeFahrstrassen(tmpFs);

      tmpFs.forEach(fs -> this.logger.debugf("Loaded %s: %s", fs.getKey(), fs.getElemente().stream().map(fe -> fe.getId() + "(" + fe.getTyp() + ")").toList()));

      tmpFs.forEach(Fahrstrasse::injectFields);

      tmpFs
        .stream()
        .flatMap(fs -> fs.getElemente().stream())
        .forEach(Fahrstrassenelement::associateFahrwegelement);

      tmpFs.forEach(this.fahrstrasseRepository::merge);
    } catch (Exception e) {
      this.logger.warn("Laden des Parcours fehlgeschlagen", e);
    }

    this.fahrstrassen.clear();
    this.fahrstrasseRepository.findAll().forEach(fs -> this.fahrstrassen.put(fs.getId(), fs));

    this.gleise.clear();
    this.gleisRepository.findAll().forEach(g -> this.gleise.put(g.getId(), g));

    this.weichen.clear();
    this.weicheRepository.findAll().forEach(g -> this.weichen.put(g.getId(), g));
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

  public Optional<Gleis> findGleisById(BereichselementId id) {
    return Optional.ofNullable(this.gleise.get(id));
  }

  @Transactional
  public void update(Gleis receivedObject) {
    Gleis gleis = this.gleise.get(receivedObject.getId());
    if (gleis != null) {
      copyStatus(gleis, receivedObject);
      this.gleisRepository.merge(gleis);
    }
  }

  @Transactional
  public void update(Weiche receivedObject) {
    Weiche weiche = this.weichen.get(receivedObject.getId());
    if (weiche != null) {
      copyStatus(weiche, receivedObject);
      this.weicheRepository.merge(weiche);
    }
  }

  private void copyStatus(Fahrwegelement to, Fahrwegelement from) {
    if (to != null) {
      if (to.copyStatus(from)) {
        if (this.logger.isDebugEnabled()) {
          this.logger.debug(to);
        }

        this.eventFirer.fire(to, Changed.Literal.INSTANCE);
      }
    }
  }

  public Collection<Gleis> getGleise() {
    return this.gleise.values();
  }

  @Transactional
  public void saveGleis(BereichselementId id) {
    Gleis gleis = this.gleise.get(id);
    if (gleis != null) {
      this.gleisRepository.merge(gleis);
    }
  }

  public List<Fahrstrasse> findFahrstrassen(Gleis start) {
    if (start == null) {
      return List.of();
    }
    return this.fahrstrassen
      .values()
      .stream()
      .filter(fs -> fs.getStart().getId().equals(start.getId()))
      .toList();
  }

  public Gleis findGleisVor(Gleis gleis) {
    return findFahrstrassen(gleis)
      .stream()
      .filter(fs -> !fs.getStart().isZaehlrichtung())
      .map(fs -> fs.getEnde().getId())
      .findAny()
      .flatMap(this::findGleisById)
      .orElse(null);
  }

  public Gleis findGleisNach(Gleis gleis) {
    return findFahrstrassen(gleis)
      .stream()
      .filter(fs -> fs.getStart().isZaehlrichtung())
      .map(fs -> fs.getEnde().getId())
      .findAny()
      .flatMap(this::findGleisById)
      .orElse(null);
  }
}
