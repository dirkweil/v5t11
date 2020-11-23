package de.gedoplan.v5t11.util.domain.entity;

import de.gedoplan.v5t11.util.jsonb.JsonbInclude;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Element eines Fahrwegs.
 *
 * Ein solches Element kann ein Gerät sein (Signal, Weiche etc.) oder ein Gleisabschnitt.
 *
 * @author dw
 */
@XmlAccessorType(XmlAccessType.NONE)
@MappedSuperclass
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Fahrwegelement extends Bereichselement {

  @Getter(onMethod_ = @JsonbInclude)
  @Setter(onMethod_ = @JsonbInclude)
  @Column(name = "LAST_CHANGE_MS")
  protected long lastChangeMillis;

  protected Fahrwegelement(String bereich, String name) {
    super(bereich, name);
  }
}
