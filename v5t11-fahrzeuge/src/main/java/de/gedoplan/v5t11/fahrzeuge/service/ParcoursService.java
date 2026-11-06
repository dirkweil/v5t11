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

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.eclipse.microprofile.config.inject.ConfigProperty;
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

  @Inject
  ParcoursService self;

  @ConfigProperty(name = "v5t11.parcours.retry-interval", defaultValue = "10S")
  Duration retryInterval;

  // Maps werden beim (ggf. wiederholten) Laden komplett ersetzt, nicht verändert, da parallel gelesen wird
  private volatile SortedMap<BereichselementId, Gleis> gleise = new TreeMap<>();
  private volatile SortedMap<BereichselementId, Weiche> weichen = new TreeMap<>();
  private volatile SortedMap<BereichselementId, Fahrstrasse> fahrstrassen = new TreeMap<>();

  private ScheduledExecutorService retryExecutor;

  /**
   * Parcours laden; bei Fehlschlag parallel zum Betrieb periodisch wiederholen, bis es gelingt.
   */
  public void loadParcoursWithRetry() {
    if (!this.self.loadParcours(true)) {
      this.logger.warnf("Laden des Parcours wird alle %s wiederholt, bis es gelingt", this.retryInterval);
      this.retryExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread thread = new Thread(r, "parcours-loader");
        thread.setDaemon(true);
        return thread;
      });
      scheduleRetry();
    }
  }

  private void scheduleRetry() {
    this.retryExecutor.schedule(() -> {
      boolean success = false;
      try {
        success = this.self.loadParcours(false);
      } catch (Exception e) {
        this.logger.warn("Wiederholtes Laden des Parcours fehlgeschlagen", e);
      }

      if (success) {
        this.logger.info("Parcours geladen");
        this.retryExecutor.shutdown();
      } else {
        scheduleRetry();
      }
    }, this.retryInterval.toMillis(), TimeUnit.MILLISECONDS);
  }

  @PreDestroy
  void shutdownRetryExecutor() {
    if (this.retryExecutor != null) {
      this.retryExecutor.shutdownNow();
    }
  }

  /**
   * Parcours laden.
   *
   * @param logStacktrace Stacktrace loggen, wenn das Laden fehlschlägt
   * @return {@code true}, wenn die Fahrstrassen erfolgreich geladen wurden
   */
  @Transactional
  public boolean loadParcours(boolean logStacktrace) {
    boolean success = false;
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
      success = true;
    } catch (Exception e) {
      if (logStacktrace) {
        this.logger.warn("Laden des Parcours fehlgeschlagen", e);
      } else {
        this.logger.warnf("Laden des Parcours fehlgeschlagen: %s", e.toString());
      }
    }

    SortedMap<BereichselementId, Fahrstrasse> newFahrstrassen = new TreeMap<>();
    this.fahrstrasseRepository.findAll().forEach(fs -> newFahrstrassen.put(fs.getId(), fs));
    this.fahrstrassen = newFahrstrassen;

    SortedMap<BereichselementId, Gleis> newGleise = new TreeMap<>();
    this.gleisRepository.findAll().forEach(g -> newGleise.put(g.getId(), g));
    this.gleise = newGleise;

    SortedMap<BereichselementId, Weiche> newWeichen = new TreeMap<>();
    this.weicheRepository.findAll().forEach(w -> newWeichen.put(w.getId(), w));
    this.weichen = newWeichen;

    return success;
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
      .filter(this::weichenstellungKorrekt)
      .map(fs -> fs.getEnde().getId())
      .findAny()
      .flatMap(this::findGleisById)
      .orElse(null);
  }

  public Gleis findGleisNach(Gleis gleis) {
    return findFahrstrassen(gleis)
      .stream()
      .filter(fs -> fs.getStart().isZaehlrichtung())
      .filter(this::weichenstellungKorrekt)
      .map(fs -> fs.getEnde().getId())
      .findAny()
      .flatMap(this::findGleisById)
      .orElse(null);
  }

  private boolean weichenstellungKorrekt(Fahrstrasse fahrstrasse) {
    for (Fahrstrassenelement fse: fahrstrasse.getElemente()) {
      if (fse.getTyp() == FahrstrassenelementTyp.WEICHE) {
        Weiche weiche = this.weichen.get(fse.getId());
        if (weiche.getStellung() != fse.getWeichenstellung()) {
          return false;
        }
      }
    }

    return true;
  }
}
