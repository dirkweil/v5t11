package de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse;

import de.gedoplan.v5t11.fahrstrassen.entity.Bereichselement;
import de.gedoplan.v5t11.fahrstrassen.entity.Parcours;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Fahrwegelement;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import lombok.NoArgsConstructor;

@XmlAccessorType(XmlAccessType.NONE)
@NoArgsConstructor
public abstract class Fahrstrassenelement extends Bereichselement {

  @XmlAttribute
  protected Boolean zaehlrichtung;

  public Fahrstrassenelement(String bereich, String name, boolean zaehlrichtung) {
    super(bereich, name);
    this.zaehlrichtung = zaehlrichtung;
  }

  /**
   * In Zählrichtung orientiert?
   *
   * @return <code>true</code>, wenn in Zählrichtung
   */
  public boolean isZaehlrichtung() {
    return this.zaehlrichtung != null ? this.zaehlrichtung : false;
  }

  /**
   * Schutzfunktion?
   * Siehe {@link FahrstrassenGeraet#schutz}.
   *
   * @return <code>true</code>, wenn Schutzfunktion
   */
  public boolean isSchutz() {
    return false;
  }

  /**
   * Zugehöriges Fahrwegelement liefern.
   *
   * @return Fahrwegelement
   */
  public abstract Fahrwegelement getFahrwegelement();

  /**
   * Rang für Anordnung von Fahrstzrassenvorschlägen liefern.
   *
   * @return Rang der Fahrstrasse (kleiner = besser passend)
   */
  public int getRank() {
    return 0;
  }

  /**
   * Zugehöriges Fahrwegelement suchen und eintragen.
   * 
   * @param parcours
   *          Parcours
   */
  public abstract void linkFahrwegelement(Parcours parcours);

  @Override
  public String toString() {
    return getFahrwegelement() + ", zaehlrichtung=" + this.zaehlrichtung;
  }

}
