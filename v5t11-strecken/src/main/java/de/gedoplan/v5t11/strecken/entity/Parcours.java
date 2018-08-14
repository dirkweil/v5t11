package de.gedoplan.v5t11.strecken.entity;

import de.gedoplan.v5t11.strecken.entity.fahrweg.Gleisabschnitt;
import de.gedoplan.v5t11.strecken.entity.fahrweg.Signal;
import de.gedoplan.v5t11.strecken.entity.fahrweg.Weiche;
import de.gedoplan.v5t11.strecken.entity.strecke.Strecke;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.xml.bind.Unmarshaller;
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
  private SortedSet<Strecke> strecken = new TreeSet<>();

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
      sb.append(String.format("%s zaehlrichtung=%b rank=%d combi=%b\n", strecke, strecke.isZaehlrichtung(), strecke.getRank(), strecke.isCombi()));

      strecke.getElemente().forEach(element -> sb.append(String.format("  %s\n", element)));
    });

    return sb.toString();
  }

  private Map<Gleisabschnitt, SortedSet<Strecke>> mapStartToStrecken = new HashMap<>();

  public SortedSet<Strecke> getStreckenMitStart(Gleisabschnitt gleisabschnitt) {
    SortedSet<Strecke> strecken = this.mapStartToStrecken.get(gleisabschnitt);
    if (strecken != null) {
      return strecken;
    } else {
      return Collections.emptySortedSet();
    }
  }

  private void add2MapStartToStrecken(Iterable<Strecke> strecken) {
    strecken.forEach(f -> add2MapStartToStrecken(f));
  }

  private void add2MapStartToStrecken(Strecke strecke) {
    Gleisabschnitt start = strecke.getStart().getFahrwegelement();

    SortedSet<Strecke> strecken = this.mapStartToStrecken.get(start);
    if (strecken == null) {
      strecken = new TreeSet<>();
      this.mapStartToStrecken.put(start, strecken);
    }

    strecken.add(strecke);
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
    // Strecken kombinieren
    add2MapStartToStrecken(this.strecken);

    SortedSet<Strecke> zuPruefendeStrecken = this.strecken;
    while (true) {
      SortedSet<Strecke> weitereStrecken = new TreeSet<>();
      for (Strecke fahrstrasse1 : zuPruefendeStrecken) {
        for (Strecke fahrstrasse2 : getStreckenMitStart(fahrstrasse1.getEnde().getFahrwegelement())) {
          Strecke kombiStrecke = Strecke.concat(fahrstrasse1, fahrstrasse2);
          if (kombiStrecke != null && !this.strecken.contains(kombiStrecke)) {
            weitereStrecken.add(kombiStrecke);
          }
        }
      }

      if (weitereStrecken.isEmpty()) {
        break;
      }

      this.strecken.addAll(weitereStrecken);
      add2MapStartToStrecken(weitereStrecken);

      zuPruefendeStrecken = weitereStrecken;
    }

    // Doppeleinträge in Strecken eliminieren und Signale auf Langsamfahrt korrigieren, wenn nötig.
    this.strecken.forEach(f -> {
      f.removeDoppeleintraege();
      // f.adjustLangsamfahrt();
    });
  }

}
