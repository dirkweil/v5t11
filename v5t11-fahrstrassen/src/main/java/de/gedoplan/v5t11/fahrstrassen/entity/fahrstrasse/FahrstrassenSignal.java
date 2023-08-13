package de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse;

import de.gedoplan.baselibs.utils.exception.BugException;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Signal;
import de.gedoplan.v5t11.fahrstrassen.persistence.SignalRepository;
import de.gedoplan.v5t11.util.domain.attribute.FahrstrassenelementTyp;
import de.gedoplan.v5t11.util.domain.attribute.SignalStellung;
import de.gedoplan.v5t11.util.jsonb.JsonbInclude;

import jakarta.inject.Inject;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;

import lombok.Getter;
import lombok.NoArgsConstructor;

@XmlAccessorType(XmlAccessType.NONE)
@NoArgsConstructor
public class FahrstrassenSignal extends FahrstrassenGeraet implements Cloneable {

  @Inject
  SignalRepository signalRepository;

  @Getter(onMethod_ = @JsonbInclude(full = true))
  @XmlAttribute
  private SignalStellung stellung;

  @Override
  public Signal getFahrwegelement() {
    return this.signalRepository
      .findById(getId())
      .orElseThrow(() -> new IllegalStateException("Signal nicht vorhanden: " + getId()));
  }

  @Override
  public void createFahrwegelement() {
    if (this.signalRepository.findById(getId()).isEmpty()) {
      this.signalRepository.persist(new Signal(getBereich(), getName()));
    }
  }

  //  @Override
  //  public String toString() {
  //    return super.toString() + ", stellung=" + this.stellung;
  //  }

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
  public FahrstrassenelementTyp getTyp() {
    return FahrstrassenelementTyp.SIGNAL;
  }

  @Override
  public boolean isHauptsignal() {
    return SignalTyp.fromName(getName()) == SignalTyp.HAUPTSIGNAL;
  }

  @Override
  public boolean isVorsignal() {
    return SignalTyp.fromName(getName()) == SignalTyp.VORSIGNAL;
  }

  @Override
  public boolean isSperrsignal() {
    return SignalTyp.fromName(getName()) == SignalTyp.SPERRSIGNAL;
  }

  @Override
  protected Object clone() throws CloneNotSupportedException {
    // TODO Auto-generated method stub
    return super.clone();
  }

  public static enum SignalTyp {
    HAUPTSIGNAL, SPERRSIGNAL, VORSIGNAL;

    public static SignalTyp fromName(String name) {
      char erstesZeichen = name.charAt(0);
      if (Character.isUpperCase(erstesZeichen)) {
        return HAUPTSIGNAL;
      }

      if (Character.isLowerCase(erstesZeichen)) {
        return VORSIGNAL;
      }

      assert Character.isDigit(erstesZeichen);

      return SPERRSIGNAL;
    }
  }
}
