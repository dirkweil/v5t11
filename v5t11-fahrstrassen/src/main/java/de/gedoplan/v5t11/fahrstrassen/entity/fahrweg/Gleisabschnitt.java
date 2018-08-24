package de.gedoplan.v5t11.fahrstrassen.entity.fahrweg;

import de.gedoplan.v5t11.util.jsonb.JsonbInclude;

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
  @Setter(onMethod = @__(@JsonbInclude))
  private volatile boolean besetzt;

  public Gleisabschnitt(String bereich, String name) {
    super(bereich, name);
  }

  public void copyStatus(Gleisabschnitt other) {
    this.besetzt = other.besetzt;
  }
}
