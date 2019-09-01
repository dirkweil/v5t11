package de.gedoplan.v5t11.status.service.besetztmelder.vm5262;

import de.gedoplan.v5t11.status.entity.BausteinConfiguration;
import de.gedoplan.v5t11.status.service.ConfigurationAdapter;

import lombok.Getter;

/**
 * Konfigurations-Adapter für Besetztmelder des Typs Viessmann 5262.
 *
 * @author dw
 */
@Getter
public class VM5262ConfigurationAdapter extends ConfigurationAdapter {
  private ConfigurationPropertyAdapter<Integer> ansprechVerzoegerung;
  private ConfigurationPropertyAdapter<Integer> abfallVerzoegerung;

  public VM5262ConfigurationAdapter(BausteinConfiguration istConfiguration, BausteinConfiguration sollConfiguration) {
    super(istConfiguration, sollConfiguration);

    this.ansprechVerzoegerung = new ConfigurationPropertyAdapter<>(this.istProperties, "ansprechVerzoegerung", 0, this.sollProperties, Integer.class);
    this.abfallVerzoegerung = new ConfigurationPropertyAdapter<>(this.istProperties, "abfallVerzoegerung", 10 * 100, this.sollProperties, Integer.class);
  }

}
