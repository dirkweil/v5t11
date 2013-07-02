package de.gedoplan.v5t11.betriebssteuerung.background;

import de.gedoplan.v5t11.betriebssteuerung.qualifier.FahrstrassenZuordnung;
import de.gedoplan.v5t11.betriebssteuerung.qualifier.StellungsAenderung;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.Steuerung;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg.geraet.Signal;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg.geraet.Vorsignal;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.konfiguration.VorsignalKonfiguration;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@ApplicationScoped
public class VorsignalSteuerung implements InitializableService
{
  @Inject
  Steuerung                                steuerung;

  private static final Log                 LOG               = LogFactory.getLog(VorsignalSteuerung.class);

  private Map<Vorsignal, VorsignalMonitor> vorsignalMonitore = new HashMap<>();

  @PostConstruct
  public void postConstruct()
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("Vorsignale ermitteln");
    }

    for (VorsignalKonfiguration vorsignalKonfiguration : this.steuerung.getVorsignalKonfigurationen())
    {
      VorsignalMonitor vorsignalMonitor = new VorsignalMonitor(vorsignalKonfiguration);
      this.vorsignalMonitore.put(vorsignalKonfiguration.getVorsignal(), vorsignalMonitor);
    }
  }

  public void processFahrstrassenZuordnung(@Observes @FahrstrassenZuordnung Signal signal)
  {
    if (LOG.isTraceEnabled())
    {
      LOG.trace("FahrstrassenZuordnung: " + signal + " -> " + signal.getReservierteFahrstrasse());
    }

    for (VorsignalMonitor vorsignalMonitor : this.vorsignalMonitore.values())
    {
      vorsignalMonitor.processFahrstrassenZuordnung(signal);
    }
  }

  public void processStellungsAenderung(@Observes @StellungsAenderung Signal signal)
  {
    if (LOG.isTraceEnabled())
    {
      LOG.trace("StellungsAenderung: " + signal + " -> " + signal.getStellung());
    }

    for (VorsignalMonitor vorsignalMonitor : this.vorsignalMonitore.values())
    {
      vorsignalMonitor.processStellungsAenderung(signal);
    }
  }

  @Override
  public void init()
  {
    // Nichts zu tun (macht postConstruct)

    if (LOG.isTraceEnabled())
    {
      for (VorsignalMonitor vorsignalMonitor : this.vorsignalMonitore.values())
      {
        LOG.trace(vorsignalMonitor);
      }
    }
  }
}
