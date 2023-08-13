package de.gedoplan.v5t11.fahrstrassen.entity;

import de.gedoplan.baselibs.utils.inject.InjectionUtil;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse.Fahrstrasse;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Gleis;
import de.gedoplan.v5t11.util.domain.attribute.BereichselementId;
import de.gedoplan.v5t11.util.domain.attribute.FahrstrassenFilter;
import de.gedoplan.v5t11.util.domain.attribute.FahrstrassenReservierungsTyp;
import de.gedoplan.v5t11.util.domain.entity.Bereichselement;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.jboss.logging.Logger;

import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.SetMultimap;

import lombok.Getter;

/**
 * Parcours.
 * <p>
 * Diese Klasse fasst alle zum Parcours gehörenden Elemente zusammen.
 *
 * @author dw
 */
@XmlRootElement(name = "Parcours")
@XmlAccessorType(XmlAccessType.NONE)
public class Parcours {

  Logger logger = Logger.getLogger(Parcours.class);

  @XmlElement(name = "Fahrstrasse", type = Fahrstrasse.class)
  @Getter
  private Set<Fahrstrasse> fahrstrassen = new HashSet<>();

  @XmlElement(name = "AutoFahrstrasse")
  @Getter
  private Set<AutoFahrstrasse> autoFahrstrassen = new HashSet<>();

  /**
   * Fahrstrasse liefern.
   *
   * @param bereich Bereich
   * @param name Name
   * @return gefundene Fahrstrasse oder <code>null</code>
   */
  public Fahrstrasse getFahrstrasse(String bereich, String name) {
    return getBereichselement(bereich, name, this.fahrstrassen);
  }

  /**
   * Fahrstrasse liefern.
   *
   * @param id Id
   * @return gefundene Fahrstrasse oder <code>null</code>
   */
  public Fahrstrasse getFahrstrasse(BereichselementId id) {
    return getBereichselement(id.getBereich(), id.getName(), this.fahrstrassen);
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

  private SetMultimap<Gleis, Fahrstrasse> mapStartToFahrstrassen = MultimapBuilder.hashKeys().hashSetValues().build();

  public Set<Fahrstrasse> getFahrstrassenMitStart(Gleis gleis) {
    return this.mapStartToFahrstrassen.get(gleis);
  }

  private void mapStartToFahrstrassenAdd(Iterable<Fahrstrasse> fahrstrassen) {
    fahrstrassen.forEach(f -> mapStartToFahrstrassenAdd(f));
  }

  private void mapStartToFahrstrassenAdd(Fahrstrasse fahrstrasse) {
    Gleis start = fahrstrasse.getStart().getFahrwegelement();
    this.mapStartToFahrstrassen.put(start, fahrstrasse);
  }

  private void mapStartToFahrstrassenRemove(Iterable<Fahrstrasse> fahrstrassen) {
    fahrstrassen.forEach(f -> mapStartToFahrstrassenRemove(f));
  }

  private void mapStartToFahrstrassenRemove(Fahrstrasse fahrstrasse) {
    Gleis start = fahrstrasse.getStart().getFahrwegelement();
    this.mapStartToFahrstrassen.remove(start, fahrstrasse);
  }

  /**
   * Fahrstrassen komplettieren.
   * <p>
   * - Fahrstrassen kombinieren,
   * - Signalstellungen korrigieren,
   * - Doppeleinträge entfernen.
   */
  public void completeFahrstrassen() {
    this.logger.debugf("# Fahrstrassen initial: %d", this.fahrstrassen.size());

    // Umkehrbare Fahrstrassen invers duplizieren
    Set<Fahrstrasse> umkehrFahrstrassen = new HashSet<>();
    this.fahrstrassen.stream().filter(Fahrstrasse::isUmkehrbar).forEach(fs -> umkehrFahrstrassen.add(fs.createUmkehrung()));
    this.fahrstrassen.addAll(umkehrFahrstrassen);
    this.logger.debugf("# UmkehrFahrstrassen: %d", umkehrFahrstrassen.size());

    // Fahrstrassen kombinieren
    int combiAnzahl = 0;

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

      combiAnzahl += weitereFahrstrassen.size();
      this.fahrstrassen.addAll(weitereFahrstrassen);
      mapStartToFahrstrassenAdd(weitereFahrstrassen);

      zuPruefendeFahrstrassen = weitereFahrstrassen;
    }

    this.logger.debugf("# CombiFahrstrassen: %d", combiAnzahl);
  }

  /**
   * Fahrstrassen entfernen, wenn sie mit einem nicht als Start oder Ende erlaubten Weichengleis starten bzw. enden.
   */
  public void removeUnerlaubteFahrstrassen() {
    Set<Fahrstrasse> ungueltigeFahrstrassen = this.fahrstrassen.stream()
      .filter(fs -> !fs.getStart().isStartErlaubt() || !fs.getEnde().isEndeErlaubt())
      .collect(Collectors.toSet());
    this.fahrstrassen.removeAll(ungueltigeFahrstrassen);
    mapStartToFahrstrassenRemove(ungueltigeFahrstrassen);

    this.logger.debugf("# Ungültige Fahrstrassen: %d", ungueltigeFahrstrassen.size());
  }

  /**
   * Signale auf Langsamfahrt korrigieren, wenn nötig.
   */
  public void adjustLangsamfahrt() {
    this.fahrstrassen.forEach(f -> {
      f.adjustLangsamfahrt();
    });
  }

  /**
   * Noch nicht erzeugte persistente Elemente erzeugen.
   */
  public void addPersistentEntries() {
    this.fahrstrassen.forEach(Fahrstrasse::addPersistentEntries);
  }

  /**
   * Fahrstrassen suchen.
   *
   * @param beginnGleisId Beginn-Gleis
   * @param endeGleisId Ende-Gleis
   * @param filter Filter (nur freie/reservierte/unkombinierte) oder <code>null</code> für alle
   * @return gefundene Fahrstrassen in aufsteigender Rang-Reihenfolge
   */
  public List<Fahrstrasse> getFahrstrassen(BereichselementId beginnGleisId, BereichselementId endeGleisId, FahrstrassenFilter filter) {
    Stream<Fahrstrasse> stream = this.fahrstrassen.stream();

    if (beginnGleisId != null) {
      stream = stream.filter(fs -> fs.startsWith(beginnGleisId));
    }

    if (endeGleisId != null) {
      stream = stream.filter(fs -> fs.endsWith(endeGleisId));
    }

    if (filter != null) {
      stream = switch (filter) {
        case FREI -> stream.filter(fs -> fs.isFrei(false, false));
        case RESERVIERT -> stream.filter(fs -> fs.getFahrstrassenStatus().getReservierungsTyp() != FahrstrassenReservierungsTyp.UNRESERVIERT);
        case NON_COMBI -> stream.filter(fs -> !fs.isCombi());
      };
    }

    return stream.sorted((fs1, fs2) -> fs1.getRank() - fs2.getRank()).collect(Collectors.toList());
  }

  public void injectFields() {
    InjectionUtil.injectFields(this);
    this.fahrstrassen.forEach(Fahrstrasse::injectFields);
    this.autoFahrstrassen.forEach(AutoFahrstrasse::injectFields);
  }

}
