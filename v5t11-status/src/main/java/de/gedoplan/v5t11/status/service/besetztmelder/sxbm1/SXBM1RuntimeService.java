package de.gedoplan.v5t11.status.service.besetztmelder.sxbm1;

import de.gedoplan.v5t11.status.entity.BausteinConfiguration;
import de.gedoplan.v5t11.status.entity.baustein.Baustein;
import de.gedoplan.v5t11.status.entity.baustein.besetztmelder.SXBM1;
import de.gedoplan.v5t11.status.service.BausteinConfigurationService;
import de.gedoplan.v5t11.status.service.ConfigurationRuntimeService;
import de.gedoplan.v5t11.status.service.Current;
import de.gedoplan.v5t11.status.service.Programmierfamilie;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

import lombok.Getter;

/**
 * Service zum Lesen und Schreiben der Konfiguration eines Besetztmelders des Typs SX-BM 1.
 *
 * @author dw
 */
@Dependent
@Programmierfamilie(SXBM1.class)
public class SXBM1RuntimeService extends ConfigurationRuntimeService {

  private static final int LOCAL_ADR_ADR = 0;
  private static final int LOCAL_ADR_ABFALLVERZOEGERUNG = 3;
  private static final int LOCAL_ADR_MODUS = 5;
  private static final int[] LOCAL_ADRESSEN = { LOCAL_ADR_ADR, LOCAL_ADR_ABFALLVERZOEGERUNG, LOCAL_ADR_MODUS };

  @Getter
  private SXBM1ConfigurationAdapter configuration;

  @Inject
  public SXBM1RuntimeService(@Current Baustein baustein, BausteinConfigurationService bausteinConfigurationService) {
    super(baustein);

    BausteinConfiguration bausteinSollConfiguration = bausteinConfigurationService.getBausteinConfiguration(baustein);
    BausteinConfiguration bausteinIstConfiguration = new BausteinConfiguration(baustein.getId());
    this.configuration = new SXBM1ConfigurationAdapter(bausteinIstConfiguration, bausteinSollConfiguration);
  }

  protected SXBM1RuntimeService() {
  }

  @Override
  public void getRuntimeValues() {
    this.configuration.setLocalAdrIst(getWert(LOCAL_ADR_ADR));

    this.configuration.getAbfallVerzoegerung().setIst(getWert(LOCAL_ADR_ABFALLVERZOEGERUNG) * 80);
  }

  @Override
  public void setRuntimeValues() {
    // Betriebsmodus "Input 8"
    setWert(LOCAL_ADR_MODUS, 0);

    setWert(LOCAL_ADR_ADR, this.configuration.getLocalAdrIst());

    int verz = this.configuration.getAbfallVerzoegerung().getIst() / 80;
    if (verz < 0) {
      verz = 0;
    }
    if (verz > 255) {
      verz = 255;
    }
    setWert(LOCAL_ADR_ABFALLVERZOEGERUNG, verz);
  }

  @Override
  protected int[] getProgLocalAdressen() {
    return LOCAL_ADRESSEN;
  }
}
