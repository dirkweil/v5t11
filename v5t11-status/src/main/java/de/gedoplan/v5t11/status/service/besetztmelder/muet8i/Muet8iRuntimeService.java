package de.gedoplan.v5t11.status.service.besetztmelder.muet8i;

import de.gedoplan.v5t11.status.entity.BausteinConfiguration;
import de.gedoplan.v5t11.status.entity.baustein.Baustein;
import de.gedoplan.v5t11.status.entity.baustein.besetztmelder.Muet8i;
import de.gedoplan.v5t11.status.service.BausteinConfigurationService;
import de.gedoplan.v5t11.status.service.ConfigurationRuntimeService;
import de.gedoplan.v5t11.status.service.Current;
import de.gedoplan.v5t11.status.service.Programmierfamilie;
import de.gedoplan.v5t11.status.service.besetztmelder.muet8i.Muet8iConfigurationAdapter.MeldungsModus;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;

import lombok.Getter;

/**
 * Service zum Lesen und Schreiben der Konfiguration eines Besetztmelders des Typs Muet 8i.
 *
 * @author dw
 */
@ConversationScoped
@Programmierfamilie(Muet8i.class)
public class Muet8iRuntimeService extends ConfigurationRuntimeService {

  private static final int STEUER_ADR = 0;
  private static final int WERT_ADR = 1;

  @Getter
  private Muet8iConfigurationAdapter configuration;

  @Inject
  public Muet8iRuntimeService(@Current Baustein baustein, BausteinConfigurationService bausteinConfigurationService) {
    BausteinConfiguration bausteinSollConfiguration = bausteinConfigurationService.getBausteinConfiguration(baustein);
    BausteinConfiguration bausteinIstConfiguration = new BausteinConfiguration(baustein.getId());
    this.configuration = new Muet8iConfigurationAdapter(bausteinIstConfiguration, bausteinSollConfiguration);
  }

  protected Muet8iRuntimeService() {
  }

  @Override
  public void getRuntimeValues() {
    this.configuration.setAdresseIst(getParameter(STEUER_ADR, WERT_ADR, 1));

    int options = getParameter(STEUER_ADR, WERT_ADR, 2);
    this.configuration.getAbfallVerzoegerung().setIst((options & 0b0000_0111) * 350);
    this.configuration.getMeldungBeiZeStopp().setIst(MeldungsModus.valueOf(options & 0b0100_0000));
    this.configuration.getMeldungsNegation().setIst((options & 0b1000_0000) != 0);
  }

  @Override
  public void setRuntimeValues() {
    setParameter(STEUER_ADR, WERT_ADR, 1, this.configuration.getAdresseIst());

    int options = this.configuration.getAbfallVerzoegerung().getIst() / 350;
    options |= this.configuration.getMeldungBeiZeStopp().getIst().getBits();
    if (this.configuration.getMeldungsNegation().getIst()) {
      options |= 0b1000_0000;
    }
    setParameter(STEUER_ADR, WERT_ADR, 2, options);
  }

}
