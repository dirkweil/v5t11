/*
 * Created on 22.12.2005 by dw
 */
package de.gedoplan.v5t11.util.domain.entity.fahrweg.geraet;

import de.gedoplan.v5t11.util.domain.attribute.WeichenStellung;
import de.gedoplan.v5t11.util.domain.entity.fahrweg.Geraet;
import de.gedoplan.v5t11.util.jsonb.JsonbInclude;

import javax.persistence.MappedSuperclass;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Weiche.
 *
 * @author dw
 *
 */
@XmlAccessorType(XmlAccessType.NONE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@MappedSuperclass
public abstract class AbstractWeiche extends Geraet {

  /**
   * Aktuelle Stellung der Weiche.
   */
  @Getter(onMethod_ = @JsonbInclude)
  protected WeichenStellung stellung = WeichenStellung.GERADE;

  @JsonbInclude(full = true)
  public String getGleisabschnittName() {
    String name = getName();
    boolean doppelweiche = Character.isAlphabetic(name.charAt(name.length() - 1));
    if (doppelweiche) {
      return "W" + name.substring(0, name.length() - 1);
    } else {
      return "W" + name;
    }
  }

}
