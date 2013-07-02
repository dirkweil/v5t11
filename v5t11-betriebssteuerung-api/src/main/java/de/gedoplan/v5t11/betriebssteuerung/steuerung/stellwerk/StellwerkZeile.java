package de.gedoplan.v5t11.betriebssteuerung.steuerung.stellwerk;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonMethod;

/**
 * Stellwerk.
 * 
 * @author dw
 */
@XmlAccessorType(XmlAccessType.NONE)
@JsonAutoDetect(JsonMethod.NONE)
public class StellwerkZeile implements Serializable
{
  @XmlElement(name = "Element")
  private List<StellwerkElement> elemente;

  /**
   * Wert liefern: {@link #elemente}.
   * 
   * @return Wert
   */
  public List<StellwerkElement> getElemente()
  {
    return this.elemente;
  }

}
