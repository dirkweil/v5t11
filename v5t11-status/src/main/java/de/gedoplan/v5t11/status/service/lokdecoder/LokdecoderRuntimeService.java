package de.gedoplan.v5t11.status.service.lokdecoder;

import de.gedoplan.v5t11.selectrix.SelectrixException;
import de.gedoplan.v5t11.status.entity.Steuerung;
import de.gedoplan.v5t11.status.entity.baustein.Zentrale;
import de.gedoplan.v5t11.status.service.ConfigurationRuntimeService;
import de.gedoplan.v5t11.status.service.lokdecoder.LokdecoderConfigurationAdapter.Impulsbreite;

import java.util.function.Supplier;

import javax.inject.Inject;

import lombok.Getter;

public abstract class LokdecoderRuntimeService<C extends LokdecoderConfigurationAdapter> extends ConfigurationRuntimeService {
  private static final int WAIT_MILLIS = 2000;

  protected static final int HOECHSTGESCHWINDIGKEIT_MASK = 0b0000_0000_0000_0111;
  protected static final int HOECHSTGESCHWINDIGKEIT_OFFSET = 0;
  protected static final int TRAEGHEIT_MASK = 0b0000_0000_0011_1000;
  protected static final int TRAEGHEIT_OFFSET = 3;
  protected static final int IMPULSBREITE_MASK = 0b0000_0000_1100_0000;
  protected static final int IMPULSBREITE_OFFSET = 6;
  protected static final int ADRESSE_MASK = 0b0111_1111_0000_0000;
  protected static final int ADRESSE_OFFSET = 8;
  protected static final int MEHRTEILIGERHALTEABSCHNITT_BIT = 0b1000_0000_0000_0000;

  @Getter
  protected C configuration;

  Zentrale zentrale;

  @Inject
  void init(Steuerung steuerung) {
    this.zentrale = steuerung.getZentrale();
  }

  protected void startProgMode() {
    if (this.log.isDebugEnabled()) {
      this.log.debug("Start Programmier-Modus");
    }

    this.zentrale.setGleisspannung(false);

    // Bit 6 in Adresse 106 setzen und auf gesetztes Bit 5 von Adresse 109
    // warten
    this.selectrixGateway.setValue(106, 0b0100_0000);
    waitFor(() -> (this.selectrixGateway.getValue(109) & 0b0010_0000) != 0, WAIT_MILLIS, "Kann Programmiermodus nicht anfordern");
  }

  protected void stopProgMode() {
    if (this.log.isDebugEnabled()) {
      this.log.debug("Stop Programmier-Modus");
    }

    // Bit 6 in Adresse 106 löschen
    this.selectrixGateway.setValue(106, 0b0000_0000);
    delay(WAIT_MILLIS);
  }

  protected int readDecoderDaten() {
    if (this.log.isDebugEnabled()) {
      this.log.debug("Decoderdaten lesen");
    }

    /*
     * Bit 7 zusätzlich zu Bit 6 in Adresse 106 setzen.
     * Als Ausführungsbestätigung soll lt. Doku Bit 5 in Adresse 9 gesetzt
     * werden, was aber (zumindest mit der CC2000 und SLX825 im
     * Rautenhaus-Format) nicht passiert.
     * Daher zunächst Adresse 104 auf 0 setzen und auf Änderung warten
     * (zumindest ein Bit in Höchstgeschwindigkeit und Trägkeit ist 1).
     */

    this.selectrixGateway.setValue(104, 0b0000_0000);
    delay(WAIT_MILLIS);
    this.selectrixGateway.setValue(106, 0b1100_0001);
    waitFor(() -> (this.selectrixGateway.getValue(104) & 0b1111_1111) != 0, WAIT_MILLIS, "Kann Decoderdaten nicht lesen");

    int decoderDaten = (this.selectrixGateway.getValue(104) & 0b1111_1111) | ((this.selectrixGateway.getValue(105) & 0b1111_1111) << 8);

    if (this.log.isTraceEnabled()) {
      this.log.trace(String.format("readDecoderDaten: 0x%04x", decoderDaten));
    }

    this.selectrixGateway.setValue(106, 0b0100_0000);
    delay(WAIT_MILLIS);

    return decoderDaten;
  }

