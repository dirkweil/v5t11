package de.gedoplan.v5t11.status.entity.autoskript;

import de.gedoplan.v5t11.status.entity.Steuerung;
import de.gedoplan.v5t11.util.domain.attribute.SignalStellung;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.NONE)
public class SkriptSignal extends SkriptObjekt {

  @Override
  protected String getDefaultVarPrefix() {
    return "signal";
  }

  @Override
  void linkSteuerungsObjekt(Steuerung steuerung) {
    this.steuerungsObjekt = steuerung.getSignal(this.bereich, this.name);
    if (this.steuerungsObjekt == null) {
      throw new IllegalArgumentException("Skript-Objekt nicht gefunden: Signal{bereich=" + this.bereich + ", name=" + this.name + "}");
    }
  }

  @Override
  protected boolean uses(Class<?> enumClass) {
    return enumClass == SignalStellung.class;
  }
}
