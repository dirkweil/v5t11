package de.gedoplan.v5t11.betriebssteuerung.background;

import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrstrasse.Fahrstrasse;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrstrasse.FahrstrassenElement;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrstrasse.element.FahrstrassenGleisabschnitt;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg.Gleisabschnitt;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FahrstrassenMonitor
{
  private Fahrstrasse                fahrstrasse;
  private Set<Gleisabschnitt>        gleisabschnitte           = new HashSet<>();
  private List<GleisabschnittStatus> gleisabschnittStatusListe = new ArrayList<>();
  private FahrstrassenSteuerung      fahrstrassenSteuerung;

  private static final Log           LOG                       = LogFactory.getLog(FahrstrassenMonitor.class);

  public FahrstrassenMonitor(Fahrstrasse fahrstrasse, FahrstrassenSteuerung fahrstrassenSteuerung)
  {
    this.fahrstrasse = fahrstrasse;
    this.fahrstrassenSteuerung = fahrstrassenSteuerung;

    GleisabschnittStatus gleisabschnittStatus = null;
    for (FahrstrassenElement element : fahrstrasse.getElemente())
    {
      if (element instanceof FahrstrassenGleisabschnitt)
      {
        Gleisabschnitt gleisabschnitt = ((FahrstrassenGleisabschnitt) element).getFahrwegelement();
        this.gleisabschnitte.add(gleisabschnitt);
        gleisabschnittStatus = new GleisabschnittStatus(gleisabschnitt);
        this.gleisabschnittStatusListe.add(gleisabschnittStatus);

        if (LOG.isDebugEnabled())
        {
          LOG.debug("Monitor fuer " + fahrstrasse + " ueberwacht " + gleisabschnitt);
        }
      }
      else if (gleisabschnittStatus != null)
      {
        gleisabschnittStatus.gleisabschnittFolgt = false;
      }
    }
  }

  public void processGleisBelegung(Gleisabschnitt gleisabschnitt)
  {
    if (this.gleisabschnitte.contains(gleisabschnitt))
    {
      // Zum gemeldeten Gleisabschnitt passenden Status updaten
      for (GleisabschnittStatus gleisabschnittStatus : this.gleisabschnittStatusListe)
      {
        if (gleisabschnittStatus.gleisabschnitt.equals(gleisabschnitt))
        {
          gleisabschnittStatus.update();
        }
      }

      // Teilfreigabestrecke ermitteln: Schon durchfahrene Abschnitte vor einem belegten Abschnitt
      boolean freigabeMoeglich = false;
      GleisabschnittStatus freigabeEnde = null;
      int freigabeEndeIndex = 0;
      Iterator<GleisabschnittStatus> iterator = this.gleisabschnittStatusListe.iterator();
      while (iterator.hasNext())
      {
        freigabeEnde = iterator.next();
        if (freigabeEnde.besetzt)
        {
          break;
        }

        if (!freigabeEnde.durchfahren)
        {
          freigabeMoeglich = false;
          break;
        }

        freigabeMoeglich = true;
        freigabeEndeIndex++;
      }

      // Falls Liste komplett durchlaufen, ist Komplettfreigabe möglich
      if (!iterator.hasNext())
      {
        freigabeEnde = null;
      }

      if (freigabeMoeglich)
      {
        if (freigabeEnde != null)
        {
          // Statusliste bis vor freigabeEnde leeren
          this.gleisabschnittStatusListe.subList(0, freigabeEndeIndex).clear();

          // Fahrstrasse soweit freigeben
          this.fahrstrassenSteuerung.fahrstrasseFreigeben(this.fahrstrasse, freigabeEnde.gleisabschnitt);
        }
        else
        {
          // Fahrstrasse komplett freigeben
          this.fahrstrassenSteuerung.fahrstrasseFreigeben(this.fahrstrasse, null);
          return;
        }
      }

      // Falls restliche Fahrstrasse nur aus besetzten Gleisabschnitten besteht, ist Komplettfreigabe möglich
      for (GleisabschnittStatus gleisabschnittStatus : this.gleisabschnittStatusListe)
      {
        if (!gleisabschnittStatus.besetzt || !gleisabschnittStatus.gleisabschnittFolgt)
        {
          return;
        }
      }
      this.fahrstrassenSteuerung.fahrstrasseFreigeben(this.fahrstrasse, null);
    }
  }

  private static class GleisabschnittStatus
  {
    private Gleisabschnitt gleisabschnitt;
    private boolean        gleisabschnittFolgt = true;
    private boolean        besetzt;
    private boolean        durchfahren;
    private String         name;

    public GleisabschnittStatus(Gleisabschnitt gleisabschnitt)
    {
      this.gleisabschnitt = gleisabschnitt;
      this.besetzt = gleisabschnitt.isBesetzt();

      // TODO Hack für noch nicht an Besetztmelder angeschlossene Gleisabschnitte. Muss am Ende raus!
      if (gleisabschnitt.getBesetztmelder().getAdresse() >= 50 && gleisabschnitt.getBesetztmelder().getAdresse() < 60)
      {
        this.durchfahren = true;
      }
    }

    public void update()
    {
      if (this.gleisabschnitt.isBesetzt())
      {
        this.durchfahren = false;
      }
      else if (this.besetzt)
      {
        this.durchfahren = true;
      }

      this.besetzt = this.gleisabschnitt.isBesetzt();
    }

    public GleisabschnittStatus(String name, boolean besetzt, boolean durchfahren)
    {
      this.name = name;
      this.besetzt = besetzt;
      this.durchfahren = durchfahren;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
      return this.name + (this.besetzt ? "b" : "-") + (this.durchfahren ? "d" : "-");
    }
  }

  private static void f(String... l)
  {
    List<GleisabschnittStatus> gleisabschnittStatusListe = new ArrayList<>();
    for (String s : l)
    {
      gleisabschnittStatusListe.add(new GleisabschnittStatus(s.substring(0, 1), s.charAt(1) != '-', s.charAt(2) != '-'));
    }

    boolean freigabeMoeglich = false;
    GleisabschnittStatus freigabeEnde = null;
    int freigabeEndeIndex = 0;
    Iterator<GleisabschnittStatus> iterator = gleisabschnittStatusListe.iterator();
    while (iterator.hasNext())
    {
      freigabeEnde = iterator.next();
      if (freigabeEnde.besetzt)
      {
        break;
      }

      if (!freigabeEnde.durchfahren)
      {
        freigabeMoeglich = false;
        break;
      }

      freigabeMoeglich = true;
      freigabeEndeIndex++;
    }

    if (!iterator.hasNext())
    {
      freigabeEnde = null;
    }

    System.out.println(gleisabschnittStatusListe + ": freigabeMoeglich: " + freigabeMoeglich + ", freigabeEnde: " + freigabeEnde + " (" + freigabeEndeIndex + ")");
  }

  public static void main(String[] args)
  {
    f("A--", "B--", "C--", "D--");
    f("Ab-", "B--", "C--", "D--");
    f("Ab-", "Bb-", "C--", "D--");
    f("A-d", "Bb-", "Cb-", "D--");
    f("A-d", "B-d", "C-d", "Db-");
    f("A-d", "B-d", "C-d", "D-d");
    System.out.println("---");
    f("A--", "B--", "C--", "D--");
    f("Ab-", "B--", "C--", "D--");
    f("A-d", "B--", "C--", "D--");
    f("A-d", "Bb-", "C--", "D--");
    f("A-d", "B-d", "C--", "D--");
    f("A-d", "B-d", "Cb-", "D--");
    f("A-d", "B-d", "C-d", "D--");
    f("A-d", "B-d", "C-d", "Db-");
  }
}
