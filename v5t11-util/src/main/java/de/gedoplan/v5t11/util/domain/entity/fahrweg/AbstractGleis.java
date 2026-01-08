package de.gedoplan.v5t11.util.domain.entity.fahrweg;

import de.gedoplan.v5t11.util.domain.attribute.BereichselementId;
import de.gedoplan.v5t11.util.domain.entity.Fahrwegelement;
import de.gedoplan.v5t11.util.domain.entity.fahrweg.geraet.AbstractWeiche;
import de.gedoplan.v5t11.util.jsonb.JsonbShort;

import jakarta.persistence.MappedSuperclass;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

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
  @Getter(onMethod_ = @JsonbShort)
  @Setter(onMethod_ = @JsonbShort)
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
    if (other instanceof AbstractGleis source) {
      return changeBesetzt(source.besetzt);
    }
    return false;
  }

  public boolean changeBesetzt(boolean besetzt, long changeMillis) {
    if (this.besetzt != besetzt) {
      this.besetzt = besetzt;
      this.lastChangeMillis = changeMillis;
      return true;
    }

    return false;
  }

  public boolean changeBesetzt(boolean besetzt) {
    return changeBesetzt(besetzt, System.currentTimeMillis());
  }

}
