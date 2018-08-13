package de.gedoplan.v5t11.strecken.entity.strecke;

import de.gedoplan.v5t11.strecken.entity.Parcours;
import de.gedoplan.v5t11.strecken.entity.fahrweg.Signal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@XmlAccessorType(XmlAccessType.NONE)
@NoArgsConstructor
public class StreckenSignal extends StreckenGeraet {

  @Getter
  @Setter
  private Signal signal;

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
}
