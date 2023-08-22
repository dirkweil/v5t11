package de.gedoplan.v5t11.fahrzeuge.webui;

import de.gedoplan.baselibs.utils.util.ResourceUtil;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.Fahrzeug;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.Fahrzeug.FahrzeugFunktion;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe;
import de.gedoplan.v5t11.fahrzeuge.gateway.StatusGateway;
import de.gedoplan.v5t11.fahrzeuge.persistence.FahrzeugRepository;
import de.gedoplan.v5t11.util.cdi.Current;
import de.gedoplan.v5t11.util.domain.attribute.FahrzeugId;
import de.gedoplan.v5t11.util.domain.attribute.SystemTyp;

import java.io.Serializable;
import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.validation.constraints.NotNull;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Named
@SessionScoped
public class FahrzeugPresenter implements Serializable {

  @Inject
  FahrzeugRepository fahrzeugRepository;

  @Inject
  @RestClient
  StatusGateway statusGateway;

  @Inject
  Logger logger;

  @Getter
  private List<Fahrzeug> fahrzeuge;

  private Fahrzeug currentFahrzeug;

  @Getter
  @NotNull
  private FahrzeugId newId = new FahrzeugId(SystemTyp.DCC, 3);

  /*
   * Achtung: Die Lokcontroller-Ansteuerung ist eine Quick&Dirty-Implementierung.
   * Es wird davon ausgegangen, dass es zwei Lokcontroller mit den Ids 0 und 1 gibt.
   * Mittelfristig werden die Lokcontroller durch selbstentwickelte mobile Geräte
   * ersetzt.
   */
  private Fahrzeug[] lokcontrollerAssignment = new Fahrzeug[2];

  @PostConstruct
  void refreshFahrzeuge() {
    this.fahrzeuge = this.fahrzeugRepository.findAllSortedByBetriebsnummer();
  }

  @Produces
  @Current
  public Fahrzeug getCurrentFahrzeug() {
    return this.currentFahrzeug;
  }

  public String getImage(Fahrzeug fahrzeug) {
    if (fahrzeug.getImage() != null) {
      throw new UnsupportedOperationException("not yet implemented");
    }

    if (fahrzeug.getBetriebsnummer() != null) {
      String name = fahrzeug.getBetriebsnummer().replaceAll("\\s+", "_");
      while (!name.isEmpty()) {
        String resourceName = "images/loks/" + name + ".png";
        if (ResourceUtil.getResource("META-INF/resources/" + resourceName) != null) {
          return resourceName;
        }

        name = name.substring(0, name.length() - 1);
      }
    }

    return "images/loks/none.png";

  }

  public SystemTyp[] getSystemTypen() {
    return SystemTyp.values();
  }

  public FahrzeugFunktionsGruppe[] getFunktionsGruppen() {
    return FahrzeugFunktionsGruppe.values();
  }

  public String create() {
    this.currentFahrzeug = new Fahrzeug(this.newId);
    this.fahrzeuge.add(this.currentFahrzeug);
    this.logger.debugf("Create %s", this.currentFahrzeug);
    return "edit";
  }

  public String edit(Fahrzeug fahrzeug) {
    this.currentFahrzeug = fahrzeug;
    this.logger.debugf("Edit %s", this.currentFahrzeug);
    return "edit";
  }

  public String program(Fahrzeug fahrzeug) {
    this.currentFahrzeug = fahrzeug;
    this.logger.debugf("Program %s", this.currentFahrzeug);
    return "program";
  }

  public String control(Fahrzeug fahrzeug) {
    this.currentFahrzeug = fahrzeug;
    this.logger.debugf("Control %s", this.currentFahrzeug);
    return "control";
  }

  public String remove() {
    this.fahrzeugRepository.removeById(this.currentFahrzeug.getId());
    refreshFahrzeuge();
    return "finished";
  }

  public String cancel() {
    refreshFahrzeuge();
    return "finished";
  }

  public LokcontrollerAdapter getLokcontrollerAdapter(Fahrzeug fahrzeug, int lokcontrollerNr) {
    return new LokcontrollerAdapter(fahrzeug, lokcontrollerNr);
  }

  @AllArgsConstructor
  public class LokcontrollerAdapter {

    private Fahrzeug fahrzeug;
    private int lokcontrollerId;

    public boolean isAssigned() {
      return this.fahrzeug.equals(FahrzeugPresenter.this.lokcontrollerAssignment[this.lokcontrollerId]);
    }

    public void setAssigned(boolean assigned) {
      if (assigned) {
        FahrzeugPresenter.this.lokcontrollerAssignment[this.lokcontrollerId] = this.fahrzeug;
        assignLokcontroller(this.lokcontrollerId);

        int otherLokcontrollerIdx = 1 - this.lokcontrollerId;
        if (this.fahrzeug.equals(FahrzeugPresenter.this.lokcontrollerAssignment[otherLokcontrollerIdx])) {
          FahrzeugPresenter.this.lokcontrollerAssignment[otherLokcontrollerIdx] = null;
          assignLokcontroller(otherLokcontrollerIdx);
        }
      } else {
        if (this.fahrzeug.equals(FahrzeugPresenter.this.lokcontrollerAssignment[this.lokcontrollerId])) {
          FahrzeugPresenter.this.lokcontrollerAssignment[this.lokcontrollerId] = null;
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

    this.statusGateway.setLokcontrollerAssignment(Integer.toString(lokcontrollerId), fahrzeugId, hornBits);

  }

}