  protected void pullStandardConfig() {
    int decoderDaten = readDecoderDaten();

    this.configuration.hoechstGeschwindigkeit.setIst((decoderDaten & HOECHSTGESCHWINDIGKEIT_MASK) >>> HOECHSTGESCHWINDIGKEIT_OFFSET, false);
    this.configuration.traegheit.setIst((decoderDaten & TRAEGHEIT_MASK) >>> TRAEGHEIT_OFFSET, false);
    this.configuration.impulsbreite.setIst(Impulsbreite.valueOf((decoderDaten & IMPULSBREITE_MASK) >>> IMPULSBREITE_OFFSET), false);

    this.configuration.setAdresseIst((decoderDaten & ADRESSE_MASK) >>> ADRESSE_OFFSET, false);
    this.configuration.mehrteiligerHalteabschnitt.setIst((decoderDaten & MEHRTEILIGERHALTEABSCHNITT_BIT) != 0, false);
  }

  protected void writeDecoderDaten(int decoderDaten) {
    writeDecoderDatenUnverified(decoderDaten);

    int istDecoderDaten = readDecoderDaten();
    if (istDecoderDaten != decoderDaten) {
      throw new SelectrixException(String.format("Kann Decoderdaten nicht schreiben: soll=0x%04x, ist=0x%04x", decoderDaten, istDecoderDaten));
    }
  }

  protected void writeDecoderDatenUnverified(int decoderDaten) {
    if (this.log.isDebugEnabled()) {
      this.log.debug("Decoderdaten schreiben");
    }

    if (this.log.isTraceEnabled()) {
      this.log.trace(String.format("writeDecoderDaten: 0x%04x", decoderDaten));
    }

    this.selectrixGateway.setValue(104, decoderDaten & 0b1111_1111);
    this.selectrixGateway.setValue(105, (decoderDaten >>> 8) & 0b1111_1111);

    /*
     * Bits 7 und 3 zusätzlich zu Bit 6 in Adresse 106 setzen.
     * Als Ausführungsbestätigung soll lt. Doku Bit 5 in Adresse 9 gesetzt
     * werden, was aber (zumindest mit der CC2000 und SLX825 im
     * Rautenhaus-Format) nicht passiert.
     * Daher zunächst etwas warten und dann die Daten zur Kontrolle zurücklesen
     * und vergleichen.
     */
    this.selectrixGateway.setValue(106, 0b1100_1001);
    delay(WAIT_MILLIS);

    this.selectrixGateway.setValue(106, 0b0100_0000);
    delay(WAIT_MILLIS);
  }

  protected void pushStandardConfig() {
    int decoderDaten = ((this.configuration.hoechstGeschwindigkeit.getIst() << HOECHSTGESCHWINDIGKEIT_OFFSET) & HOECHSTGESCHWINDIGKEIT_MASK)
        | ((this.configuration.traegheit.getIst() << TRAEGHEIT_OFFSET) & TRAEGHEIT_MASK)
        | ((this.configuration.impulsbreite.getIst().getBits() << IMPULSBREITE_OFFSET) & IMPULSBREITE_MASK)
        | ((this.configuration.getAdresseIst() << ADRESSE_OFFSET) & ADRESSE_MASK)
        | (this.configuration.mehrteiligerHalteabschnitt.getIst() ? MEHRTEILIGERHALTEABSCHNITT_BIT : 0);

    writeDecoderDaten(decoderDaten);
  }

  private static void waitFor(Supplier<Boolean> condition, long millis, String errorMessage) {
    if (System.getProperty("v5t11.portName", "none").equalsIgnoreCase("none")) {
      return;
    }

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
      Thread.sleep(millis);
    } catch (InterruptedException e) {}
  }
}
