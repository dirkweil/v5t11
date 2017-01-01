package de.gedoplan.v5t11.betriebssteuerung.service.funktionsdecoder.sxsd1;

import de.gedoplan.v5t11.betriebssteuerung.service.ConfigurationRuntimeService;

import javax.enterprise.context.ApplicationScoped;

/**
 * Service zum Lesen und Schreiben der Konfiguration eines Funktionsdecoders des Typs SX-SD 1.
 *
 * @author dw
 */
@ApplicationScoped
public class SXSD1RuntimeService extends ConfigurationRuntimeService<SXSD1ConfigurationAdapter> {
  @Override
  public void getRuntimeValues(SXSD1ConfigurationAdapter configuration) {
    configuration.setAdresseIst(this.selectrixGateway.getValue(0));
  }

  @Override
  public void setRuntimeValues(SXSD1ConfigurationAdapter configuration) {
    // Betriebsmodus "Output 16"
    this.selectrixGateway.setValue(5, 3);

    this.selectrixGateway.setValue(0, configuration.getAdresseIst());
    this.selectrixGateway.setValue(2, configuration.getAdresseIst() + 1);
  }

}
