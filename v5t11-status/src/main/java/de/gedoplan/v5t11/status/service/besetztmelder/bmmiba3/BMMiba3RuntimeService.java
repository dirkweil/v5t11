package de.gedoplan.v5t11.status.service.besetztmelder.bmmiba3;

import de.gedoplan.v5t11.status.entity.BausteinConfiguration;
import de.gedoplan.v5t11.status.entity.baustein.Baustein;
import de.gedoplan.v5t11.status.entity.baustein.besetztmelder.BMMiba3;
import de.gedoplan.v5t11.status.service.BausteinConfigurationService;
import de.gedoplan.v5t11.status.service.ConfigurationRuntimeService;
import de.gedoplan.v5t11.status.service.Current;
import de.gedoplan.v5t11.status.service.Programmierfamilie;
import de.gedoplan.v5t11.status.service.besetztmelder.bmmiba3.BMMiba3ConfigurationAdapter.MeldungsModus;
import de.gedoplan.v5t11.status.service.besetztmelder.bmmiba3.BMMiba3ConfigurationAdapter.MeldungsSpeicherung;
import de.gedoplan.v5t11.status.service.besetztmelder.bmmiba3.BMMiba3ConfigurationAdapter.Zeittakt;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;

import lombok.Getter;

/**
 * Service zum Lesen und Schreiben der Konfiguration eines Besetztmelders des Typs BM-MIBA 3.
 *
 * @author dw
 */
@ConversationScoped
@Programmierfamilie(BMMiba3.class)
public class BMMiba3RuntimeService extends ConfigurationRuntimeService {

  private static final int LOCAL_ADR_ADR = 0;
  private static final int LOCAL_ADR_ANSPRECHVERZOEGERUNG = 1;
  private static final int LOCAL_ADR_ABFALLVERZOEGERUNG = 2;
  private static final int LOCAL_ADR_OPTIONS = 3;
  private static final int[] LOCAL_ADRESSEN = { LOCAL_ADR_ADR, LOCAL_ADR_ANSPRECHVERZOEGERUNG, LOCAL_ADR_ABFALLVERZOEGERUNG, LOCAL_ADR_OPTIONS };

  @Getter
  private BMMiba3ConfigurationAdapter configuration;

  @Inject
  public BMMiba3RuntimeService(@Current Baustein baustein, BausteinConfigurationService bausteinConfigurationService) {
    BausteinConfiguration bausteinSollConfiguration = bausteinConfigurationService.getBausteinConfiguration(baustein);
    BausteinConfiguration bausteinIstConfiguration = new BausteinConfiguration(baustein.getId());
    this.configuration = new BMMiba3ConfigurationAdapter(bausteinIstConfiguration, bausteinSollConfiguration);
  }

  protected BMMiba3RuntimeService() {
  }

  @Override
  public void getRuntimeValues() {
    this.configuration.setLocalAdrIst(getWert(LOCAL_ADR_ADR));

    this.configuration.getAnsprechVerzoegerung().setIst(getWert(LOCAL_ADR_ANSPRECHVERZOEGERUNG));

    this.configuration.getAbfallVerzoegerung().setIst(getWert(LOCAL_ADR_ABFALLVERZOEGERUNG));

    int options = getWert(LOCAL_ADR_OPTIONS);
    this.configuration.getMeldungBeiZeStopp().setIst(MeldungsModus.valueOf(options & 0b00000011));
    this.configuration.getMeldungBeiFehlendemFahrstrom().setIst(MeldungsModus.valueOf((options & 0b00001100) >> 2));
    this.configuration.getMeldungsNegation().setIst((options & 0b00010000) != 0);
    this.configuration.getZeittakt().setIst(Zeittakt.valueof((options & 0b01100000) >> 5));
    this.configuration.getMeldungsSpeicherung().setIst(MeldungsSpeicherung.valueof((options & 0b10000000) >> 7));
  }

  @Override
  public void setRuntimeValues() {
    setWert(LOCAL_ADR_ADR, this.configuration.getLocalAdrIst());
    setWert(LOCAL_ADR_ANSPRECHVERZOEGERUNG, this.configuration.getAnsprechVerzoegerung().getIst());
    setWert(LOCAL_ADR_ABFALLVERZOEGERUNG, this.configuration.getAbfallVerzoegerung().getIst());
    int options = this.configuration.getMeldungBeiZeStopp().getIst().getBits();
    options |= this.configuration.getMeldungBeiFehlendemFahrstrom().getIst().getBits() << 2;
    if (this.configuration.getMeldungsNegation().getIst()) {
      options |= 0b00010000;
    }
    options |= this.configuration.getZeittakt().getIst().getBits() << 5;
    options |= this.configuration.getMeldungsSpeicherung().getIst().getBits() << 7;
    setWert(LOCAL_ADR_OPTIONS, options);
  }

  @Override
  protected int[] getProgLocalAdressen() {
    return LOCAL_ADRESSEN;
  }
}
