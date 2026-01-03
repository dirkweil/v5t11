package de.gedoplan.v5t11.fahrzeuge.webui;

import de.gedoplan.v5t11.fahrzeuge.entity.fahrweg.Gleis;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.Fahrzeug;
import de.gedoplan.v5t11.fahrzeuge.persistence.GleisRepository;
import de.gedoplan.v5t11.util.domain.attribute.BereichselementId;

import java.io.Serializable;
import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;

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
  GleisRepository gleisRepository;

  private SortedMap<BereichselementId, Gleis> gleise = new TreeMap<>();

  @PostConstruct
  void init() {
    this.gleise.clear();
    this.gleisRepository.findAll().forEach(g -> this.gleise.put(g.getId(), g));
  }

  public Fahrzeug getCurrentFahrzeug() {
    return this.fahrzeugListPresenter.getCurrentFahrzeug();
  }

  public String saveCurrentFahrzeug() {
    return this.fahrzeugListPresenter.saveCurrentFahrzeug();
  }

  public Collection<Gleis> getGleise() {
    return this.gleise.values();
  }

  public class GleisConverter implements Converter<Gleis> {

    @Override
    public Gleis getAsObject(FacesContext context, UIComponent component, String value) throws ConverterException {
      return value == null ? null : gleise.get(BereichselementId.fromString(value));
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Gleis value) throws ConverterException {
      return value == null ? null : value.getId().toString();
    }
  }

  @Getter
  private GleisConverter gleisConverter = new GleisConverter();

}
