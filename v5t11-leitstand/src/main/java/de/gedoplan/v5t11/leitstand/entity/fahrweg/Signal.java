package de.gedoplan.v5t11.leitstand.entity.fahrweg;

import de.gedoplan.v5t11.util.domain.attribute.BereichselementId;
import de.gedoplan.v5t11.util.domain.attribute.SignalTyp;
import de.gedoplan.v5t11.util.domain.entity.Fahrwegelement;
import de.gedoplan.v5t11.util.domain.entity.fahrweg.geraet.AbstractSignal;
import de.gedoplan.v5t11.util.jsonb.JsonbInclude;

import javax.persistence.Convert;
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
  @Convert(converter = SignalTyp.Adapter4Jpa.class)
  private SignalTyp typ;

  public Signal(BereichselementId id) {
    super(id);
  }

  @Override
  public boolean copyStatus(Fahrwegelement other) {
    Signal otherSignal = (Signal) other;
    if (otherSignal.typ != null) {
      this.typ = otherSignal.typ;
    }
    return super.copyStatus(other);
  }

}
