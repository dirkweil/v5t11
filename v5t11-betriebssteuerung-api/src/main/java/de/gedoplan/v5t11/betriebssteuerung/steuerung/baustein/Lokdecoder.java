package de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein;

import javax.enterprise.inject.Typed;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonMethod;

/**
 * Lokdecoder.
 *
 * @author dw
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@JsonAutoDetect(JsonMethod.NONE)
@Typed
public abstract class Lokdecoder extends Baustein implements Decoder
{
  /**
   * Konstruktor für JPA und JAXB.
   */
  protected Lokdecoder()
  {
    super(1);
  }

  public void setId(String id)
  {
    this.id = id;
  }

  public void setAdresse(int adresse)
  {
    this.adresse = adresse;
  }

  @Override
  public String getLabelPrefix()
  {
    return "Lokdecoder";
  }

  @Override
  public boolean isBusBaustein()
  {
    return false;
  }

}
