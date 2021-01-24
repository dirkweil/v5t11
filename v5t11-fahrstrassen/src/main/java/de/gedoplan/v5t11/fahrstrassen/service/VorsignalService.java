package de.gedoplan.v5t11.fahrstrassen.service;

import de.gedoplan.v5t11.fahrstrassen.entity.Parcours;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse.Fahrstrasse;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse.FahrstrassenGleis;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse.FahrstrassenSignal;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse.FahrstrassenSignal.SignalTyp;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse.FahrstrassenWeiche;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse.Fahrstrassenelement;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Signal;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Weiche;
import de.gedoplan.v5t11.fahrstrassen.gateway.StatusGateway;
import de.gedoplan.v5t11.fahrstrassen.persistence.SignalRepository;
import de.gedoplan.v5t11.fahrstrassen.persistence.WeicheRepository;
import de.gedoplan.v5t11.util.cdi.Changed;
import de.gedoplan.v5t11.util.cdi.Created;
import de.gedoplan.v5t11.util.domain.attribute.BereichselementId;
import de.gedoplan.v5t11.util.domain.attribute.SignalStellung;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.event.ObservesAsync;
import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;

/**
 * Vorsignal-Automatik.
 * Die Vorsignale werden entsprechend der gestellten Fahrwege und der damit erreichbaren Haupt- und Sperrsignale gestellt.
 *
 * @author dw
 */
@ApplicationScoped
public class VorsignalService {

  @Inject
  WeicheRepository weicheRepository;

  @Inject
  SignalRepository signalRepository;

  @Inject
  @RestClient
  StatusGateway statusGateway;

  @Inject
  Logger logger;

  // Abbildung beteiligte weiche -> Vorsignalregel
  private ListMultimap<BereichselementId, VorsignalRegel> weiche2Regel = MultimapBuilder.hashKeys().arrayListValues().build();

  // Abbildung beteiligtes Haupt- oder Sperr-Siganl -> Vorsignalregel
  private ListMultimap<BereichselementId, VorsignalRegel> signal2Regel = MultimapBuilder.hashKeys().arrayListValues().build();

  // Aus Fahrstrassen Vorsignalregeln ableiten
  void init(@Observes @Created Parcours parcours) {
    // Pro Fahrstraße mit Start-Vorsignal eine Vorsignalregel erstellen
    parcours.getFahrstrassen()
        .stream()
        .filter(fs -> fs.getStartVorsignalName() != null)
        .map(VorsignalRegel::new)
        .forEach(vsr -> {
          // Beteilgte Weichen und Signale für schnellen Lookup in entsprechende Maps eintragen
          vsr.fahrstrassenWeichen.forEach(w -> this.weiche2Regel.put(w.getId(), vsr));
          vsr.sperrSignalIds.forEach(s -> this.signal2Regel.put(s, vsr));
          if (vsr.zielHauptsignalId != null) {
            this.signal2Regel.put(vsr.zielHauptsignalId, vsr);
          }

          this.logger.trace(vsr);

          // Regel initial einmal ausführen
          vsr.execute();
        });

  }

  // Bei Weichen-Änderung davon betroffene Regeln ausführen
  void weicheChanged(@ObservesAsync @Changed Weiche weiche) {
    this.weiche2Regel.get(weiche.getId()).forEach(VorsignalRegel::execute);
  }

  // Bei Signal-Änderung davon betroffene Regeln ausführen
  void signalChanged(@ObservesAsync @Changed Signal signal) {
    this.signal2Regel.get(signal.getId()).forEach(VorsignalRegel::execute);
  }

  /**
   * Vorsignalregel.
   * Enthält
   * - als Ausführungsbedingung die beteiligten Weichen mit ihren jeweiligen Stellungen,
   * - zur Berechnung der Vorsignalstellung die Liste der betroffenen Sperrsignale,
   * - das Ziel-Hauptsignal, falls vorhanden,
   * - das betroffene Vorsignal.
   *
   * @author dw
   *
   */
  private class VorsignalRegel {
    private Collection<FahrstrassenWeiche> fahrstrassenWeichen = new ArrayList<>();
    private List<BereichselementId> sperrSignalIds = new ArrayList<>();
    private BereichselementId zielHauptsignalId;
    private BereichselementId vorsignalId;

