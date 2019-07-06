package de.gedoplan.v5t11.status.service.funktionsdecoder.sd8;

import de.gedoplan.v5t11.status.entity.BausteinConfiguration;
import de.gedoplan.v5t11.status.entity.baustein.Baustein;
import de.gedoplan.v5t11.status.service.ConfigurationAdapter;
import de.gedoplan.v5t11.status.service.funktionsdecoder.FunktionsdecoderConfigurationAdapter;

import java.util.Map;

import lombok.Getter;

/**
 * Konfigurations-Adapter für Funktionsdecoder des Typs SD 8.
 *
 * Achtung: Die Basisklasse ist nicht {@link FunktionsdecoderConfigurationAdapter}, da die darin enthaltenen Properties hier nicht benötigt werden.
 *
 * @author dw
 */
@Getter
public class SD8ConfigurationAdapter extends ConfigurationAdapter {
  private ServoConfiguration[] servoConfiguration;
  private ConfigurationPropertyAdapter<Integer> abschaltZeit;

  public SD8ConfigurationAdapter(BausteinConfiguration istConfiguration, BausteinConfiguration sollConfiguration) {
    super(istConfiguration, sollConfiguration);

    this.servoConfiguration = new ServoConfiguration[8];
    for (int servoNummer = 0; servoNummer < 8; ++servoNummer) {
      this.servoConfiguration[servoNummer] = new ServoConfiguration(servoNummer, this.istProperties, this.sollProperties);
    }

    this.abschaltZeit = new ConfigurationPropertyAdapter<>(this.istProperties, "abschaltZeit", 0, this.sollProperties, Integer.class);
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
    private ConfigurationPropertyAdapter<Integer> startNachwippen;
    private ConfigurationPropertyAdapter<Integer> endeNachwippen;

    public ServoConfiguration(int servoNummer, Map<String, String> istProperties, Map<String, String> sollProperties) {
      this.servoNummer = servoNummer;
      this.start = new ConfigurationPropertyAdapter<>(istProperties, "start_" + servoNummer, 50, sollProperties, Integer.class);
      this.ende = new ConfigurationPropertyAdapter<>(istProperties, "ende_" + servoNummer, 80, sollProperties, Integer.class);
      this.geschwindigkeit = new ConfigurationPropertyAdapter<>(istProperties, "geschwindigkeit_" + servoNummer, 50, sollProperties, Integer.class);
      this.startNachwippen = new ConfigurationPropertyAdapter<>(istProperties, "startWippen_" + servoNummer, 0, sollProperties, Integer.class);
      this.endeNachwippen = new ConfigurationPropertyAdapter<>(istProperties, "endeWippen_" + servoNummer, 0, sollProperties, Integer.class);
    }

    public void resetToSoll() {
      this.start.resetToSoll();
      this.ende.resetToSoll();
      this.geschwindigkeit.resetToSoll();
    }

  }
}
