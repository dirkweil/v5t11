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
import java.util.Objects;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.ObservesAsync;
import jakarta.inject.Inject;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

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
    List<VorsignalRegel> vorsignalRegeln = parcours.getFahrstrassen()
      .stream()
      .filter(fs -> fs.getStartVorsignalName() != null)
      .map(this::createVorsignalRegel)
      .filter(Objects::nonNull)
      .collect(Collectors.toList());

    /*
     * Es können nun Regeln in der Liste sein, die einander enthalten, d. h. der Fahrweg der einen ist Teil der anderen.
     * Daher werden Regeln so sortiert, dass die für ein Vorsignal hintereinander stehen, und zwar nach id (bereich/name) geordnet.
     * Dadurch liegen die Regeln stets vor derjenigen, für die sie ggf. ein Anfangsstück darstellen.
     */
    vorsignalRegeln.sort((a, b) -> {
      int diff = a.vorsignalId.compareTo(b.vorsignalId);
      return (diff != 0) ? diff : a.id.compareTo(b.id);
    });

    /*
     * Die Liste wird nun nach Regeln a, b durchsucht, wo a ein Anfangsstück von b darstellt
     */
    int i = 0;
    while (i < vorsignalRegeln.size() - 1) {
      VorsignalRegel a = vorsignalRegeln.get(i);
      VorsignalRegel b = vorsignalRegeln.get(i + 1);

      if (a.vorsignalId.equals(b.vorsignalId) && a.id.getBereich().equals(b.id.getBereich()) && b.id.getName().startsWith(a.id.getName())) {
        // this.logger.tracef(">>>>>>>>>>>>>>>> %s", a);
        // this.logger.tracef(" ist Präfix von %s", b);

        if (a.zielHauptsignalId != null) {
          // Sollte a ein Ziel-Hauptsignal haben, wird b entfernt (da das Vorsignal dann nur bis zum Hauptsignal "reicht")
          // this.logger.trace(" entferne zweite");
          vorsignalRegeln.remove(i + 1);
        } else {
          // Andernfalls wird a entfernt (da das Vorsignal dann mindestens bis zu b "reicht")
          // this.logger.trace(" entferne erste");
          vorsignalRegeln.remove(i);
        }
      } else {
        ++i;
      }
    }

    // Nun ist die Liste "präfixfrei", d. h. für jeden Fahrweg gibt es nur eine Vorsignalregel, die ihn umfasst

    vorsignalRegeln.forEach(vsr -> {
      // Beteiligte Weichen und Signale für schnellen Lookup in entsprechende Maps eintragen
      vsr.fahrstrassenWeichen.forEach(w -> this.weiche2Regel.put(w.getId(), vsr));
      vsr.sperrSignalIds.forEach(s -> this.signal2Regel.put(s, vsr));
      this.signal2Regel.put(vsr.vorsignalId, vsr);
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
    List<VorsignalRegel> vorsignalregeln = this.weiche2Regel.get(weiche.getId());
    this.logger.tracef("Change von %s", weiche);
    vorsignalregeln.forEach(VorsignalRegel::execute);
  }

  // Bei Signal-Änderung davon betroffene Regeln ausführen
  void signalChanged(@ObservesAsync @Changed Signal signal) {
    List<VorsignalRegel> vorsignalregeln = this.signal2Regel.get(signal.getId());
    this.logger.tracef("Change von %s", signal);
    vorsignalregeln.forEach(VorsignalRegel::execute);
  }

  /**
   * Vorsignalregel aus Fahrstraße ableiten
   *
   * @param fahrstrasse Fahrstraße
   * @return Vorsignalregel oder <code>null</code>, falls Fahrstraße ab dem zweiten Abschnitt nicht nur Sperrsignale enthält
   */
  private VorsignalRegel createVorsignalRegel(Fahrstrasse fahrstrasse) {
    VorsignalRegel vorsignalRegel = new VorsignalRegel();
    vorsignalRegel.id = fahrstrasse.getId();

    // Alle Weichen mit ihren Stellungen übernehmen,
    // alle Signale ab dem zweiten Abschnitt übernehmen (können nur Sperrsignale sein)
    int gleisCount = 0;
    for (Fahrstrassenelement e : fahrstrasse.getElemente()) {
      if (e instanceof FahrstrassenGleis) {
        ++gleisCount;
      } else if (!e.isSchutz()) {
        if (e instanceof FahrstrassenWeiche) {
          vorsignalRegel.fahrstrassenWeichen.add((FahrstrassenWeiche) e);
        } else if (e instanceof FahrstrassenSignal && gleisCount > 1) {
          if (FahrstrassenSignal.SignalTyp.fromName(e.getName()) != SignalTyp.SPERRSIGNAL) {
            return null;
          }
          vorsignalRegel.sperrSignalIds.add(e.getId());
        }
      }
    }

    // Ziel- und Startsignal übernehmen
    if (fahrstrasse.getZielHauptsignalName() != null) {
      vorsignalRegel.zielHauptsignalId = new BereichselementId(fahrstrasse.getZielHauptsignalBereich(), fahrstrasse.getZielHauptsignalName());
    }

    vorsignalRegel.vorsignalId = new BereichselementId(fahrstrasse.getStartVorsignalBereich(), fahrstrasse.getStartVorsignalName());

    return vorsignalRegel;
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
   */
  private class VorsignalRegel {
    private BereichselementId id;
    private Collection<FahrstrassenWeiche> fahrstrassenWeichen = new ArrayList<>();
    private List<BereichselementId> sperrSignalIds = new ArrayList<>();
    private BereichselementId zielHauptsignalId;
    private BereichselementId vorsignalId;

    @Override
    public String toString() {
      return "VorsignalRegel{"
        + this.vorsignalId
        + " -> " + this.zielHauptsignalId
        + ", id=" + this.id
        + ", weichen=" + this.fahrstrassenWeichen.stream().map(x -> x.getId() + ":" + x.getStellung()).collect(Collectors.joining(","))
        + ", signale=" + this.sperrSignalIds.stream().map(x -> x.toString()).collect(Collectors.joining(","))
        + "}";
    }

    // Regel ausführen.
    private void execute() {
      if (!weichestellungenPassen()) {
        VorsignalService.this.logger.tracef("  kein Match: %s", this);
        return;
      }

      SignalStellung vorsignalStellung = getVorsignalStellung();

      VorsignalService.this.signalRepository
        .findById(this.vorsignalId)
        .ifPresentOrElse(vorsignal -> {
            if (vorsignal.getStellung() == vorsignalStellung) {
              VorsignalService.this.logger.tracef("  bereits richtige Stellung: %s", this);
              return;
            }

            VorsignalService.this.logger.debugf("Vorsignal %s auf %s stellen", this.vorsignalId, vorsignalStellung);
            VorsignalService.this.logger.tracef("  wegen %s", this.toString());

            try {
              VorsignalService.this.statusGateway.signalStellen(this.vorsignalId.getBereich(), this.vorsignalId.getName(), vorsignalStellung);
            } catch (Exception e) {
              VorsignalService.this.logger.errorf(e, "Kann Vorsignal %s nicht auf %s stellen", this.vorsignalId, vorsignalStellung);
            }
          },
          () -> VorsignalService.this.logger.tracef("  kein Vorsignal: %s", this));
    }

    // Prüfen, ob alle Weichen in den gewünschten Stellungen stehen
    private boolean weichestellungenPassen() {
      for (FahrstrassenWeiche fahrstrassenWeiche : this.fahrstrassenWeichen) {
        Weiche weiche = VorsignalService.this.weicheRepository.findById(fahrstrassenWeiche.getId()).get();
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
        Signal sperrSignal = VorsignalService.this.signalRepository.findById(sperrSignalId).get();
        if (sperrSignal.getStellung() == SignalStellung.HALT) {
          return SignalStellung.HALT;
        }
      }

      // Falls Fahrweg ohne Ziel-Signal endet, HALT
      if (this.zielHauptsignalId == null) {
        return SignalStellung.HALT;
      }

      // Sonst Stellung des Ziel-Hauptsignals (allerdings HALT bei RANGIERFAHRT)
      Signal zielHauptsignal = VorsignalService.this.signalRepository.findById(this.zielHauptsignalId).get();
      SignalStellung vorsignalStellung = zielHauptsignal.getStellung();
      if (vorsignalStellung == SignalStellung.RANGIERFAHRT) {
        vorsignalStellung = SignalStellung.HALT;
      }
      return vorsignalStellung;
    }
  }

}