    /**
     * Vorsignalregel aus Fahrstraße erstellen.
     *
     * @param fahrstrasse
     *        Fahrstraße
     */
    public VorsignalRegel(Fahrstrasse fahrstrasse) {
      // Alle Weichen mit ihren Stellungen übernehmen,
      // alle Signale ab dem zweiten Abschnitt übernehmen (können nur Sperrsignale sein)
      int gleisCount = 0;
      for (Fahrstrassenelement e : fahrstrasse.getElemente()) {
        if (e instanceof FahrstrassenGleis) {
          ++gleisCount;
        } else if (!e.isSchutz()) {
          if (e instanceof FahrstrassenWeiche) {
            this.fahrstrassenWeichen.add((FahrstrassenWeiche) e);
          } else if (e instanceof FahrstrassenSignal && gleisCount > 1) {
            assert FahrstrassenSignal.SignalTyp.fromName(e.getName()) == SignalTyp.SPERRSIGNAL;
            this.sperrSignalIds.add(e.getId());
          }
        }
      }

      // Ziel- und Startsignal übernehmen
      if (fahrstrasse.getZielHauptsignalName() != null) {
        this.zielHauptsignalId = new BereichselementId(fahrstrasse.getZielHauptsignalBereich(), fahrstrasse.getZielHauptsignalName());
      }

      this.vorsignalId = new BereichselementId(fahrstrasse.getStartVorsignalBereich(), fahrstrasse.getStartVorsignalName());
    }

    @Override
    public String toString() {
      return "VorsignalRegel{"
          + "weichen=" + this.fahrstrassenWeichen.stream().map(x -> x.getId() + ":" + x.getStellung()).collect(Collectors.joining(","))
          + ", signale=" + this.sperrSignalIds.stream().map(x -> x.toString()).collect(Collectors.joining(","))
          + ", zielHauptsignalId=" + this.zielHauptsignalId
          + ", vorsignal=" + this.vorsignalId
          + "}";
    }

    // Regel ausführen.
    private void execute() {
      if (weichestellungenPassen()) {
        SignalStellung vorsignalStellung = getVorsignalStellung();

        VorsignalService.this.logger.debugf("Vorsignal %s auf %s stellen", this.vorsignalId, vorsignalStellung);

        try {
          VorsignalService.this.statusGateway.signalStellen(this.vorsignalId.getBereich(), this.vorsignalId.getName(), vorsignalStellung);
        } catch (Exception e) {
          VorsignalService.this.logger.errorf(e, "Kann Vorsignal %s nicht auf %s stellen", this.vorsignalId, vorsignalStellung);
        }
      }
    }

    // Prüfen, ob alle Weichen in den gewünschten Stellungen stehen
    private boolean weichestellungenPassen() {
      for (FahrstrassenWeiche fahrstrassenWeiche : this.fahrstrassenWeichen) {
        Weiche weiche = VorsignalService.this.weicheRepository.findById(fahrstrassenWeiche.getId());
        assert weiche != null;
        if (weiche.getStellung() != fahrstrassenWeiche.getStellung()) {
          return false;
        }
      }
      return true;
    }

    // Vorsignalstellung berechnen
    private SignalStellung getVorsignalStellung() {
      // Sollte ein im Weg liegendes Sperrsignal HALT anzeigen, HALT
      for (BereichselementId sperrSignalId : this.sperrSignalIds) {
        Signal sperrSignal = VorsignalService.this.signalRepository.findById(sperrSignalId);
        assert sperrSignal != null;
        if (sperrSignal.getStellung() == SignalStellung.HALT) {
          return SignalStellung.HALT;
        }
      }

      // Falls Fahrweg ohne Ziel-Signal endet, HALT
      if (this.zielHauptsignalId == null) {
        return SignalStellung.HALT;
      }

      // Sonst Stellung des Ziel-Hauptsignals (allerdings HALT bei RANGIERFAHRT)
      Signal zielHauptsignal = VorsignalService.this.signalRepository.findById(this.zielHauptsignalId);
      assert zielHauptsignal != null;
      SignalStellung vorsignalStellung = zielHauptsignal.getStellung();
      if (vorsignalStellung == SignalStellung.RANGIERFAHRT) {
        vorsignalStellung = SignalStellung.HALT;
      }
      return vorsignalStellung;
    }
  }

}
