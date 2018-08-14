package de.gedoplan.v5t11.strecken.entity.strecke;

import de.gedoplan.v5t11.strecken.entity.Parcours;
import de.gedoplan.v5t11.strecken.entity.fahrweg.Signal;
import de.gedoplan.v5t11.util.domain.SignalStellung;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import lombok.Getter;
import lombok.NoArgsConstructor;

@XmlAccessorType(XmlAccessType.NONE)
@NoArgsConstructor
public class StreckenSignal extends StreckenGeraet {

  @Getter
  private Signal signal;

  @Getter
  @XmlAttribute
  private SignalStellung stellung;

  @Override
  public Signal getFahrwegelement() {
    return this.signal;
  }

  @Override
  public void linkFahrwegelement(Parcours parcours) {
    this.signal = parcours.getSignal(this.bereich, this.name);
    if (this.signal == null) {
      this.signal = new Signal(this.bereich, this.name);
      parcours.addSignal(this.signal);
    }
  }

  @Override
  public String toString() {
    return super.toString() + ", stellung=" + this.stellung;
  }

}
