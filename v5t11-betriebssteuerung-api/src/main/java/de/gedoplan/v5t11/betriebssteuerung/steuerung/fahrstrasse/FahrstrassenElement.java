package de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrstrasse;

import de.gedoplan.v5t11.betriebssteuerung.steuerung.Bereichselement;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.Steuerung;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg.Fahrwegelement;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Fahrstrassenelement.
 * 
 * Kann Gleisabschnitt, Signal, Weiche oder andere Fahrstrasse sein. Bereich und Name sind mit denen des referenzierten Elements
 * identisch.
 * 
 * @author dw
 * 
 */
@XmlAccessorType(XmlAccessType.NONE)
@JsonAutoDetect(JsonMethod.NONE)
public abstract class FahrstrassenElement extends Bereichselement
{
  /**
   * In Zählrichtung orientiert?
   */
  @XmlAttribute
  @JsonProperty
  protected Boolean zaehlrichtung;

  /**
   * Name der gewünschten Stellung.
   */
  @XmlAttribute(name = "stellung")
  @JsonProperty
  protected String  stellungsName;

  /**
   * Schutzfunktion?
   * 
   * Elemente mit Schutzfunktion liegen nicht im eigentlichen Fahrweg, sondern schützen ihn nur vor Kollisionen.
   */
  @XmlAttribute
  @JsonProperty
  protected boolean schutz = false;

  /**
   * Konstruktor.
   */
  public FahrstrassenElement()
  {
  }

  /**
   * Wert liefern: {@link #zaehlrichtung}.
   * 
   * @return Wert
   */
  public boolean isZaehlrichtung()
  {
    return this.zaehlrichtung != null ? this.zaehlrichtung : false;
  }

  /**
   * Wert setzen: {@link #zaehlrichtung}.
   * 
   * @param zaehlrichtung Wert
   */
  public void setZaehlrichtungIfNull(boolean zaehlrichtung)
  {
    if (this.zaehlrichtung == null)
    {
      this.zaehlrichtung = zaehlrichtung;
    }
  }

  /**
   * Wert liefern: {@link #stellungsName}.
   * 
   * @return Wert
   */
  public String getStellungsName()
  {
    return this.stellungsName;
  }

  /**
   * Wert liefern: {@link #schutz}.
   * 
   * @return Wert
   */
  public boolean isSchutz()
  {
    return this.schutz;
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + (this.schutz ? 1231 : 1237);
    result = prime * result + ((this.stellungsName == null) ? 0 : this.stellungsName.hashCode());
    result = prime * result + ((this.zaehlrichtung == null) ? 0 : this.zaehlrichtung.hashCode());
    return result;
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj)
  {
    if (this == obj)
    {
      return true;
    }
    if (!super.equals(obj))
    {
      return false;
    }
    if (getClass() != obj.getClass())
    {
      return false;
    }
    FahrstrassenElement other = (FahrstrassenElement) obj;
    if (this.schutz != other.schutz)
    {
      return false;
    }
    if (this.stellungsName == null)
    {
      if (other.stellungsName != null)
      {
        return false;
      }
    }
    else if (!this.stellungsName.equals(other.stellungsName))
    {
      return false;
    }
    if (this.zaehlrichtung == null)
    {
      if (other.zaehlrichtung != null)
      {
        return false;
      }
    }
    else if (!this.zaehlrichtung.equals(other.zaehlrichtung))
    {
      return false;
    }
    return true;
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    StringBuilder buf = new StringBuilder();
    if (getFahrwegelement() != null)
    {
      buf.append(getFahrwegelement());
      if (this.stellungsName != null)
      {
        buf.append(" -> ").append(this.stellungsName);
      }
      buf.append(" [");
      if (this.zaehlrichtung)
      {
        buf.append("zaehlrichtung ");
      }
      if (this.schutz)
      {
        buf.append("schutz ");
      }
      buf.append("]");
    }
    else
    {
      buf.append("Unbekanntes Fahrwegelement: " + this.getClass().getSimpleName() + ", " + this.bereich + ", " + this.name);
    }
    return buf.toString();
  }

  /**
   * Referenziertes Fahrwegelement liefern.
   * 
   * @return Wert
   */
  public abstract Fahrwegelement getFahrwegelement();

  /**
   * Referenziertes Fahrwegelement setzen.
   * 
   * @param steuerung Steuerung
   */
  public abstract void setFahrwegelement(Steuerung steuerung);

  /**
   * Element für Fahrstrasse reservieren bzw. freigeben.
   * 
   * @param fahrstrasse <code>null</code> zum Freigeben, sonst Fahrstrasse
   */
  public abstract void reservieren(Fahrstrasse fahrstrasse);

  /**
   * Element für Fahrstrasse vorschlagen.
   * 
   * @param fahrstrasse <code>null</code> zum Freigeben, sonst Fahrstrasse
   */
  public abstract void vorschlagen(Fahrstrasse fahrstrasse);

  /**
   * Teil-Rang liefern.
   * 
   * Der Gesamtrang einer Fahrstrasse dient der Auswahl unter Alternativen. Jedes Element trägt einen Teil dazu bei.
   * 
   * @return Rang oder <code>null</code>, falls Element nicht relevant
   */
  public Rank getRank()
  {
    return null;
  }

  /**
   * Verfügbare Ränge.
   * 
   * @author dw
   */
  protected static enum Rank
  {
    GLEISABSCHNITT
    {
      @Override
      public int intValue()
      {
        return 1;
      }
    },

    WEICHE_GERADE
    {
      @Override
      public int intValue()
      {
        return 1;
      }
    },

    WEICHE_ABZWEIGEND
    {
      @Override
      public int intValue()
      {
        return 2;
      }
    };

    public abstract int intValue();
  }
}
