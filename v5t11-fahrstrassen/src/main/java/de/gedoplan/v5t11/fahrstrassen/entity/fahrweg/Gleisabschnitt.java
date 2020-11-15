package de.gedoplan.v5t11.fahrstrassen.entity.fahrweg;

import de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse.Fahrstrasse;
import de.gedoplan.v5t11.util.domain.entity.fahrweg.AbstractGleisabschnitt;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = Gleisabschnitt.TABLE_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@NoArgsConstructor
public class Gleisabschnitt extends AbstractGleisabschnitt implements ReservierbaresFahrwegelement {

  public static final String TABLE_NAME = "FS_GLEISABSCHNITT";

  /**
   * Falls dieses Element Teil einer reservierten Fahrstrasse ist, diese Fahrstrasse, sonst <code>null</code>
   */
  @Getter
  @Setter
  @ManyToOne
  @JoinColumn(name = "RESERVIERTE_FAHRSTRASSE_BEREICH", referencedColumnName = "BEREICH")
  @JoinColumn(name = "RESERVIERTE_FAHRSTRASSE_NAME", referencedColumnName = "NAME")
  protected Fahrstrasse reserviertefahrstrasse;

  public Gleisabschnitt(String bereich, String name) {
    super(bereich, name);
  }

  public synchronized void copyStatus(Gleisabschnitt other) {
    setBesetzt(other.isBesetzt());
  }

}
