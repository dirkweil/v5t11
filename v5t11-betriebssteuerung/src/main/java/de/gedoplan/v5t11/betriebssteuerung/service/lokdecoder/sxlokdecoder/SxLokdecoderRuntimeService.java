package de.gedoplan.v5t11.betriebssteuerung.service.lokdecoder.sxlokdecoder;

import de.gedoplan.v5t11.betriebssteuerung.entity.BausteinConfiguration;
import de.gedoplan.v5t11.betriebssteuerung.service.BausteinConfigurationService;
import de.gedoplan.v5t11.betriebssteuerung.service.ConfigurationRuntimeService;
import de.gedoplan.v5t11.betriebssteuerung.service.Current;
import de.gedoplan.v5t11.betriebssteuerung.service.Programmierfamilie;
import de.gedoplan.v5t11.betriebssteuerung.service.lokdecoder.sxlokdecoder.SxLokdecoderConfigurationAdapter.Impulsbreite;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.Steuerung;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.Baustein;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.Lokdecoder;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.Zentrale;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.lokdecoder.SxLokdecoder;
import de.gedoplan.v5t11.selectrix.SelectrixException;

import java.util.function.Supplier;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;

import lombok.Getter;

@ConversationScoped
@Programmierfamilie(SxLokdecoder.class)
public class SxLokdecoderRuntimeService extends ConfigurationRuntimeService {
  @Getter
  private SxLokdecoderConfigurationAdapter configuration;

  @Inject
  public SxLokdecoderRuntimeService(@Current Baustein baustein, BausteinConfigurationService bausteinConfigurationService) {
    BausteinConfiguration bausteinSollConfiguration = bausteinConfigurationService.getBausteinConfiguration(baustein);
    BausteinConfiguration bausteinIstConfiguration = new BausteinConfiguration(baustein.getId());
    this.configuration = new SxLokdecoderConfigurationAdapter((Lokdecoder) baustein, bausteinIstConfiguration, bausteinSollConfiguration);
  }

  Zentrale zentrale;

  @Inject
  void init(Steuerung steuerung) {
    this.zentrale = steuerung.getZentrale();
  }

  @Override
  public void getRuntimeValues() {
    this.zentrale.setAktiv(false);

    startProgMode();

    try {
      requestDecoderDaten();

      int value104 = this.selectrixGateway.getValue(104);
      this.configuration.hoechstGeschwindigkeit.setIst(value104 & 0b111, false);
      this.configuration.traegheit.setIst((value104 >>> 3) & 0b111, false);
      this.configuration.impulsbreite.setIst(Impulsbreite.valueOf((value104 >>> 6) & 0b11), false);

      int value105 = this.selectrixGateway.getValue(105);
      this.configuration.setAdresseIst(value105 & 0b1111111, false);

      if (this.log.isDebugEnabled()) {
        this.log.debug("value104=0b" + Integer.toBinaryString(value104) + ", value105=0b" + Integer.toBinaryString(value105));
      }
    } finally {
      stopProgMode();
    }
  }

  @Override
  public void setRuntimeValues() {
    this.zentrale.setAktiv(false);

    startProgMode();

    try {
      int value104 = (this.configuration.hoechstGeschwindigkeit.getIst() & 0b111)
          | ((this.configuration.traegheit.getIst() & 0b111) << 3)
          | ((this.configuration.impulsbreite.getIst().getBits() & 0b11) << 6);
      this.selectrixGateway.setValue(104, value104);

      int value105 = (this.configuration.getAdresseIst() & 0b0111_1111);
      this.selectrixGateway.setValue(105, value105);

      storeDecoderDaten();
    } finally {
      stopProgMode();
    }
  }

  private void startProgMode() {
    if (this.log.isDebugEnabled()) {
      this.log.debug("Start Programmier-Modus");
    }

    this.selectrixGateway.setValue(106, 0b0100_0000);
    waitFor(this::isProgBereit, 2000, "Kann Programmiermodus nicht anfordern");
  }

  private void stopProgMode() {
    if (this.log.isDebugEnabled()) {
      this.log.debug("Stop Programmier-Modus");
    }

    this.selectrixGateway.setValue(106, 0b0000_0000);
  }

  private void requestDecoderDaten() {
    if (this.log.isDebugEnabled()) {
      this.log.debug("Decoderdaten anfordern");
    }

    this.selectrixGateway.setValue(106, 0b1100_0001);
    waitFor(this::isProgBereit, 2000, "Kann Decoderdaten nicht lesen");
  }

  private void storeDecoderDaten() {
    if (this.log.isDebugEnabled()) {
      this.log.debug("Decoderdaten schreiben");
    }

    this.selectrixGateway.setValue(106, 0b1100_1001);
    waitFor(this::isProgBereit, 2000, "Kann Decoderdaten nicht schreiben");
  }

  private boolean isProgBereit() {
    if (System.getProperty("v5t11.portName", "none").equalsIgnoreCase("none")) {
      return true;
    }

    int value109 = this.selectrixGateway.getValue(109);
    if (this.log.isTraceEnabled()) {
      this.log.trace("value109=" + Integer.toBinaryString(value109));
    }
    return (value109 & 0b0010_0000) != 0;
  }

  private static void waitFor(Supplier<Boolean> condition, long millis, String errorMessage) {
	  delay(2000);
	  return;
	  
//    for (int i = 0; i < 10; ++i) {
//      delay(millis / 10);
//
//      if (condition.get()) {
//        return;
//      }
//    }
//
//    throw new SelectrixException(errorMessage);
  }

  private static void delay(long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
    }

  }

}
