package de.gedoplan.v5t11.betriebssteuerung.service.funktionsdecoder.wdmiba3;

import de.gedoplan.v5t11.betriebssteuerung.service.ConfigurationRuntimeService;

import javax.enterprise.context.ApplicationScoped;

/**
 * Service zum Lesen und Schreiben der Konfiguration eines Funktionsdecoders des Typs WD-MIBA 3.
 *
 * @author dw
 */
@ApplicationScoped
public class WDMiba3RuntimeService extends ConfigurationRuntimeService<WDMiba3ConfigurationAdapter> {
  @Override
  public void getRuntimeValues(WDMiba3ConfigurationAdapter configuration) {
    configuration.setAdresseIst(this.selectrixGateway.getValue(0));

    int betriebsArt = this.selectrixGateway.getValue(1);
    for (int i = 0, bit = 1; i < 8; ++i, bit <<= 1) {
      configuration.getDauer()[i].setIst((betriebsArt & bit) != 0);
    }

    configuration.getImpulsDauer().setIst(this.selectrixGateway.getValue(2) * 80);
  }

  @Override
  public void setRuntimeValues(WDMiba3ConfigurationAdapter configuration) {
    this.selectrixGateway.setValue(0, configuration.getAdresseIst());

    int betriebsArt = 0;
    for (int i = 0, bit = 1; i < 8; ++i, bit <<= 1) {
      if (configuration.getDauer()[i].getIst()) {
        betriebsArt |= bit;
      }
    }
    this.selectrixGateway.setValue(1, betriebsArt);

    int dauerIn80ms = configuration.getImpulsDauer().getIst() / 80;
    if (dauerIn80ms < 1) {
      dauerIn80ms = 1;
    }
    if (dauerIn80ms > 254) {
      dauerIn80ms = 254;
    }
    this.selectrixGateway.setValue(2, dauerIn80ms);
    this.selectrixGateway.setValue(3, 255);
  }
}
