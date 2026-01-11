package de.gedoplan.v5t11.fahrzeuge.service;

import de.gedoplan.v5t11.fahrzeuge.entity.fahrweg.Gleis;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.Fahrzeug;
import de.gedoplan.v5t11.util.cdi.Changed;

import java.util.Locale;
import java.util.function.Consumer;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.ObservesAsync;
import jakarta.inject.Inject;

import org.jboss.logging.Logger;

import lombok.AllArgsConstructor;
import lombok.Getter;

@ApplicationScoped
public class HoechstgeschwindigkeitsMessService {

  @Inject
  ParcoursService parcoursService;

  @Inject
  Logger logger;

  @AllArgsConstructor
  private static enum Status {
    GESTARTET("Messung gestartet"),
    LINKS("Fahrzeug fährt von links auf Messgleis zu"),
    RECHTS("Fahrzeug fährt von rechts auf Messgleis zu"),
    LINKS_AUF("Fahrzeug fährt auf Messgleis nach rechts"),
    RECHTS_AUF("Fahrzeug fährt auf Messgleis nach links"),
    BEENDET("Messung beendet");

    @Getter
    private String description;
  }

  private Gleis messGleis;
  private Gleis linksGleis;
  private Gleis rechtsGleis;
  private Status status;
  private Consumer<String> feedbackConsumer;
  private long startMillis;

  public void start(Fahrzeug fahrzeug, Gleis messGleis, Consumer<String> feedbackConsumer) {

    this.messGleis = messGleis;
    this.linksGleis = this.parcoursService.findGleisVor(messGleis);
    this.rechtsGleis = this.parcoursService.findGleisNach(messGleis);
    this.feedbackConsumer = feedbackConsumer;

    this.logger.debugf("Höchstgeschwindigkeitsmessung für %s auf %s-%s-%s",
      fahrzeug.getBetriebsnummer(),
      messGleis,
      linksGleis,
      rechtsGleis);

    if (linksGleis == null || rechtsGleis == null) {
      feedbackConsumer.accept("Messgleis ist nicht in andere Gleise eingebettet");
      return;
    }

    changeStatus(Status.GESTARTET);
  }

  void gleisChanged(@ObservesAsync @Changed Gleis gleis) {
    if (gleis.isBesetzt()) {
      switch (this.status) {
      case GESTARTET -> {
        if (gleis.equals(this.linksGleis)) {
          changeStatus(Status.LINKS);
        } else if (gleis.equals(this.rechtsGleis)) {
          changeStatus(Status.RECHTS);
        }
      }
      case LINKS -> {
        if (gleis.equals(this.messGleis)) {
          changeStatus(Status.LINKS_AUF);
          startStopWatch(gleis);
        }
      }
      case RECHTS -> {
        if (gleis.equals(this.messGleis)) {
          changeStatus(Status.RECHTS_AUF);
          startStopWatch(gleis);
        }
      }
      case LINKS_AUF -> {
        if (gleis.equals(this.rechtsGleis)) {
          changeStatus(Status.BEENDET);
          stopStopWatch(gleis);
        }
      }
      case RECHTS_AUF -> {
        if (gleis.equals(this.linksGleis)) {
          changeStatus(Status.BEENDET);
          stopStopWatch(gleis);
        }
      }
      case null, default -> {
      }
      }
    }
  }

  private void changeStatus(Status status) {
    this.status = status;
    this.feedbackConsumer.accept(status.description);
  }

  private void startStopWatch(Gleis gleis) {
    this.startMillis = gleis.getLastChangeMillis();
  }

  private void stopStopWatch(Gleis gleis) {
    long stopMillis = gleis.getLastChangeMillis();
    long messMillis = stopMillis - this.startMillis;
    this.feedbackConsumer.accept(String.format(Locale.GERMAN, "Messgleis in %,d ms durchfahren", messMillis));

    long modellGeschwindigkeit = this.messGleis.getLaenge() * 1000L / messMillis;
    this.feedbackConsumer.accept(String.format(Locale.GERMAN, "Modellgeschwindigkeit: %,d mm/s", modellGeschwindigkeit));

    long realGeschwindigkeit = modellGeschwindigkeit * 160L * 60L * 60L / 1000L / 1000L;
    this.feedbackConsumer.accept(String.format(Locale.GERMAN, "Realgeschwindigkeit: %,d km/h", realGeschwindigkeit));
  }

}
