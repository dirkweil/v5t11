package de.gedoplan.v5t11.fahrstrassen.entity.fahrweg;

import de.gedoplan.v5t11.util.domain.attribute.BereichselementId;
import de.gedoplan.v5t11.util.domain.entity.fahrweg.geraet.AbstractWeiche;

import javax.persistence.AttributeOverride;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = Weiche.TABLE_NAME)
@Cacheable
@XmlAccessorType(XmlAccessType.NONE)
@NoArgsConstructor
public class Weiche extends AbstractWeiche implements ReservierbaresFahrwegelement {

  public static final String TABLE_NAME = "FS_WEICHE";

  /**
   * Falls dieses Element Teil einer reservierten Fahrstrasse ist, Id dieser Fahrstrasse, sonst <code>null</code>
   */
  @Getter
  @Setter
  @AttributeOverride(name = "bereich", column = @Column(name = "RES_FS_BEREICH"))
  @AttributeOverride(name = "name", column = @Column(name = "RES_FS_NAME"))
  private BereichselementId reserviertefahrstrasseId;

  public Weiche(String bereich, String name) {
    super(bereich, name);
  }

}
