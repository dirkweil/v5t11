package de.gedoplan.v5t11.util.domain.entity.fahrweg;

import de.gedoplan.v5t11.util.domain.attribute.BereichselementId;
import de.gedoplan.v5t11.util.domain.entity.Fahrwegelement;
import de.gedoplan.v5t11.util.domain.entity.fahrweg.geraet.AbstractWeiche;
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
  private boolean besetzt;

  /**
   * Ist dies ein Weichen-Gleisabschnitt?
   * 
   * @return <code>true</code>, wenn ja
   */
  public boolean isWeichenGleisabschnitt() {
    return getName().startsWith(AbstractWeiche.PREFIX_WEICHEN_GLEISABSCHNITT);
  }

  protected AbstractGleisabschnitt(String bereich, String name) {
    super(bereich, name);
  }

  protected AbstractGleisabschnitt(BereichselementId id) {
    super(id);
  }

  @Override
  public boolean copyStatus(Fahrwegelement other) {
    if (other instanceof AbstractGleisabschnitt) {
      AbstractGleisabschnitt source = (AbstractGleisabschnitt) other;
      if (this.besetzt != source.besetzt) {
        this.besetzt = source.besetzt;
        this.lastChangeMillis = source.lastChangeMillis;
        return true;
      }
    }
    return false;
  }

}
