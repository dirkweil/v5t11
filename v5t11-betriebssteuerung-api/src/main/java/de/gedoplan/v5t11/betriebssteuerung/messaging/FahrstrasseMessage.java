package de.gedoplan.v5t11.betriebssteuerung.messaging;

import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrstrasse.Fahrstrasse.ReservierungsTyp;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg.Gleisabschnitt;

import java.io.Serializable;

public class FahrstrasseMessage implements Serializable
{
  private String           bereich;
  private String           name;
  private ReservierungsTyp reservierungsTyp;
  private Gleisabschnitt   teilFreigabeEnde;

  /**
   * Fahrstrassenreservierungsmeldung.
   * 
   * @param bereich Bereich
   * @param name Name
   * @param reservierungsTyp Typ der Reservierung oder <code>null</code> bei Freigabe
   * @param teilFreigabeEnde nur bei Freigabe: <code>null</code> für Komplettfreigabe oder erster Gleisabschnitt, der nicht
   *        freigegeben wird
   */
  public FahrstrasseMessage(String bereich, String name, ReservierungsTyp reservierungsTyp, Gleisabschnitt teilFreigabeEnde)
  {
    this.bereich = bereich;
    this.name = name;
    this.reservierungsTyp = reservierungsTyp;
    this.teilFreigabeEnde = teilFreigabeEnde;
  }

  /**
   * Wert liefern: {@link #bereich}.
   * 
   * @return Wert
   */
  public String getBereich()
  {
    return this.bereich;
  }

  /**
   * Wert liefern: {@link #name}.
   * 
   * @return Wert
   */
  public String getName()
  {
    return this.name;
  }

  /**
   * Wert liefern: {@link #reservierungsTyp}.
   * 
   * @return Wert
   */
  public ReservierungsTyp getReservierungsTyp()
  {
    return this.reservierungsTyp;
  }

  /**
   * Wert liefern: {@link #teilFreigabeEnde}.
   * 
   * @return Wert
   */
  public Gleisabschnitt getTeilFreigabeEnde()
  {
    return this.teilFreigabeEnde;
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    return "FahrstrasseMessage [bereich=" + this.bereich + ", name=" + this.name + ", reservierungsTyp=" + this.reservierungsTyp + ", teilFreigabeEnde=" + this.teilFreigabeEnde + "]";
  }
}
