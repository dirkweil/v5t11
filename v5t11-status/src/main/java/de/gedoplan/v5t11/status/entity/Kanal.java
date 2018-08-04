package de.gedoplan.v5t11.status.entity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
public class Kanal {
  @XmlAttribute(name = "adr", required = true)
  private int adresse;

  @Setter
  private int wert;

  public Kanal(int adresse) {
    this.adresse = adresse;
  }

}
