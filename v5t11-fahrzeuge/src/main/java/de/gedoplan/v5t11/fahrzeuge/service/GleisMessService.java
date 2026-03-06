package de.gedoplan.v5t11.fahrzeuge.service;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import de.gedoplan.v5t11.fahrzeuge.entity.fahrweg.Gleis;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.Fahrzeug;
import de.gedoplan.v5t11.fahrzeuge.persistence.FahrzeugRepository;
import de.gedoplan.v5t11.fahrzeuge.persistence.GleisRepository;
import de.gedoplan.v5t11.util.cdi.Changed;
import de.gedoplan.v5t11.util.domain.attribute.BereichselementId;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.ObservesAsync;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Getter;

@ApplicationScoped
public class GleisMessService {

  // Minimale Fahrzeit; darunter keine valide Messung
  private static final long MIN_T = 500L;

  @Inject
  ParcoursService parcoursService;

  @Inject
  FahrzeugRepository fahrzeugRepository;

  @Inject
  GleisRepository gleisRepository;

  @Inject
  Logger logger;

  @AllArgsConstructor
  private static enum Status {
    START("Messung beginnt"),
    MESSUNG_IN_ZAEHLRICHTUNG("Fahrzeug fährt auf Gleis %s in Zählrichtung"),
    MESSUNG_GEGEN_ZAEHLRICHTUNG("Fahrzeug fährt auf Gleis %s entgegen der Zählrichtung"),
    KEINE_MESSUNG_GLEIS("Keine Messung für Gleis %s, da Stumpfgleis"),
    KEINE_MESSUNG_BELEGT("Keine Messung für Gleis %s, da nächstes Gleis bereits belegt"),
    KEINE_MESSUNG_FAHRZEUG("Keine Messung für Gleis %s, da Änderung der Fahrzeuggeschwindigkeit oder -richtung"),
    BEENDET("Messung beendet"),
    FEHLER("Messung fehlgeschlagen (s. Server-Log)");

    @Getter
    private String description;
  }

  // private Fahrzeug fahrzeug;
  private String fahrzeugId;
  private String fahrzeugBetriebsnummer;
  private Map<Integer, Long> fahrzeugGeschwindigkeit;

  private Status status;

  @Getter
  private String statusDescription;

  private Runnable observer;

  private long startMillis;

  @Getter
  private SortedMap<Gleis, Integer> messungen = new TreeMap<>();

  public void attachObserver(Runnable observer) {
    // TODO mehrere Observer? ggf. ausschliessen?
    this.observer = observer;
    this.logger.debug("View attached");
  }

  public void detachObserver() {
    this.observer = null;
    this.logger.debug("View detached");
  }

  public void startLaengenMessung(Fahrzeug fahrzeug) {

    this.fahrzeugId = fahrzeug.getId();
    if (this.fahrzeugRepository.findById(fahrzeug.getId()).isEmpty()) {
      this.logger.warnf("Fahrzeug %s nicht gefunden", this.fahrzeugId);
      return;
    }

    this.fahrzeugBetriebsnummer = fahrzeug.getBetriebsnummer();
    this.fahrzeugGeschwindigkeit = fahrzeug.getGeschwindigkeit();

    this.messungen.clear();

    if (isSimulation()) {
      this.parcoursService
          .getGleise()
          .stream()
          .filter(g -> g.getBereich().equals("NBf"))
          .forEach(g -> this.messungen.put(g, 1234));

      changeStatus(Status.BEENDET);
      return;
    }

    start("Gleislängenmessung");
  }

  private void start(String name) {
    if (isAktiv()) {
      this.logger.errorf("Mess-Service ist bereits im Status %s", this.status.name());
      return;
    }

    this.logger.debugf("%s mit %s (Fahrstufe %d)",
        name,
        this.fahrzeugBetriebsnummer,
        getGerichteteFahrstufe());

    start();
  }

  private void start() {
    changeStatus(Status.START);
  }

  public boolean isAktiv() {
    return this.status != null && this.status != Status.BEENDET;
  }

  public void abbrechen() {
    if (isAktiv()) {
      this.logger.debug("Messung abbrechen");
      changeStatus(Status.BEENDET);
    }
  }

  private Gleis gleis;
  private Gleis gleisNach;
  private Gleis gleisVor;

  void gleisChanged(@ObservesAsync @Changed Gleis gleis) {
    if (gleis.isBesetzt()) {
      switch (this.status) {
      case START, KEINE_MESSUNG_BELEGT, KEINE_MESSUNG_FAHRZEUG, KEINE_MESSUNG_GLEIS -> {
        this.gleis = gleis;
        this.gleisNach = this.parcoursService.findGleisNach(gleis);
        this.gleisVor = this.parcoursService.findGleisVor(gleis);

        this.logger.debugf("Gleis %s belegt; gleisNach: %s; gleisVor: %s",
            this.gleis.getId(),
            this.gleisNach != null ? this.gleisNach.getId() : null,
            this.gleisVor != null ? this.gleisVor.getId() : null);

        if (this.gleisVor == null || this.gleisNach == null) {
          this.logger.warnf("Messung von Stumpfgleis %s nicht möglich", this.gleis.getId());
          changeStatus(Status.KEINE_MESSUNG_GLEIS);
          return;
        }

        boolean vonLinks = this.gleisVor.isBesetzt();
        boolean vonRechts = this.gleisNach.isBesetzt();
        if (vonLinks && !vonRechts) {
          this.logger.debugf("Messung von %s mit Fahrt in Zählrichtung", this.gleis.getId());
          startStopWatch(gleis);
          changeStatus(Status.MESSUNG_IN_ZAEHLRICHTUNG);
        } else if (vonRechts && !vonLinks) {
          this.logger.debugf("Messung von %s mit Fahrt gegen Zählrichtung", this.gleis.getId());
          startStopWatch(gleis);
          changeStatus(Status.MESSUNG_GEGEN_ZAEHLRICHTUNG);
        } else {
          this.logger.warn("Gleise vor und nach sind nicht oder beide belegt; keine Messung");
          changeStatus(Status.KEINE_MESSUNG_BELEGT);
        }
      }

      case MESSUNG_IN_ZAEHLRICHTUNG -> {
        if (gleis.equals(this.gleisNach)) {
          if (stopStopWatch(gleis)) {
            gleisChanged(gleis);
          }
        }
      }

      case MESSUNG_GEGEN_ZAEHLRICHTUNG -> {
        if (gleis.equals(this.gleisVor)) {
          if (stopStopWatch(gleis)) {
            gleisChanged(gleis);
          }
        }
      }

      case null -> {
      }

      default -> {
      }
      }

    } else {
      if (gleis.equals(this.gleis)) {
        if (this.status == Status.MESSUNG_IN_ZAEHLRICHTUNG || this.status == Status.MESSUNG_GEGEN_ZAEHLRICHTUNG) {
          this.logger.warnf("Gleis %s ist wieder frei ohne Übergang auf angrenzende Gleise; Messung neu starten", gleis.getId());
          changeStatus(Status.START);
        }
      }
    }
  }

