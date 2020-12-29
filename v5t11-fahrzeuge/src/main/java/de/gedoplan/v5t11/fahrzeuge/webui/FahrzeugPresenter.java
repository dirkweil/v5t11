package de.gedoplan.v5t11.fahrzeuge.webui;

import de.gedoplan.baselibs.utils.util.ResourceUtil;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.Fahrzeug;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe;
import de.gedoplan.v5t11.fahrzeuge.persistence.FahrzeugRepository;
import de.gedoplan.v5t11.util.cdi.Current;
import de.gedoplan.v5t11.util.domain.attribute.FahrzeugId;
import de.gedoplan.v5t11.util.domain.attribute.SystemTyp;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.constraints.NotNull;

import org.jboss.logging.Logger;

import lombok.Getter;

@Named
@SessionScoped
public class FahrzeugPresenter implements Serializable {

  @Inject
  FahrzeugRepository fahrzeugRepository;

  @Inject
  Logger logger;

  @Getter
  private List<Fahrzeug> fahrzeuge;

  @Getter
  @Produces
  @Current
  private Fahrzeug currentFahrzeug;

  @Getter
  @NotNull
  private FahrzeugId newId = new FahrzeugId(SystemTyp.DCC, 3);

  @PostConstruct
  void refreshFahrzeuge() {
    this.fahrzeuge = this.fahrzeugRepository.findAll();
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
    this.currentFahrzeug = new Fahrzeug(this.newId, null, null);
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

}
