package de.gedoplan.v5t11.fahrzeuge.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import de.gedoplan.v5t11.fahrzeuge.entity.fahrweg.Gleis;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.Fahrzeug;
import de.gedoplan.v5t11.fahrzeuge.gateway.StatusGateway;
import de.gedoplan.v5t11.fahrzeuge.persistence.FahrstrasseRepository;
import de.gedoplan.v5t11.fahrzeuge.persistence.FahrzeugRepository;
import de.gedoplan.v5t11.util.cdi.Changed;
import de.gedoplan.v5t11.util.domain.attribute.BereichselementId;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.ObservesAsync;
import jakarta.inject.Inject;
import lombok.AllArgsConstructor;
import lombok.Getter;

@ApplicationScoped
public class GeschwindigkeitsMessService {

  @Inject
  ParcoursService parcoursService;

  @Inject
  @RestClient
  StatusGateway statusGateway;

  @Inject
  FahrzeugRepository fahrzeugRepository;

  @Inject
  Logger logger;

  @AllArgsConstructor
  private static enum Status {
    START("Messung beginnt für %s"), VON_LINKS_VOR("Fahrzeug fährt von links auf Messgleis zu"), VON_RECHTS_VOR("Fahrzeug fährt von rechts auf Messgleis zu"), VON_LINKS_AUF(
        "Fahrzeug fährt auf Messgleis nach rechts"), VON_RECHTS_AUF(
            "Fahrzeug fährt auf Messgleis nach links"), AUSLAUF("Fahrzeug fährt zum Umkehrgleis"), BEENDET("Messung beendet"), FEHLER("Messung fehlgeschlagen (s. Server-Log)");

    @Getter
    private String description;
  }

  private Fahrzeug fahrzeug;

  @Getter
  private Gleis linkesUmkehrGleis;
  @Getter
  private Gleis linkesAnschlussGleis;
  @Getter
  private Gleis messGleis;
  @Getter
  private Gleis rechtesAnschlussGleis;
  @Getter
  private Gleis rechtesUmkehrGleis;

  private Status status;

  @Getter
  private String statusDescription;

  private Runnable observer;

  private List<Integer> messPlan;

  private int planFahrstufe;
  private int fahrstufe;
  private boolean rueckwaerts;
  private String fahrstufenBeschreibung;

  private long startMillis;

  @Getter
  private Map<Integer, Long> geschwindigkeit = new HashMap<>();

  @PostConstruct
  void init() {
    this.messGleis = this.parcoursService.findGleisById(new BereichselementId("HBf", "506")).get();
    this.linkesUmkehrGleis = this.parcoursService.findGleisById(new BereichselementId("HBf", "6")).get();
    this.linkesAnschlussGleis = this.parcoursService.findGleisVor(messGleis);
    this.rechtesAnschlussGleis = this.parcoursService.findGleisNach(messGleis);
    this.rechtesUmkehrGleis = this.parcoursService.findGleisById(new BereichselementId("SBf", "1004")).get();
  }

  public void attachObserver(Runnable observer) {
    // TODO mehrere Observer? ggf. ausschliessen?
    this.observer = observer;
    this.logger.debug("View attached");
  }

  public void detachObserver() {
    this.observer = null;
    this.logger.debug("View dettached");
  }

  public void startHoechstgeschwindigkeitsMessung(Fahrzeug fahrzeug) {
    if (isAktiv()) {
      this.logger.errorf("Mess-Service ist bereits im Status %s", this.status.name());
      return;
    }

    this.fahrzeug = this.fahrzeugRepository.findById(fahrzeug.getId()).get();

    this.logger.debugf("Höchstgeschwindigkeitsmessung für %s auf %s-%s-%s-%s-%s",
        fahrzeug.getBetriebsnummer(),
        linkesUmkehrGleis.getId(),
        linkesAnschlussGleis.getId(),
        messGleis.getId(),
        rechtesAnschlussGleis.getId(),
        rechtesUmkehrGleis.getId());

    int maxFahrstufe = this.fahrzeug.getFahrzeugdecoder().getDecoderAdr().getSystemTyp().getMaxFahrstufe();
    int faktor = this.fahrzeug.getFahrzeugdecoder().isRueckwaerts() ? -1 : 1;
    this.logger.debugf("maxFahrstufe=%d, faktor=%d", maxFahrstufe, faktor);
    this.messPlan = new ArrayList<>();
    this.messPlan.add(maxFahrstufe * faktor);
    this.messPlan.add(-maxFahrstufe * faktor);

    this.logger.debugf("Messplan: %s", messPlan);
    if (messPlan.isEmpty()) {
      this.statusDescription = "Messplan ist leer";
      if (this.observer != null) {
        this.observer.run();
      }
      return;
    }

    start();
  }

