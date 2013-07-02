package de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein;

import java.util.Arrays;

import javax.enterprise.inject.Typed;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Selectrix-Zentrale.
 * 
 * @author dw
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@JsonAutoDetect(JsonMethod.NONE)
@Typed
public class Zentrale extends Baustein
{
  // Adressen:___________________________________[--127--]_[--109--]_[--106--]_[--105--]_[--104--]
  private static final long MASK_AKTIV       = 0b1000_0000_0000_0000_0000_0000_0000_0000_0000_0000L;
  private static final long MASK_KURZSCHLUSS = 0b0000_0000_0001_0000_0000_0000_0000_0000_0000_0000L;

  /**
   * Konstruktor.
   */
  public Zentrale()
  {
    super(5);
    this.id = "Zentrale";
    this.adresse = 104;
    this.adressCache.set(Arrays.asList(104, 105, 106, 109, 127));
  }

  /**
   * Ist die Zentrale aktiv (Gleisspannung an)?
   * 
   * @return <code>true</code>, wenn Zentrale aktiv
   */
  @XmlElement
  @JsonProperty
  public boolean isAktiv()
  {
    return (getWert() & MASK_AKTIV) != 0;
  }

  /**
   * Zentrale ein/ausschalten.
   * 
   * @param aktiv <code>true</code> zum Einschalten
   */
  public void setAktiv(boolean aktiv)
  {
    long wert = getWert() & ~MASK_AKTIV;
    if (aktiv)
    {
      wert |= MASK_AKTIV;
    }
    setWert(wert);
  }

  /**
   * Kurzschluss?
   * 
   * @return <code>true</code> bei Kurzschluss
   */
  @XmlElement
  @JsonProperty
  public boolean isKurzschluss()
  {
    return (getWert() & MASK_KURZSCHLUSS) != 0;
  }
}
