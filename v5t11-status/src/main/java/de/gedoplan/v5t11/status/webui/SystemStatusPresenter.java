package de.gedoplan.v5t11.status.webui;

import de.gedoplan.v5t11.status.entity.Steuerung;
import de.gedoplan.v5t11.status.entity.baustein.Zentrale;
import de.gedoplan.v5t11.status.entity.fahrweg.Gleisabschnitt;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Signal;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Weiche;
import de.gedoplan.v5t11.status.entity.lok.Lok;

import java.io.Serializable;
import java.util.Collection;

import javax.enterprise.inject.Model;
import javax.inject.Inject;

@Model
public class SystemStatusPresenter implements Serializable {
  @Inject
  private Steuerung steuerung;

  public Zentrale getZentrale() {
    return this.steuerung.getZentrale();
  }

  public Collection<Lok> getLoks() {
    return this.steuerung.getLoks();
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

  public Collection<Gleisabschnitt> getGleisabschnitte(String bereich) {
    return this.steuerung.getGleisabschnitte(bereich);
  }

}
