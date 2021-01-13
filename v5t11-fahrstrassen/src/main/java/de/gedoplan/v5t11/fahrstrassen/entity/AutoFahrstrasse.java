package de.gedoplan.v5t11.fahrstrassen.entity;

import de.gedoplan.baselibs.persistence.entity.UuidEntity;
import de.gedoplan.baselibs.utils.inject.InjectionUtil;

import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import lombok.Getter;
import lombok.ToString;

/**
 * Automatik-Fahrstrasse.
 * 
 * Eine AutoFahrstrasse verknüpft einen auslösenden Gleis - den sog. Trigger - mit einer Liste von
 * Fahrstrassen. Ändert sich im Betrieb der Trigger auf besetzt, wird eine der Fahrstrassen reserviert, soweit
 * das möglich ist. Die Fahrstrassen werden in der Reihenfolge der Liste berücksichtigt.
 * 
 * @author dw
 *
 */
@XmlAccessorType(XmlAccessType.NONE)
@Getter
public class AutoFahrstrasse extends UuidEntity {

  public static final String TABLE_NAME = "FS_AUTO_FAHRSTRASSE";

  // Bereich des Triggers (und der AutoFahrstrasse)
  @XmlAttribute(name = "bereich", required = true)
  private String triggerBereich;

  // Name des Triggers
  @XmlAttribute(name = "trigger", required = true)
  private String triggerName;

  @XmlElement(name = "Fahrstrasse")
  private List<AutoFahrstrassenElement> elemente;

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder("AutoFahrstrasse{bereich=" + this.triggerBereich + ", trigger=" + this.triggerName + "}");
    this.elemente.forEach(e -> {
      builder.append("\n  ");
      builder.append(e);
    });
    return builder.toString();
  }

  @XmlAccessorType(XmlAccessType.NONE)
  @Getter
  @ToString
  public static class AutoFahrstrassenElement {

    @XmlAttribute
    private String startBereich;

    @XmlAttribute(name = "start")
    private String startName;

    @XmlAttribute
    private String endeBereich;

    @XmlAttribute(name = "ende", required = true)
    private String endeName;

  }

  @SuppressWarnings("unused")
  private void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
    this.elemente.forEach(e -> {
      if (e.startBereich == null) {
        e.startBereich = this.triggerBereich;
      }
      if (e.startName == null) {
        e.startName = this.triggerName;
      }
      if (e.endeBereich == null) {
        e.endeBereich = this.triggerBereich;
      }
    });
  }

  public void injectFields() {
    InjectionUtil.injectFields(this);
  }

}
