package de.gedoplan.v5t11.status.service.besetztmelder.vm5262;

import de.gedoplan.v5t11.status.entity.BausteinConfiguration;
import de.gedoplan.v5t11.status.entity.baustein.Baustein;
import de.gedoplan.v5t11.status.entity.baustein.besetztmelder.VM5262;
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
@Programmierfamilie(VM5262.class)
public class VM5262RuntimeService extends ConfigurationRuntimeService {

  private static final int LOCAL_ADR_ADR = 0;
  private static final int LOCAL_ADR_ANSPRECHVERZOEGERUNG = 1;
  private static final int LOCAL_ADR_ABFALLVERZOEGERUNG = 2;
  private static final int[] LOCAL_ADRESSEN = { LOCAL_ADR_ADR, LOCAL_ADR_ABFALLVERZOEGERUNG };

  @Getter
  private VM5262ConfigurationAdapter configuration;

  @Inject
  public VM5262RuntimeService(@Current Baustein baustein, BausteinConfigurationService bausteinConfigurationService) {
    BausteinConfiguration bausteinSollConfiguration = bausteinConfigurationService.getBausteinConfiguration(baustein);
    BausteinConfiguration bausteinIstConfiguration = new BausteinConfiguration(baustein.getId());
    this.configuration = new VM5262ConfigurationAdapter(bausteinIstConfiguration, bausteinSollConfiguration);
  }

  protected VM5262RuntimeService() {
  }

  @Override
  public void getRuntimeValues() {
    this.configuration.setLocalAdrIst(getWert(LOCAL_ADR_ADR));
    this.configuration.getAnsprechVerzoegerung().setIst(getWert(LOCAL_ADR_ANSPRECHVERZOEGERUNG) * 100);
    this.configuration.getAbfallVerzoegerung().setIst(getWert(LOCAL_ADR_ABFALLVERZOEGERUNG) * 100);
  }

  @Override
  public void setRuntimeValues() {
    setWert(LOCAL_ADR_ADR, this.configuration.getLocalAdrIst());

    int verz = this.configuration.getAnsprechVerzoegerung().getIst() / 100;
    setWert(LOCAL_ADR_ANSPRECHVERZOEGERUNG, verz < 0 ? 0 : (verz > 255 ? 255 : verz));

    verz = this.configuration.getAbfallVerzoegerung().getIst() / 100;
    setWert(LOCAL_ADR_ABFALLVERZOEGERUNG, verz < 0 ? 0 : (verz > 255 ? 255 : verz));
  }

  @Override
  protected int[] getProgLocalAdressen() {
    return LOCAL_ADRESSEN;
  }
}
