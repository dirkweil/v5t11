package de.gedoplan.v5t11.status.entity.baustein;

import de.gedoplan.v5t11.status.entity.fahrweg.Gleisabschnitt;

import java.util.SortedSet;
import java.util.TreeSet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Besetztmelder.
 *
 * @author dw
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Besetztmelder extends Baustein implements Encoder {
  /**
   * Zugeordnete Gleisabschnitte.
   */
  @XmlElement(name = "Gleisabschnitt", type = Gleisabschnitt.class)
  protected SortedSet<Gleisabschnitt> gleisabschnitte = new TreeSet<>();

  /**
   * Konstruktor.
   *
   * Wird nur während des JAXB-Unmarshalling aufgerufen.
   *
   * @param byteAnzahl
   *          Anzahl belegter Bytes (Adressen)
   */
  protected Besetztmelder(int byteAnzahl) {
    super(byteAnzahl);
  }

  @Override
  public String getLabelPrefix() {
    return "Besetzmelder";
  }

}