  private void changeStatus(Status status) {
    this.status = status;
    String gleisDescription = this.gleis != null ? this.gleis.getKey().toString() : null;
    this.statusDescription = String.format(Locale.GERMAN, status.description, gleisDescription);
    if (this.observer != null) {
      this.observer.run();
    }
  }

  @Transactional
  public void save() {
    this.messungen
        .entrySet()
        .forEach(e -> {
          BereichselementId gleisId = e.getKey().getId();
          Integer neueLaenge = e.getValue();
          this.logger.debugf("gleisId: %s, neueLaenge: %d", gleisId, neueLaenge);

          this.parcoursService.findGleisById(gleisId).ifPresent(g -> {
            this.logger.debug("  ... setze neue Länge");
            g.setLaenge(neueLaenge);
            this.parcoursService.saveGleis(gleisId);
          });
        });
  }

  @ConfigProperty(name = "v5t11.host")
  String v5t11Host;

  private boolean isSimulation() {
    return !("mbahn".equals(this.v5t11Host));
  }

  private void startStopWatch(Gleis gleis) {
    this.startMillis = gleis.getLastChangeMillis();
  }

  /**
   * Verstrichene Zeit messen und Gleislänge ermitteln.
   * @param gleis befahrenes Gleis nach dem gemessenen Gleis
   * @return true, wenn Messung plausibel und neu befahrenes Gleis direkt in weitere Messung einbezogen werden kann
   */
  private boolean stopStopWatch(Gleis gleis) {
    long stopMillis = gleis.getLastChangeMillis();
    long t = stopMillis - this.startMillis;

    if (t < MIN_T) {
      logger.warnf("Gleis %s in %d ms durchfahren; keine valide Messung", this.gleis.getId(), t);
      changeStatus(Status.KEINE_MESSUNG_BELEGT);
      return false;
    }

    int fahrstufe = getGerichteteFahrstufe();
    Long v = this.fahrzeugGeschwindigkeit.get(fahrstufe);
    if (v == null || v == 0) {
      logger.errorf("Fahrzeug %s hat für Fahrstufe %d keine Geschwindigkeit > 0", this.fahrzeugBetriebsnummer, v);
      changeStatus(Status.START);
      return true;
    }

    long s = v * t / 1_000_000L;
    logger.debugf("Gleis %s in %d ms mit %d µm/s durchfahren; Strecke: %d mm", this.gleis.getId(), t, v, s);

    this.messungen.putIfAbsent(this.gleis, (int) s);

    changeStatus(Status.START);
    return true;
  }

  private int getGerichteteFahrstufe() {
    return getGerichteteFahrstufe(this.fahrzeugRepository.findById(this.fahrzeugId).get());
  }

  private int getGerichteteFahrstufe(Fahrzeug fahrzeug) {
    int fahrstufe = fahrzeug.getFahrzeugdecoder().getFahrstufe();
    if (fahrzeug.getFahrzeugdecoder().isRueckwaerts()) {
      fahrstufe = -fahrstufe;
    }
    return fahrstufe;
  }

  void fahrzeugChanged(@ObservesAsync Fahrzeug fahrzeug) {
    if (fahrzeug.getId().equals(this.fahrzeugId)) {
      int oldFs = getGerichteteFahrstufe();
      int newFs = getGerichteteFahrstufe(fahrzeug);
      if (oldFs != newFs) {
        this.logger.debugf("Fahrstufe geändert (%d -> %d)", oldFs, newFs);
        if (this.status == Status.MESSUNG_IN_ZAEHLRICHTUNG || this.status == Status.MESSUNG_GEGEN_ZAEHLRICHTUNG) {
          this.logger.warnf("Keine Messung wegen Fahrstufenänderung", oldFs, newFs);
          changeStatus(Status.KEINE_MESSUNG_FAHRZEUG);
        }
      }
    }
  }

  public void removeMessung(Gleis gleis) {
    this.logger.debugf("Messung für Gleis %s entfernen", gleis.getId());
    this.messungen.remove(gleis);
  }

  public void removeOverrides() {
    List<Gleis> zuLoeschen = this.messungen
    .entrySet()
    .stream()
    .filter(e -> e.getKey().getLaenge() != null)
    .map(e -> e.getKey())
    .toList();
    
    zuLoeschen.forEach(this::removeMessung);
  }
}
