package de.gedoplan.v5t11.status.entity;

import de.gedoplan.v5t11.util.jsonb.JsonbInclude;
import de.gedoplan.v5t11.util.misc.NameComparator;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import lombok.Getter;

/**
 * Element in einem Bereich.
 *
 * @author dw
 */
@XmlAccessorType(XmlAccessType.NONE)
public class Bereichselement implements Comparable<Bereichselement>, Serializable {
  @XmlAttribute
  @Getter(onMethod_ = @JsonbInclude)
  protected String bereich;

  @XmlAttribute
  @Getter(onMethod_ = @JsonbInclude)
  protected String name;

  public void setBereich(String bereich) {
    this.bereich = bereich;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public int compareTo(Bereichselement other) {
    int diff = this.bereich.compareTo(other.bereich);
    if (diff != 0) {
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
  public int hashCode() {
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
  public boolean equals(Object other) {
    if (other == null || other.getClass() != getClass()) {
      return false;
    }

    return compareTo((Bereichselement) other) == 0;
  }

  /**
   * {@inheritDoc}
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return getClass().getSimpleName() + "{" + this.bereich + "/" + this.name + "}";
  }

  public String toDisplayString() {
    return this.bereich + "/" + this.name;
  }

}
