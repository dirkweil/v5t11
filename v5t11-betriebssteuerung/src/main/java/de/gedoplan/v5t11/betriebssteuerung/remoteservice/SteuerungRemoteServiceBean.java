package de.gedoplan.v5t11.betriebssteuerung.remoteservice;

import de.gedoplan.v5t11.betriebssteuerung.background.FahrstrassenSteuerung;
import de.gedoplan.v5t11.betriebssteuerung.background.LokControllerSteuerung;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.Steuerung;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrstrasse.Fahrstrasse.ReservierungsTyp;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg.geraet.Signal;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg.geraet.Weiche;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg.geraet.Weiche.Stellung;

import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
public class SteuerungRemoteServiceBean implements SteuerungRemoteService
{
  @Inject
  Steuerung              steuerung;

  @Inject
  FahrstrassenSteuerung  fahrstrassenSteuerung;

  @Inject
  LokControllerSteuerung lokControllerSteuerung;

  @Override
  public Steuerung getSteuerung()
  {
    return this.steuerung;
  }

  @Override
  public void setSignalStellung(String bereich, String name, Signal.Stellung stellung)
  {
    Signal signal = this.steuerung.getSignal(bereich, name);
    if (signal != null)
    {
      signal.setStellung(stellung);
    }
  }

  @Override
  public void setWeicheStellung(String bereich, String name, Stellung stellung)
  {
    Weiche weiche = this.steuerung.getWeiche(bereich, name);
    if (weiche != null)
    {
      weiche.setStellung(stellung);
    }
  }

  @Override
  public void setFahrstrasseReserviert(String bereich, String name, ReservierungsTyp reservierungsTyp)
  {
    this.fahrstrassenSteuerung.reserviereFahrstrasse(bereich, name, reservierungsTyp);
  }

  @Override
  public void setZentraleAktiv(boolean on)
  {
    this.steuerung.getZentrale().setAktiv(on);

  }

  @Override
  public void assignLokController(String lokControllerId, int lokAdresse)
  {
    this.lokControllerSteuerung.assignLokController(lokControllerId, lokAdresse);
  }
}
