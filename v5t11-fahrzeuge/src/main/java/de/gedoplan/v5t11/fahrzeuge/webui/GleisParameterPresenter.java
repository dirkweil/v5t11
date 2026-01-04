package de.gedoplan.v5t11.fahrzeuge.webui;

import de.gedoplan.v5t11.fahrzeuge.entity.fahrweg.Gleis;
import de.gedoplan.v5t11.fahrzeuge.service.ParcoursService;

import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.primefaces.event.RowEditEvent;

import lombok.Getter;

@ViewScoped
@Named
public class GleisParameterPresenter {

  @Inject
  ParcoursService parcoursService;

  @Getter
  private List<Gleis> gleise;

  @PostConstruct
  void init() {
    this.gleise = new ArrayList<>(this.parcoursService.getGleise());
  }

  public void onRowEdit(RowEditEvent<Gleis> event) {
    this.parcoursService.saveGleis(event.getObject().getId());
  }
}
