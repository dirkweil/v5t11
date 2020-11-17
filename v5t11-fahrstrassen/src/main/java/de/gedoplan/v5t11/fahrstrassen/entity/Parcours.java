package de.gedoplan.v5t11.fahrstrassen.entity;

import de.gedoplan.baselibs.persistence.entity.StringIdEntity;
import de.gedoplan.baselibs.utils.inject.InjectionUtil;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse.Fahrstrasse;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Gleisabschnitt;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Signal;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Weiche;
import de.gedoplan.v5t11.util.domain.attribute.FahrstrassenFilter;
import de.gedoplan.v5t11.util.domain.attribute.FahrstrassenReservierungsTyp;
import de.gedoplan.v5t11.util.domain.entity.Bereichselement;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.SetMultimap;

import lombok.Getter;
import lombok.Setter;

/**
 * Steuerung.
 *
 * Diese Klasse fasst alle zur Steuerung gehörenden Elemente zusammen.
 *
 * @author dw
 */
@XmlRootElement(name = "Parcours")
@XmlAccessorType(XmlAccessType.NONE)
@Entity
@Table(name = Parcours.TABLE_NAME)
public class Parcours extends StringIdEntity {

  public static final String TABLE_NAME = "FS_PARCOURS";

  @Getter
  @Setter
  private long lastModified;

  @XmlElement(name = "Fahrstrasse", type = Fahrstrasse.class)
  @Getter
  @OneToMany(mappedBy = "parcours", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<Fahrstrasse> fahrstrassen = new HashSet<>();

  @XmlElement(name = "AutoFahrstrasse")
  @Getter
  @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "PARCOURS_ID")
  private Set<AutoFahrstrasse> autoFahrstrassen = new HashSet<>();

  @Getter
  @Transient
  private Set<Gleisabschnitt> gleisabschnitte = new HashSet<>();

  @Getter
  @Transient
  private Set<Signal> signale = new HashSet<>();

  @Getter
  @Transient
  private Set<Weiche> weichen = new HashSet<>();

  protected Parcours() {
  }

  public Parcours(String id) {
    super(id);
  }

  public void setId(String id) {
    this.id = id;
  }

  /**
   * Gleisabschnitt liefern.
   *
   * @param bereich
   *        Bereich
   * @param name
   *        Name
   * @return gefundener Gleisabschnitt oder <code>null</code>
   */
  public Gleisabschnitt getGleisabschnitt(String bereich, String name) {
    return getBereichselement(bereich, name, this.gleisabschnitte);
  }

  /**
   * Signal liefern.
   *
   * @param bereich
   *        Bereich
   * @param name
   *        Name
   * @return gefundenes Signal oder <code>null</code>
   */
  public Signal getSignal(String bereich, String name) {
    return getBereichselement(bereich, name, this.signale);
  }

  /**
   * Weiche liefern.
   *
   * @param bereich
   *        Bereich
   * @param name
   *        Name
   * @return gefundene Weiche oder <code>null</code>
   */
  public Weiche getWeiche(String bereich, String name) {
    return getBereichselement(bereich, name, this.weichen);
  }

  /**
   * Fahrstrasse liefern.
   *
   * @param bereich
   *        Bereich
   * @param name
   *        Name
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

  @Transient
  private SetMultimap<Gleisabschnitt, Fahrstrasse> mapStartToFahrstrassen = MultimapBuilder.hashKeys().hashSetValues().build();

  public Set<Fahrstrasse> getFahrstrassenMitStart(Gleisabschnitt gleisabschnitt) {
    return this.mapStartToFahrstrassen.get(gleisabschnitt);
  }

  private void mapStartToFahrstrassenAdd(Iterable<Fahrstrasse> fahrstrassen) {
    fahrstrassen.forEach(f -> mapStartToFahrstrassenAdd(f));
  }

  private void mapStartToFahrstrassenAdd(Fahrstrasse fahrstrasse) {
    Gleisabschnitt start = fahrstrasse.getStart().getFahrwegelement();
    this.mapStartToFahrstrassen.put(start, fahrstrasse);
  }

  private void mapStartToFahrstrassenRemove(Iterable<Fahrstrasse> fahrstrassen) {
    fahrstrassen.forEach(f -> mapStartToFahrstrassenRemove(f));
  }

  private void mapStartToFahrstrassenRemove(Fahrstrasse fahrstrasse) {
    Gleisabschnitt start = fahrstrasse.getStart().getFahrwegelement();
    this.mapStartToFahrstrassen.remove(start, fahrstrasse);
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
    Set<Fahrstrasse> umkehrFahrstrassen = new HashSet<>();
    this.fahrstrassen.stream().filter(Fahrstrasse::isUmkehrbar).forEach(fs -> umkehrFahrstrassen.add(fs.createUmkehrung()));
    this.fahrstrassen.addAll(umkehrFahrstrassen);

    // Fahrstrassen kombinieren
    mapStartToFahrstrassenAdd(this.fahrstrassen);

    Set<Fahrstrasse> zuPruefendeFahrstrassen = this.fahrstrassen;
    while (true) {
      Set<Fahrstrasse> weitereFahrstrassen = new HashSet<>();
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
      mapStartToFahrstrassenAdd(weitereFahrstrassen);

      zuPruefendeFahrstrassen = weitereFahrstrassen;
    }

    // Fahrstrassen entfernen, wenn sie mit einem nicht als Start oder Ende erlaubten Weichengleisabschnitt starten bzw. enden
    Set<Fahrstrasse> ungueltigeFahrstrassen = this.fahrstrassen.stream()
        .filter(fs -> !fs.getStart().isStartErlaubt() || !fs.getEnde().isEndeErlaubt())
        .collect(Collectors.toSet());
    this.fahrstrassen.removeAll(ungueltigeFahrstrassen);
    mapStartToFahrstrassenRemove(ungueltigeFahrstrassen);

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
   *        Beginn-Gleisabschnitt
   * @param ende
   *        Ende-Gleisabschnitt
   * @param filter
   *        Filter (nur freie/reservierte/unkombinierte) oder <code>null</code> für alle
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

    if (filter != null) {
      switch (filter) {
      case FREI:
        stream = stream.filter(fs -> fs.isFrei(false, false));
        break;

      case RESERVIERT:
        stream = stream.filter(fs -> fs.getReservierungsTyp() != FahrstrassenReservierungsTyp.UNRESERVIERT);
        break;

      case NON_COMBI:
        stream = stream.filter(fs -> !fs.isCombi());
        break;

      }
    }

    return stream.sorted((fs1, fs2) -> fs1.getRank() - fs2.getRank()).collect(Collectors.toList());
  }

  public void injectFields() {
    InjectionUtil.injectFields(this);
    this.fahrstrassen.forEach(Fahrstrasse::injectFields);
  }

}
