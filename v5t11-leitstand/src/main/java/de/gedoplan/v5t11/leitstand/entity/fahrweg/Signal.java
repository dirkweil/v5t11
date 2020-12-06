package de.gedoplan.v5t11.leitstand.entity.fahrweg;

import de.gedoplan.v5t11.util.domain.attribute.BereichselementId;
import de.gedoplan.v5t11.util.domain.attribute.SignalStellung;
import de.gedoplan.v5t11.util.domain.entity.Fahrwegelement;
import de.gedoplan.v5t11.util.domain.entity.fahrweg.geraet.AbstractSignal;
import de.gedoplan.v5t11.util.jsonb.JsonbInclude;

import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
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
public class Signal extends AbstractSignal {

  public static final String TABLE_NAME = "LS_SIGNAL";

  @Getter
  @Setter
  @JsonbInclude
  @ElementCollection
  @CollectionTable(name = "LS_SIGNAL_ERLAUBT")
  @Convert(converter = SignalStellung.Adapter4Jpa.class)
  private Set<SignalStellung> erlaubteStellungen;

  @Getter
  @Setter
  @JsonbInclude
  private String typ;

  public Signal(BereichselementId id) {
    super(id);
  }

  @Override
  public boolean copyStatus(Fahrwegelement other) {
    Signal otherSignal = (Signal) other;
    if (otherSignal.erlaubteStellungen != null) {
      this.erlaubteStellungen = otherSignal.erlaubteStellungen;
    }
    if (otherSignal.typ != null) {
      this.typ = otherSignal.typ;
    }
    return super.copyStatus(other);
  }

}
