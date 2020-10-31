package de.gedoplan.v5t11.fahrstrassen.entity;

import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import lombok.Getter;
import lombok.ToString;

@XmlAccessorType(XmlAccessType.NONE)
@Getter
public class AutoFahrstrasse {

  @XmlAttribute(required = true)
  private String bereich;

  @XmlAttribute(required = true)
  private String trigger;

  @XmlElement(name = "Fahrstrasse")
  private List<AutoFahrstrassenElement> elemente;

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder("AutoFahrstrasse{bereich=" + this.bereich + ", trigger=" + this.trigger + "}");
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

    @XmlAttribute
    private String start;

    @XmlAttribute
    private String endeBereich;

    @XmlAttribute(required = true)
    private String ende;

  }

  @SuppressWarnings("unused")
  private void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
    this.elemente.forEach(e -> {
      if (e.startBereich == null) {
        e.startBereich = this.bereich;
      }
      if (e.start == null) {
        e.start = this.trigger;
      }
      if (e.endeBereich == null) {
        e.endeBereich = this.bereich;
      }
    });
  }

}
