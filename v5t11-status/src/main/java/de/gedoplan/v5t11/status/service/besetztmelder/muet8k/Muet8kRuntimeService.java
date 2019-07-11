package de.gedoplan.v5t11.status.service.besetztmelder.muet8k;

import de.gedoplan.v5t11.status.entity.BausteinConfiguration;
import de.gedoplan.v5t11.status.entity.baustein.Baustein;
import de.gedoplan.v5t11.status.entity.baustein.besetztmelder.Muet8k;
import de.gedoplan.v5t11.status.service.BausteinConfigurationService;
import de.gedoplan.v5t11.status.service.ConfigurationRuntimeService;
import de.gedoplan.v5t11.status.service.Current;
import de.gedoplan.v5t11.status.service.Programmierfamilie;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;

import lombok.Getter;

/**
 * Service zum Lesen und Schreiben der Konfiguration eines Besetztmelders des Typs Muet 8i.
 *
 * @author dw
 */
@ConversationScoped
@Programmierfamilie(Muet8k.class)
public class Muet8kRuntimeService extends ConfigurationRuntimeService {

  private static final int LOCAL_ADR_STEUER = 0;
  private static final int LOCAL_ADR_WERT = 1;
  private static final int[] LOCAL_ADRESSEN = { LOCAL_ADR_STEUER, LOCAL_ADR_WERT };

  @Getter
  private Muet8kConfigurationAdapter configuration;

  @Inject
  public Muet8kRuntimeService(@Current Baustein baustein, BausteinConfigurationService bausteinConfigurationService) {
    BausteinConfiguration bausteinSollConfiguration = bausteinConfigurationService.getBausteinConfiguration(baustein);
    BausteinConfiguration bausteinIstConfiguration = new BausteinConfiguration(baustein.getId());
    this.configuration = new Muet8kConfigurationAdapter(bausteinIstConfiguration, bausteinSollConfiguration);
  }

  protected Muet8kRuntimeService() {
  }

  @Override
  public void getRuntimeValues() {
    this.configuration.setLocalAdrIst(getParameter(LOCAL_ADR_STEUER, LOCAL_ADR_WERT, 1));

    int options = getParameter(LOCAL_ADR_STEUER, LOCAL_ADR_WERT, 2);
    this.configuration.getAbfallVerzoegerung().setIst((options & 0b0001_1111) * 80);
  }

  @Override
  public void setRuntimeValues() {
    setParameter(LOCAL_ADR_STEUER, LOCAL_ADR_WERT, 1, this.configuration.getLocalAdrIst());

    setParameter(LOCAL_ADR_STEUER, LOCAL_ADR_WERT, 2, (this.configuration.getAbfallVerzoegerung().getIst() / 80) & 0b0001_1111);
  }

  @Override
  protected int[] getProgLocalAdressen() {
    return LOCAL_ADRESSEN;
  }
}
