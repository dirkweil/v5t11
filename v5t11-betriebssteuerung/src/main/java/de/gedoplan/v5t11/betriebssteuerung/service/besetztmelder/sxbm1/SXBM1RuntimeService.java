package de.gedoplan.v5t11.betriebssteuerung.service.besetztmelder.sxbm1;

import de.gedoplan.v5t11.betriebssteuerung.service.ConfigurationRuntimeService;

import javax.enterprise.context.ApplicationScoped;

/**
 * Service zum Lesen und Schreiben der Konfiguration eines Besetztmelders des Typs SX-BM 1.
 *
 * @author dw
 */
@ApplicationScoped
public class SXBM1RuntimeService extends ConfigurationRuntimeService<SXBM1ConfigurationAdapter> {
  @Override
  public void getRuntimeValues(SXBM1ConfigurationAdapter configuration) {
    configuration.setAdresseIst(this.selectrixGateway.getValue(0));

    configuration.getAbfallVerzoegerung().setIst(this.selectrixGateway.getValue(3) * 80);
  }

  @Override
  public void setRuntimeValues(SXBM1ConfigurationAdapter configuration) {
    // Betriebsmodus "Input 8"
    this.selectrixGateway.setValue(5, 0);

    this.selectrixGateway.setValue(0, configuration.getAdresseIst());

    int verz = configuration.getAbfallVerzoegerung().getIst() / 80;
    if (verz < 0) {
      verz = 0;
    }
    if (verz > 255) {
      verz = 255;
    }
    this.selectrixGateway.setValue(3, verz);
  }

}
