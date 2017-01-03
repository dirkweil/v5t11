package de.gedoplan.v5t11.betriebssteuerung.service.funktionsdecoder.wdmiba3;

import de.gedoplan.v5t11.betriebssteuerung.entity.BausteinConfiguration;
import de.gedoplan.v5t11.betriebssteuerung.service.BausteinConfigurationService;
import de.gedoplan.v5t11.betriebssteuerung.service.ConfigurationRuntimeService;
import de.gedoplan.v5t11.betriebssteuerung.service.Current;
import de.gedoplan.v5t11.betriebssteuerung.service.Programmierfamilie;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.Baustein;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.Funktionsdecoder;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.funktionsdecoder.WDMiba3;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;

import lombok.Getter;

/**
 * Service zum Lesen und Schreiben der Konfiguration eines Funktionsdecoders des Typs WD-MIBA 3.
 *
 * @author dw
 */
@ConversationScoped
@Programmierfamilie(WDMiba3.class)
public class WDMiba3RuntimeService extends ConfigurationRuntimeService {
  @Getter
  private WDMiba3ConfigurationAdapter configuration;

  @Inject
  public WDMiba3RuntimeService(@Current Baustein baustein, BausteinConfigurationService bausteinConfigurationService) {
    BausteinConfiguration bausteinSollConfiguration = bausteinConfigurationService.getBausteinConfiguration(baustein);
    BausteinConfiguration bausteinIstConfiguration = new BausteinConfiguration(baustein.getId());
    this.configuration = new WDMiba3ConfigurationAdapter((Funktionsdecoder) baustein, bausteinIstConfiguration, bausteinSollConfiguration);
  }

  @Override
  public void getRuntimeValues() {
    this.configuration.setAdresseIst(this.selectrixGateway.getValue(0));

    int betriebsArt = this.selectrixGateway.getValue(1);
    for (int i = 0, bit = 1; i < 8; ++i, bit <<= 1) {
      this.configuration.getDauer()[i].setIst((betriebsArt & bit) != 0);
    }

    this.configuration.getImpulsDauer().setIst(this.selectrixGateway.getValue(2) * 80);
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

    int dauerIn80ms = this.configuration.getImpulsDauer().getIst() / 80;
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
