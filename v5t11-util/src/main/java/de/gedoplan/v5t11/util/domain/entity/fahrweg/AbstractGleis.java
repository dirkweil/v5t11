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
 * Gleis.
 *
 * @author dw
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@MappedSuperclass
public abstract class AbstractGleis extends Fahrwegelement {
  /**
   * Gleis besetzt?
   */
  @Getter(onMethod_ = @JsonbInclude)
  @Setter(onMethod_ = @JsonbInclude)
  private boolean besetzt;

  /**
   * Ist dies ein Weichen-Gleis?
   * 
   * @return <code>true</code>, wenn ja
   */
  public boolean isWeichenGleis() {
    return getName().startsWith(AbstractWeiche.PREFIX_WEICHEN_GLEIS);
  }

  protected AbstractGleis(String bereich, String name) {
    super(bereich, name);
  }

  protected AbstractGleis(BereichselementId id) {
    super(id);
  }

  @Override
  public boolean copyStatus(Fahrwegelement other) {
    if (other instanceof AbstractGleis) {
      AbstractGleis source = (AbstractGleis) other;
      if (this.besetzt != source.besetzt) {
        this.besetzt = source.besetzt;
        this.lastChangeMillis = source.lastChangeMillis;
        return true;
      }
    }
    return false;
  }

}
