package de.gedoplan.v5t11.status.service.funktionsdecoder.wdmiba;

import de.gedoplan.v5t11.status.entity.BausteinConfiguration;
import de.gedoplan.v5t11.status.entity.baustein.Baustein;
import de.gedoplan.v5t11.status.entity.baustein.Funktionsdecoder;
import de.gedoplan.v5t11.status.entity.baustein.funktionsdecoder.WDMiba;
import de.gedoplan.v5t11.status.service.BausteinConfigurationService;
import de.gedoplan.v5t11.status.service.ConfigurationRuntimeService;
import de.gedoplan.v5t11.status.service.Current;
import de.gedoplan.v5t11.status.service.Programmierfamilie;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;

import lombok.Getter;

/**
 * Service zum Lesen und Schreiben der Konfiguration eines Funktionsdecoders des Typs WD-MIBA.
 *
 * @author dw
 */
@ConversationScoped
@Programmierfamilie(WDMiba.class)
public class WDMibaRuntimeService extends ConfigurationRuntimeService {
  @Getter
  private WDMibaConfigurationAdapter configuration;

  @Inject
  public WDMibaRuntimeService(@Current Baustein baustein, BausteinConfigurationService bausteinConfigurationService) {
    BausteinConfiguration bausteinSollConfiguration = bausteinConfigurationService.getBausteinConfiguration(baustein);
    BausteinConfiguration bausteinIstConfiguration = new BausteinConfiguration(baustein.getId());
    this.configuration = new WDMibaConfigurationAdapter((Funktionsdecoder) baustein, bausteinIstConfiguration, bausteinSollConfiguration);
  }

  protected WDMibaRuntimeService() {
  }

  @Override
  public void getRuntimeValues() {
    this.configuration.setAdresseIst(this.selectrixGateway.getValue(0));

    int betriebsArt = this.selectrixGateway.getValue(1);
    for (int i = 0, bit = 1; i < 8; ++i, bit <<= 1) {
      this.configuration.getDauer()[i].setIst((betriebsArt & bit) != 0);
    }
  }

  @Override
  public void setRuntimeValues() {
    this.selectrixGateway.setValue(0, this.configuration.getAdresseIst());

    int betriebsArt = 0;
    for (int i = 0, bit = 1; i < 8; ++i, bit <<= 1) {
      if (this.configuration.getDauer()[i].getIst()) {
        betriebsArt |= bit;
      }
    }
    this.selectrixGateway.setValue(1, betriebsArt);
  }

}
