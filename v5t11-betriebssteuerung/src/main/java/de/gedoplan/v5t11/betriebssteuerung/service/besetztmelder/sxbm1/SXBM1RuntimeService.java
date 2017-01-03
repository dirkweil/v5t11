package de.gedoplan.v5t11.betriebssteuerung.service.besetztmelder.sxbm1;

import de.gedoplan.v5t11.betriebssteuerung.entity.BausteinConfiguration;
import de.gedoplan.v5t11.betriebssteuerung.service.BausteinConfigurationService;
import de.gedoplan.v5t11.betriebssteuerung.service.ConfigurationRuntimeService;
import de.gedoplan.v5t11.betriebssteuerung.service.Current;
import de.gedoplan.v5t11.betriebssteuerung.service.Programmierfamilie;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.Baustein;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;

import lombok.Getter;

/**
 * Service zum Lesen und Schreiben der Konfiguration eines Besetztmelders des Typs SX-BM 1.
 *
 * @author dw
 */
@ConversationScoped
@Programmierfamilie("SXBM1")
public class SXBM1RuntimeService extends ConfigurationRuntimeService {
  @Getter
  private SXBM1ConfigurationAdapter configuration;

  @Inject
  public SXBM1RuntimeService(@Current Baustein baustein, BausteinConfigurationService bausteinConfigurationService) {
    BausteinConfiguration bausteinSollConfiguration = bausteinConfigurationService.getBausteinConfiguration(baustein);
    BausteinConfiguration bausteinIstConfiguration = new BausteinConfiguration(baustein.getId());
    this.configuration = new SXBM1ConfigurationAdapter(bausteinIstConfiguration, bausteinSollConfiguration);
  }

  @Override
  public void getRuntimeValues() {
    this.configuration.setAdresseIst(this.selectrixGateway.getValue(0));

    this.configuration.getAbfallVerzoegerung().setIst(this.selectrixGateway.getValue(3) * 80);
  }

  @Override
  public void setRuntimeValues() {
    // Betriebsmodus "Input 8"
    this.selectrixGateway.setValue(5, 0);

    this.selectrixGateway.setValue(0, this.configuration.getAdresseIst());

    int verz = this.configuration.getAbfallVerzoegerung().getIst() / 80;
    if (verz < 0) {
      verz = 0;
    }
    if (verz > 255) {
      verz = 255;
    }
    this.selectrixGateway.setValue(3, verz);
  }

}
