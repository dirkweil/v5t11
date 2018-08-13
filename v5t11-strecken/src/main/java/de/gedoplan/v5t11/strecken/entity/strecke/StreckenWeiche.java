package de.gedoplan.v5t11.strecken.entity.strecke;

import de.gedoplan.v5t11.strecken.entity.Parcours;
import de.gedoplan.v5t11.strecken.entity.fahrweg.Weiche;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@XmlAccessorType(XmlAccessType.NONE)
@NoArgsConstructor
public class StreckenWeiche extends StreckenGeraet {

  @Getter
  @Setter
  private Weiche weiche;

  @Override
  public Weiche getFahrwegelement() {
    return this.weiche;
  }

  public StreckenGleisabschnitt createStreckenGleisabschnitt() {

    /*
     * Konvention: Eine Doppelweiche (z. B. DKW) besteht aus zwei Einzelweichen, deren Namen
     * mit der gleichen Nummer beginnen und einen eindeutigen Suffixbuchstaben haben (z. B. 11a, 11b).
     * Der zugehörige Gleisabschnitt enthält nur den numerischen Teil im Namen.
     */
    boolean doppelweiche = Character.isAlphabetic(this.name.charAt(this.name.length() - 1));
    String name;
    if (doppelweiche) {
      name = "W" + this.name.substring(0, this.name.length() - 1);
    } else {
      name = "W" + this.name;
    }

    return new StreckenGleisabschnitt(this.bereich, name);
  }

  @Override
  public void linkFahrwegelement(Parcours parcours) {
    this.weiche = parcours.getWeiche(this.bereich, this.name);
    if (this.weiche == null) {
      this.weiche = new Weiche(this.bereich, this.name);
      parcours.addWeiche(this.weiche);
    }
  }
}
