package de.gedoplan.v5t11.leitstand.entity.fahrstrasse;

import de.gedoplan.v5t11.util.domain.attribute.BereichselementId;
import de.gedoplan.v5t11.util.domain.attribute.FahrstrassenelementTyp;
import de.gedoplan.v5t11.util.domain.attribute.SignalStellung;
import de.gedoplan.v5t11.util.domain.attribute.WeichenStellung;
import de.gedoplan.v5t11.util.jsonb.JsonbInclude;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Convert;
import javax.persistence.Embeddable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Access(AccessType.FIELD)
@NoArgsConstructor
public class Fahrstrassenelement {

  @Getter
  private BereichselementId id;

  @Getter
  @Setter(onMethod_ = @JsonbInclude(full = true))
  protected boolean zaehlrichtung;

  @Getter
  @Setter(onMethod_ = @JsonbInclude(full = true))
  protected boolean schutz;

  @Getter
  @Setter(onMethod_ = @JsonbInclude(full = true))
  @Convert(converter = FahrstrassenelementTyp.Adapter4Jpa.class)
  protected FahrstrassenelementTyp typ;

  @Getter
  @Setter(onMethod_ = @JsonbInclude(full = true))
  protected String stellung;

  @JsonbInclude
  public void setKey(BereichselementId id) {
    this.id = id;
  }

  /**
   * Conveniance-Methode: Zugeordnete Weichenstellung liefern.
   * Darf nur aufgerufen werden, wenn es sich um ein Weichenelement handelt!
   *
   * @return Weichenstellung
   */
  public WeichenStellung getWeichenstellung() {
    assert this.typ == FahrstrassenelementTyp.WEICHE;
    return WeichenStellung.fromString(this.stellung);
  }

  /**
   * Conveniance-Methode: Zugeordnete Signalstellung liefern.
   * Darf nur aufgerufen werden, wenn es sich um ein Signalelement handelt!
   *
   * @return Signalstellung
   */
  public SignalStellung getSignalstellung() {
    assert this.typ == FahrstrassenelementTyp.SIGNAL;
    return SignalStellung.fromString(this.stellung);
  }

}
