package de.gedoplan.v5t11.betriebssteuerung.service.besetztmelder.sxbm1;

import de.gedoplan.v5t11.betriebssteuerung.entity.BausteinConfiguration;
import de.gedoplan.v5t11.betriebssteuerung.service.ConfigurationAdapter;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.Baustein;

import lombok.Getter;

/**
 * Konfigurations-Adapter für Besetztmelder des Typs SX-BM 1.
 *
 * @author dw
 */
@Getter
public class SXBM1ConfigurationAdapter extends ConfigurationAdapter {
  private ConfigurationPropertyAdapter<Integer> abfallVerzoegerung;

  public SXBM1ConfigurationAdapter(BausteinConfiguration istConfiguration, BausteinConfiguration sollConfiguration) {
    super(istConfiguration, sollConfiguration);

    this.abfallVerzoegerung = new ConfigurationPropertyAdapter<>(this.istProperties, "abfallVerzoegerung", 500, this.sollProperties, Integer.class);
  }

  public static SXBM1ConfigurationAdapter createInstance(Baustein baustein, BausteinConfiguration istConfiguration, BausteinConfiguration sollConfiguration) {
    return new SXBM1ConfigurationAdapter(istConfiguration, sollConfiguration);
  }
}