  public void startProfilMessung(Fahrzeug fahrzeug) {
    if (isAktiv()) {
      this.logger.errorf("Mess-Service ist bereits im Status %s", this.status.name());
      return;
    }

    this.fahrzeug = this.fahrzeugRepository.findById(fahrzeug.getId()).get();

    this.logger.debugf("Geschwindigkeitsprofilmessung für %s auf %s-%s-%s-%s-%s",
        fahrzeug.getBetriebsnummer(),
        linkesUmkehrGleis.getId(),
        linkesAnschlussGleis.getId(),
        messGleis.getId(),
        rechtesAnschlussGleis.getId(),
        rechtesUmkehrGleis.getId());

    int maxFahrstufe = this.fahrzeug.getFahrzeugdecoder().getDecoderAdr().getSystemTyp().getMaxFahrstufe();
    int schrittweite = Math.max(5, maxFahrstufe / 12);
    int limit = (maxFahrstufe + schrittweite - 1) / schrittweite * schrittweite;
    int faktor = this.fahrzeug.getFahrzeugdecoder().isRueckwaerts() ? -1 : 1;
    this.logger.debugf("maxFahrstufe=%d, schrittweite=%d, limit=%d, faktor=%d", maxFahrstufe, schrittweite, limit,
        faktor);
    this.messPlan = new ArrayList<>();
    for (int i = 0; i <= limit; i += schrittweite) {
      int low = Math.min(Math.max(i, 1), maxFahrstufe);
      this.messPlan.add(low * faktor);

      int high = Math.min(Math.max(limit - i, 1), maxFahrstufe);
      this.messPlan.add(-high * faktor);
    }

    // TODO Nur für erste Tests
    this.messPlan = this.messPlan.subList(12, 16);

    this.logger.debugf("Messplan: %s", messPlan);
    if (messPlan.isEmpty()) {
      this.statusDescription = "Messplan ist leer";
      if (this.observer != null) {
        this.observer.run();
      }
      return;
    }

    start();
  }

  private void start() {
    this.planFahrstufe = this.messPlan.remove(0);
    steuereFahrzeug();
    changeStatus(Status.START);
  }

  public boolean isAktiv() {
    return this.status != null && this.status != Status.BEENDET;
  }

  public void abbrechen() {
    if (isAktiv()) {
      this.logger.debug("Messung abbrechen");

      this.planFahrstufe = 0;
      steuereFahrzeug();
      changeStatus(Status.BEENDET);
    }
  }

  private void steuereFahrzeug() {
    this.fahrstufe = Math.abs(planFahrstufe);
    if (this.fahrstufe != 0) {
      this.rueckwaerts = planFahrstufe < 0;
    }
    this.fahrstufenBeschreibung = String.format("Fahrstufe %d %s", this.fahrstufe, this.rueckwaerts ? "rückwärts" : "vorwärts");
    this.logger.debug(this.fahrstufenBeschreibung);
    try {
      this.statusGateway.changeFahrzeugdecoder(
          this.fahrzeug.getFahrzeugdecoder().getDecoderAdr(),
          true,
          this.fahrstufe,
          0,
          false,
          this.rueckwaerts);
    } catch (Exception e) {
      this.logger.error("Kann Fahrzeug nicht steuern", e);
      changeStatus(Status.BEENDET);
    }
  }

