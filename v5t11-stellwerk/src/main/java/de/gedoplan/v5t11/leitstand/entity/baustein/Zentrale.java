package de.gedoplan.v5t11.leitstand.entity.baustein;

import de.gedoplan.v5t11.util.jsonb.JsonbShort;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class Zentrale {
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
