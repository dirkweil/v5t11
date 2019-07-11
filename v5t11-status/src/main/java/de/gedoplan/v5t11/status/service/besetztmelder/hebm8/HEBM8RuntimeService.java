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

  private static final int LOCAL_ADR_ADR = 0;
  private static final int LOCAL_ADR_ABFALLVERZOEGERUNG = 2;
  private static final int[] LOCAL_ADRESSEN = { LOCAL_ADR_ADR, LOCAL_ADR_ABFALLVERZOEGERUNG };

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
    this.configuration.localAdrResetToSoll();
    this.configuration.getAbfallVerzoegerung().resetToSoll();
    ;
  }

  @Override
  public void setRuntimeValues() {
    setWert(LOCAL_ADR_ADR, this.configuration.getLocalAdrIst());

    int verz = this.configuration.getAbfallVerzoegerung().getIst() / 100;
    if (verz < 0) {
      verz = 0;
    }
    if (verz > 255) {
      verz = 255;
    }
    setWert(LOCAL_ADR_ABFALLVERZOEGERUNG, verz);
  }

  @Override
  public String getOpenProgModeMessage() {
    return "Von Bausteinen dieses Typs können keine Ist-Werte gelesen werden. Es werden daher die Soll-Werte dafür eingesetzt.";
  }

  @Override
  public String getCloseProgModeMessage() {
    return "Programmiertaster am Baustein so lange drücken, bis die LED aufleuchtet!";
  }

  @Override
  protected int[] getProgLocalAdressen() {
    return LOCAL_ADRESSEN;
  }
}
