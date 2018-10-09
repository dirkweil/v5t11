package de.gedoplan.v5t11.fahrstrassen.entity;

import de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse.Fahrstrasse;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Gleisabschnitt;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Signal;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Weiche;
import de.gedoplan.v5t11.util.domain.attribute.FahrstrassenFilter;
import de.gedoplan.v5t11.util.domain.attribute.FahrstrassenReservierungsTyp;
import de.gedoplan.v5t11.util.domain.entity.Bereichselement;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

  @XmlElement(name = "Fahrstrasse", type = Fahrstrasse.class)
  @Getter
  private SortedSet<Fahrstrasse> fahrstrassen = new TreeSet<>();

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

  /**
   * Fahrstrasse liefern.
   *
   * @param bereich
   *          Bereich
   * @param name
   *          Name
   * @return gefundene Fahrstrasse oder <code>null</code>
   */
  public Fahrstrasse getFahrstrasse(String bereich, String name) {
    return getBereichselement(bereich, name, this.fahrstrassen);
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

    this.fahrstrassen.forEach(fahrstrasse -> {
      sb.append(String.format("%s zaehlrichtung=%b rank=%d combi=%b\n", fahrstrasse, fahrstrasse.isZaehlrichtung(), fahrstrasse.getRank(), fahrstrasse.isCombi()));

      fahrstrasse.getElemente().forEach(element -> sb.append(String.format("  %s\n", element)));
    });

    return sb.toString();
  }

  private Map<Gleisabschnitt, SortedSet<Fahrstrasse>> mapStartToFahrstrassen = new HashMap<>();

  public SortedSet<Fahrstrasse> getFahrstrassenMitStart(Gleisabschnitt gleisabschnitt) {
    SortedSet<Fahrstrasse> fahrstrassen = this.mapStartToFahrstrassen.get(gleisabschnitt);
    if (fahrstrassen != null) {
      return fahrstrassen;
    } else {
      return Collections.emptySortedSet();
    }
  }

  private void add2MapStartToFahrstrassen(Iterable<Fahrstrasse> fahrstrassen) {
    fahrstrassen.forEach(f -> add2MapStartToFahrstrassen(f));
  }

  private void add2MapStartToFahrstrassen(Fahrstrasse fahrstrasse) {
    Gleisabschnitt start = fahrstrasse.getStart().getFahrwegelement();

    SortedSet<Fahrstrasse> fahrstrassen = this.mapStartToFahrstrassen.get(start);
    if (fahrstrassen == null) {
      fahrstrassen = new TreeSet<>();
      this.mapStartToFahrstrassen.put(start, fahrstrassen);
    }

    fahrstrassen.add(fahrstrasse);
  }

  /**
   * Fahrstrassen komplettieren.
   *
   * - Fahrstrassen kombinieren,
   * - Signalstellungen korrigieren,
   * - Doppeleinträge entfernen.
   */
  public void completeFahrstrassen() {
    // Umkehrbare Fahrstrassen invers duplizieren
    SortedSet<Fahrstrasse> umkehrFahrstrassen = new TreeSet<>();
    this.fahrstrassen.stream().filter(Fahrstrasse::isUmkehrbar).forEach(fs -> umkehrFahrstrassen.add(fs.createUmkehrung()));
    this.fahrstrassen.addAll(umkehrFahrstrassen);

    // Fahrstrassen kombinieren
    add2MapStartToFahrstrassen(this.fahrstrassen);

    SortedSet<Fahrstrasse> zuPruefendeFahrstrassen = this.fahrstrassen;
    while (true) {
      SortedSet<Fahrstrasse> weitereFahrstrassen = new TreeSet<>();
      for (Fahrstrasse fahrstrasse1 : zuPruefendeFahrstrassen) {
        for (Fahrstrasse fahrstrasse2 : getFahrstrassenMitStart(fahrstrasse1.getEnde().getFahrwegelement())) {
          Fahrstrasse kombiFahrstrasse = Fahrstrasse.concat(fahrstrasse1, fahrstrasse2);
          if (kombiFahrstrasse != null && !this.fahrstrassen.contains(kombiFahrstrasse)) {
            weitereFahrstrassen.add(kombiFahrstrasse);
          }
        }
      }

      if (weitereFahrstrassen.isEmpty()) {
        break;
      }

      this.fahrstrassen.addAll(weitereFahrstrassen);
      add2MapStartToFahrstrassen(weitereFahrstrassen);

      zuPruefendeFahrstrassen = weitereFahrstrassen;
    }

    // Doppeleinträge in Fahrstrassen eliminieren und Signale auf Langsamfahrt korrigieren, wenn nötig.
    this.fahrstrassen.forEach(f -> {
      f.removeDoppeleintraege();
      f.adjustLangsamfahrt();
    });
  }

  /**
   * Fahrstrassen suchen.
   *
   * @param beginn
   *          Beginn-Gleisabschnitt
   * @param ende
   *          Ende-Gleisabschnitt
   * @param filter
   *          <code>true</code>, wenn nur freie Fahrstrassen geliefert werden sollen
   * @return gefundene Fahrstrassen in aufsteigender Rang-Reihenfolge
   */
  public List<Fahrstrasse> getFahrstrassen(Gleisabschnitt beginn, Gleisabschnitt ende, FahrstrassenFilter filter) {
    Stream<Fahrstrasse> stream = this.fahrstrassen.stream();

    if (beginn != null) {
      stream = stream.filter(fs -> fs.startsWith(beginn));
    }

    if (ende != null) {
      stream = stream.filter(fs -> fs.endsWith(ende));
    }

    if (filter == FahrstrassenFilter.FREI) {
      stream = stream.filter(fs -> fs.isFrei(false, true));
    } else if (filter == FahrstrassenFilter.RESERVIERT) {
      stream = stream.filter(fs -> fs.getReservierungsTyp() != FahrstrassenReservierungsTyp.UNRESERVIERT);
    }

    return stream.sorted((fs1, fs2) -> fs1.getRank() - fs2.getRank()).collect(Collectors.toList());
  }

}
