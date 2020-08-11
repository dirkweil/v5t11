package de.gedoplan.v5t11.leitstand.entity.stellwerk;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;

import lombok.Getter;

/**
 * Stellwerk.
 *
 * @author dw
 */
@XmlAccessorType(XmlAccessType.NONE)
public class StellwerkZeile implements Serializable {
  @XmlElements({
      @XmlElement(name = "Leer", type = StellwerkLeer.class),
      @XmlElement(name = "Gleisabschnitt", type = StellwerkGleisabschnitt.class),
      @XmlElement(name = "Weiche", type = StellwerkWeiche.class),
      @XmlElement(name = "Dkw2", type = StellwerkDkw2.class),
  })
  @Getter
  private List<StellwerkElement> elemente;
}
