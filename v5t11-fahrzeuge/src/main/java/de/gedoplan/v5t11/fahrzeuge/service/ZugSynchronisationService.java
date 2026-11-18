package de.gedoplan.v5t11.fahrzeuge.service;

import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.Fahrzeug;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.Fahrzeugdecoder;
import de.gedoplan.v5t11.fahrzeuge.gateway.StatusGateway;
import de.gedoplan.v5t11.util.cdi.Changed;
import de.gedoplan.v5t11.util.domain.attribute.DecoderAdr;

import java.util.LinkedHashMap;
import java.util.Map;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.TransactionPhase;
import jakarta.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

/**
 * Synchronisation der Decoderattribute aktiv, rueckwaerts und licht vom Zugfahrzeug zu den gezogenen Fahrzeugen.
 * <p>
 * Ändert sich der Status eines Zugfahrzeugs (egal ob durch eine Aktion in diesem Service oder durch eine eingehende
 * Meldung), werden die Decoder der gezogenen Fahrzeuge mit abweichender Adresse über v5t11-status nachgezogen.
 * Änderungen gezogener Fahrzeuge werden nicht weitergegeben. Rückwärts wird anhand von
 * {@link Fahrzeug#isZugGegenlaeufig()} umgerechnet.
 */
@ApplicationScoped
public class ZugSynchronisationService {

  @Inject
  @RestClient
  StatusGateway statusGateway;

  @Inject
  Logger logger;

  void fahrzeugChanged(@Observes(during = TransactionPhase.AFTER_SUCCESS) @Changed Fahrzeug fahrzeug) {
    if (fahrzeug.isRemoved() || fahrzeug.getFahrzeugdecoder() == null) {
      return;
    }

    Fahrzeugdecoder source = fahrzeug.getFahrzeugdecoder();
    DecoderAdr sourceAdr = source.getDecoderAdr();
    if (sourceAdr == null) {
      return;
    }

    // Synchronisation nur vom Zugfahrzeug zu den gezogenen Fahrzeugen. Änderungen an gezogenen Fahrzeugen werden
    // nicht weitergegeben; so kann es keine Rückkopplung geben (auch nicht bei gleicher Decoderadresse mehrerer
    // gezogener Fahrzeuge mit unterschiedlicher Ausrichtung).
    if (fahrzeug.getZugFahrzeug() != null || fahrzeug.getGezogeneFahrzeuge().isEmpty()) {
      return;
    }

    // Decoder mit gleicher Adresse zählen als einer: pro Adresse nur ein Aufruf. Maßgeblich ist das letzte Fahrzeug
    // des Zuges mit dieser Adresse (bei inkonsistenter Ausrichtung soll "rückwärts" zu diesem passen).
    Map<DecoderAdr, Fahrzeug> targets = new LinkedHashMap<>();
    for (Fahrzeug member : fahrzeug.getGezogeneFahrzeuge()) {
      Fahrzeugdecoder decoder = member.getFahrzeugdecoder();
      if (decoder == null || decoder.getDecoderAdr() == null || decoder.getDecoderAdr().equals(sourceAdr)) {
        continue;
      }
      targets.put(decoder.getDecoderAdr(), member);
    }
    targets.values().removeIf(member -> !differs(member, fahrzeug));

    targets.forEach((adr, member) -> {
      Fahrzeugdecoder decoder = member.getFahrzeugdecoder();
      Boolean aktiv = decoder.isAktiv() != source.isAktiv() ? source.isAktiv() : null;
      Boolean licht = decoder.isLicht() != source.isLicht() ? source.isLicht() : null;
      boolean sollRueckwaerts = sollRueckwaerts(member, fahrzeug);
      Boolean rueckwaerts = decoder.isRueckwaerts() != sollRueckwaerts ? sollRueckwaerts : null;
      try {
        this.statusGateway.changeFahrzeugdecoder(adr, aktiv, null, null, licht, rueckwaerts);
      } catch (Exception e) {
        this.logger.warnf(e, "Zugsynchronisation für Decoder %s fehlgeschlagen", adr);
      }
    });
  }

  /**
   * Sollwert für "rückwärts" des Zielfahrzeugs: Quellwert, ggf. invertiert, wenn Quelle und Ziel im Zug
   * unterschiedlich ausgerichtet sind.
   */
  private static boolean sollRueckwaerts(Fahrzeug target, Fahrzeug source) {
    boolean gegenlaeufig = target.isZugGegenlaeufig() != source.isZugGegenlaeufig();
    return source.getFahrzeugdecoder().isRueckwaerts() != gegenlaeufig;
  }

  private static boolean differs(Fahrzeug target, Fahrzeug source) {
    Fahrzeugdecoder a = target.getFahrzeugdecoder();
    Fahrzeugdecoder b = source.getFahrzeugdecoder();
    return a.isAktiv() != b.isAktiv() || a.isLicht() != b.isLicht() || a.isRueckwaerts() != sollRueckwaerts(target, source);
  }
}
