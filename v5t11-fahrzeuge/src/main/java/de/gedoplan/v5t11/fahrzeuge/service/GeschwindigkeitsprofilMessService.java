package de.gedoplan.v5t11.fahrzeuge.service;

import de.gedoplan.v5t11.fahrzeuge.entity.fahrweg.Gleis;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.Fahrzeug;
import de.gedoplan.v5t11.fahrzeuge.gateway.StatusGateway;
import de.gedoplan.v5t11.util.cdi.Changed;
import de.gedoplan.v5t11.util.domain.attribute.BereichselementId;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.ObservesAsync;
import jakarta.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import lombok.AllArgsConstructor;
import lombok.Getter;

@ApplicationScoped
public class GeschwindigkeitsprofilMessService {

  @Inject
  ParcoursService parcoursService;

  @Inject
  @RestClient
  StatusGateway statusGateway;

  @Inject
  Logger logger;

  @AllArgsConstructor
  private static enum Status {
    START("Fahrzeug fährt mit Fahrstufe %d"),
    VON_LINKS_VOR("Fahrzeug fährt von links auf Messgleis zu"),
    VON_RECHTS_VOR("Fahrzeug fährt von rechts auf Messgleis zu"),
    VON_LINKS_AUF("Fahrzeug fährt auf Messgleis nach rechts"),
    VON_RECHTS_AUF("Fahrzeug fährt auf Messgleis nach links"),
    AUSLAUF("Fahrzeug fährt zum Umkehrgleis"),
    BEENDET("Messung beendet"),
    FEHLER("Messung fehlgeschlagen (s. Server-Log)");

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
  private Consumer<String> feedbackConsumer;

  private List<Integer> messPlan;

  private int planFahrstufe;
  private int fahrstufe;
  private boolean rueckwaerts;

  private long startMillis;

  @PostConstruct
  void init() {
    this.messGleis = this.parcoursService.findGleisById(new BereichselementId("HBf", "506")).get();
    this.linkesUmkehrGleis = this.parcoursService.findGleisById(new BereichselementId("HBf", "6")).get();
    this.linkesAnschlussGleis = this.parcoursService.findGleisVor(messGleis);
    this.rechtesAnschlussGleis = this.parcoursService.findGleisNach(messGleis);
    this.rechtesUmkehrGleis = this.parcoursService.findGleisById(new BereichselementId("SBf", "1004")).get();
  }

  public void start(Fahrzeug fahrzeug, Consumer<String> feedbackConsumer) {

    this.fahrzeug = fahrzeug;
    this.feedbackConsumer = feedbackConsumer;

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
    this.messPlan.remove(0);
    this.messPlan.remove(0);
    this.messPlan.remove(0);
    this.messPlan.remove(0);
    this.messPlan.remove(0);
    this.messPlan.remove(0);
    this.messPlan.remove(0);
    this.messPlan.remove(0);
    this.messPlan.remove(0);
    this.messPlan.remove(0);
    this.messPlan.remove(0);
    this.messPlan.remove(0);

    this.logger.debugf("Messplan: %s", messPlan);
    if (messPlan.isEmpty()) {
      feedbackConsumer.accept("Messplan ist leer");
      return;
    }

    start();
  }

  private void start() {
    this.planFahrstufe = this.messPlan.remove(0);
    this.fahrstufe = Math.abs(planFahrstufe);
    this.rueckwaerts = planFahrstufe < 0;
    steuereFahrzeug();
    changeStatus(Status.START);
  }

  private void steuereFahrzeug() {
    this.logger.debugf("fahrstufe %d %s setzen", this.fahrstufe, this.rueckwaerts ? "rückwärts" : "vorwärts");
    try {
      this.statusGateway.changeFahrzeugdecoder(
        this.fahrzeug.getFahrzeugdecoder().getDecoderAdr(),
        true,
        this.fahrstufe,
        0,
        false,
        this.rueckwaerts
      );
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
    this.fahrstufe = this.fahrzeug.getFahrzeugdecoder().getDecoderAdr().getSystemTyp().getMaxFahrstufe() / 2;
    steuereFahrzeug();
    changeStatus(Status.AUSLAUF);
  }

  private void stop() {
    this.fahrstufe = 0;
    steuereFahrzeug();
    changeStatus(Status.BEENDET);
    this.feedbackConsumer = null;
  }

  private void changeStatus(Status status) {
    this.status = status;
    this.feedbackConsumer.accept(String.format(Locale.GERMAN, status.description, this.fahrzeug.getFahrzeugdecoder().getFahrstufe()));
  }

  private void startStopWatch(Gleis gleis) {
    this.startMillis = gleis.getLastChangeMillis();
  }

  private void stopStopWatch(Gleis gleis) {
    long stopMillis = gleis.getLastChangeMillis();
    long messMillis = stopMillis - this.startMillis;
    this.feedbackConsumer.accept(String.format(Locale.GERMAN, "Messgleis in %,d ms durchfahren", messMillis));

    long modellGeschwindigkeit = this.messGleis.getLaenge() * 1000L / messMillis;
    long realGeschwindigkeit = modellGeschwindigkeit * 160L * 60L * 60L / 1000L / 1000L;
    this.feedbackConsumer.accept(String.format(Locale.GERMAN, "Geschwindigkeit: %,d mm/s ≙ %,d km/h", modellGeschwindigkeit, realGeschwindigkeit));
  }

}
