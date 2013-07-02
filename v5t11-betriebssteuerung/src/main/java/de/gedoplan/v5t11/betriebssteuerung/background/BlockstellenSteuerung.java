package de.gedoplan.v5t11.betriebssteuerung.background;

import de.gedoplan.v5t11.betriebssteuerung.qualifier.GleisBelegung;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.Steuerung;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrstrasse.Fahrstrasse.ReservierungsTyp;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg.Gleisabschnitt;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg.geraet.Signal;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.konfiguration.BlockstellenKonfiguration;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@ApplicationScoped
public class BlockstellenSteuerung implements InitializableService
{
  private Map<Gleisabschnitt, Signal> blockstellenMap = new HashMap<>();

  private static final Log            LOG             = LogFactory.getLog(BlockstellenSteuerung.class);

  @Inject
  Steuerung                           steuerung;

  @PostConstruct
  public void postConstruct()
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("Blockstellen ermitteln");
    }

    for (BlockstellenKonfiguration blockstellenKonfiguration : this.steuerung.getBlockstellenKonfigurationen())
    {
      Signal signal = blockstellenKonfiguration.getSignal();
      Gleisabschnitt gleisabschnitt = blockstellenKonfiguration.getGleisabschnitt();

      if (signal != null && gleisabschnitt != null)
      {
        this.blockstellenMap.put(gleisabschnitt, signal);

        if (LOG.isDebugEnabled())
        {
          LOG.debug(signal + " sichert " + gleisabschnitt);
        }

        signalStellen(gleisabschnitt, signal);
      }
    }
  }

  private void signalStellen(Gleisabschnitt blockAbschnitt, Signal blockSignal)
  {
    boolean besetzt = blockAbschnitt.isBesetzt();
    if (besetzt)
    {
      blockSignal.setStellung(Signal.Stellung.HALT);
    }
    else
    {
      blockSignal.setStellung(Signal.Stellung.FAHRT, ReservierungsTyp.ZUGFAHRT);
    }

    if (LOG.isDebugEnabled())
    {
      LOG.debug(blockSignal + " auf " + blockSignal.getStellung() + ", da " + blockAbschnitt + (besetzt ? " besetzt" : " frei"));
    }
  }

  public void processGleisBelegung(@Observes @GleisBelegung Gleisabschnitt gleisabschnitt)
  {
    Signal signal = this.blockstellenMap.get(gleisabschnitt);
    if (signal != null)
    {
      signalStellen(gleisabschnitt, signal);
    }
  }

  @Override
  public void init()
  {
    // Nichts zu tun (macht postConstruct)
  }
}
