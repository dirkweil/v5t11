package de.gedoplan.v5t11.fahrzeuge.entity.baustein;

import de.gedoplan.v5t11.fahrzeuge.entity.fahrweg.StatusUpdateable;
import de.gedoplan.v5t11.util.jsonb.JsonbShort;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class Zentrale implements StatusUpdateable<Zentrale> {
  @Getter(onMethod_ = @JsonbShort)
  @Setter(onMethod_ = @JsonbShort)
  private boolean gleisspannung;

  @Getter(onMethod_ = @JsonbShort)
  @Setter(onMethod_ = @JsonbShort)
  private boolean kurzschluss;

  public void copyStatus(Zentrale statusZentrale) {
    this.gleisspannung = statusZentrale.gleisspannung;
    this.kurzschluss = statusZentrale.kurzschluss;
  }
}
