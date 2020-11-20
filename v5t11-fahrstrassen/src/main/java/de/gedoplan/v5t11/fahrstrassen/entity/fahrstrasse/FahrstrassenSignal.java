package de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse;

import de.gedoplan.baselibs.utils.exception.BugException;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Signal;
import de.gedoplan.v5t11.fahrstrassen.persistence.SignalRepository;
import de.gedoplan.v5t11.util.domain.attribute.SignalStellung;
import de.gedoplan.v5t11.util.jsonb.JsonbInclude;

import javax.inject.Inject;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import lombok.Getter;
import lombok.NoArgsConstructor;

@XmlAccessorType(XmlAccessType.NONE)
@NoArgsConstructor
public abstract class FahrstrassenSignal extends FahrstrassenGeraet implements Cloneable {

  @Inject
  SignalRepository signalRepository;

  @Getter(onMethod_ = @JsonbInclude(full = true))
  @XmlAttribute
  private SignalStellung stellung;

  @Override
  public Signal getFahrwegelement() {
    Signal signal = this.signalRepository.findById(getId());
    if (signal == null) {
      throw new IllegalStateException("Signal nicht vorhanden: " + getId());
    }
    return signal;
  }

  @Override
  public void createFahrwegelement() {
    Signal signal = this.signalRepository.findById(getId());
    if (signal == null) {
      this.signalRepository.persist(new Signal(getBereich(), getName()));
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
  @JsonbInclude(full = true)
  public String getTyp() {
    return "SIGNAL";
  }
}
