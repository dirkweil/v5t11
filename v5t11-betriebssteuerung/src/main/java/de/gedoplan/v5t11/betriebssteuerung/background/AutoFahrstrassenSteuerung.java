package de.gedoplan.v5t11.betriebssteuerung.background;

import de.gedoplan.v5t11.betriebssteuerung.qualifier.GleisBelegung;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.Steuerung;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrstrasse.Fahrstrasse;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrstrasse.Fahrstrasse.ReservierungsTyp;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg.Gleisabschnitt;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.konfiguration.AutoFahrstrassenKonfiguration;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@ApplicationScoped
public class AutoFahrstrassenSteuerung implements InitializableService
{
  private Map<Gleisabschnitt, Collection<Gleisabschnitt>> triggerMap = new HashMap<>();

  private static final Log                                LOG        = LogFactory.getLog(AutoFahrstrassenSteuerung.class);

  @Inject
  Steuerung                                               steuerung;

  @Inject
  FahrstrassenSteuerung                                   fahrstrassenSteuerung;

  @PostConstruct
  public void postConstruct()
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("Autofahrstrassen ermitteln");
    }

    for (AutoFahrstrassenKonfiguration konfiguration : this.steuerung.getAutoFahrstrassenKonfigurationen())
    {
      Gleisabschnitt gleisabschnitt = konfiguration.getGleisabschnitt();
      List<Gleisabschnitt> zielGleisabschnitte = konfiguration.getZielGleisabschnitte();
      this.triggerMap.put(gleisabschnitt, zielGleisabschnitte);

      if (LOG.isDebugEnabled())
      {
        LOG.debug(gleisabschnitt + " triggert Fahrstrassen nach " + zielGleisabschnitte);
      }
    }
  }

  public void processGleisBelegung(@Observes @GleisBelegung Gleisabschnitt gleisabschnitt)
  {
    if (gleisabschnitt.isBesetzt())
    {
      Collection<Gleisabschnitt> zielGleisabschnitte = this.triggerMap.get(gleisabschnitt);
      if (zielGleisabschnitte != null)
      {
        for (Gleisabschnitt zielGleisabschnitt : zielGleisabschnitte)
        {
          List<Fahrstrasse> freieFahrstrassen = this.steuerung.getFreieFahrstrassen(gleisabschnitt, false, zielGleisabschnitt, true);
          if (freieFahrstrassen.size() > 0)
          {
            Fahrstrasse fahrstrasse = freieFahrstrassen.get(0);
            this.fahrstrassenSteuerung.reserviereFahrstrasse(fahrstrasse.getBereich(), fahrstrasse.getName(), ReservierungsTyp.ZUGFAHRT);
          }
        }
      }
    }
  }

  @Override
  public void init()
  {
    // Nichts zu tun (macht postConstruct)
  }
}
