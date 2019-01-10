package de.gedoplan.v5t11.status.service;

import de.gedoplan.v5t11.status.entity.BausteinConfiguration;
import de.gedoplan.v5t11.status.entity.baustein.Baustein;
import de.gedoplan.v5t11.status.persistence.BausteinConfigurationRepository;

import java.io.Serializable;

import javax.ejb.Stateless;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

// TODO Warum ist das eine EJB?
@Stateless
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
      bausteinConfiguration.setAdresse(baustein.getAdresse());

      // if (baustein.getProperties() != null) {
      // bausteinConfiguration.getProperties().putAll(baustein.getProperties());
      // }
    }

    return bausteinConfiguration;
  }

  public void save(BausteinConfiguration bausteinConfiguration) {
    if (bausteinConfiguration.getId() != null) {
      this.bausteinConfigurationRepository.merge(bausteinConfiguration);
    }
  }
}
