package de.gedoplan.v5t11.betriebssteuerung.service.lokdecoder;

import de.gedoplan.v5t11.betriebssteuerung.service.ConfigurationRuntimeService;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.Steuerung;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.Zentrale;
import de.gedoplan.v5t11.selectrix.SelectrixException;

import java.util.function.Supplier;

import javax.inject.Inject;

public abstract class LokdecoderRuntimeService extends ConfigurationRuntimeService {
  private static final int WAIT_MILLIS = 2000;

  Zentrale zentrale;

  @Inject
  void init(Steuerung steuerung) {
    this.zentrale = steuerung.getZentrale();
  }

  protected void startProgMode() {
    if (this.log.isDebugEnabled()) {
      this.log.debug("Start Programmier-Modus");
    }

    this.zentrale.setAktiv(false);

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

  protected void writeDecoderDaten(int decoderDaten) {
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

    int istDecoderDaten = readDecoderDaten();
    if (istDecoderDaten != decoderDaten) {
      throw new SelectrixException(String.format("Kann Decoderdaten nicht schreiben: soll=0x%04x, ist=0x%04x", decoderDaten, istDecoderDaten));
    }
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
    } catch (InterruptedException e) {
    }
  }
}
