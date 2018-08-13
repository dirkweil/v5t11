package de.gedoplan.v5t11.strecken.entity.fahrweg;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@XmlAccessorType(XmlAccessType.NONE)
@NoArgsConstructor
public class Gleisabschnitt extends Fahrwegelement {

  /**
   * Gleisabschnitt besetzt?
   */
  @Getter
  @Setter
  private boolean besetzt;

  public Gleisabschnitt(String bereich, String name) {
    super(bereich, name);
  }
}
