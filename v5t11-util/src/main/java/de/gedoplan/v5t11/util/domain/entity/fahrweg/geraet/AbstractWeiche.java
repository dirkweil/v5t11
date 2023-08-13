/*
 * Created on 22.12.2005 by dw
 */
package de.gedoplan.v5t11.util.domain.entity.fahrweg.geraet;

import de.gedoplan.v5t11.util.domain.attribute.BereichselementId;
import de.gedoplan.v5t11.util.domain.attribute.WeichenStellung;
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
 * Weiche.
 *
 * @author dw
 */
@XmlAccessorType(XmlAccessType.NONE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@MappedSuperclass
public abstract class AbstractWeiche extends AbstractGeraet {

  public static final String PREFIX_WEICHEN_GLEIS = "W";

  /**
   * Aktuelle Stellung der Weiche.
   */
  @Getter(onMethod_ = @JsonbInclude)
  @Setter(onMethod_ = @JsonbInclude)
  @Convert(converter = WeichenStellung.Adapter4Jpa.class)
  private WeichenStellung stellung = WeichenStellung.GERADE;

  protected AbstractWeiche(String bereich, String name) {
    super(bereich, name);
  }

  protected AbstractWeiche(BereichselementId id) {
    super(id);
  }

  @JsonbInclude(full = true)
  public String getGleisName() {
    String name = getName();
    boolean doppelweiche = Character.isAlphabetic(name.charAt(name.length() - 1));
    if (doppelweiche) {
      return PREFIX_WEICHEN_GLEIS + name.substring(0, name.length() - 1);
    } else {
      return PREFIX_WEICHEN_GLEIS + name;
    }
  }

  @Override
  public boolean copyStatus(Fahrwegelement other) {
    if (other instanceof AbstractWeiche) {
      AbstractWeiche source = (AbstractWeiche) other;
      if (this.stellung != source.stellung) {
        this.stellung = source.stellung;
        this.lastChangeMillis = source.lastChangeMillis;
        return true;
      }
    }
    return false;
  }

}
