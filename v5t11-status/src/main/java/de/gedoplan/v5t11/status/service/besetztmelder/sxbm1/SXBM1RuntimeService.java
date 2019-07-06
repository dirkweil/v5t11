package de.gedoplan.v5t11.status.service.besetztmelder.sxbm1;

import de.gedoplan.v5t11.status.entity.BausteinConfiguration;
import de.gedoplan.v5t11.status.entity.baustein.Baustein;
import de.gedoplan.v5t11.status.entity.baustein.besetztmelder.SXBM1;
import de.gedoplan.v5t11.status.service.BausteinConfigurationService;
import de.gedoplan.v5t11.status.service.ConfigurationRuntimeService;
import de.gedoplan.v5t11.status.service.Current;
import de.gedoplan.v5t11.status.service.Programmierfamilie;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;

import lombok.Getter;

/**
 * Service zum Lesen und Schreiben der Konfiguration eines Besetztmelders des Typs SX-BM 1.
 *
 * @author dw
 */
@ConversationScoped
@Programmierfamilie(SXBM1.class)
public class SXBM1RuntimeService extends ConfigurationRuntimeService {
  @Getter
  private SXBM1ConfigurationAdapter configuration;

  @Inject
  public SXBM1RuntimeService(@Current Baustein baustein, BausteinConfigurationService bausteinConfigurationService) {
    BausteinConfiguration bausteinSollConfiguration = bausteinConfigurationService.getBausteinConfiguration(baustein);
    BausteinConfiguration bausteinIstConfiguration = new BausteinConfiguration(baustein.getId());
    this.configuration = new SXBM1ConfigurationAdapter(bausteinIstConfiguration, bausteinSollConfiguration);
  }

  protected SXBM1RuntimeService() {
  }

  @Override
  public void getRuntimeValues() {
    this.configuration.setAdresseIst(this.steuerung.getSX1Kanal(0));

    this.configuration.getAbfallVerzoegerung().setIst(this.steuerung.getSX1Kanal(3) * 80);
  }

  @Override
  public void setRuntimeValues() {
    // Betriebsmodus "Input 8"
    this.steuerung.setSX1Kanal(5, 0);

    this.steuerung.setSX1Kanal(0, this.configuration.getAdresseIst());

    int verz = this.configuration.getAbfallVerzoegerung().getIst() / 80;
    if (verz < 0) {
      verz = 0;
    }
    if (verz > 255) {
      verz = 255;
    }
    this.steuerung.setSX1Kanal(3, verz);
  }

}