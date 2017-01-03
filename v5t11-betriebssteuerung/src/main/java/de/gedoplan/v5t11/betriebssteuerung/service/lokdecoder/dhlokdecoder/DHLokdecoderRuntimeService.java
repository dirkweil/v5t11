package de.gedoplan.v5t11.betriebssteuerung.service.lokdecoder.dhlokdecoder;

import de.gedoplan.v5t11.betriebssteuerung.entity.BausteinConfiguration;
import de.gedoplan.v5t11.betriebssteuerung.service.BausteinConfigurationService;
import de.gedoplan.v5t11.betriebssteuerung.service.ConfigurationRuntimeService;
import de.gedoplan.v5t11.betriebssteuerung.service.Current;
import de.gedoplan.v5t11.betriebssteuerung.service.Programmierfamilie;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.Baustein;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.Lokdecoder;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.lokdecoder.DHLokdecoder;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;

import lombok.Getter;

@ConversationScoped
@Programmierfamilie(DHLokdecoder.class)
public class DHLokdecoderRuntimeService extends ConfigurationRuntimeService {
  @Getter
  private DHLokdecoderConfigurationAdapter configuration;

  @Inject
  public DHLokdecoderRuntimeService(@Current Baustein baustein, BausteinConfigurationService bausteinConfigurationService) {
    BausteinConfiguration bausteinSollConfiguration = bausteinConfigurationService.getBausteinConfiguration(baustein);
    BausteinConfiguration bausteinIstConfiguration = new BausteinConfiguration(baustein.getId());
    this.configuration = new DHLokdecoderConfigurationAdapter((Lokdecoder) baustein, bausteinIstConfiguration, bausteinSollConfiguration);
  }

  @Override
  public void getRuntimeValues() {
    this.configuration.setAdresseIst(this.selectrixGateway.getValue(0));
  }

  @Override
  public void setRuntimeValues() {
    this.selectrixGateway.setValue(0, this.configuration.getAdresseIst());
  }

}
