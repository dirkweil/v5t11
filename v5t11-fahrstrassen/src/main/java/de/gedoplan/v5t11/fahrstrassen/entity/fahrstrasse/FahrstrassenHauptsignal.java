package de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("HS")
@XmlAccessorType(XmlAccessType.NONE)
@NoArgsConstructor
public class FahrstrassenHauptsignal extends FahrstrassenSignal {
  @Override
  public boolean isHauptsignal() {
    return true;
  }

}
