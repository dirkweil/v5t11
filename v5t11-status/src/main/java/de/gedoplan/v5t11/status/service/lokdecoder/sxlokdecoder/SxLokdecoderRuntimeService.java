package de.gedoplan.v5t11.status.service.lokdecoder.sxlokdecoder;

import de.gedoplan.v5t11.status.entity.BausteinConfiguration;
import de.gedoplan.v5t11.status.entity.baustein.Baustein;
import de.gedoplan.v5t11.status.entity.baustein.Lokdecoder;
import de.gedoplan.v5t11.status.entity.baustein.lokdecoder.SxLokdecoder;
import de.gedoplan.v5t11.status.service.BausteinConfigurationService;
import de.gedoplan.v5t11.status.service.Current;
import de.gedoplan.v5t11.status.service.Programmierfamilie;
import de.gedoplan.v5t11.status.service.lokdecoder.LokdecoderConfigurationAdapter;
import de.gedoplan.v5t11.status.service.lokdecoder.LokdecoderRuntimeService;

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

  protected SxLokdecoderRuntimeService() {
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
