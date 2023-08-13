package de.gedoplan.v5t11.status.entity;

import de.gedoplan.v5t11.util.domain.attribute.SystemTyp;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Getter;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@AllArgsConstructor
public class SX2Kanal {

  private int index;

  private SystemTyp systemTyp;

  private int adresse;

  private boolean licht;

  private boolean rueckwaerts;

  private int fahrstufe;

  private int funktionStatus;

  @Override
  public String toString() {
    return String.format("SX2Kanal [index=%d, systemTyp=%s, adresse=%d, licht=%b, rueckwaerts=%b, fahrstufe=%d, funktionStatus=0x%04x]", this.index, this.systemTyp, this.adresse, this.licht,
      this.rueckwaerts,
      this.fahrstufe,
      this.funktionStatus);
  }
}
