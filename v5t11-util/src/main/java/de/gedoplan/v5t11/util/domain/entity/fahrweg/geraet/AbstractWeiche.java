/*
 * Created on 22.12.2005 by dw
 */
package de.gedoplan.v5t11.util.domain.entity.fahrweg.geraet;

import de.gedoplan.v5t11.util.domain.attribute.WeichenStellung;
import de.gedoplan.v5t11.util.domain.entity.Fahrwegelement;
import de.gedoplan.v5t11.util.domain.entity.fahrweg.AbstractGeraet;
import de.gedoplan.v5t11.util.jsonb.JsonbInclude;

import javax.persistence.Convert;
import javax.persistence.MappedSuperclass;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Weiche.
 *
 * @author dw
 *
 */
@XmlAccessorType(XmlAccessType.NONE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@MappedSuperclass
public abstract class AbstractWeiche extends AbstractGeraet {

  public static final String PREFIX_WEICHEN_GLEISABSCHNITT = "W";

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

  @JsonbInclude(full = true)
  public String getGleisabschnittName() {
    String name = getName();
    boolean doppelweiche = Character.isAlphabetic(name.charAt(name.length() - 1));
    if (doppelweiche) {
      return PREFIX_WEICHEN_GLEISABSCHNITT + name.substring(0, name.length() - 1);
    } else {
      return PREFIX_WEICHEN_GLEISABSCHNITT + name;
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
