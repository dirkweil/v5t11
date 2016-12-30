package de.gedoplan.v5t11.betriebssteuerung.service.funktionsdecoder.sd8;

import de.gedoplan.v5t11.betriebssteuerung.entity.BausteinConfiguration;
import de.gedoplan.v5t11.betriebssteuerung.service.ConfigurationAdapter;
import de.gedoplan.v5t11.betriebssteuerung.service.funktionsdecoder.FunktionsdecoderConfigurationAdapter;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.Baustein;

import java.util.Collections;
import java.util.Map;

import lombok.Getter;

/**
 * Konfigurations-Adapter für Funktionsdecoder des Typs SD 8.
 *
 * Achtung: Die Basisklasse ist nicht
 * {@link FunktionsdecoderConfigurationAdapter}, da die darin enthaltenen
 * Properties hier nicht benötigt werden.
 *
 * @author dw
 */
@Getter
public class SD8ConfigurationAdapter extends ConfigurationAdapter {
  private ServoConfiguration[] servoConfiguration;
  private ConfigurationPropertyAdapter<Integer> abschaltZeit;

  public SD8ConfigurationAdapter(BausteinConfiguration istConfiguration, BausteinConfiguration sollConfiguration) {
    super(istConfiguration, sollConfiguration);

    Map<String, String> istProperties = this.istConfiguration.getProperties();
    Map<String, String> sollProperties = null;
    if (this.sollConfiguration != null) {
      sollProperties = this.sollConfiguration.getProperties();
    }
    if (sollProperties == null) {
      sollProperties = Collections.emptyMap();
    }

    this.servoConfiguration = new ServoConfiguration[8];
    for (int servoNummer = 1; servoNummer <= 8; ++servoNummer) {
      this.servoConfiguration[servoNummer - 1] = new ServoConfiguration(servoNummer, istProperties, sollProperties);
    }

    this.abschaltZeit = new ConfigurationPropertyAdapter<>(istProperties, "abschaltZeit", 0, sollProperties, Integer.class);
  }

  public static SD8ConfigurationAdapter createInstance(Baustein baustein, BausteinConfiguration istConfiguration, BausteinConfiguration sollConfiguration) {
    return new SD8ConfigurationAdapter(istConfiguration, sollConfiguration);
  }

  @Getter
  public static class ServoConfiguration {
    private int servoNummer;
    private ConfigurationPropertyAdapter<Integer> start;
    private ConfigurationPropertyAdapter<Integer> ende;
    private ConfigurationPropertyAdapter<Integer> geschwindigkeit;

    public ServoConfiguration(int servoNummer, Map<String, String> istProperties, Map<String, String> sollProperties) {
      this.servoNummer = servoNummer;
      this.start = new ConfigurationPropertyAdapter<>(istProperties, "start_" + servoNummer, 50, sollProperties, Integer.class);
      this.ende = new ConfigurationPropertyAdapter<>(istProperties, "ende_" + servoNummer, 80, sollProperties, Integer.class);
      this.geschwindigkeit = new ConfigurationPropertyAdapter<>(istProperties, "geschwindigkeit_" + servoNummer, 50, sollProperties, Integer.class);
    }

    public void resetToSoll() {
      this.start.resetToSoll();
      this.ende.resetToSoll();
      this.geschwindigkeit.resetToSoll();
    }

  }
}
