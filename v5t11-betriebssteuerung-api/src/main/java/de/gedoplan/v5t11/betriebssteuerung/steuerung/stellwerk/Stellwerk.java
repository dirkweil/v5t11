package de.gedoplan.v5t11.betriebssteuerung.steuerung.stellwerk;

import de.gedoplan.baselibs.persistence.entity.SingleIdEntity;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Stellwerk.
 * 
 * @author dw
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@JsonAutoDetect(JsonMethod.NONE)
public class Stellwerk extends SingleIdEntity<String> implements Serializable, Comparable<Stellwerk>
{
  /**
   * Bereich.
   * 
   * Dient auch als ID.
   */
  @XmlAttribute
  @JsonProperty
  String                       bereich;

  @XmlElement(name = "Zeile")
  private List<StellwerkZeile> zeilen;

  /**
   * Wert liefern: {@link #bereich}.
   * 
   * @return Wert
   */
  public String getBereich()
  {
    return this.bereich;
  }

  /**
   * Wert liefern: {@link #zeilen}.
   * 
   * @return Wert
   */
  public List<StellwerkZeile> getZeilen()
  {
    return this.zeilen;
  }

  @Override
  public String getId()
  {
    return this.bereich;
  }

  @Override
  public int compareTo(Stellwerk o)
  {
    return this.bereich.compareTo(o.bereich);
  }

}
