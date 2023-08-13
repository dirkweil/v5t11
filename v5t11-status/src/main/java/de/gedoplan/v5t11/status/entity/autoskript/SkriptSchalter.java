package de.gedoplan.v5t11.status.entity.autoskript;

import de.gedoplan.v5t11.status.entity.Steuerung;
import de.gedoplan.v5t11.util.domain.attribute.SchalterStellung;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.NONE)
public class SkriptSchalter extends SkriptObjekt {
  @Override
  protected String getDefaultVarPrefix() {
    return "schalter";
  }

  @Override
  void linkSteuerungsObjekt(Steuerung steuerung) {
    this.steuerungsObjekt = steuerung.getSchalter(this.bereich, this.name);
    if (this.steuerungsObjekt == null) {
      throw new IllegalArgumentException("Skript-Objekt nicht gefunden: Schalter{bereich=" + this.bereich + ", name=" + this.name + "}");
    }
  }

  @Override
  protected boolean uses(Class<?> enumClass) {
    return enumClass == SchalterStellung.class;
  }

}
