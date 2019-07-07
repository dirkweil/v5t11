package de.gedoplan.v5t11.status.service.besetztmelder.muet8k;

import de.gedoplan.v5t11.status.entity.BausteinConfiguration;
import de.gedoplan.v5t11.status.service.ConfigurationAdapter;

import lombok.Getter;

/**
 * Konfigurations-Adapter für Besetztmelder des Typs Muet 8k.
 *
 * @author dw
 */
@Getter
public class Muet8kConfigurationAdapter extends ConfigurationAdapter {
  private ConfigurationPropertyAdapter<Integer> abfallVerzoegerung;

  public Muet8kConfigurationAdapter(BausteinConfiguration istConfiguration, BausteinConfiguration sollConfiguration) {
    super(istConfiguration, sollConfiguration);

    this.abfallVerzoegerung = new ConfigurationPropertyAdapter<>(this.istProperties, "abfallVerzoegerung", 12 * 80, this.sollProperties, Integer.class);
  }

}
