package de.gedoplan.v5t11.betriebssteuerung.remoteservice;

import de.gedoplan.v5t11.betriebssteuerung.steuerung.Steuerung;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrstrasse.Fahrstrasse.ReservierungsTyp;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg.geraet.Signal;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg.geraet.Weiche;

import javax.ejb.Remote;

@Remote
public interface SteuerungRemoteService
{
  public Steuerung getSteuerung();

  public void setSignalStellung(String bereich, String name, Signal.Stellung stellung);

  public void setWeicheStellung(String bereich, String name, Weiche.Stellung stellung);

  public void setFahrstrasseReserviert(String bereich, String name, ReservierungsTyp reservierungsTyp);

  public void setZentraleAktiv(boolean on);

  public void assignLokController(String lokControllerId, int lokAdresse);
}
