package de.gedoplan.v5t11.betriebssteuerung.background;

import de.gedoplan.v5t11.betriebssteuerung.qualifier.FahrstrassenZuordnung;
import de.gedoplan.v5t11.betriebssteuerung.qualifier.GleisBelegung;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.Steuerung;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg.Gleisabschnitt;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg.geraet.Bahnuebergang;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.konfiguration.BahnuebergangKonfiguration;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@ApplicationScoped
public class BahnuebergangSteuerung implements InitializableService
{
  @Inject
  Steuerung                                        steuerung;

  private Map<Bahnuebergang, BahnuebergangMonitor> bahnuebergangMonitorMap = new HashMap<>();

  private static final Log                         LOG                     = LogFactory.getLog(BahnuebergangSteuerung.class);

  @PostConstruct
  public void postConstruct()
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("Bahnuebergaenge ermitteln");
    }

    for (BahnuebergangKonfiguration bahnuebergangKonfiguration : this.steuerung.getBahnuebergangKonfigurationen())
    {
      Bahnuebergang bahnuebergang = bahnuebergangKonfiguration.getBahnuebergang();
      BahnuebergangMonitor bahnuebergangMonitor = this.bahnuebergangMonitorMap.get(bahnuebergang);
      if (bahnuebergangMonitor == null)
      {
        bahnuebergangMonitor = new BahnuebergangMonitor(bahnuebergang, bahnuebergangKonfiguration.getGleisabschnitte());
        this.bahnuebergangMonitorMap.put(bahnuebergang, bahnuebergangMonitor);
      }
    }
  }

  public void processGleisBelegung(@Observes @GleisBelegung Gleisabschnitt gleisabschnitt)
  {
    for (BahnuebergangMonitor bahnuebergangMonitor : this.bahnuebergangMonitorMap.values())
    {
      bahnuebergangMonitor.processGleisBelegung(gleisabschnitt);
    }
  }

  public void processFahrstrassenZuordnung(@Observes @FahrstrassenZuordnung Gleisabschnitt gleisabschnitt)
  {
    for (BahnuebergangMonitor bahnuebergangMonitor : this.bahnuebergangMonitorMap.values())
    {
      bahnuebergangMonitor.processFahrstrassenZuordnung(gleisabschnitt);
    }
  }

  @Override
  public void init()
  {
    // Nichts zu tun (macht postConstruct)
  }

}
