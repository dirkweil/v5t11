package de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse;

import de.gedoplan.baselibs.utils.exception.BugException;
import de.gedoplan.v5t11.fahrstrassen.entity.Parcours;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Signal;
import de.gedoplan.v5t11.util.domain.attribute.SignalStellung;
import de.gedoplan.v5t11.util.jsonb.JsonbInclude;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import lombok.Getter;
import lombok.NoArgsConstructor;

@XmlAccessorType(XmlAccessType.NONE)
@NoArgsConstructor
public abstract class FahrstrassenSignal extends FahrstrassenGeraet implements Cloneable {

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
    this.signal = parcours.getSignal(getBereich(), getName());
    if (this.signal == null) {
      this.signal = new Signal(getBereich(), getName());
      parcours.addSignal(this.signal);
    }
  }

  @Override
  public String toString() {
    return super.toString() + ", stellung=" + this.stellung;
  }

  public Fahrstrassenelement createCopy(SignalStellung stellung) {
    try {
      FahrstrassenSignal copy = (FahrstrassenSignal) clone();
      copy.stellung = stellung;
      return copy;
    } catch (CloneNotSupportedException e) {
      throw new BugException("Cannot clone FahrstrassenSignal");
    }
  }

  @SuppressWarnings("unused")
  private void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
    if (isSchutz()) {
      this.stellung = SignalStellung.HALT;
    }
  }

  @Override
  public void reservieren(Fahrstrasse fahrstrasse) {
    if (this.signal != null) {
      if (!this.schutz) {
        this.signal.reserviereFuerFahrstrasse(fahrstrasse, isZaehlrichtung());
      }
    }
  }

  @Override
  @JsonbInclude(full = true)
  public String getTyp() {
    return "SIGNAL";
  }
}
