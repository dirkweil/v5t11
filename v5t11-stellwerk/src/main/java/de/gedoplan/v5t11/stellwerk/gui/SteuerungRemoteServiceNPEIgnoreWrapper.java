package de.gedoplan.v5t11.stellwerk.gui;

import de.gedoplan.v5t11.betriebssteuerung.remoteservice.SteuerungRemoteService;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.Steuerung;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrstrasse.Fahrstrasse.ReservierungsTyp;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg.geraet.Signal.Stellung;

import javax.ejb.EJBException;

public class SteuerungRemoteServiceNPEIgnoreWrapper implements SteuerungRemoteService
{

  private SteuerungRemoteService steuerungRemoteService;

  public SteuerungRemoteServiceNPEIgnoreWrapper(SteuerungRemoteService steuerungRemoteService)
  {
    this.steuerungRemoteService = steuerungRemoteService;
  }

  @Override
  public Steuerung getSteuerung()
  {
    return this.steuerungRemoteService.getSteuerung();
  }

  @Override
  public void setSignalStellung(String bereich, String name, Stellung stellung)
  {
    try
    {
      this.steuerungRemoteService.setSignalStellung(bereich, name, stellung);
    }
    catch (EJBException exception)
    {
      if (!"java.lang.NullPointerException".equals(exception.getMessage()))
      {
        throw exception;
      }
    }
  }

  @Override
  public void setWeicheStellung(String bereich, String name, de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg.geraet.Weiche.Stellung stellung)
  {
    try
    {
      this.steuerungRemoteService.setWeicheStellung(bereich, name, stellung);
    }
    catch (EJBException exception)
    {
      if (!"java.lang.NullPointerException".equals(exception.getMessage()))
      {
        throw exception;
      }
    }
  }

  @Override
  public void setFahrstrasseReserviert(String bereich, String name, ReservierungsTyp reservierungsTyp)
  {
    try
    {
      this.steuerungRemoteService.setFahrstrasseReserviert(bereich, name, reservierungsTyp);
    }
    catch (EJBException exception)
    {
      if (!"java.lang.NullPointerException".equals(exception.getMessage()))
      {
        throw exception;
      }
    }
  }

  @Override
  public void setZentraleAktiv(boolean on)
  {
    try
    {
      this.steuerungRemoteService.setZentraleAktiv(on);
    }
    catch (EJBException exception)
    {
      if (!"java.lang.NullPointerException".equals(exception.getMessage()))
      {
        throw exception;
      }
    }
  }

  @Override
  public void assignLokController(String lokControllerId, int lokAdresse)
  {
    try
    {
      this.steuerungRemoteService.assignLokController(lokControllerId, lokAdresse);
    }
    catch (EJBException exception)
    {
      if (!"java.lang.NullPointerException".equals(exception.getMessage()))
      {
        throw exception;
      }
    }

  }

}
