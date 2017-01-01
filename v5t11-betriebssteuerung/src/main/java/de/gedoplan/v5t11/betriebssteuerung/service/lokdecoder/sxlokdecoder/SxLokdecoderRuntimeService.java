package de.gedoplan.v5t11.betriebssteuerung.service.lokdecoder.sxlokdecoder;

import de.gedoplan.v5t11.betriebssteuerung.service.ConfigurationRuntimeService;
import de.gedoplan.v5t11.betriebssteuerung.service.lokdecoder.sxlokdecoder.SxLokdecoderConfigurationAdapter.Impulsbreite;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.Steuerung;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.Zentrale;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class SxLokdecoderRuntimeService extends ConfigurationRuntimeService<SxLokdecoderConfigurationAdapter> {

  Zentrale zentrale;

  @Inject
  void init(Steuerung steuerung) {
    this.zentrale = steuerung.getZentrale();
  }

  @Override
  public void getRuntimeValues(SxLokdecoderConfigurationAdapter configuration) {

    int decoderDaten = this.zentrale.readDecoderDaten();

    configuration.hoechstGeschwindigkeit.setIst(decoderDaten & 0b111, false);
    configuration.traegheit.setIst((decoderDaten >>> 3) & 0b111, false);
    configuration.impulsbreite.setIst(Impulsbreite.valueOf((decoderDaten >>> 6) & 0b11), false);
    configuration.setAdresseIst((decoderDaten >>> 8) & 0b1111111, false);
  }

  @Override
  public void setRuntimeValues(SxLokdecoderConfigurationAdapter configuration) {
    int decoderDaten = (configuration.hoechstGeschwindigkeit.getIst() & 0b111)
        | ((configuration.traegheit.getIst() << 3) & 0b111)
        | ((configuration.impulsbreite.getIst().getBits() << 6) & 0b11)
        | ((configuration.getAdresseIst() << 8) & 0b1111111);

    this.zentrale.writeDecoderDaten(decoderDaten);
  }

}
