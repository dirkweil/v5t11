package de.gedoplan.v5t11.fahrzeuge.webui;

import de.gedoplan.v5t11.fahrzeuge.entity.fahrweg.Gleis;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.Fahrzeug;
import de.gedoplan.v5t11.fahrzeuge.service.ParcoursService;
import de.gedoplan.v5t11.util.domain.attribute.BereichselementId;

import java.io.Serializable;
import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.ConverterException;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import lombok.Getter;

@Named
@ViewScoped
public class FahrzeugPositionPresenter implements Serializable {

  @Inject
  FahrzeugListPresenter fahrzeugListPresenter;

  @Inject
  ParcoursService parcoursService;

  @Getter
  private List<BereichselementId> gleisIds;

  @PostConstruct
  void init() {
    this.gleisIds = this.parcoursService.getGleise().stream().map(Gleis::getId).toList();
  }

  public Fahrzeug getCurrentFahrzeug() {
    return this.fahrzeugListPresenter.getCurrentFahrzeug();
  }

  public String saveCurrentFahrzeug() {
    return this.fahrzeugListPresenter.saveCurrentFahrzeug();
  }

  public static class BereichselementIdConverter implements Converter<BereichselementId> {

    @Override
    public BereichselementId getAsObject(FacesContext context, UIComponent component, String value) throws ConverterException {
      return value == null ? null : BereichselementId.fromString(value);
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, BereichselementId value) throws ConverterException {
      return value == null ? null : value.toString();
    }
  }

  @Getter
  private BereichselementIdConverter bereichselementIdConverter = new BereichselementIdConverter();

}
