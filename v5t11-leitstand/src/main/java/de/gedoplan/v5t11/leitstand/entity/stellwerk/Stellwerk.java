package de.gedoplan.v5t11.leitstand.entity.stellwerk;

import java.io.Serializable;
import java.util.List;
import java.util.ListIterator;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;

/**
 * Stellwerk.
 *
 * @author dw
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class Stellwerk implements Serializable, Comparable<Stellwerk> {
  /**
   * Bereich.
   *
   * Dient auch als ID.
   */
  @XmlAttribute
  @Getter
  private String bereich;

  @XmlElement(name = "Zeile")
  @Getter
  private List<StellwerkZeile> zeilen;

  @Override
  public int compareTo(Stellwerk o) {
    return this.bereich.compareTo(o.bereich);
  }

  @Override
  public String toString() {
    return "Stellwerk [bereich=" + this.bereich + "]";
  }

  /**
   * Nachbearbeitung nach JAXB-Unmarshal.
   *
   * @param unmarshaller
   *          Unmarshaller
   * @param parent
   *          Parent
   */
  @SuppressWarnings("unused")
  private void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {

    /*
     * Bereich in Elementen ergänzen, wenn nötig.
     * Zudem Spaltenanzahl feststellen.
     */
    int anzahlSpalten = 0;
    for (StellwerkZeile zeile : this.zeilen) {
      int anzahlSpaltenInZeile = 0;
      for (StellwerkElement element : zeile.getElemente()) {
        if (element.getBereich() == null) {
          element.setBereich(this.bereich);
        }

        anzahlSpaltenInZeile += element.anzahl;
      }

      if (anzahlSpalten < anzahlSpaltenInZeile) {
        anzahlSpalten = anzahlSpaltenInZeile;
      }
    }

    // Elemente mit anzahl>1 entsprechend oft vervielfältigen.
    for (StellwerkZeile zeile : this.zeilen) {
      ListIterator<StellwerkElement> iterator = zeile.getElemente().listIterator();
      while (iterator.hasNext()) {
        StellwerkElement element = iterator.next();
        int anzahl = element.getAnzahl();
        element.setAnzahl(1);
        while (anzahl > 1) {
          iterator.add(element);
          --anzahl;
        }
      }
    }

    // Zeilen mit Leerelementen auffüllen.
    StellwerkElement leerElement = new StellwerkElement();
    leerElement.setBereich(this.bereich);
    for (StellwerkZeile zeile : this.zeilen) {
      int anzahlSpaltenInZeile = zeile.getElemente().size();
      while (anzahlSpaltenInZeile < anzahlSpalten) {
        zeile.getElemente().add(leerElement);
        ++anzahlSpaltenInZeile;
      }
    }
  }

}
