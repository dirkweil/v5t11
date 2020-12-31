package de.gedoplan.v5t11.util.domain.entity.fahrweg;

import de.gedoplan.v5t11.util.domain.attribute.BereichselementId;
import de.gedoplan.v5t11.util.domain.entity.Fahrwegelement;

import javax.persistence.MappedSuperclass;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Basisklasse für Geräte (die an Funktionsdecoder angeschlossen werden).
 *
 * @author dw
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@MappedSuperclass
public abstract class AbstractGeraet extends Fahrwegelement {

  protected AbstractGeraet(String bereich, String name) {
    super(bereich, name);
  }

  protected AbstractGeraet(BereichselementId id) {
    super(id);
  }

}