package de.gedoplan.v5t11.fahrzeuge.webui;

import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.Fahrzeug;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.FahrzeugKonfiguration;
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
public class FahrzeugProgramPresenter implements Serializable {

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
    } else {
      this.fahrzeugRepository.merge(this.currentFahrzeug);
    }

    return null;

  }

  public void addKonfiguration() {
    this.currentFahrzeug.getKonfigurationen().add(0, new FahrzeugKonfiguration(null, null, null));
  }

  public void removeKonfiguration(FahrzeugKonfiguration konfiguration) {
    Iterator<FahrzeugKonfiguration> iterator = this.currentFahrzeug.getKonfigurationen().iterator();
    while (iterator.hasNext()) {
      if (iterator.next() == konfiguration) {
        iterator.remove();
        break;
      }
    }
  }

}
