package de.gedoplan.v5t11.fahrstrassen.entity.fahrweg;

import de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse.Fahrstrasse;
import de.gedoplan.v5t11.util.domain.attribute.SignalStellung;
import de.gedoplan.v5t11.util.domain.entity.fahrweg.geraet.AbstractSignal;

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
@Table(name = Signal.TABLE_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@NoArgsConstructor
public class Signal extends AbstractSignal implements ReservierbaresFahrwegelement {

  public static final String TABLE_NAME = "FS_SIGNAL";

  /**
   * Falls dieses Element Teil einer reservierten Fahrstrasse ist, diese Fahrstrasse, sonst <code>null</code>
   */
  @Getter
  @Setter
  @ManyToOne
  @JoinColumn(name = "RESERVIERTE_FAHRSTRASSE_BEREICH", referencedColumnName = "BEREICH")
  @JoinColumn(name = "RESERVIERTE_FAHRSTRASSE_NAME", referencedColumnName = "NAME")
  protected Fahrstrasse reserviertefahrstrasse;

  public Signal(String bereich, String name) {
    super(bereich, name);
  }

  @Override
  public SignalStellung getStellung() {
    throw new UnsupportedOperationException("Stellung wird nicht mit v5t11-status synchroniert");
  }

  @Override
  public void setStellung(SignalStellung stellung) {
    throw new UnsupportedOperationException("Stellung wird nicht mit v5t11-status synchroniert");
  }

}
