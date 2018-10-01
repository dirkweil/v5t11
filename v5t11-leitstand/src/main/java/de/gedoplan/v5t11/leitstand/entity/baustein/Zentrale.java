package de.gedoplan.v5t11.leitstand.entity.baustein;

import de.gedoplan.v5t11.util.jsonb.JsonbInclude;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class Zentrale {
  @Getter(onMethod_ = @JsonbInclude)
  @Setter(onMethod_ = @JsonbInclude)
  private boolean aktiv;

  @Getter(onMethod_ = @JsonbInclude)
  @Setter(onMethod_ = @JsonbInclude)
  private boolean kurzschluss;

  public void copyStatus(Zentrale statusZentrale) {
    this.aktiv = statusZentrale.aktiv;
    this.kurzschluss = statusZentrale.kurzschluss;
  }
}
