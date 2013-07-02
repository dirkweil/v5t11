package de.gedoplan.v5t11.betriebssteuerung.steuerung.stellwerk;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Stellwerk.
 * 
 * @author dw
 */
@XmlAccessorType(XmlAccessType.NONE)
@JsonAutoDetect(JsonMethod.NONE)
public class StellwerkElement implements Serializable
{
  @XmlAttribute
  @JsonProperty
  String  typ;

  @XmlAttribute
  @JsonProperty
  String  lage;

  @XmlAttribute
  @JsonProperty
  String  bereich;

  @XmlAttribute
  @JsonProperty
  String  name;

  @XmlAttribute
  @JsonProperty
  boolean label;

  @XmlAttribute(name = "signal")
  @JsonProperty
  String  signalName;

  @XmlAttribute(name = "signalPos")
  @JsonProperty
  String  signalPosition;

  @XmlAttribute
  @JsonProperty
  int     anzahl = 1;

  /**
   * Wert liefern: {@link #typ}.
   * 
   * @return Wert
   */
  public String getTyp()
  {
    return this.typ;
  }

  /**
   * Wert liefern: {@link #lage}.
   * 
   * @return Wert
   */
  public String getLage()
  {
    return this.lage;
  }

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
   * Wert setzen: {@link #bereich}.
   * 
   * @param bereich Wert
   */
  public void setBereich(String bereich)
  {
    this.bereich = bereich;
  }

  /**
   * Wert liefern: {@link #name}.
   * 
   * @return Wert
   */
  public String getName()
  {
    return this.name;
  }

  /**
   * Wert liefern: {@link #label}.
   * 
   * @return Wert
   */
  public boolean isLabel()
  {
    return this.label;
  }

  /**
   * Wert liefern: {@link #signalName}.
   * 
   * @return Wert
   */
  public String getSignalName()
  {
    return this.signalName;
  }

  /**
   * Wert liefern: {@link #signalPosition}.
   * 
   * @return Wert
   */
  public String getSignalPosition()
  {
    return this.signalPosition;
  }

  /**
   * Wert liefern: {@link #anzahl}.
   * 
   * @return Wert
   */
  public int getAnzahl()
  {
    return this.anzahl;
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    return "StellwerkElement [typ=" + this.typ + ", lage=" + this.lage + ", bereich=" + this.bereich + ", name=" + this.name + ", signal=" + this.signalName + ", signalPosition="
        + this.signalPosition + ", anzahl=" + this.anzahl + "]";
  }

}
