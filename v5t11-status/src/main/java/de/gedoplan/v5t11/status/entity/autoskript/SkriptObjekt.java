package de.gedoplan.v5t11.status.entity.autoskript;

import de.gedoplan.v5t11.status.entity.Steuerung;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import lombok.Getter;

@XmlAccessorType(XmlAccessType.NONE)
public abstract class SkriptObjekt {
  @XmlAttribute
  @Getter
  protected String bereich;

  @XmlAttribute(required = true)
  @Getter
  protected String name;

  @XmlAttribute
  @Getter
  protected String var;

  @Getter
  protected Object steuerungsObjekt;

  protected abstract String getDefaultVarPrefix();

  abstract void linkSteuerungsObjekt(Steuerung steuerung);

  /*
   * Nachbearbeitung nach JAXB-Unmarshal.
   */
  protected void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
    AutoSkript autoSkript;
    if (parent instanceof AutoSkript) {
      autoSkript = (AutoSkript) parent;
    } else {
      throw new IllegalArgumentException("Illegal parent " + parent);
    }

    if (this.bereich == null) {
      this.bereich = autoSkript.getBereich();
    }

    if (this.var == null) {
      this.var = getDefaultVarPrefix() + Character.toUpperCase(this.name.charAt(0)) + this.name.substring(1);
    }
  }
}
