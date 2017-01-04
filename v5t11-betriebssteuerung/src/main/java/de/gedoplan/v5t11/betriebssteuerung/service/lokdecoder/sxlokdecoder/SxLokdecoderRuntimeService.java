package de.gedoplan.v5t11.betriebssteuerung.service.lokdecoder.sxlokdecoder;

import de.gedoplan.v5t11.betriebssteuerung.entity.BausteinConfiguration;
import de.gedoplan.v5t11.betriebssteuerung.service.BausteinConfigurationService;
import de.gedoplan.v5t11.betriebssteuerung.service.Current;
import de.gedoplan.v5t11.betriebssteuerung.service.Programmierfamilie;
import de.gedoplan.v5t11.betriebssteuerung.service.lokdecoder.LokdecoderRuntimeService;
import de.gedoplan.v5t11.betriebssteuerung.service.lokdecoder.sxlokdecoder.SxLokdecoderConfigurationAdapter.Impulsbreite;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.Baustein;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.Lokdecoder;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.lokdecoder.SxLokdecoder;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;

import lombok.Getter;

@ConversationScoped
@Programmierfamilie(SxLokdecoder.class)
public class SxLokdecoderRuntimeService extends LokdecoderRuntimeService {
  @Getter
  private SxLokdecoderConfigurationAdapter configuration;

  @Inject
  public SxLokdecoderRuntimeService(@Current Baustein baustein, BausteinConfigurationService bausteinConfigurationService) {
    BausteinConfiguration bausteinSollConfiguration = bausteinConfigurationService.getBausteinConfiguration(baustein);
    BausteinConfiguration bausteinIstConfiguration = new BausteinConfiguration(baustein.getId());
    this.configuration = new SxLokdecoderConfigurationAdapter((Lokdecoder) baustein, bausteinIstConfiguration, bausteinSollConfiguration);
  }

  @Override
  public void getRuntimeValues() {
    startProgMode();

    try {
      int decoderDaten = readDecoderDaten();

      if (this.log.isTraceEnabled()) {
        this.log.trace(String.format("readDecoderDaten: 0x%04x", decoderDaten));
      }

      this.configuration.hoechstGeschwindigkeit.setIst(decoderDaten & 0b111, false);
      this.configuration.traegheit.setIst((decoderDaten >>> 3) & 0b111, false);
      this.configuration.impulsbreite.setIst(Impulsbreite.valueOf((decoderDaten >>> 6) & 0b11), false);

      this.configuration.setAdresseIst((decoderDaten >>> 8) & 0b1111111, false);
    } finally {
      stopProgMode();
    }
  }

  @Override
  public void setRuntimeValues() {
    startProgMode();

    try {
      int decoderDaten = (this.configuration.hoechstGeschwindigkeit.getIst() & 0b111)
          | ((this.configuration.traegheit.getIst() & 0b111) << 3)
          | ((this.configuration.impulsbreite.getIst().getBits() & 0b11) << 6)
          | ((this.configuration.getAdresseIst() & 0b0111_1111) << 8);

      if (this.log.isTraceEnabled()) {
        this.log.trace(String.format("writeDecoderDaten: 0x%04x", decoderDaten));
      }

      writeDecoderDaten(decoderDaten);
    } finally {
      stopProgMode();
    }
  }
}
