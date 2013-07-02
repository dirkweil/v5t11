package de.gedoplan.v5t11.betriebssteuerung.background;

import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrstrasse.Fahrstrasse;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg.Gleisabschnitt;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg.geraet.Bahnuebergang;

import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BahnuebergangMonitor
{
  private Bahnuebergang       bahnuebergang;
  private Set<Gleisabschnitt> gleisabschnitte;

  private static final Log    LOG = LogFactory.getLog(BahnuebergangMonitor.class);

  public BahnuebergangMonitor(Bahnuebergang bahnuebergang, Set<Gleisabschnitt> gleisabschnitte)
  {
    this.bahnuebergang = bahnuebergang;
    this.gleisabschnitte = gleisabschnitte;

    if (LOG.isDebugEnabled())
    {
      LOG.debug(this.bahnuebergang + " sichert " + this.gleisabschnitte);
    }
  }

  private void bahnuebergangStellen()
  {
    for (Gleisabschnitt gleisabschnitt : this.gleisabschnitte)
    {
      if (gleisabschnitt.isBesetzt())
      {
        if (LOG.isDebugEnabled())
        {
          LOG.debug(this.bahnuebergang + " ZU, da " + gleisabschnitt + " besetzt");
        }
        this.bahnuebergang.schliessen();
        return;
      }

      Fahrstrasse fahrstrasse = gleisabschnitt.getReservierteFahrstrasse();
      if (fahrstrasse != null)
      {
        if (LOG.isDebugEnabled())
        {
          LOG.debug(this.bahnuebergang + " ZU, da in " + fahrstrasse);
        }
        this.bahnuebergang.schliessen();
        return;
      }
    }

    if (LOG.isDebugEnabled())
    {
      LOG.debug(this.bahnuebergang + " AUF, da alle betroffenen Gleisabschnitte frei und nicht in Fahrstrassen");
    }
    this.bahnuebergang.oeffnen();
  }

  public void processGleisBelegung(Gleisabschnitt gleisabschnitt)
  {
    if (this.gleisabschnitte.contains(gleisabschnitt))
    {
      bahnuebergangStellen();
    }
  }

  public void processFahrstrassenZuordnung(Gleisabschnitt gleisabschnitt)
  {
    if (this.gleisabschnitte.contains(gleisabschnitt))
    {
      bahnuebergangStellen();
    }
  }
}
