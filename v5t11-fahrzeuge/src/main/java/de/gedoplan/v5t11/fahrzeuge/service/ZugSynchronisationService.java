package de.gedoplan.v5t11.fahrzeuge.service;

import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.Fahrzeug;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.Fahrzeugdecoder;
import de.gedoplan.v5t11.fahrzeuge.gateway.StatusGateway;
import de.gedoplan.v5t11.util.cdi.Changed;
import de.gedoplan.v5t11.util.domain.attribute.DecoderAdr;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.TransactionPhase;
import jakarta.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

/**
 * Synchronisation der Decoderattribute aktiv, rueckwaerts und licht innerhalb eines Zuges.
 * <p>
 * Ändert sich der Status eines Fahrzeugs (egal ob durch eine Aktion in diesem Service oder durch eine eingehende
 * Meldung), werden die Decoder der anderen Fahrzeuge des Zuges mit abweichender Adresse über v5t11-status
 * nachgezogen. Die Rückmeldung kommt wieder als Meldung zurück; die Synchronisation endet, sobald keine Abweichung
 * mehr besteht.
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

    Fahrzeug zugFahrzeug = fahrzeug.getZugFahrzeug() != null ? fahrzeug.getZugFahrzeug() : fahrzeug;

    Set<Fahrzeug> zug = new LinkedHashSet<>();
    zug.add(zugFahrzeug);
    zug.addAll(zugFahrzeug.getGezogeneFahrzeuge());
    if (zug.size() < 2) {
      return;
    }

    // Decoder mit gleicher Adresse zählen als einer: pro Adresse nur ein Aufruf
    Map<DecoderAdr, Fahrzeugdecoder> targets = new LinkedHashMap<>();
    for (Fahrzeug member : zug) {
      Fahrzeugdecoder decoder = member.getFahrzeugdecoder();
      if (decoder == null || decoder.getDecoderAdr() == null || decoder.getDecoderAdr().equals(sourceAdr)) {
        continue;
      }
      if (differs(decoder, source)) {
        targets.putIfAbsent(decoder.getDecoderAdr(), decoder);
      }
    }

    targets.forEach((adr, decoder) -> {
      Boolean aktiv = decoder.isAktiv() != source.isAktiv() ? source.isAktiv() : null;
      Boolean licht = decoder.isLicht() != source.isLicht() ? source.isLicht() : null;
      Boolean rueckwaerts = decoder.isRueckwaerts() != source.isRueckwaerts() ? source.isRueckwaerts() : null;
      try {
        this.statusGateway.changeFahrzeugdecoder(adr, aktiv, null, null, licht, rueckwaerts);
      } catch (Exception e) {
        this.logger.warnf(e, "Zugsynchronisation für Decoder %s fehlgeschlagen", adr);
      }
    });
  }

  private static boolean differs(Fahrzeugdecoder a, Fahrzeugdecoder b) {
    return a.isAktiv() != b.isAktiv() || a.isLicht() != b.isLicht() || a.isRueckwaerts() != b.isRueckwaerts();
  }
}
