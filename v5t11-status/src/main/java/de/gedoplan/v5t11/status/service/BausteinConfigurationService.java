package de.gedoplan.v5t11.status.service;

import de.gedoplan.v5t11.status.entity.BausteinConfiguration;
import de.gedoplan.v5t11.status.entity.Kanal;
import de.gedoplan.v5t11.status.entity.baustein.Baustein;
import de.gedoplan.v5t11.status.persistence.BausteinConfigurationRepository;

import java.io.Serializable;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.transaction.Transactional;

@Dependent
public class BausteinConfigurationService implements Serializable {
  @Inject
  BausteinConfigurationRepository bausteinConfigurationRepository;

  public BausteinConfiguration getBausteinConfiguration(Baustein baustein) {
    BausteinConfiguration bausteinConfiguration = null;

    if (baustein.getId() != null) {
      bausteinConfiguration = this.bausteinConfigurationRepository.findById(baustein.getId());
    }

    if (bausteinConfiguration == null) {
      bausteinConfiguration = new BausteinConfiguration(baustein.getId());
      bausteinConfiguration.setLocalAdr(Kanal.toLocalAdr(baustein.getAdresse()));

      // if (baustein.getProperties() != null) {
      // bausteinConfiguration.getProperties().putAll(baustein.getProperties());
      // }
    }

    return bausteinConfiguration;
  }

  @Transactional(rollbackOn = Exception.class)
  public void save(BausteinConfiguration bausteinConfiguration) {
    if (bausteinConfiguration.getId() != null) {
      this.bausteinConfigurationRepository.merge(bausteinConfiguration);
    }
  }
}
