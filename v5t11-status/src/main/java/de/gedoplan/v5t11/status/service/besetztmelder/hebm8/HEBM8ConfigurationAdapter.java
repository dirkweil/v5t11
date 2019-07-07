package de.gedoplan.v5t11.status.service.besetztmelder.hebm8;

import de.gedoplan.v5t11.status.entity.BausteinConfiguration;
import de.gedoplan.v5t11.status.service.ConfigurationAdapter;

import lombok.Getter;

/**
 * Konfigurations-Adapter für Besetztmelder des Typs Engelmann BM8.
 *
 * @author dw
 */
@Getter
public class HEBM8ConfigurationAdapter extends ConfigurationAdapter {
  private ConfigurationPropertyAdapter<Integer> abfallVerzoegerung;

  public HEBM8ConfigurationAdapter(BausteinConfiguration istConfiguration, BausteinConfiguration sollConfiguration) {
    super(istConfiguration, sollConfiguration);

    this.abfallVerzoegerung = new ConfigurationPropertyAdapter<>(this.istProperties, "abfallVerzoegerung", 10 * 100, this.sollProperties, Integer.class);
  }
}
