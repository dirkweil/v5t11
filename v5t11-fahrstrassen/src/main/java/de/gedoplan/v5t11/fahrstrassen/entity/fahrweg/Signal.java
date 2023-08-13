package de.gedoplan.v5t11.fahrstrassen.entity.fahrweg;

import de.gedoplan.v5t11.util.domain.attribute.BereichselementId;
import de.gedoplan.v5t11.util.domain.entity.fahrweg.geraet.AbstractSignal;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = Signal.TABLE_NAME)
@Cacheable
@XmlAccessorType(XmlAccessType.NONE)
@NoArgsConstructor
public class Signal extends AbstractSignal implements ReservierbaresFahrwegelement {

  public static final String TABLE_NAME = "FS_SIGNAL";

  /**
   * Falls dieses Element Teil einer reservierten Fahrstrasse ist, Id dieser Fahrstrasse, sonst <code>null</code>
   */
  @Getter
  @Setter
  @AttributeOverride(name = "bereich", column = @Column(name = "RES_FS_BEREICH"))
  @AttributeOverride(name = "name", column = @Column(name = "RES_FS_NAME"))
  private BereichselementId reserviertefahrstrasseId;

  public Signal(String bereich, String name) {
    super(bereich, name);
  }

}
