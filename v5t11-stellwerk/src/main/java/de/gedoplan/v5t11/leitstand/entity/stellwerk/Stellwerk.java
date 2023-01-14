package de.gedoplan.v5t11.leitstand.entity.stellwerk;

import de.gedoplan.baselibs.utils.inject.InjectionUtil;
import de.gedoplan.v5t11.leitstand.entity.Leitstand;
import lombok.Getter;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.List;
import java.util.ListIterator;

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
  @XmlAttribute(required = true)
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
   *   Unmarshaller
   * @param parent
   *   Parent
   */
  @SuppressWarnings("unused")
  private void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {

    if (!(parent instanceof Leitstand)) {
      throw new IllegalArgumentException("Illegal parent " + parent);
    }

    Leitstand leitstand = (Leitstand) parent;

    leitstand.getBereiche().add(this.bereich);

    /*
     * Bereich in Elementen ergänzen, wenn nötig.
     * Zudem Spaltenanzahl feststellen.
     */
    int anzahlSpalten = 0;
    for (StellwerkZeile zeile : this.zeilen) {
      int anzahlSpaltenInZeile = 0;
      for (StellwerkElement element : zeile.getElemente()) {
        element.stellwerksBereich = this.bereich;
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
        int anzahl = element.anzahl;
        element.anzahl = 1;
        while (anzahl > 1) {
          iterator.add(element.clone());
          --anzahl;
        }
      }
    }

    // Zeilen mit Leerelementen auffüllen.
    for (StellwerkZeile zeile : this.zeilen) {
      int anzahlSpaltenInZeile = zeile.getElemente().size();
      while (anzahlSpaltenInZeile < anzahlSpalten) {
        StellwerkElement leerElement = new StellwerkLeer();
        leerElement.setBereich(this.bereich);
        zeile.getElemente().add(leerElement);
        ++anzahlSpaltenInZeile;
      }
    }

    // Zeilen-, Spaltennummern und UI-ID setzen
    int zeile = 1;
    for (StellwerkZeile stellwerkZeile : this.zeilen) {
      int spalte = 1;
      for (StellwerkElement stellwerkElement : stellwerkZeile.getElemente()) {
        stellwerkElement.zeilenNr = zeile;
        stellwerkElement.spaltenNr = spalte;

        stellwerkElement.uiId = this.bereich + "_" + zeile + "_" + spalte;

        ++spalte;
      }

      ++zeile;
    }
  }

  public void injectFields() {
    InjectionUtil.injectFields(this);
    this.zeilen.forEach(StellwerkZeile::injectFields);
  }

  public void addPersistentEntries() {
    this.zeilen.forEach(StellwerkZeile::addPersistentEntries);
  }
}
