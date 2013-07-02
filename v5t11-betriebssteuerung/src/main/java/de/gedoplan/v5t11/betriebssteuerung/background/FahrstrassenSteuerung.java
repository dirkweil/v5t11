package de.gedoplan.v5t11.betriebssteuerung.background;

import de.gedoplan.v5t11.betriebssteuerung.messaging.FahrstrasseMessage;
import de.gedoplan.v5t11.betriebssteuerung.messaging.MessagePusher;
import de.gedoplan.v5t11.betriebssteuerung.qualifier.GleisBelegung;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.Steuerung;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrstrasse.Fahrstrasse;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrstrasse.Fahrstrasse.ReservierungsTyp;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg.Gleisabschnitt;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@ApplicationScoped
public class FahrstrassenSteuerung
{
  @Inject
  Steuerung                                     steuerung;

  @Inject
  MessagePusher                                 messagePusher;

  private static final Log                      LOG                    = LogFactory.getLog(FahrstrassenSteuerung.class);

  private Map<Fahrstrasse, FahrstrassenMonitor> fahrstrassenMonitorMap = new ConcurrentHashMap<>();

  /**
   * Fahrstrasse reservieren bzw. freigeben.
   * 
   * @param bereich Bereich
   * @param name Name
   * @param reservierungsTyp Reservierungstyp für Reservierung, <code>null</code> für Freigabe
   */
  public void reserviereFahrstrasse(String bereich, String name, ReservierungsTyp reservierungsTyp)
  {
    Fahrstrasse fahrstrasse = this.steuerung.getFahrstrasse(bereich, name);
    if (fahrstrasse != null)
    {
      if (fahrstrasse.getReservierungsTyp() != reservierungsTyp)
      {
        if (LOG.isDebugEnabled())
        {
          if (reservierungsTyp != null)
          {
            LOG.debug("Fahrstrasse reservieren: " + fahrstrasse + " (" + reservierungsTyp + ")");
            FahrstrassenMonitor fahrstrassenMonitor = new FahrstrassenMonitor(fahrstrasse, this);
            this.fahrstrassenMonitorMap.put(fahrstrasse, fahrstrassenMonitor);
          }
          else
          {
            LOG.debug("Fahrstrasse komplett freigeben: " + fahrstrasse);
            this.fahrstrassenMonitorMap.remove(fahrstrasse);
          }
        }

        fahrstrasse.reservieren(reservierungsTyp);
        this.messagePusher.pushMessage(new FahrstrasseMessage(bereich, name, reservierungsTyp, null));
      }
    }
  }

  /**
   * Fahrstrasse (ggf. teilweise) freigeben.
   * 
   * @param fahrstrasse Fahrstrasse
   * @param teilfreigabeEnde bei Teilfreigabe erster nicht freizugebender Gleisabschnitt; bei Komplettfreigabe <code>null</code>
   */
  public void fahrstrasseFreigeben(Fahrstrasse fahrstrasse, Gleisabschnitt teilfreigabeEnde)
  {
    if (teilfreigabeEnde == null)
    {
      reserviereFahrstrasse(fahrstrasse.getBereich(), fahrstrasse.getName(), null);
    }
    else
    {
      if (LOG.isDebugEnabled())
      {
        LOG.debug("Fahrstrasse freigeben: " + fahrstrasse + " bis " + teilfreigabeEnde);
      }

      fahrstrasse.freigeben(teilfreigabeEnde);
      this.messagePusher.pushMessage(new FahrstrasseMessage(fahrstrasse.getBereich(), fahrstrasse.getName(), null, teilfreigabeEnde));
    }

  }

  public void processGleisBelegung(@Observes @GleisBelegung Gleisabschnitt gleisabschnitt)
  {
    for (FahrstrassenMonitor fahrstrassenMonitor : this.fahrstrassenMonitorMap.values())
    {
      fahrstrassenMonitor.processGleisBelegung(gleisabschnitt);
    }
  }

}
