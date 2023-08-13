package de.gedoplan.v5t11.status.entity.autoskript;

import de.gedoplan.v5t11.status.entity.Steuerung;

import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;

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

  protected abstract boolean uses(Class<?> enumClass);
}
