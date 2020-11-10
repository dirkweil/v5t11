package de.gedoplan.v5t11.fahrzeuge.webui;

import de.gedoplan.baselibs.utils.util.ResourceUtil;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.Fahrzeug;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.Fahrzeug.FahrzeugFunktion;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe;
import de.gedoplan.v5t11.fahrzeuge.persistence.FahrzeugRepository;
import de.gedoplan.v5t11.util.domain.attribute.SystemTyp;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.constraints.NotEmpty;

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

  @Getter
  @Setter
  @NotEmpty
  private String newId;

  @PostConstruct
  void refreshFahrzeuge() {
    this.fahrzeuge = this.fahrzeugRepository.findAll();
  }

  public String create() {
    this.currentFahrzeug = new Fahrzeug(this.newId, null, SystemTyp.DCC, 0);
    this.fahrzeuge.add(this.currentFahrzeug);

    if (this.log.isDebugEnabled()) {
      this.log.debug("Create " + this.currentFahrzeug);
    }

    return "edit";
  }

  public String edit(Fahrzeug fahrzeug) {
    this.currentFahrzeug = fahrzeug;

    if (this.log.isDebugEnabled()) {
      this.log.debug("Edit " + this.currentFahrzeug);
    }

    return "edit";
  }

  public void addFunktion() {
    this.currentFahrzeug.getFunktionen().add(0, new FahrzeugFunktion(FahrzeugFunktionsGruppe.AF, 0, 0, false, false, false, ""));
  }

  public void removeFunktion(FahrzeugFunktion funktion) {
    Iterator<FahrzeugFunktion> iterator = this.currentFahrzeug.getFunktionen().iterator();
    while (iterator.hasNext()) {
      if (iterator.next() == funktion) {
        iterator.remove();
        break;
      }
    }
  }

  @Inject
  Validator validator;

  public String save() {
    Set<ConstraintViolation<Fahrzeug>> violations = this.validator.validate(this.currentFahrzeug);
    if (!violations.isEmpty()) {
      FacesContext facesContext = FacesContext.getCurrentInstance();
      violations.forEach(cv -> {
        FacesMessage facesMessage = new FacesMessage(cv.getMessage());
        facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
        facesContext.addMessage(null, facesMessage);
      });
      facesContext.validationFailed();
      return null;
    }

    Iterator<FahrzeugFunktion> iterator = this.currentFahrzeug.getFunktionen().iterator();
    while (iterator.hasNext()) {
      FahrzeugFunktion funktion = iterator.next();
      if (funktion.getMaske() == 0 || funktion.getBeschreibung() == null || funktion.getBeschreibung().strip().isEmpty()) {
        iterator.remove();
      }
    }
    this.fahrzeugRepository.merge(this.currentFahrzeug);

    return "finished";

  }

  public String remove() {
    // TODO Confirmation!

    this.fahrzeugRepository.removeById(this.currentFahrzeug.getId());
    refreshFahrzeuge();

    return "finished";
  }

  public String cancel() {
    refreshFahrzeuge();

    return "finished";
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

  public String control(Fahrzeug fahrzeug) {
    this.currentFahrzeug = fahrzeug;
    return "control";
  }

  @Getter
  @Setter
  boolean lokAktiv;

  @Getter
  @Setter
  boolean lokRueckwaerts;

  @Getter
  @Setter
  int lokFahrstufe;

  public int getLokMaxFahrstufe() {
    return this.currentFahrzeug.getSystemTyp().getMaxFahrstufe();
  }

  public List<FahrzeugFunktion> getLokFunktionen(FahrzeugFunktionsGruppe fahrzeugFunktionsGruppe) {
    return this.currentFahrzeug
        .getFunktionen()
        .stream()
        .filter(f -> f.getGruppe() == fahrzeugFunktionsGruppe)
        .collect(Collectors.toList());
  }

  @Getter
  @Setter
  boolean funktionAktiv;

}
