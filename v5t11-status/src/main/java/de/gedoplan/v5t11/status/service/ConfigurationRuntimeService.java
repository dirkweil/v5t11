package de.gedoplan.v5t11.status.service;

import de.gedoplan.v5t11.status.entity.Kanal;
import de.gedoplan.v5t11.status.entity.Steuerung;
import de.gedoplan.v5t11.status.entity.baustein.Baustein;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import jakarta.annotation.PreDestroy;
import jakarta.inject.Inject;

import org.jboss.logging.Logger;

import lombok.Setter;

/**
 * Basisklasse für Services, die die Konfiguration eines Bausteins lesen und schreiben.
 *
 * @author dw
 */
public abstract class ConfigurationRuntimeService implements Serializable {

  private static final int WAIT_MILLIS = 1000;

  @Inject
  Steuerung steuerung;

  @Inject
  BausteinConfigurationService bausteinConfigurationService;

  @Setter
  protected int busNr;

  protected Baustein baustein;

  private Map<Integer, Integer> savedKanalWerte = new HashMap<>();

  protected Logger log = Logger.getLogger(getClass());

  public abstract ConfigurationAdapter getConfiguration();

  protected abstract int[] getProgLocalAdressen();

  public abstract void getRuntimeValues();

  public abstract void setRuntimeValues();

  protected ConfigurationRuntimeService(Baustein baustein) {
    this.baustein = baustein;

    if (this.log.isDebugEnabled()) {
      this.log.debug("ConfigurationRuntimeService für " + baustein + " erzeugen");
    }
  }

  protected ConfigurationRuntimeService() {
  }

  public void program() {
    setRuntimeValues();
    this.bausteinConfigurationService.save(getConfiguration().istConfiguration);
  }

  public void saveProgKanalWerte() {
    for (int busNr = 0; busNr < this.steuerung.getZentrale().getBusAnzahl(); ++busNr) {
      for (int localAdr : getProgLocalAdressen()) {
        int adr = Kanal.toAdr(busNr, localAdr);
        this.savedKanalWerte.put(adr, this.steuerung.getSX1Kanal(adr));
        this.steuerung.addSuppressedKanal(adr);
      }
    }
  }

  @PreDestroy
  void preDestroy() {
    if (this.log.isDebugEnabled()) {
      this.log.debug("ConfigurationRuntimeService für " + this.baustein + " zerstören");
    }

    this.savedKanalWerte.entrySet().stream().forEach(entry -> {
      int adr = entry.getKey();
      int wert = entry.getValue();
      this.steuerung.setSX1Kanal(adr, wert);
      this.steuerung.removeSuppressedKanal(adr);
    });
  }

  protected int getWert(int localAdr) {
    return this.steuerung.getSX1Kanal(Kanal.toAdr(this.busNr, localAdr));
  }

  protected void setWert(int localAdr, int wert) {
    this.steuerung.setSX1Kanal(Kanal.toAdr(this.busNr, localAdr), wert);
  }

  protected int getParameter(int steuerLocalAdr, int wertLocalAdr, int parameterNummer) {
    int steuerAdr = Kanal.toAdr(this.busNr, steuerLocalAdr);
    int wertAdr = Kanal.toAdr(this.busNr, wertLocalAdr);

    if (this.log.isDebugEnabled()) {
      this.log.debug(String.format("getParameter: steuerAdr=%d, wertAdr=%d, parameterNummer=%d", steuerAdr, wertAdr, parameterNummer));
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

  protected void setParameter(int steuerLocalAdr, int wertLocalAdr, int parameterNummer, int newValue) {
    int steuerAdr = Kanal.toAdr(this.busNr, steuerLocalAdr);
    int wertAdr = Kanal.toAdr(this.busNr, wertLocalAdr);

    if (this.log.isDebugEnabled()) {
      this.log.debug(String.format("setParameter: steuerAdr=%d, wertAdr=%d, parameterNummer=%d, newValue=%d (0x%02x)", steuerAdr, wertAdr, parameterNummer, newValue, newValue));
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
    } catch (Exception e) {
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
