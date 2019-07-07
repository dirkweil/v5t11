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

  private static final int WAIT_MILLIS = 1000;

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

  protected int getParameter(int steuerAdr, int wertAdr, int parameterNummer) {
    if (this.log.isDebugEnabled()) {
      this.log.debug(String.format("getParameter: steuerAdr=%d, wertAdr=5d, parameterNummer=%d", steuerAdr, wertAdr, parameterNummer));
    }

    this.steuerung.setSX1Kanal(steuerAdr, parameterNummer);
    delay(WAIT_MILLIS);
    this.steuerung.awaitSync();
    int value = this.steuerung.getSX1Kanal(wertAdr);

    if (this.log.isDebugEnabled()) {
      this.log.debug(String.format("getParameter: value=%d (0x%02x)", value, value));
    }
    return value;
  }

  protected void setParameter(int steuerAdr, int wertAdr, int parameterNummer, int newValue) {
    if (this.log.isDebugEnabled()) {
      this.log.debug(String.format("setParameter: steuerAdr=%d, wertAdr=5d, parameterNummer=%d, newValue=%d (0x%02x)", steuerAdr, wertAdr, parameterNummer, newValue, newValue));
    }

    this.steuerung.setSX1Kanal(steuerAdr, parameterNummer);
    delay(WAIT_MILLIS);
    this.steuerung.awaitSync();

    int oldValue = this.steuerung.getSX1Kanal(wertAdr);
    if (newValue != oldValue) {
      this.steuerung.setSX1Kanal(wertAdr, newValue);
      delay(WAIT_MILLIS);
      this.steuerung.awaitSync();
    }
  }

  protected static void delay(long millis) {
    try {
      Thread.sleep(millis);
    }
    catch (Exception e) {
      // ignore
    }
  }

  public String getOpenProgModeMessage() {
    return "Bitte Programmiertaster am Baustein drücken!";
  }

  public String getCloseProgModeMessage() {
    return "Bitte Programmiertaster am Baustein drücken!";
  }

}
