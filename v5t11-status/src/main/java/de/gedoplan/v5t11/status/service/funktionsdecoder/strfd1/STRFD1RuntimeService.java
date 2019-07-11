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

  private static final int LOCAL_ADR_ADR = 0;
  private static final int LOCAL_ADR_BETRIEBSART = 1;
  private static final int LOCAL_ADR_IMPULSDAUER = 3;
  private static final int LOCAL_ADR_MODUS = 5;
  private static final int[] LOCAL_ADRESSEN = { LOCAL_ADR_ADR, LOCAL_ADR_BETRIEBSART, LOCAL_ADR_IMPULSDAUER, LOCAL_ADR_MODUS };

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
    this.configuration.setLocalAdrIst(getWert(LOCAL_ADR_ADR));

    int betriebsArt = getWert(LOCAL_ADR_BETRIEBSART);
    for (int i = 0, bit = 1; i < 8; ++i, bit <<= 1) {
      this.configuration.getDauer()[i].setIst((betriebsArt & bit) != 0);
    }

    this.configuration.getImpulsDauer().setIst(getWert(LOCAL_ADR_IMPULSDAUER) * 80);
  }

  @Override
  public void setRuntimeValues() {
    // Betriebsmodus "Output 8"
    setWert(LOCAL_ADR_MODUS, 1);

    setWert(LOCAL_ADR_ADR, this.configuration.getLocalAdrIst());

    int betriebsArt = 0;
    for (int i = 0, bit = 1; i < 8; ++i, bit <<= 1) {
      if (this.configuration.getDauer()[i].getIst()) {
        betriebsArt |= bit;
      }
    }
    setWert(LOCAL_ADR_BETRIEBSART, betriebsArt);

    int verz = this.configuration.getImpulsDauer().getIst() / 80;
    if (verz < 0) {
      verz = 0;
    }
    if (verz > 255) {
      verz = 255;
    }
    setWert(LOCAL_ADR_IMPULSDAUER, verz);
  }

  @Override
  protected int[] getProgLocalAdressen() {
    return LOCAL_ADRESSEN;
  }
}
