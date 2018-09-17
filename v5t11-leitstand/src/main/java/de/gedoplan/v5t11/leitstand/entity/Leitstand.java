package de.gedoplan.v5t11.leitstand.entity;

import de.gedoplan.v5t11.leitstand.entity.fahrweg.Gleisabschnitt;
import de.gedoplan.v5t11.leitstand.entity.fahrweg.Signal;
import de.gedoplan.v5t11.leitstand.entity.fahrweg.Weiche;
import de.gedoplan.v5t11.leitstand.entity.stellwerk.Stellwerk;
import de.gedoplan.v5t11.leitstand.entity.stellwerk.StellwerkElement;
import de.gedoplan.v5t11.leitstand.entity.stellwerk.StellwerkZeile;
import de.gedoplan.v5t11.util.domain.entity.Bereichselement;

import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.BiFunction;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;

/**
 * Leitstand.
 *
 * Diese Klasse fasst alle zum Leitstand gehörenden Elemente zusammen.
 *
 * @author dw
 */
@XmlRootElement(name = "Leitstand")
@XmlAccessorType(XmlAccessType.NONE)
public class Leitstand {

  @XmlElement(name = "Stellwerk")
  private SortedSet<Stellwerk> stellwerke = new TreeSet<>();

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

  /**
   * Gleisabschnitt liefern oder bei Bedarf neu anlegen.
   *
   * @param bereich
   *          Bereich
   * @param name
   *          Name
   * @return gefundener oder erzeugter Gleisabschnitt
   */
  public Gleisabschnitt getOrCreateGleisabschnitt(String bereich, String name) {
    return getOrCreateBereichselement(bereich, name, this.gleisabschnitte, Gleisabschnitt::new);
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

  /**
   * Signal liefern oder bei Bedarf neu anlegen.
   *
   * @param bereich
   *          Bereich
   * @param name
   *          Name
   * @return gefundenes oder erzeugtes Signal
   */
  public Signal getOrCreateSignal(String bereich, String name) {
    return getOrCreateBereichselement(bereich, name, this.signale, Signal::new);
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

  /**
   * Weiche liefern oder bei Bedarf neu anlegen.
   *
   * @param bereich
   *          Bereich
   * @param name
   *          Name
   * @return gefundene oder erzeugte Weiche
   */
  public Weiche getOrCreateWeiche(String bereich, String name) {
    return getOrCreateBereichselement(bereich, name, this.weichen, Weiche::new);
  }

  private static <T extends Bereichselement> T getBereichselement(String bereich, String name, Collection<T> set) {
    for (T element : set) {
      if (element.getBereich().equals(bereich) && element.getName().equals(name)) {
        return element;
      }
    }

    return null;
  }

  private static synchronized <T extends Bereichselement> T getOrCreateBereichselement(String bereich, String name, Collection<T> set, BiFunction<String, String, T> creator) {
    T element = getBereichselement(bereich, name, set);
    if (element == null) {
      element = creator.apply(bereich, name);
      set.add(element);
    }
    return element;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder();
    for (Stellwerk stellwerk : this.stellwerke) {
      buf.append("\n").append(stellwerk);
      for (StellwerkZeile zeile : stellwerk.getZeilen()) {
        buf.append("\n  Zeile");
        for (StellwerkElement element : zeile.getElemente()) {
          buf.append("\n    " + element);
        }
      }
    }

    return buf.toString();
  }
}
