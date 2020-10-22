package de.gedoplan.v5t11.status.entity.autoskript;

import de.gedoplan.v5t11.status.entity.Steuerung;
import de.gedoplan.v5t11.util.domain.attribute.WeichenStellung;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.NONE)
public class SkriptWeiche extends SkriptObjekt {

  @Override
  protected String getDefaultVarPrefix() {
    return "weiche";
  }

  @Override
  void linkSteuerungsObjekt(Steuerung steuerung) {
    this.steuerungsObjekt = steuerung.getWeiche(this.bereich, this.name);
    if (this.steuerungsObjekt == null) {
      throw new IllegalArgumentException("Skript-Objekt nicht gefunden: Weiche{bereich=" + this.bereich + ", name=" + this.name + "}");
    }
  }

  @Override
  protected boolean uses(Class<?> enumClass) {
    return enumClass == WeichenStellung.class;
  }
}