  void gleisChanged(@ObservesAsync @Changed Gleis gleis) {
    if (gleis.isBesetzt()) {
      switch (this.status) {
      case START -> {
        if (gleis.equals(this.linkesAnschlussGleis)) {
          this.logger.debugf("Fahrzeug hat linkes Anschlussgleis %s erreicht", gleis.getId());
          changeStatus(Status.VON_LINKS_VOR);
        } else if (gleis.equals(this.rechtesAnschlussGleis)) {
          this.logger.debugf("Fahrzeug hat rechtes Anschlussgleis %s erreicht", gleis.getId());
          changeStatus(Status.VON_RECHTS_VOR);
        }
      }
      case VON_LINKS_VOR -> {
        if (gleis.equals(this.messGleis)) {
          startStopWatch(gleis);
          this.logger.debugf("Fahrzeug hat Messgleis %s erreicht", gleis.getId());
          changeStatus(Status.VON_LINKS_AUF);
        }
      }
      case VON_RECHTS_VOR -> {
        if (gleis.equals(this.messGleis)) {
          startStopWatch(gleis);
          this.logger.debugf("Fahrzeug hat Messgleis %s erreicht", gleis.getId());
          changeStatus(Status.VON_RECHTS_AUF);
        }
      }
      case VON_LINKS_AUF -> {
        if (gleis.equals(this.rechtesAnschlussGleis)) {
          stopStopWatch(gleis);
          this.logger.debugf("Fahrzeug hat rechtes Anschlussgleis %s erreicht", gleis.getId());
          auslauf();
        }
      }
      case VON_RECHTS_AUF -> {
        if (gleis.equals(this.linkesAnschlussGleis)) {
          stopStopWatch(gleis);
          this.logger.debugf("Fahrzeug hat linkes Anschlussgleis %s erreicht", gleis.getId());
          auslauf();
        }
      }
      case AUSLAUF -> {
        if (gleis.equals(this.linkesUmkehrGleis) || gleis.equals(this.rechtesUmkehrGleis)) {
          this.logger.debugf("Fahrzeug hat Umkehrgleis %s erreicht", gleis.getId());
          if (this.messPlan.isEmpty()) {
            stop();
          } else {
            start();
          }
        }
      }
      case null, default -> {
      }
      }
    }
  }

  private void auslauf() {
    this.planFahrstufe = this.fahrzeug.getFahrzeugdecoder().getDecoderAdr().getSystemTyp().getMaxFahrstufe();
    if (this.rueckwaerts) {
      this.planFahrstufe = -this.planFahrstufe;
    }
    steuereFahrzeug();
    changeStatus(Status.AUSLAUF);
  }

  private void stop() {
    this.planFahrstufe = 0;
    steuereFahrzeug();
    changeStatus(Status.BEENDET);
  }

  private void changeStatus(Status status) {
    this.status = status;
    this.statusDescription = String.format(Locale.GERMAN, status.description, this.fahrstufenBeschreibung);
    if (this.observer != null) {
      this.observer.run();
    }
  }

  private void startStopWatch(Gleis gleis) {
    this.startMillis = gleis.getLastChangeMillis();
  }

  private void stopStopWatch(Gleis gleis) {
    long stopMillis = gleis.getLastChangeMillis();
    long messMillis = stopMillis - this.startMillis;

    // Modellgeschwindigkeit in µm/s
    long modellGeschwindigkeit = this.messGleis.getLaenge() * 1000L / messMillis;

    // Realgeschwindigkeit in km/h
    long realGeschwindigkeit = modellGeschwindigkeit * 160L * 60L * 60L / 1000L / 1000L;

    this.logger.debugf("Zeit: %,d ms, Geschwindigkeit: %,d µm/s ≙ %,d km/h", messMillis, modellGeschwindigkeit, realGeschwindigkeit);

    this.geschwindigkeit.put(this.planFahrstufe, modellGeschwindigkeit);

    if (this.observer != null) {
      this.observer.run();
    }
  }

}
