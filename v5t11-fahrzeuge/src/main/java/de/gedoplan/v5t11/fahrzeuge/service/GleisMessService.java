package de.gedoplan.v5t11.fahrzeuge.service;

import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import de.gedoplan.v5t11.fahrzeuge.entity.fahrweg.Gleis;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.Fahrzeug;
import de.gedoplan.v5t11.fahrzeuge.persistence.FahrzeugRepository;
import de.gedoplan.v5t11.fahrzeuge.persistence.GleisRepository;
import de.gedoplan.v5t11.util.cdi.Changed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.ObservesAsync;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Getter;

@ApplicationScoped
public class GleisMessService {

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
    START("Messung beginnt für %s"), 
    VON_LINKS_VOR("Fahrzeug fährt mit %s von links auf Messgleis zu"), 
    VON_RECHTS_VOR("Fahrzeug fährt mit %s von rechts auf Messgleis zu"), 
    VON_LINKS_AUF("Fahrzeug fährt mit %s auf Messgleis nach rechts"), 
    VON_RECHTS_AUF("Fahrzeug fährt mit %s auf Messgleis nach links"), 
    AUSLAUF("Fahrzeug fährt mit %s zum Umkehrgleis"), 
    BEENDET("Messung beendet"), 
    FEHLER("Messung fehlgeschlagen (s. Server-Log)");

    @Getter
    private String description;
  }

  private Fahrzeug fahrzeug;

  private Status status;

  @Getter
  private String statusDescription;

  private Runnable observer;

  private long startMillis;

  @Getter
  private SortedSet<Gleis> gleise = new TreeSet<>();

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

    if (isSimulation()) {
      this.gleise.clear();
      this.gleise.addAll(parcoursService.getGleise().stream().filter(g -> g.getBereich().equals("NBf")).toList());
      this.gleise.forEach(g -> g.setLaenge(1234));
      changeStatus(Status.BEENDET);
    } else {
      start("Gleislängenmessung");
    }
  }

  private void start(String name) {
    if (isAktiv()) {
      this.logger.errorf("Mess-Service ist bereits im Status %s", this.status.name());
      return;
    }

    this.logger.debugf("%s mit %s",
        name,
        fahrzeug.getBetriebsnummer());

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

  void gleisChanged(@ObservesAsync @Changed Gleis gleis) {
  }

  private void changeStatus(Status status) {
    this.status = status;
    if (this.observer != null) {
      this.observer.run();
    }
  }

  @Transactional
  public void save() {
    this.gleise.forEach(g1 -> this.gleisRepository.findById(g1.getId()).ifPresent(g2 -> g2.setLaenge(g1.getLaenge())));
  }

  @ConfigProperty(name = "v5t11.host")
  String v5t11Host;

  private boolean isSimulation() {
    return !("mbahn".equals(this.v5t11Host));
  }

}
