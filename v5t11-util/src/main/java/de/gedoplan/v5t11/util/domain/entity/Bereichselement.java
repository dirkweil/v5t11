package de.gedoplan.v5t11.util.domain.entity;

import de.gedoplan.baselibs.persistence.entity.SingleIdEntity;
import de.gedoplan.v5t11.util.cdi.EventFirer;
import de.gedoplan.v5t11.util.domain.attribute.BereichselementId;
import de.gedoplan.v5t11.util.jsonb.JsonbInclude;

import jakarta.inject.Inject;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Element in einem Bereich.
 *
 * @author dw
 */
@XmlAccessorType(XmlAccessType.NONE)
@MappedSuperclass
@IdClass(BereichselementId.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Bereichselement extends SingleIdEntity<BereichselementId> implements Comparable<Bereichselement> {
  @XmlAttribute
  @Getter
  @Id
  private String bereich;

  @XmlAttribute
  @Getter
  @Id
  private String name;

  private transient BereichselementId id;

  // Gleis, Schalter, Signal, Weiche
  @Transient
  @Inject
  protected EventFirer eventFirer;

  protected Bereichselement(String bereich, String name) {
    this.bereich = bereich;
    this.name = name;
  }

  protected Bereichselement(BereichselementId id) {
    this.id = id;
    this.bereich = id.getBereich();
    this.name = id.getName();
  }

  @JsonbInclude
  public void setBereich(String bereich) {
    this.bereich = bereich;
    this.id = null;
  }

  @JsonbInclude
  public void setName(String name) {
    this.name = name;
    this.id = null;
  }

  @Override
  public BereichselementId getId() {
    if (this.id == null) {
      this.id = new BereichselementId(this.bereich, this.name);
    }
    return this.id;
  }

  /*
   * Aus irgendeinem schleierhaften Grund nimmt Jsonb die id nur, wenn sie mit einer anderen Methode
   * geliefert wird - nicht getId()!
   * Könnte ein Bug in Yasson sein.
   */
  @JsonbInclude
  public BereichselementId getKey() {
    return getId();
  }

  @JsonbInclude
  public void setKey(BereichselementId id) {
    this.id = id;
    this.bereich = id.getBereich();
    this.name = id.getName();
  }

  public String toDisplayString() {
    return this.bereich + "/" + this.name;
  }

  @Override
  public int compareTo(Bereichselement other) {
    return getId().compareTo(other.getId());
  }

}
