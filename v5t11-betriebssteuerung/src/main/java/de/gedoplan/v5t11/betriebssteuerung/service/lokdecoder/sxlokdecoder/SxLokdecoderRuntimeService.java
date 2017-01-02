package de.gedoplan.v5t11.betriebssteuerung.service.lokdecoder.sxlokdecoder;

import de.gedoplan.v5t11.betriebssteuerung.service.ConfigurationRuntimeService;
import de.gedoplan.v5t11.betriebssteuerung.service.lokdecoder.sxlokdecoder.SxLokdecoderConfigurationAdapter.Impulsbreite;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.Steuerung;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.Zentrale;
import de.gedoplan.v5t11.selectrix.SelectrixException;

import java.util.function.Supplier;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class SxLokdecoderRuntimeService extends ConfigurationRuntimeService<SxLokdecoderConfigurationAdapter> {

  Zentrale zentrale;

  @Inject
  void init(Steuerung steuerung) {
    this.zentrale = steuerung.getZentrale();
  }

  @Override
  public void getRuntimeValues(SxLokdecoderConfigurationAdapter configuration) {
    this.zentrale.setAktiv(false);

    startProgMode();

    try {
      requestDecoderDaten();

      int value104 = this.selectrixGateway.getValue(104);
      configuration.hoechstGeschwindigkeit.setIst(value104 & 0b111, false);
      configuration.traegheit.setIst((value104 >>> 3) & 0b111, false);
      configuration.impulsbreite.setIst(Impulsbreite.valueOf((value104 >>> 6) & 0b11), false);

      int value105 = this.selectrixGateway.getValue(105);
      configuration.setAdresseIst(value105 & 0b1111111, false);
    } finally {
      stopProgMode();
    }
  }

  @Override
  public void setRuntimeValues(SxLokdecoderConfigurationAdapter configuration) {
    this.zentrale.setAktiv(false);

    startProgMode();

    try {
      int value104 = (configuration.hoechstGeschwindigkeit.getIst() & 0b111)
          | ((configuration.traegheit.getIst() << 3) & 0b111)
          | ((configuration.impulsbreite.getIst().getBits() << 6) & 0b11);
      this.selectrixGateway.setValue(104, value104);

      int value105 = (configuration.getAdresseIst() & 0b0111_1111);
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
    int value109 = this.selectrixGateway.getValue(109);
    if (this.log.isTraceEnabled()) {
      this.log.trace("value109=" + Integer.toBinaryString(value109));
    }
    return (value109 & 0b0010_0000) != 0;
  }

  private static void waitFor(Supplier<Boolean> condition, long millis, String errorMessage) {
    for (int i = 0; i < 10; ++i) {
      delay(millis / 10);

      if (condition.get()) {
        return;
      }
    }

    throw new SelectrixException(errorMessage);
  }

  private static void delay(long millis) {
    try {
      Thread.currentThread().wait(millis);
    } catch (InterruptedException e) {
    }

  }

}
