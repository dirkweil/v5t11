package de.gedoplan.v5t11.leitstand.entity.stellwerk;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import lombok.Getter;

/**
 * Stellwerk.
 *
 * @author dw
 */
@XmlAccessorType(XmlAccessType.NONE)
public class StellwerkZeile implements Serializable {
  @XmlElement(name = "Element")
  @Getter
  private List<StellwerkElement> elemente;
}
