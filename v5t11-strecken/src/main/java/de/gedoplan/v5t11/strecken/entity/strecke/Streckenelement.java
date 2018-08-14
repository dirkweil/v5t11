package de.gedoplan.v5t11.strecken.entity.strecke;

import de.gedoplan.v5t11.strecken.entity.Bereichselement;
import de.gedoplan.v5t11.strecken.entity.Parcours;
import de.gedoplan.v5t11.strecken.entity.fahrweg.Fahrwegelement;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import lombok.NoArgsConstructor;

@XmlAccessorType(XmlAccessType.NONE)
@NoArgsConstructor
public abstract class Streckenelement extends Bereichselement {

  /**
   * In Zählrichtung orientiert?
   */
  @XmlAttribute
  protected Boolean zaehlrichtung;

  public Streckenelement(String bereich, String name, boolean zaehlrichtung) {
    super(bereich, name);
    this.zaehlrichtung = zaehlrichtung;
  }

  public boolean isZaehlrichtung() {
    return this.zaehlrichtung != null ? this.zaehlrichtung : false;
  }

  public abstract Fahrwegelement getFahrwegelement();

  public int getRank() {
    return 0;
  }

  public abstract void linkFahrwegelement(Parcours parcours);

  @Override
  public String toString() {
    return getFahrwegelement() + ", zaehlrichtung=" + this.zaehlrichtung;
  }

}
