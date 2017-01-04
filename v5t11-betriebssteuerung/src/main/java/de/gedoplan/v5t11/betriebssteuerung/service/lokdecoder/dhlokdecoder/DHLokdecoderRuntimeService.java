package de.gedoplan.v5t11.betriebssteuerung.service.lokdecoder.dhlokdecoder;

import de.gedoplan.v5t11.betriebssteuerung.entity.BausteinConfiguration;
import de.gedoplan.v5t11.betriebssteuerung.service.BausteinConfigurationService;
import de.gedoplan.v5t11.betriebssteuerung.service.Current;
import de.gedoplan.v5t11.betriebssteuerung.service.Programmierfamilie;
import de.gedoplan.v5t11.betriebssteuerung.service.lokdecoder.LokdecoderRuntimeService;
import de.gedoplan.v5t11.betriebssteuerung.service.lokdecoder.dhlokdecoder.DHLokdecoderConfigurationAdapter.RegelVariante;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.Baustein;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.Lokdecoder;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.lokdecoder.DHLokdecoder;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;

@ConversationScoped
@Programmierfamilie(DHLokdecoder.class)
public class DHLokdecoderRuntimeService extends LokdecoderRuntimeService<DHLokdecoderConfigurationAdapter> {
  @Inject
  public DHLokdecoderRuntimeService(@Current Baustein baustein, BausteinConfigurationService bausteinConfigurationService) {
    BausteinConfiguration bausteinSollConfiguration = bausteinConfigurationService.getBausteinConfiguration(baustein);
    BausteinConfiguration bausteinIstConfiguration = new BausteinConfiguration(baustein.getId());
    this.configuration = new DHLokdecoderConfigurationAdapter((Lokdecoder) baustein, bausteinIstConfiguration, bausteinSollConfiguration);
  }

  @Override
  public void getRuntimeValues() {
    startProgMode();

    try {
      // Standardparameter holen
      pullStandardConfig();

      try {
        // Decoder auf Lesen erweiterter Parameter umprogrammieren
        writeDecoderDaten(0x0000_0000_0100_1001);

        int erweiterteDecoderDaten = readDecoderDaten();

        this.configuration.getAnschlusstauschMotor().setIst((erweiterteDecoderDaten & 0b0000_0001) != 0);
        this.configuration.getAnschlusstauschLicht().setIst((erweiterteDecoderDaten & 0b0000_0010) != 0);
        this.configuration.getAnschlusstauschGleis().setIst((erweiterteDecoderDaten & 0b0000_0100) != 0);
        this.configuration.getRegelVariante().setIst(RegelVariante.valueOf((erweiterteDecoderDaten & 0b1100_0000) >>> 6));
      } finally {
        // Standardparameter restaurieren
        pushStandardConfig();
      }
    } finally {
      stopProgMode();
    }
  }

  @Override
  public void setRuntimeValues() {
    startProgMode();

    try {
      // Decoder auf Schreiben erweiterter Parameter umprogrammieren
      writeDecoderDaten(0x0000_0000_0100_1001);

      // Erweiterte Parameter schreiben
      int erweiterteDecoderDaten = (this.configuration.getAnschlusstauschMotor().getIst() ? 0b0000_0001 : 0)
          | (this.configuration.getAnschlusstauschLicht().getIst() ? 0b0000_0010 : 0)
          | (this.configuration.getAnschlusstauschGleis().getIst() ? 0b0000_0100 : 0)
          | ((this.configuration.getRegelVariante().getIst().getBits() << 6) & 0b1100_0000);
      writeDecoderDaten(erweiterteDecoderDaten);

      // Standardparameter schreiben
      pushStandardConfig();
    } finally {
      stopProgMode();
    }
  }
}
