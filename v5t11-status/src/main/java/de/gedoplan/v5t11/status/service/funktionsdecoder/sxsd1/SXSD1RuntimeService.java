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

  private static final int LOCAL_ADR_ADR = 0;
  private static final int LOCAL_ADR_ADR2 = 2;
  private static final int LOCAL_ADR_MODUS = 5;
  private static final int[] LOCAL_ADRESSEN = { LOCAL_ADR_ADR, LOCAL_ADR_ADR2, LOCAL_ADR_MODUS };

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
    this.configuration.setLocalAdrIst(getWert(LOCAL_ADR_ADR));
  }

  @Override
  public void setRuntimeValues() {
    // Betriebsmodus "Output 16"
    setWert(LOCAL_ADR_MODUS, 3);

    setWert(LOCAL_ADR_ADR, this.configuration.getLocalAdrIst());
    setWert(LOCAL_ADR_ADR2, this.configuration.getLocalAdrIst() + 1);
  }

  @Override
  protected int[] getProgLocalAdressen() {
    return LOCAL_ADRESSEN;
  }
}
