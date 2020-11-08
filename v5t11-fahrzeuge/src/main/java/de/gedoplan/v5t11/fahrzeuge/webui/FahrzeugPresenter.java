package de.gedoplan.v5t11.fahrzeuge.webui;

import de.gedoplan.baselibs.utils.util.ResourceUtil;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.Fahrzeug;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.Fahrzeug.FahrzeugFunktion;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe;
import de.gedoplan.v5t11.fahrzeuge.persistence.FahrzeugRepository;
import de.gedoplan.v5t11.util.domain.attribute.SystemTyp;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.logging.Log;

import lombok.Getter;
import lombok.Setter;

@Named
@SessionScoped
public class FahrzeugPresenter implements Serializable {

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

    return "edit";
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

  public SystemTyp[] getSystemTypen() {
    return SystemTyp.values();
  }

  public FahrzeugFunktionsGruppe[] getFunktionsGruppen() {
    return FahrzeugFunktionsGruppe.values();
  }

  public TriStateAdapter getTriStateAdapter(FahrzeugFunktion fahrzeugFunktion, int bitNr) {
    return new TriStateAdapter(fahrzeugFunktion, bitNr);
  }

  public static class TriStateAdapter {

    private FahrzeugFunktion fahrzeugFunktion;
    private int maske;

    public TriStateAdapter(FahrzeugFunktion fahrzeugFunktion, int bitNr) {
      this.fahrzeugFunktion = fahrzeugFunktion;
      this.maske = (1 << bitNr);

      System.out.println("##### " + bitNr);
    }

    public String getValue() {
      if ((this.fahrzeugFunktion.getMaske() & this.maske) == 0) {
        return "0";
      }
      if ((this.fahrzeugFunktion.getWert() & this.maske) == 0) {
        return "2";
      }
      return "1";
    }

    public void setValue(String s) {
      switch (s) {
      default:
      case "0":
        this.fahrzeugFunktion.setMaske(this.fahrzeugFunktion.getMaske() & (~this.maske));
        this.fahrzeugFunktion.setWert(this.fahrzeugFunktion.getWert() & (~this.maske));
        break;

      case "1":
        this.fahrzeugFunktion.setMaske(this.fahrzeugFunktion.getMaske() | this.maske);
        this.fahrzeugFunktion.setWert(this.fahrzeugFunktion.getWert() | this.maske);
        break;

      case "2":
        this.fahrzeugFunktion.setMaske(this.fahrzeugFunktion.getMaske() | this.maske);
        this.fahrzeugFunktion.setWert(this.fahrzeugFunktion.getWert() & (~this.maske));
        break;
      }
    }

  }
}
