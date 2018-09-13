package de.gedoplan.v5t11.util.domain.entity;

import de.gedoplan.baselibs.persistence.entity.SingleIdEntity;
import de.gedoplan.v5t11.util.domain.attribute.BereichselementId;
import de.gedoplan.v5t11.util.jsonb.JsonbInclude;

import javax.persistence.IdClass;
import javax.persistence.MappedSuperclass;
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
  @Getter(onMethod_ = @JsonbInclude)
  private String bereich;

  @XmlAttribute
  @Getter(onMethod_ = @JsonbInclude)
  private String name;

  private transient BereichselementId id;

  @JsonbInclude
  protected void setBereich(String bereich) {
    this.bereich = bereich;
    this.id = null;
  }

  @JsonbInclude
  protected void setName(String name) {
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

  public String toDisplayString() {
    return this.bereich + "/" + this.name;
  }

  @Override
  public int compareTo(Bereichselement other) {
    return getId().compareTo(other.getId());
  }

}
