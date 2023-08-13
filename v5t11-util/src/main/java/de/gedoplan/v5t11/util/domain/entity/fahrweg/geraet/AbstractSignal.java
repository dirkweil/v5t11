package de.gedoplan.v5t11.util.domain.entity.fahrweg.geraet;

import de.gedoplan.v5t11.util.domain.attribute.BereichselementId;
import de.gedoplan.v5t11.util.domain.attribute.SignalStellung;
import de.gedoplan.v5t11.util.domain.entity.Fahrwegelement;
import de.gedoplan.v5t11.util.domain.entity.fahrweg.AbstractGeraet;
import de.gedoplan.v5t11.util.jsonb.JsonbInclude;

import jakarta.persistence.Convert;
import jakarta.persistence.MappedSuperclass;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;

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
public abstract class AbstractSignal extends AbstractGeraet {

  /**
   * Aktuelle Signalstellung.
   */
  @Getter(onMethod_ = @JsonbInclude)
  @Setter(onMethod_ = @JsonbInclude)
  @Convert(converter = SignalStellung.Adapter4Jpa.class)
  protected SignalStellung stellung = SignalStellung.HALT;

  protected AbstractSignal(String bereich, String name) {
    super(bereich, name);
  }

  protected AbstractSignal(BereichselementId id) {
    super(id);
  }

  @Override
  public boolean copyStatus(Fahrwegelement other) {
    if (other instanceof AbstractSignal) {
      AbstractSignal source = (AbstractSignal) other;
      if (this.stellung != source.stellung) {
        this.stellung = source.stellung;
        this.lastChangeMillis = source.lastChangeMillis;
        return true;
      }
    }
    return false;
  }

}
