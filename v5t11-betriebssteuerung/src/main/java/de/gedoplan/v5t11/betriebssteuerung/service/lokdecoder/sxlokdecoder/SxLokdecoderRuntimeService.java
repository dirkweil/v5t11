package de.gedoplan.v5t11.betriebssteuerung.service.lokdecoder.sxlokdecoder;

import de.gedoplan.v5t11.betriebssteuerung.entity.BausteinConfiguration;
import de.gedoplan.v5t11.betriebssteuerung.service.BausteinConfigurationService;
import de.gedoplan.v5t11.betriebssteuerung.service.Current;
import de.gedoplan.v5t11.betriebssteuerung.service.Programmierfamilie;
import de.gedoplan.v5t11.betriebssteuerung.service.lokdecoder.LokdecoderConfigurationAdapter;
import de.gedoplan.v5t11.betriebssteuerung.service.lokdecoder.LokdecoderRuntimeService;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.Baustein;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.Lokdecoder;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.lokdecoder.SxLokdecoder;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;

@ConversationScoped
@Programmierfamilie(SxLokdecoder.class)
public class SxLokdecoderRuntimeService extends LokdecoderRuntimeService<LokdecoderConfigurationAdapter> {
  @Inject
  public SxLokdecoderRuntimeService(@Current Baustein baustein, BausteinConfigurationService bausteinConfigurationService) {
    BausteinConfiguration bausteinSollConfiguration = bausteinConfigurationService.getBausteinConfiguration(baustein);
    BausteinConfiguration bausteinIstConfiguration = new BausteinConfiguration(baustein.getId());
    this.configuration = new LokdecoderConfigurationAdapter((Lokdecoder) baustein, bausteinIstConfiguration, bausteinSollConfiguration);
  }

  @Override
  public void getRuntimeValues() {
    startProgMode();

    try {
      pullStandardConfig();
    } finally {
      stopProgMode();
    }
  }

  @Override
  public void setRuntimeValues() {
    startProgMode();

    try {
      pushStandardConfig();
    } finally {
      stopProgMode();
    }
  }
}
