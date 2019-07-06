package de.gedoplan.v5t11.status.service.funktionsdecoder.strfd1;

import de.gedoplan.v5t11.status.entity.BausteinConfiguration;
import de.gedoplan.v5t11.status.entity.baustein.Baustein;
import de.gedoplan.v5t11.status.entity.baustein.Funktionsdecoder;
import de.gedoplan.v5t11.status.entity.baustein.funktionsdecoder.STRFD1;
import de.gedoplan.v5t11.status.service.BausteinConfigurationService;
import de.gedoplan.v5t11.status.service.ConfigurationRuntimeService;
import de.gedoplan.v5t11.status.service.Current;
import de.gedoplan.v5t11.status.service.Programmierfamilie;

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

  protected STRFD1RuntimeService() {
  }

  @Override
  public void getRuntimeValues() {
    this.configuration.setAdresseIst(this.steuerung.getSX1Kanal(0));

    int betriebsArt = this.steuerung.getSX1Kanal(1);
    for (int i = 0, bit = 1; i < 8; ++i, bit <<= 1) {
      this.configuration.getDauer()[i].setIst((betriebsArt & bit) != 0);
    }

    this.configuration.getImpulsDauer().setIst(this.steuerung.getSX1Kanal(3) * 80);
  }

  @Override
  public void setRuntimeValues() {
    // Betriebsmodus "Output 8"
    this.steuerung.setSX1Kanal(5, 1);

    this.steuerung.setSX1Kanal(0, this.configuration.getAdresseIst());

    int betriebsArt = 0;
    for (int i = 0, bit = 1; i < 8; ++i, bit <<= 1) {
      if (this.configuration.getDauer()[i].getIst()) {
        betriebsArt |= bit;
      }
    }
    this.steuerung.setSX1Kanal(1, betriebsArt);

    int verz = this.configuration.getImpulsDauer().getIst() / 80;
    if (verz < 0) {
      verz = 0;
    }
    if (verz > 255) {
      verz = 255;
    }
    this.steuerung.setSX1Kanal(3, verz);
  }

}
