package de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse;

import de.gedoplan.v5t11.fahrstrassen.entity.Parcours;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Weiche;
import de.gedoplan.v5t11.util.domain.attribute.WeichenStellung;
import de.gedoplan.v5t11.util.domain.entity.fahrweg.geraet.AbstractWeiche;
import de.gedoplan.v5t11.util.jsonb.JsonbInclude;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import lombok.Getter;
import lombok.NoArgsConstructor;

@XmlAccessorType(XmlAccessType.NONE)
@NoArgsConstructor
public class FahrstrassenWeiche extends FahrstrassenGeraet {

  @Getter
  private Weiche weiche;

  @Getter(onMethod_ = @JsonbInclude(full = true))
  @XmlAttribute
  private WeichenStellung stellung;

  @Override
  public Weiche getFahrwegelement() {
    return this.weiche;
  }

  public FahrstrassenGleisabschnitt createFahrstrassenGleisabschnitt() {

    /*
     * Konvention: Eine Doppelweiche (z. B. DKW) besteht aus zwei Einzelweichen, deren Namen
     * mit der gleichen Nummer beginnen und einen eindeutigen Suffixbuchstaben haben (z. B. 11a, 11b).
     * Der zugehörige Gleisabschnitt enthält nur den numerischen Teil im Namen.
     */
    String name = getName();
    boolean doppelweiche = Character.isAlphabetic(name.charAt(name.length() - 1));
    String gleisAbschnittName;
    if (doppelweiche) {
      gleisAbschnittName = AbstractWeiche.PREFIX_WEICHEN_GLEISABSCHNITT + name.substring(0, name.length() - 1);
    } else {
      gleisAbschnittName = AbstractWeiche.PREFIX_WEICHEN_GLEISABSCHNITT + name;
    }

    return new FahrstrassenGleisabschnitt(getBereich(), gleisAbschnittName, this.zaehlrichtung);
  }

  @Override
  public void linkFahrwegelement(Parcours parcours) {
    this.weiche = parcours.getWeiche(getBereich(), getName());
    if (this.weiche == null) {
      this.weiche = new Weiche(getBereich(), getName());
      parcours.addWeiche(this.weiche);
    }
  }

  @Override
  public int getRank() {
    return this.stellung == WeichenStellung.ABZWEIGEND ? 1 : 0;
  }

  @Override
  public String toString() {
    return super.toString() + ", stellung=" + this.stellung;
  }

  @Override
  public void reservieren(Fahrstrasse fahrstrasse) {
    if (this.weiche != null) {
      if (!this.schutz) {
        this.weiche.reserviereFuerFahrstrasse(fahrstrasse, isZaehlrichtung());
      }
    }
  }

  @Override
  @JsonbInclude(full = true)
  public String getTyp() {
    return "WEICHE";
  }
}
