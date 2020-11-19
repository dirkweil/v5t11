package de.gedoplan.v5t11.fahrstrassen.entity;

import de.gedoplan.baselibs.persistence.entity.UuidEntity;

import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Table;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import lombok.Getter;
import lombok.ToString;

@Entity
@Table(name = AutoFahrstrasse.TABLE_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@Getter
public class AutoFahrstrasse extends UuidEntity {

  public static final String TABLE_NAME = "FS_AUTO_FAHRSTRASSE";

  @XmlAttribute(required = true)
  private String bereich;

  @XmlAttribute(required = true)
  private String trigger;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = TABLE_NAME + "_ELEMENTE")
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

  @Embeddable
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
