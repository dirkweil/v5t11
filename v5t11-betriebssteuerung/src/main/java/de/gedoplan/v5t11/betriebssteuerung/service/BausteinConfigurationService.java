package de.gedoplan.v5t11.betriebssteuerung.service;

import de.gedoplan.v5t11.betriebssteuerung.entity.BausteinConfiguration;
import de.gedoplan.v5t11.betriebssteuerung.repository.BausteinConfigurationRepository;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.Baustein;

import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
public class BausteinConfigurationService
{
  @Inject
  BausteinConfigurationRepository bausteinConfigurationRepository;

  public BausteinConfiguration getBausteinConfiguration(Baustein baustein)
  {
    BausteinConfiguration bausteinConfiguration = null;

    if (baustein.getId() != null)
    {
      bausteinConfiguration = this.bausteinConfigurationRepository.findById(baustein.getId());
    }

    if (bausteinConfiguration == null)
    {
      bausteinConfiguration = new BausteinConfiguration(baustein.getId());
      bausteinConfiguration.setAdresse(baustein.getAdresse());

      if (baustein.getProperties() != null)
      {
        bausteinConfiguration.getProperties().putAll(baustein.getProperties());
      }
    }

    return bausteinConfiguration;
  }

  public void save(BausteinConfiguration bausteinConfiguration)
  {
    if (bausteinConfiguration.getId() != null)
    {
      this.bausteinConfigurationRepository.merge(bausteinConfiguration);
    }
  }
}
