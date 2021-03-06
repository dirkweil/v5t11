package de.gedoplan.v5t11.betriebssteuerung.steuerung;

import de.gedoplan.v5t11.betriebssteuerung.util.NameComparator;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * Element in einem Bereich.
 * 
 * @author dw
 */
@XmlAccessorType(XmlAccessType.NONE)
public class Bereichselement implements Comparable<Bereichselement>, Serializable
{
  @XmlAttribute
  protected String bereich;

  @XmlAttribute
  protected String name;

  @Override
  public int compareTo(Bereichselement other)
  {
    int diff = this.bereich.compareTo(other.bereich);
    if (diff != 0)
    {
      return diff;
    }

    return NameComparator.compare(this.name, other.name);
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
    int result = 1;
    result = prime * result + ((this.bereich == null) ? 0 : this.bereich.hashCode());
    result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
    return result;
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object other)
  {
    if (other == null || other.getClass() != getClass())
    {
      return false;
    }

    return compareTo((Bereichselement) other) == 0;
  }

  /**
   * Attribut liefern: {@link #bereich}.
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
   * Attribut liefern: {@link #name}.
   * 
   * @return Wert
   */
  public String getName()
  {
    return this.name;
  }

  /**
   * Wert setzen: {@link #name}.
   * 
   * @param name Wert
   */
  public void setName(String name)
  {
    this.name = name;
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    return getClass().getSimpleName() + "{" + this.bereich + "/" + this.name + "}";
  }

  public String toDisplayString()
  {
    return this.bereich + "/" + this.name;
  }

}
