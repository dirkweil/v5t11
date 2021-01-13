package de.gedoplan.v5t11.fahrstrassen.entity.fahrweg;

import de.gedoplan.v5t11.util.domain.attribute.BereichselementId;
import de.gedoplan.v5t11.util.domain.entity.Fahrwegelement;
import de.gedoplan.v5t11.util.domain.entity.fahrweg.AbstractGleis;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = Gleis.TABLE_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@NoArgsConstructor
public class Gleis extends AbstractGleis implements ReservierbaresFahrwegelement {

  public static final String TABLE_NAME = "FS_GLEIS";

  /**
   * Falls dieses Element Teil einer reservierten Fahrstrasse ist, Id dieser Fahrstrasse, sonst <code>null</code>
   */
  @Getter
  @AttributeOverride(name = "bereich", column = @Column(name = "RES_FS_BEREICH"))
  @AttributeOverride(name = "name", column = @Column(name = "RES_FS_NAME"))
  private BereichselementId reserviertefahrstrasseId;

  @Getter
  private boolean durchfahren;

  public Gleis(String bereich, String name) {
    super(bereich, name);
  }

  @Override
  public void setReserviertefahrstrasseId(BereichselementId fahrstrasseId) {
    this.reserviertefahrstrasseId = fahrstrasseId;
    this.durchfahren = false;
  }

  @Override
  public boolean copyStatus(Fahrwegelement other) {
    boolean warBesetzt = this.isBesetzt();
    if (!super.copyStatus(other)) {
      return false;
    }

    if (this.reserviertefahrstrasseId != null) {
      if (this.isBesetzt()) {
        this.durchfahren = false;
      } else if (warBesetzt) {
        this.durchfahren = true;
      }
    }

    return true;
  }

}
