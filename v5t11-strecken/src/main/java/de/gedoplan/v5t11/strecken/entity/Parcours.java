package de.gedoplan.v5t11.strecken.entity;

import de.gedoplan.v5t11.strecken.entity.fahrweg.Gleisabschnitt;
import de.gedoplan.v5t11.strecken.entity.fahrweg.Signal;
import de.gedoplan.v5t11.strecken.entity.fahrweg.Weiche;
import de.gedoplan.v5t11.strecken.entity.strecke.Strecke;

import java.util.Collection;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;

/**
 * Steuerung.
 *
 * Diese Klasse fasst alle zur Steuerung gehörenden Elemente zusammen.
 *
 * @author dw
 */
@XmlRootElement(name = "Parcours")
@XmlAccessorType(XmlAccessType.NONE)
public class Parcours {

  @XmlElement(name = "Strecke", type = Strecke.class)
  @Getter
  private List<Strecke> strecken;

  @Getter
  private SortedSet<String> bereiche = new TreeSet<>();

  @Getter
  private SortedSet<Gleisabschnitt> gleisabschnitte = new TreeSet<>();

  @Getter
  private SortedSet<Signal> signale = new TreeSet<>();

  @Getter
  private SortedSet<Weiche> weichen = new TreeSet<>();

  /**
   * Gleisabschnitt liefern.
   *
   * @param bereich
   *          Bereich
   * @param name
   *          Name
   * @return gefundener Gleisabschnitt oder <code>null</code>
   */
  public Gleisabschnitt getGleisabschnitt(String bereich, String name) {
    return getBereichselement(bereich, name, this.gleisabschnitte);
  }

  public void addGleisabschnitt(Gleisabschnitt gleisabschnitt) {
    this.gleisabschnitte.add(gleisabschnitt);
    this.bereiche.add(gleisabschnitt.getBereich());
  }

  /**
   * Signal liefern.
   *
   * @param bereich
   *          Bereich
   * @param name
   *          Name
   * @return gefundenes Signal oder <code>null</code>
   */
  public Signal getSignal(String bereich, String name) {
    return getBereichselement(bereich, name, this.signale);
  }

  public void addSignal(Signal signal) {
    this.signale.add(signal);
    this.bereiche.add(signal.getBereich());
  }

  /**
   * Weiche liefern.
   *
   * @param bereich
   *          Bereich
   * @param name
   *          Name
   * @return gefundene Weiche oder <code>null</code>
   */
  public Weiche getWeiche(String bereich, String name) {
    return getBereichselement(bereich, name, this.weichen);
  }

  public void addWeiche(Weiche weiche) {
    this.weichen.add(weiche);
    this.bereiche.add(weiche.getBereich());
  }

  private static <T extends Bereichselement> T getBereichselement(String bereich, String name, Collection<T> set) {
    for (T element : set) {
      if (element.getBereich().equals(bereich) && element.getName().equals(name)) {
        return element;
      }
    }

    return null;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();

    this.strecken.forEach(strecke -> {
      sb.append(String.format("%s zaehlrichtung=%b\n", strecke, strecke.isZaehlrichtung()));

      strecke.getElemente().forEach(element -> sb.append(String.format("  %s\n", element)));
    });

    return sb.toString();
  }

}
