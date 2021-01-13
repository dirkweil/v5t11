package de.gedoplan.v5t11.status.entity.autoskript;

import de.gedoplan.v5t11.status.entity.Steuerung;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.NONE)
public class SkriptGleis extends SkriptObjekt {

  @Override
  protected String getDefaultVarPrefix() {
    return "gleis";
  }

  @Override
  void linkSteuerungsObjekt(Steuerung steuerung) {
    this.steuerungsObjekt = steuerung.getGleis(this.bereich, this.name);
    if (this.steuerungsObjekt == null) {
      throw new IllegalArgumentException("Skript-Objekt nicht gefunden: Gleis{bereich=" + this.bereich + ", name=" + this.name + "}");
    }
  }

  @Override
  protected boolean uses(Class<?> enumClass) {
    return false;
  }
}
