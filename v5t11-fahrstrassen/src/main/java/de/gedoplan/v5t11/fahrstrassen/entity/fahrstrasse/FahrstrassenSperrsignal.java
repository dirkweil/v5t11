package de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Embeddable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import lombok.NoArgsConstructor;

@Embeddable
// @Entity
@DiscriminatorValue("SS")
@XmlAccessorType(XmlAccessType.NONE)
@NoArgsConstructor
public class FahrstrassenSperrsignal extends FahrstrassenSignal {
  @Override
  public boolean isSperrsignal() {
    return true;
  }

}
