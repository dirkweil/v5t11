package de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg;

import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg.geraet.Weiche;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg.geraet.Weiche.Stellung;

import java.io.Serializable;

public class FolgeGleisabschnitt implements Serializable
{
  private Weiche         weiche;
  private Gleisabschnitt gleisabschnitte[] = new Gleisabschnitt[2];

  public FolgeGleisabschnitt(Weiche weiche, Stellung stellung, Gleisabschnitt gleisabschnitt)
  {
    this.weiche = weiche;
    this.gleisabschnitte[stellung != null ? stellung.getValue() : 0] = gleisabschnitt;
  }

  public Gleisabschnitt getCurrent()
  {
    return this.weiche == null
        ? this.gleisabschnitte[0]
        : this.gleisabschnitte[this.weiche.getStellung().getValue()];
  }

  public void add(Weiche weiche, Stellung stellung, Gleisabschnitt gleisabschnitt)
  {
    if (weiche == null)
    {
      if (this.weiche != null)
      {
        throw new IllegalArgumentException("Es kann kein unbedingter Folge-Gleisabschnitt eingetragen werden, wenn bereits ein bedingter eingetragen ist");
      }

      if (!gleisabschnitt.equals(this.gleisabschnitte[0]))
      {
        throw new IllegalArgumentException("Wiederspruechlicher unbedingter Folge-Gleisabschnitt");
      }
    }
    else
    {
      if (this.weiche == null)
      {
        throw new IllegalArgumentException("Es kann kein bedingter Folge-Gleisabschnitt eingetragen werden, wenn bereits ein unbedingter eingetragen ist");
      }

      if (!weiche.equals(this.weiche))
      {
        throw new IllegalArgumentException("Wiederspruechliche Weiche fuer bedingten Folge-Gleisabschnitt");
      }

      if (this.gleisabschnitte[stellung.getValue()] != null && !gleisabschnitt.equals(this.gleisabschnitte[stellung.getValue()]))
      {
        throw new IllegalArgumentException("Wiederspruechlicher unbedingter Folge-Gleisabschnitt");
      }

      this.gleisabschnitte[stellung.getValue()] = gleisabschnitt;
    }
  }

  @Override
  public String toString()
  {
    if (this.weiche == null)
    {
      return toString(this.gleisabschnitte[0]);
    }
    else
    {
      return this.weiche.getName() + " ? " + toString(this.gleisabschnitte[0]) + " : " + toString(this.gleisabschnitte[1]);
    }
  }

  private String toString(Gleisabschnitt gleisabschnitt)
  {
    return gleisabschnitt == null ? "unknown" : gleisabschnitt.getName();
  }

}
