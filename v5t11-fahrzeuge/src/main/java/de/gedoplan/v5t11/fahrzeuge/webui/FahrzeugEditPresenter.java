package de.gedoplan.v5t11.fahrzeuge.webui;

import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.Fahrzeug;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.FahrzeugFunktion;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe;
import de.gedoplan.v5t11.fahrzeuge.persistence.FahrzeugRepository;
import de.gedoplan.v5t11.util.cdi.Current;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Set;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import org.jboss.logging.Logger;

import lombok.Getter;

@Named
@ViewScoped
public class FahrzeugEditPresenter implements Serializable {

  @Inject
  @Current
  @Getter
  Fahrzeug currentFahrzeug;

  @Inject
  FahrzeugRepository fahrzeugRepository;

  @Inject
  Logger log;

  @Inject
  Validator validator;

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

}
