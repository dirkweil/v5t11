package de.gedoplan.v5t11.util.domain.entity.fahrweg.geraet;

import de.gedoplan.v5t11.util.domain.attribute.SignalStellung;
import de.gedoplan.v5t11.util.domain.entity.fahrweg.Geraet;
import de.gedoplan.v5t11.util.jsonb.JsonbInclude;

import javax.persistence.MappedSuperclass;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Signale.
 *
 * @author dw
 */
@XmlAccessorType(XmlAccessType.NONE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@MappedSuperclass
public abstract class AbstractSignal extends Geraet {

  /**
   * Aktuelle Signalstellung.
   */
  @Getter(onMethod_ = @JsonbInclude)
  @Setter(onMethod_ = @JsonbInclude)
  private SignalStellung stellung = SignalStellung.HALT;

  protected AbstractSignal(String bereich, String name) {
    super(bereich, name);
  }

}
