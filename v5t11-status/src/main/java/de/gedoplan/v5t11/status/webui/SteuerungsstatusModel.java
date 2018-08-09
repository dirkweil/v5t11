package de.gedoplan.v5t11.status.webui;

import de.gedoplan.v5t11.status.entity.Steuerung;
import de.gedoplan.v5t11.status.entity.baustein.Zentrale;
import de.gedoplan.v5t11.status.entity.fahrweg.Gleisabschnitt;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Signal;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Weiche;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.inject.Model;
import javax.inject.Inject;

@Model
public class SteuerungsstatusModel implements Serializable {
  @Inject
  private Steuerung steuerung;

  public Zentrale getZentrale() {
    return this.steuerung.getZentrale();
  }

  // public List<Lok> getLoks() {
  // return new ArrayList<>(this.steuerung.getLoks());
  // }

  public List<String> getBereiche() {
    return new ArrayList<>(this.steuerung.getBereiche());
  }

  public List<Weiche> getWeichen(String bereich) {
    return new ArrayList<>(this.steuerung.getWeichen(bereich));
  }

  public List<Signal> getSignale(String bereich) {
    return new ArrayList<>(this.steuerung.getSignale(bereich));
  }

  public List<Gleisabschnitt> getGleisabschnitte(String bereich) {
    return new ArrayList<>(this.steuerung.getGleisabschnitte(bereich));
  }

}
