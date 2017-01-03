package de.gedoplan.v5t11.betriebssteuerung.service.besetztmelder.bmmiba3;

import de.gedoplan.v5t11.betriebssteuerung.entity.BausteinConfiguration;
import de.gedoplan.v5t11.betriebssteuerung.service.BausteinConfigurationService;
import de.gedoplan.v5t11.betriebssteuerung.service.ConfigurationRuntimeService;
import de.gedoplan.v5t11.betriebssteuerung.service.Current;
import de.gedoplan.v5t11.betriebssteuerung.service.Programmierfamilie;
import de.gedoplan.v5t11.betriebssteuerung.service.besetztmelder.bmmiba3.BMMiba3ConfigurationAdapter.MeldungsModus;
import de.gedoplan.v5t11.betriebssteuerung.service.besetztmelder.bmmiba3.BMMiba3ConfigurationAdapter.MeldungsSpeicherung;
import de.gedoplan.v5t11.betriebssteuerung.service.besetztmelder.bmmiba3.BMMiba3ConfigurationAdapter.Zeittakt;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.Baustein;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;

import lombok.Getter;

/**
 * Service zum Lesen und Schreiben der Konfiguration eines Besetztmelders des Typs BM-MIBA 3.
 *
 * @author dw
 */
@ConversationScoped
@Programmierfamilie("BMMiba3")
public class BMMiba3RuntimeService extends ConfigurationRuntimeService {

  @Getter
  private BMMiba3ConfigurationAdapter configuration;

  @Inject
  public BMMiba3RuntimeService(@Current Baustein baustein, BausteinConfigurationService bausteinConfigurationService) {
    BausteinConfiguration bausteinSollConfiguration = bausteinConfigurationService.getBausteinConfiguration(baustein);
    BausteinConfiguration bausteinIstConfiguration = new BausteinConfiguration(baustein.getId());
    this.configuration = new BMMiba3ConfigurationAdapter(bausteinIstConfiguration, bausteinSollConfiguration);
  }

  @Override
  public void getRuntimeValues() {
    this.configuration.setAdresseIst(this.selectrixGateway.getValue(0));

    this.configuration.getAnsprechVerzoegerung().setIst(this.selectrixGateway.getValue(1));

    this.configuration.getAbfallVerzoegerung().setIst(this.selectrixGateway.getValue(2));

    int options = this.selectrixGateway.getValue(3);
    this.configuration.getMeldungBeiZeStopp().setIst(MeldungsModus.valueOf(options & 0b00000011));
    this.configuration.getMeldungBeiFehlendemFahrstrom().setIst(MeldungsModus.valueOf((options & 0b00001100) >> 2));
    this.configuration.getMeldungsNegation().setIst((options & 0b00010000) != 0);
    this.configuration.getZeittakt().setIst(Zeittakt.valueof((options & 0b01100000) >> 5));
    this.configuration.getMeldungsSpeicherung().setIst(MeldungsSpeicherung.valueof((options & 0b10000000) >> 7));
  }

  @Override
  public void setRuntimeValues() {
    this.selectrixGateway.setValue(0, this.configuration.getAdresseIst());
    this.selectrixGateway.setValue(1, this.configuration.getAnsprechVerzoegerung().getIst());
    this.selectrixGateway.setValue(2, this.configuration.getAbfallVerzoegerung().getIst());
    int options = this.configuration.getMeldungBeiZeStopp().getIst().getBits();
    options |= this.configuration.getMeldungBeiFehlendemFahrstrom().getIst().getBits() << 2;
    if (this.configuration.getMeldungsNegation().getIst()) {
      options |= 0b00010000;
    }
    options |= this.configuration.getZeittakt().getIst().getBits() << 5;
    options |= this.configuration.getMeldungsSpeicherung().getIst().getBits() << 7;
    this.selectrixGateway.setValue(3, options);
  }
}
