package de.gedoplan.v5t11.status.service.besetztmelder.hebm8;

import de.gedoplan.v5t11.status.entity.BausteinConfiguration;
import de.gedoplan.v5t11.status.entity.baustein.Baustein;
import de.gedoplan.v5t11.status.entity.baustein.besetztmelder.HEBM8;
import de.gedoplan.v5t11.status.service.BausteinConfigurationService;
import de.gedoplan.v5t11.status.service.ConfigurationRuntimeService;
import de.gedoplan.v5t11.status.service.Current;
import de.gedoplan.v5t11.status.service.Programmierfamilie;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;

import lombok.Getter;

/**
 * Service zum Lesen und Schreiben der Konfiguration eines Besetztmelders des Typs Engelmann BM8.
 *
 * @author dw
 */
@ConversationScoped
@Programmierfamilie(HEBM8.class)
public class HEBM8RuntimeService extends ConfigurationRuntimeService {
  @Getter
  private HEBM8ConfigurationAdapter configuration;

  @Inject
  public HEBM8RuntimeService(@Current Baustein baustein, BausteinConfigurationService bausteinConfigurationService) {
    BausteinConfiguration bausteinSollConfiguration = bausteinConfigurationService.getBausteinConfiguration(baustein);
    BausteinConfiguration bausteinIstConfiguration = new BausteinConfiguration(baustein.getId());
    this.configuration = new HEBM8ConfigurationAdapter(bausteinIstConfiguration, bausteinSollConfiguration);
  }

  protected HEBM8RuntimeService() {
  }

  @Override
  public void getRuntimeValues() {
    // Werte können nicht gelesen werden, daher nur auf Soll setzen
    this.configuration.adresseResetToSoll();
    this.configuration.getAbfallVerzoegerung().resetToSoll();
    ;
  }

  @Override
  public void setRuntimeValues() {
    this.steuerung.setSX1Kanal(0, this.configuration.getAdresseIst());

    int verz = this.configuration.getAbfallVerzoegerung().getIst() / 100;
    if (verz < 0) {
      verz = 0;
    }
    if (verz > 255) {
      verz = 255;
    }
    this.steuerung.setSX1Kanal(1, verz);
  }

  @Override
  public String getOpenProgModeMessage() {
    return "Von Bausteinen dieses Typs können keine Ist-Werte gelesen werden. Es werden daher die Soll-Werte dafür eingesetzt.";
  }

  @Override
  public String getCloseProgModeMessage() {
    return "Programmiertaster am Baustein so lange drücken, bis die LED aufleuchtet!";
  }

}
