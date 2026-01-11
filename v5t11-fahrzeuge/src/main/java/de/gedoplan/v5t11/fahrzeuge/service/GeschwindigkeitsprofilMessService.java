package de.gedoplan.v5t11.fahrzeuge.service;

import de.gedoplan.v5t11.fahrzeuge.entity.fahrweg.Gleis;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.Fahrzeug;
import de.gedoplan.v5t11.fahrzeuge.gateway.StatusGateway;
import de.gedoplan.v5t11.util.cdi.Changed;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

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
    PAUSE("Fahrzeug stoppt"),
    BEENDET("Messung beendet");

    @Getter
    private String description;
  }

  private Fahrzeug fahrzeug;
  private Gleis messGleis;
  private Gleis linksGleis;
  private Gleis rechtsGleis;
  private Status status;
  private Consumer<String> feedbackConsumer;

  private List<Integer> messPlan;

  private int planFahrstufe;
  private int fahrstufe;
  private boolean rueckwaerts;

  private long startMillis;

  public void start(Fahrzeug fahrzeug, Gleis messGleis, Consumer<String> feedbackConsumer) {

    this.fahrzeug = fahrzeug;
    this.messGleis = messGleis;
    this.linksGleis = this.parcoursService.findGleisVor(messGleis);
    this.rechtsGleis = this.parcoursService.findGleisNach(messGleis);
    this.feedbackConsumer = feedbackConsumer;

    this.logger.debugf("Geschwindigkeitsprofilmessung für %s auf %s-%s-%s",
      fahrzeug.getBetriebsnummer(),
      messGleis,
      linksGleis,
      rechtsGleis);

    if (linksGleis == null || rechtsGleis == null) {
      feedbackConsumer.accept("Messgleis ist nicht in andere Gleise eingebettet");
      return;
    }

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
    this.statusGateway.changeFahrzeugdecoder(
      this.fahrzeug.getFahrzeugdecoder().getDecoderAdr(),
      true,
      this.fahrstufe,
      0,
      false,
      this.rueckwaerts
    );
    changeStatus(Status.START);
  }

  void gleisChanged(@ObservesAsync @Changed Gleis gleis) {
    if (gleis.isBesetzt()) {
      switch (this.status) {
      case START -> {
        if (gleis.equals(this.linksGleis)) {
          changeStatus(Status.VON_LINKS_VOR);
        } else if (gleis.equals(this.rechtsGleis)) {
          changeStatus(Status.VON_RECHTS_VOR);
        }
      }
      case VON_LINKS_VOR -> {
        if (gleis.equals(this.messGleis)) {
          startStopWatch(gleis);
          changeStatus(Status.VON_LINKS_AUF);
        }
      }
      case VON_RECHTS_VOR -> {
        if (gleis.equals(this.messGleis)) {
          startStopWatch(gleis);
          changeStatus(Status.VON_RECHTS_AUF);
        }
      }
      case VON_LINKS_AUF -> {
        if (gleis.equals(this.rechtsGleis)) {
          stopStopWatch(gleis);
          pause();
        }
      }
      case VON_RECHTS_AUF -> {
        if (gleis.equals(this.linksGleis)) {
          stopStopWatch(gleis);
          pause();
        }
      }
      default -> {
      }
      }
    }
  }

  private void pause() {
    this.statusGateway.changeFahrzeugdecoder(
      this.fahrzeug.getFahrzeugdecoder().getDecoderAdr(),
      true,
      0,
      0,
      false,
      this.rueckwaerts
    );
    changeStatus(Status.PAUSE);

    try {
      Thread.sleep(5000);
    } catch (InterruptedException e) {
      // ignore
    }

    if (this.messPlan.isEmpty()) {
      stop();
    } else {
      changeStatus(Status.START);
    }
  }

  private void stop() {
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
