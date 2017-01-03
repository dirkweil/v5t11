package de.gedoplan.v5t11.betriebssteuerung.service.funktionsdecoder.strfd1;

import de.gedoplan.v5t11.betriebssteuerung.entity.BausteinConfiguration;
import de.gedoplan.v5t11.betriebssteuerung.service.BausteinConfigurationService;
import de.gedoplan.v5t11.betriebssteuerung.service.ConfigurationRuntimeService;
import de.gedoplan.v5t11.betriebssteuerung.service.Current;
import de.gedoplan.v5t11.betriebssteuerung.service.Programmierfamilie;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.Baustein;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.Funktionsdecoder;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.funktionsdecoder.STRFD1;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;

import lombok.Getter;

/**
 * Service zum Lesen und Schreiben der Konfiguration eines Funktionsdecoders des Typs STR-FD 1.
 *
 * @author dw
 */
@ConversationScoped
@Programmierfamilie(STRFD1.class)
public class STRFD1RuntimeService extends ConfigurationRuntimeService {
  @Getter
  private STRFD1ConfigurationAdapter configuration;

  @Inject
  public STRFD1RuntimeService(@Current Baustein baustein, BausteinConfigurationService bausteinConfigurationService) {
    BausteinConfiguration bausteinSollConfiguration = bausteinConfigurationService.getBausteinConfiguration(baustein);
    BausteinConfiguration bausteinIstConfiguration = new BausteinConfiguration(baustein.getId());
    this.configuration = new STRFD1ConfigurationAdapter((Funktionsdecoder) baustein, bausteinIstConfiguration, bausteinSollConfiguration);
  }

  @Override
  public void getRuntimeValues() {
    this.configuration.setAdresseIst(this.selectrixGateway.getValue(0));

    int betriebsArt = this.selectrixGateway.getValue(1);
    for (int i = 0, bit = 1; i < 8; ++i, bit <<= 1) {
      this.configuration.getDauer()[i].setIst((betriebsArt & bit) != 0);
    }

    this.configuration.getImpulsDauer().setIst(this.selectrixGateway.getValue(3) * 80);
  }

  @Override
  public void setRuntimeValues() {
    // Betriebsmodus "Output 8"
    this.selectrixGateway.setValue(5, 1);

    this.selectrixGateway.setValue(0, this.configuration.getAdresseIst());

    int betriebsArt = 0;
    for (int i = 0, bit = 1; i < 8; ++i, bit <<= 1) {
      if (this.configuration.getDauer()[i].getIst()) {
        betriebsArt |= bit;
      }
    }
    this.selectrixGateway.setValue(1, betriebsArt);

    int verz = this.configuration.getImpulsDauer().getIst() / 80;
    if (verz < 0) {
      verz = 0;
    }
    if (verz > 255) {
      verz = 255;
    }
    this.selectrixGateway.setValue(3, verz);
  }

}
