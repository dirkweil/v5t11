package de.gedoplan.v5t11.status.service;

import de.gedoplan.v5t11.status.entity.Steuerung;

import java.io.Serializable;

import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Basisklasse für Services, die die Konfiguration eines Bausteins lesen und schreiben.
 *
 * @author dw
 */
public abstract class ConfigurationRuntimeService implements Serializable {

  @Inject
  protected Steuerung steuerung;

  @Inject
  BausteinConfigurationService bausteinConfigurationService;

  private int[] savedKanalWerte = new int[10];

  protected Log log = LogFactory.getLog(getClass());

  public abstract ConfigurationAdapter getConfiguration();

  public abstract void getRuntimeValues();

  public abstract void setRuntimeValues();

  public void program() {
    setRuntimeValues();
    this.bausteinConfigurationService.save(getConfiguration().istConfiguration);
  }

  public void saveProgKanalWerte() {
    for (int adr = 0; adr < this.savedKanalWerte.length; ++adr) {
      this.savedKanalWerte[adr] = this.steuerung.getSX1Kanal(adr);
      this.steuerung.addSuppressedKanal(adr);
    }
  }

  @PreDestroy
  void preDestroy() {
    for (int adr = 0; adr < this.savedKanalWerte.length; ++adr) {
      this.steuerung.setSX1Kanal(adr, this.savedKanalWerte[adr]);
      this.steuerung.removeSuppressedKanal(adr);
    }
  }
}
