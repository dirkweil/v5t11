package de.gedoplan.v5t11.fahrzeuge.webui;

import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.Fahrzeug;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.FahrzeugFunktion;
import de.gedoplan.v5t11.fahrzeuge.gateway.StatusGateway;
import de.gedoplan.v5t11.util.domain.attribute.FahrzeugId;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import lombok.AllArgsConstructor;

/**
 * Lokcontroller-Ansteuerung.
 * Achtung: Dies ist eine Quick&Dirty-Implementierung.
 * Es wird davon ausgegangen, dass es zwei Lokcontroller mit den Ids 0 und 1 gibt.
 * Mittelfristig werden die Lokcontroller durch selbstentwickelte mobile Geräte
 * ersetzt.
 */
@Named
@ApplicationScoped
public class LokControllerPresenter {

  @Inject
  @RestClient
  StatusGateway statusGateway;

  @Inject
  Logger logger;

  private Fahrzeug[] lokcontrollerAssignment = new Fahrzeug[2];

  public LokcontrollerAdapter getLokcontrollerAdapter(Fahrzeug fahrzeug, int lokcontrollerNr) {
    return new LokcontrollerAdapter(fahrzeug, lokcontrollerNr);
  }

  @AllArgsConstructor
  public class LokcontrollerAdapter {

    private Fahrzeug fahrzeug;
    private int lokcontrollerId;

    public boolean isAssigned() {
      return this.fahrzeug.equals(LokControllerPresenter.this.lokcontrollerAssignment[this.lokcontrollerId]);
    }

    public void setAssigned(boolean assigned) {
      if (assigned) {
        LokControllerPresenter.this.lokcontrollerAssignment[this.lokcontrollerId] = this.fahrzeug;
        assignLokcontroller(this.lokcontrollerId);

        int otherLokcontrollerIdx = 1 - this.lokcontrollerId;
        if (this.fahrzeug.equals(LokControllerPresenter.this.lokcontrollerAssignment[otherLokcontrollerIdx])) {
          LokControllerPresenter.this.lokcontrollerAssignment[otherLokcontrollerIdx] = null;
          assignLokcontroller(otherLokcontrollerIdx);
        }
      } else {
        if (this.fahrzeug.equals(LokControllerPresenter.this.lokcontrollerAssignment[this.lokcontrollerId])) {
          LokControllerPresenter.this.lokcontrollerAssignment[this.lokcontrollerId] = null;
          assignLokcontroller(this.lokcontrollerId);
        }
      }

    }

  }

  private void assignLokcontroller(int lokcontrollerId) {
    FahrzeugId fahrzeugId = null;
    int hornBits = 0;

    Fahrzeug fahrzeug = this.lokcontrollerAssignment[lokcontrollerId];
    if (fahrzeug != null) {
      fahrzeugId = fahrzeug.getId();
      for (FahrzeugFunktion f : fahrzeug.getFunktionen()) {
        if (f.isHorn()) {
          hornBits |= f.getWert();
        }
      }
    }

    logger.debugf("Lokcontroller %d -> %s", lokcontrollerId, fahrzeugId);
    this.statusGateway.setLokcontrollerAssignment(Integer.toString(lokcontrollerId), fahrzeugId, hornBits);

  }

}
