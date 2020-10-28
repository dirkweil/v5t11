package de.gedoplan.v5t11.fahrzeuge.webui;

import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.Fahrzeug;
import de.gedoplan.v5t11.fahrzeuge.persistence.FahrzeugRepository;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import lombok.Getter;

@Named
@RequestScoped
public class FahrzeugPresenter {

  @Inject
  FahrzeugRepository fahrzeugRepository;

  @Getter
  private List<Fahrzeug> fahrzeuge;

  @PostConstruct
  void postConstruct() {
    this.fahrzeuge = this.fahrzeugRepository.findAll();
  }
}
