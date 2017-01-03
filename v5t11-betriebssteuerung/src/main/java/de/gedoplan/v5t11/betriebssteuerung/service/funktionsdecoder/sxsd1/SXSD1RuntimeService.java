package de.gedoplan.v5t11.betriebssteuerung.service.funktionsdecoder.sxsd1;

import de.gedoplan.v5t11.betriebssteuerung.entity.BausteinConfiguration;
import de.gedoplan.v5t11.betriebssteuerung.service.BausteinConfigurationService;
import de.gedoplan.v5t11.betriebssteuerung.service.ConfigurationRuntimeService;
import de.gedoplan.v5t11.betriebssteuerung.service.Current;
import de.gedoplan.v5t11.betriebssteuerung.service.Programmierfamilie;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.Baustein;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.Funktionsdecoder;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.funktionsdecoder.SXSD1;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;

import lombok.Getter;

/**
 * Service zum Lesen und Schreiben der Konfiguration eines Funktionsdecoders des Typs SX-SD 1.
 *
 * @author dw
 */
@ConversationScoped
@Programmierfamilie(SXSD1.class)
public class SXSD1RuntimeService extends ConfigurationRuntimeService {
  @Getter
  private SXSD1ConfigurationAdapter configuration;

  @Inject
  public SXSD1RuntimeService(@Current Baustein baustein, BausteinConfigurationService bausteinConfigurationService) {
    BausteinConfiguration bausteinSollConfiguration = bausteinConfigurationService.getBausteinConfiguration(baustein);
    BausteinConfiguration bausteinIstConfiguration = new BausteinConfiguration(baustein.getId());
    this.configuration = new SXSD1ConfigurationAdapter((Funktionsdecoder) baustein, bausteinIstConfiguration, bausteinSollConfiguration);
  }

  @Override
  public void getRuntimeValues() {
    this.configuration.setAdresseIst(this.selectrixGateway.getValue(0));
  }

  @Override
  public void setRuntimeValues() {
    // Betriebsmodus "Output 16"
    this.selectrixGateway.setValue(5, 3);

    this.selectrixGateway.setValue(0, this.configuration.getAdresseIst());
    this.selectrixGateway.setValue(2, this.configuration.getAdresseIst() + 1);
  }

}
