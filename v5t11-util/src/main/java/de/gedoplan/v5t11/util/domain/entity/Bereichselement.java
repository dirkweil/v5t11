package de.gedoplan.v5t11.util.domain.entity;

import de.gedoplan.baselibs.persistence.entity.SingleIdEntity;
import de.gedoplan.v5t11.util.cdi.EventFirer;
import de.gedoplan.v5t11.util.domain.attribute.BereichselementId;
import de.gedoplan.v5t11.util.jsonb.JsonbInclude;

import javax.inject.Inject;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

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

  @Transient
  @Inject
  protected EventFirer eventFirer;

  protected Bereichselement(String bereich, String name) {
    this.bereich = bereich;
    this.name = name;
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
