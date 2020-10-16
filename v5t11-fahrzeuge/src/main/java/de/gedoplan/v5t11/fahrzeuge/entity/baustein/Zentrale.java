package de.gedoplan.v5t11.fahrzeuge.entity.baustein;

import de.gedoplan.v5t11.fahrzeuge.entity.fahrweg.StatusUpdateable;
import de.gedoplan.v5t11.util.jsonb.JsonbInclude;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class Zentrale implements StatusUpdateable<Zentrale> {
  @Getter(onMethod_ = @JsonbInclude)
  @Setter(onMethod_ = @JsonbInclude)
  private boolean gleisspannung;

  @Getter(onMethod_ = @JsonbInclude)
  @Setter(onMethod_ = @JsonbInclude)
  private boolean kurzschluss;

  public void copyStatus(Zentrale statusZentrale) {
    this.gleisspannung = statusZentrale.gleisspannung;
    this.kurzschluss = statusZentrale.kurzschluss;
  }
}