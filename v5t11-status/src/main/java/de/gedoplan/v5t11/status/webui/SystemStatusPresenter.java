package de.gedoplan.v5t11.status.webui;

import de.gedoplan.v5t11.status.entity.Steuerung;
import de.gedoplan.v5t11.status.entity.baustein.Zentrale;
import de.gedoplan.v5t11.status.entity.fahrweg.Gleis;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Signal;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Weiche;
import de.gedoplan.v5t11.status.entity.fahrzeug.Fahrzeug;

import java.io.Serializable;
import java.util.Collection;

import jakarta.enterprise.inject.Model;
import jakarta.inject.Inject;

@Model
public class SystemStatusPresenter implements Serializable {
  @Inject
  Steuerung steuerung;

  public Zentrale getZentrale() {
    return this.steuerung.getZentrale();
  }

  public Collection<Fahrzeug> getLoks() {
    return this.steuerung.getFahrzeuge();
  }

  public Collection<String> getBereiche() {
    return this.steuerung.getBereiche();
  }

  public Collection<Weiche> getWeichen(String bereich) {
    return this.steuerung.getWeichen(bereich);
  }

  public Collection<Signal> getSignale(String bereich) {
    return this.steuerung.getSignale(bereich);
  }

  public Collection<Gleis> getGleise(String bereich) {
    return this.steuerung.getGleise(bereich);
  }

}
