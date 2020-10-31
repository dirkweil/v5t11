package de.gedoplan.v5t11.fahrzeuge.webui;

import de.gedoplan.baselibs.utils.util.ResourceUtil;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.Fahrzeug;
import de.gedoplan.v5t11.fahrzeuge.persistence.FahrzeugRepository;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.logging.Log;

import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
public class FahrzeugPresenter {

  @Inject
  FahrzeugRepository fahrzeugRepository;

  @Inject
  Log log;

  @Getter
  private List<Fahrzeug> fahrzeuge;

  @Getter
  @Setter
  private Fahrzeug currentFahrzeug;

  @PostConstruct
  void postConstruct() {
    this.fahrzeuge = this.fahrzeugRepository.findAll();
  }

  public String edit(Fahrzeug fahrzeug) {
    this.currentFahrzeug = fahrzeug;

    if (this.log.isDebugEnabled()) {
      this.log.debug("Edit " + fahrzeug);
    }

    return null;
  }

  public String program(Fahrzeug fahrzeug) {
    this.currentFahrzeug = fahrzeug;

    if (this.log.isDebugEnabled()) {
      this.log.debug("Program " + fahrzeug);
    }

    return null;
  }

  public String getImage(Fahrzeug fahrzeug) {
    if (fahrzeug.getImage() != null) {
      throw new UnsupportedOperationException("not yet implemented");
    } else {
      String name = fahrzeug.getId().replaceAll("\\s+", "_");
      while (!name.isEmpty()) {
        String resourceName = "images/loks/" + name + ".png";
        if (ResourceUtil.getResource("META-INF/resources/" + resourceName) != null) {
          return resourceName;
        }

        name = name.substring(0, name.length() - 1);
      }

      return "images/loks/none.png";

    }
  }
}
