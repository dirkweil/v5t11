package de.gedoplan.v5t11.status.service.besetztmelder.muet8i;

import de.gedoplan.v5t11.status.entity.BausteinConfiguration;
import de.gedoplan.v5t11.status.entity.baustein.Baustein;
import de.gedoplan.v5t11.status.entity.baustein.besetztmelder.Muet8i;
import de.gedoplan.v5t11.status.service.BausteinConfigurationService;
import de.gedoplan.v5t11.status.service.ConfigurationRuntimeService;
import de.gedoplan.v5t11.status.service.Current;
import de.gedoplan.v5t11.status.service.Programmierfamilie;
import de.gedoplan.v5t11.status.service.besetztmelder.muet8i.Muet8iConfigurationAdapter.MeldungsModus;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

import lombok.Getter;

/**
 * Service zum Lesen und Schreiben der Konfiguration eines Besetztmelders des Typs Muet 8i.
 *
 * @author dw
 */
@Dependent
@Programmierfamilie(Muet8i.class)
public class Muet8iRuntimeService extends ConfigurationRuntimeService {

  private static final int LOCAL_ADR_STEUER = 0;
  private static final int LOCAL_ADR_WERT = 1;
  private static final int[] LOCAL_ADRESSEN = { LOCAL_ADR_STEUER, LOCAL_ADR_WERT };

  @Getter
  private Muet8iConfigurationAdapter configuration;

  @Inject
  public Muet8iRuntimeService(@Current Baustein baustein, BausteinConfigurationService bausteinConfigurationService) {
    super(baustein);

    BausteinConfiguration bausteinSollConfiguration = bausteinConfigurationService.getBausteinConfiguration(baustein);
    BausteinConfiguration bausteinIstConfiguration = new BausteinConfiguration(baustein.getId());
    this.configuration = new Muet8iConfigurationAdapter(bausteinIstConfiguration, bausteinSollConfiguration);
  }

  protected Muet8iRuntimeService() {
  }

  @Override
  public void getRuntimeValues() {
    this.configuration.setLocalAdrIst(getParameter(LOCAL_ADR_STEUER, LOCAL_ADR_WERT, 1));

    int options = getParameter(LOCAL_ADR_STEUER, LOCAL_ADR_WERT, 2);
    this.configuration.getAbfallVerzoegerung().setIst((options & 0b0000_0111) * 350);
    this.configuration.getMeldungBeiZeStopp().setIst(MeldungsModus.valueOf(options & 0b0100_0000));
    this.configuration.getMeldungsNegation().setIst((options & 0b1000_0000) != 0);
  }

  @Override
  public void setRuntimeValues() {
    setParameter(LOCAL_ADR_STEUER, LOCAL_ADR_WERT, 1, this.configuration.getLocalAdrIst());

    int options = this.configuration.getAbfallVerzoegerung().getIst() / 350;
    options |= this.configuration.getMeldungBeiZeStopp().getIst().getBits();
    if (this.configuration.getMeldungsNegation().getIst()) {
      options |= 0b1000_0000;
    }
    setParameter(LOCAL_ADR_STEUER, LOCAL_ADR_WERT, 2, options);
  }

  @Override
  protected int[] getProgLocalAdressen() {
    return LOCAL_ADRESSEN;
  }
}
