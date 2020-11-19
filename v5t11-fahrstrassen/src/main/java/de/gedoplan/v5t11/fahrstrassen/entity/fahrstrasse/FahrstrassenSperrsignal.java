package de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import lombok.NoArgsConstructor;

@XmlAccessorType(XmlAccessType.NONE)
@NoArgsConstructor
public class FahrstrassenSperrsignal extends FahrstrassenSignal {
  @Override
  public boolean isSperrsignal() {
    return true;
  }

}
