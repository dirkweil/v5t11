package de.gedoplan.v5t11.util.domain.entity.fahrweg;

import de.gedoplan.v5t11.util.domain.entity.Fahrwegelement;
import de.gedoplan.v5t11.util.jsonb.JsonbInclude;

import javax.persistence.MappedSuperclass;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Gleisabschnitt.
 *
 * @author dw
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@MappedSuperclass
public abstract class AbstractGleisabschnitt extends Fahrwegelement {
  /**
   * Gleisabschnitt besetzt?
   */
  @Getter(onMethod_ = @JsonbInclude)
  @Setter(onMethod_ = @JsonbInclude)
  protected boolean besetzt;

  protected AbstractGleisabschnitt(String bereich, String name) {
    super(bereich, name);
  }

}
