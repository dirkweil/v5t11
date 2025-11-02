package de.gedoplan.v5t11.fahrzeuge.webui;

import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.Fahrzeug;
import de.gedoplan.v5t11.fahrzeuge.persistence.FahrzeugRepository;

import java.io.Serializable;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.primefaces.model.DualListModel;

import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
public class FahrzeugTraktionPresenter implements Serializable {

  @Inject
  FahrzeugListPresenter fahrzeugListPresenter;

  @Inject
  FahrzeugRepository fahrzeugRepository;

  @Getter
  @Setter
  private DualListModel<String> fahrzeugPicker;

  @PostConstruct
  void init() {
    this.fahrzeugPicker = new DualListModel<>(
      this.fahrzeugRepository
        .findOhneZugFahrzeugSortedByBetriebsnummer()
        .stream()
        .map(Fahrzeug::getBetriebsnummer)
        .collect(Collectors.toList()),
      getCurrentFahrzeug()
        .getGezogeneFahrzeuge()
        .stream()
        .map(Fahrzeug::getBetriebsnummer)
        .collect(Collectors.toList()));
  }

  public Fahrzeug getCurrentFahrzeug() {
    return this.fahrzeugListPresenter.getCurrentFahrzeug();
  }

  public String saveCurrentFahrzeug() {
    getCurrentFahrzeug().setGezogeneFahrzeuge(this.fahrzeugPicker
      .getTarget()
      .stream()
      .peek(System.out::println)
      .map(bn -> this.fahrzeugRepository.findById(bn))
      .flatMap(Optional::stream)
      .peek(System.out::println)
      .collect(Collectors.toList()));
    return this.fahrzeugListPresenter.saveCurrentFahrzeug();
  }

}
