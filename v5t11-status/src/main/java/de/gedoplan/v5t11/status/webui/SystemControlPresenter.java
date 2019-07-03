package de.gedoplan.v5t11.status.webui;

import de.gedoplan.v5t11.status.entity.Steuerung;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

@Named
@SessionScoped
public class SystemControlPresenter implements Serializable {

  @Inject
  Steuerung steuerung;

  public boolean isGleisspannung() {
    return this.steuerung.getZentrale().isGleisspannung();
  }

  public void setGleisspannung(boolean on) {
    this.steuerung.getZentrale().setGleisspannung(on);
  }
}
