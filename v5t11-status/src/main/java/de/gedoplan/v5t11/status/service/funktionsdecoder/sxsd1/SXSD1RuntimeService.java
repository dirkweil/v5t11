package de.gedoplan.v5t11.status.service.funktionsdecoder.sxsd1;

import de.gedoplan.v5t11.status.entity.BausteinConfiguration;
import de.gedoplan.v5t11.status.entity.baustein.Baustein;
import de.gedoplan.v5t11.status.entity.baustein.Funktionsdecoder;
import de.gedoplan.v5t11.status.entity.baustein.funktionsdecoder.SXSD1;
import de.gedoplan.v5t11.status.service.BausteinConfigurationService;
import de.gedoplan.v5t11.status.service.ConfigurationRuntimeService;
import de.gedoplan.v5t11.status.service.Current;
import de.gedoplan.v5t11.status.service.Programmierfamilie;

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

  protected SXSD1RuntimeService() {
  }

  @Override
  public void getRuntimeValues() {
    this.configuration.setAdresseIst(this.steuerung.getSX1Kanal(0));
  }

  @Override
  public void setRuntimeValues() {
    // Betriebsmodus "Output 16"
    this.steuerung.setSX1Kanal(5, 3);

    this.steuerung.setSX1Kanal(0, this.configuration.getAdresseIst());
    this.steuerung.setSX1Kanal(2, this.configuration.getAdresseIst() + 1);
  }

}
