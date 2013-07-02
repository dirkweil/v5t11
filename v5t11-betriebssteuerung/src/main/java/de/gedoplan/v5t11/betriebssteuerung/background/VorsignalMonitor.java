package de.gedoplan.v5t11.betriebssteuerung.background;

import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrstrasse.Fahrstrasse;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg.geraet.Hauptsignal;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg.geraet.Signal;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg.geraet.Signal.Stellung;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.konfiguration.VorsignalKonfiguration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class VorsignalMonitor
{
  private VorsignalKonfiguration vorsignalKonfiguration;

  private static final Log       LOG = LogFactory.getLog(VorsignalMonitor.class);

  public VorsignalMonitor(VorsignalKonfiguration vorsignalKonfiguration)
  {
    this.vorsignalKonfiguration = vorsignalKonfiguration;

    vorsignalStellen();
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    return "VorsignalMonitor [vorsignalKonfiguration=" + this.vorsignalKonfiguration + "]";
  }

  public void processFahrstrassenZuordnung(Signal signal)
  {
    if (signal.equals(this.vorsignalKonfiguration.getVorsignal()) || this.vorsignalKonfiguration.getHauptsignale().contains(signal)
        || signal.equals(this.vorsignalKonfiguration.getHauptsignalAmGleichenMast()))
    {
      vorsignalStellen();
    }
  }

  public void processStellungsAenderung(Signal signal)
  {
    if (this.vorsignalKonfiguration.getHauptsignale().contains(signal) || signal.equals(this.vorsignalKonfiguration.getHauptsignalAmGleichenMast()))
    {
      vorsignalStellen();
    }
  }

  private void vorsignalStellen()
  {
    if (LOG.isTraceEnabled())
    {
      LOG.trace("vorsignalStellen[" + this.vorsignalKonfiguration.getVorsignal() + "]");
    }

    // Falls Hauptsignal am gleichen Mast HALT zeigt, Vorsignal DUNKEL stellen
    if (this.vorsignalKonfiguration.getHauptsignalAmGleichenMast() != null)
    {
      Stellung stellung = this.vorsignalKonfiguration.getHauptsignalAmGleichenMast().getStellung();

      if (LOG.isTraceEnabled())
      {
        LOG.trace("vorsignalStellen[" + this.vorsignalKonfiguration.getVorsignal() + "]: " + this.vorsignalKonfiguration.getHauptsignalAmGleichenMast() + " am gleichen Mast zeigt " + stellung);
      }

      if (stellung.equals(Stellung.HALT))
      {
        if (this.vorsignalKonfiguration.isAutodunkel())
        {
          if (LOG.isDebugEnabled())
          {
            LOG.debug(this.vorsignalKonfiguration.getVorsignal() + " automatisch auf DUNKEL, da " + this.vorsignalKonfiguration.getHauptsignalAmGleichenMast() + " auf " + stellung);
          }
        }
        else
        {
          if (LOG.isDebugEnabled())
          {
            LOG.debug(this.vorsignalKonfiguration.getVorsignal() + " auf DUNKEL, da " + this.vorsignalKonfiguration.getHauptsignalAmGleichenMast() + " auf " + stellung);
          }
          this.vorsignalKonfiguration.getVorsignal().setStellung(Stellung.DUNKEL);
        }

        return;
      }
    }

    if (LOG.isTraceEnabled())
    {
      LOG.trace("vorsignalStellen[" + this.vorsignalKonfiguration.getVorsignal() + "]: Zugeordnete Hauptsignale: " + this.vorsignalKonfiguration.getHauptsignale());
    }

    // Wenn nur ein Hauptsignal zugeordnet, dessen Stellung übernehmen
    if (this.vorsignalKonfiguration.getHauptsignale().size() == 1)
    {
      Hauptsignal hauptsignal = this.vorsignalKonfiguration.getHauptsignale().iterator().next();
      Stellung stellung = hauptsignal.getStellung();

      if (LOG.isDebugEnabled())
      {
        LOG.debug(this.vorsignalKonfiguration.getVorsignal() + " auf " + stellung + ", da " + hauptsignal + " so steht");
      }

      this.vorsignalKonfiguration.getVorsignal().setStellung(stellung);
      return;
    }

    // Sonst ein zugeordnetes Hauptsignal in gleicher Fahrstrasse suchen und dessen Stellung übernehmen
    Fahrstrasse vorsignalFahrstrasse = this.vorsignalKonfiguration.getVorsignal().getReservierteFahrstrasse();
    if (LOG.isTraceEnabled())
    {
      LOG.trace("vorsignalStellen[" + this.vorsignalKonfiguration.getVorsignal() + "]: Ist in " + (vorsignalFahrstrasse != null ? vorsignalFahrstrasse.toString() : " keiner Fahrstrasse"));
    }

    for (Hauptsignal hauptsignal : this.vorsignalKonfiguration.getHauptsignale())
    {
      Fahrstrasse hauptsignalFahrstrasse = hauptsignal.getReservierteFahrstrasse();

      if (LOG.isTraceEnabled())
      {
        LOG.trace("vorsignalStellen[" + this.vorsignalKonfiguration.getVorsignal() + "]: " + hauptsignal + " ist in "
            + (hauptsignalFahrstrasse != null ? hauptsignalFahrstrasse.toString() : " keiner Fahrstrasse"));
      }

      if (vorsignalFahrstrasse != null)
      {
        if (vorsignalFahrstrasse.equals(hauptsignalFahrstrasse))
        {
          Stellung stellung = hauptsignal.getStellung();

          if (LOG.isDebugEnabled())
          {
            LOG.debug(this.vorsignalKonfiguration.getVorsignal() + " auf " + stellung + ", da mit " + hauptsignal + " in " + vorsignalFahrstrasse);
          }

          this.vorsignalKonfiguration.getVorsignal().setStellung(stellung);
          return;
        }
      }
    }

    // Wenn alles nicht passte, auf HALT stellen
    if (LOG.isDebugEnabled())
    {
      LOG.debug(this.vorsignalKonfiguration.getVorsignal() + " auf HALT, da in keiner Fahrstrasse mit zugehoerigem Hauptsignal");
    }

    this.vorsignalKonfiguration.getVorsignal().setStellung(Stellung.HALT);
  }

}
