package de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse;

import de.gedoplan.v5t11.fahrstrassen.entity.Bereichselement;
import de.gedoplan.v5t11.fahrstrassen.entity.Parcours;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Fahrwegelement;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Gleisabschnitt;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Signal;
import de.gedoplan.v5t11.util.domain.SignalStellung;
import de.gedoplan.v5t11.util.domain.WeichenStellung;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class Fahrstrasse extends Bereichselement {
  /**
   * In Zählrichtung orientiert?
   * Dieses Attribut dient nur als Default für die zugehörigen Fahrstrassenelemente.
   */
  @XmlAttribute
  @Getter
  private boolean zaehlrichtung;

  /**
   * Aus anderen Fahrstrassen kombiniert?
   */
  @Getter
  private boolean combi;

  /**
   * Ranking (für Auswahl aus Alternativen).
   */
  @Getter
  private int rank;

  /**
   * Liste der Fahrstrassenelemente. Beginnt und endet immer mit einem Gleisabschnitt.
   */
  @XmlElements({
      @XmlElement(name = "Gleisabschnitt", type = FahrstrassenGleisabschnitt.class),
      @XmlElement(name = "Hauptsignal", type = FahrstrassenHauptsignal.class),
      @XmlElement(name = "Vorsignal", type = FahrstrassenVorsignal.class),
      @XmlElement(name = "Sperrsignal", type = FahrstrassenSperrsignal.class),
      @XmlElement(name = "Weiche", type = FahrstrassenWeiche.class) })
  @Getter
  private List<Fahrstrassenelement> elemente = new ArrayList<>();

  @Getter
  private FahrstrassenGleisabschnitt start;

  @Getter
  private FahrstrassenGleisabschnitt ende;

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
    if (!(parent instanceof Parcours)) {
      throw new IllegalArgumentException("Parent von Fahrstrasse muss Parcours sein");
    }

    Parcours parcours = (Parcours) parent;

    // Fuer alle Elemente Defaults übernehmen wenn nötig
    this.elemente.forEach(element -> {
      if (element.getBereich() == null) {
        element.setBereich(this.bereich);
      }

      if (element.zaehlrichtung == null) {
        element.zaehlrichtung = this.zaehlrichtung;
      }
    });

    // Zu Weichen die entsprechenden Gleisabschitte ergänzen, wenn es nicht nur Schutzweichen sind
    ListIterator<Fahrstrassenelement> iterator = this.elemente.listIterator();
    while (iterator.hasNext()) {
      Fahrstrassenelement fahrstrassenelement = iterator.next();
      if (fahrstrassenelement instanceof FahrstrassenWeiche && !fahrstrassenelement.isSchutz()) {
        FahrstrassenGleisabschnitt fahrstrassenGleisabschnitt = ((FahrstrassenWeiche) fahrstrassenelement).createFahrstrassenGleisabschnitt();
        if (!this.elemente.contains(fahrstrassenGleisabschnitt)) {
          iterator.add(fahrstrassenGleisabschnitt);
        }
      }
    }

    // Für alle Elemente die zugehörigen Fahrwegelemente erzeugen bzw. zuordnen
    this.elemente.forEach(element -> element.linkFahrwegelement(parcours));

    // Erstes und letztes Element müssen FahrstrassenGleisabschnitte sein
    int count = this.elemente.size();
    if (count < 2 || !(this.elemente.get(0) instanceof FahrstrassenGleisabschnitt) || !(this.elemente.get(count - 1) instanceof FahrstrassenGleisabschnitt)) {
      throw new IllegalArgumentException("Fahrstrasse muss min. 2 Elemente haben und mit Gleisabschnitten beginnen und enden");
    }
    this.start = (FahrstrassenGleisabschnitt) this.elemente.get(0);
    this.ende = (FahrstrassenGleisabschnitt) this.elemente.get(count - 1);

    // Bereich der Stecke und des Startelements müssen gleich sein
    if (!this.bereich.equals(this.start.getBereich())) {
      throw new IllegalArgumentException("Erster Gleisabschnitte muss im gleichen Bereich wie die Fahrstrasse liegen");
    }

    // Ranking berechnen
    this.rank = this.elemente.stream().mapToInt(e -> e.getRank()).sum();

    // Fahrstrassen-Name aus enthaltenen Gleisabschnitten zusammenstellen
    this.name = this.elemente
        .stream()
        .filter(e -> e instanceof FahrstrassenGleisabschnitt)
        .map(e -> (FahrstrassenGleisabschnitt) e)
        .filter(g -> !g.isWeichenGleisabschnitt())
        .map(g -> g.getBereich().equals(this.bereich) ? g.getName() : g.getBereich() + "/" + g.getName())
        .collect(Collectors.joining("-"));
  }

  /**
   * Fahrstrassen kombinieren.
   *
   * Die angegebenen Fahrstrassen werden in der Reihenfolge links-rechts kombiniert, wenn dies möglich ist.
   *
   * @param linkeFahrstrasse
   *          Linke Fahrstrasse (Einfahrt)
   * @param rechteFahrstrasse
   *          Rechte Fahrstrasse (Ausfahrt)
   * @return Kombi-Fahrstrasse, wenn Kombination möglich, sonst <code>null</code>
   */
  public static Fahrstrasse concat(Fahrstrasse linkeFahrstrasse, Fahrstrasse rechteFahrstrasse) {
    // Wenn verschiedene Bereiche, nicht kombinieren
    if (!linkeFahrstrasse.bereich.equals(rechteFahrstrasse.bereich)) {
      return null;
    }

    // Wenn Ende links nicht Anfang rechts, nicht kombinieren
    Fahrstrassenelement linksLast = linkeFahrstrasse.getEnde();
    Fahrstrassenelement rechtsFirst = rechteFahrstrasse.getStart();
    if (!linksLast.equals(rechtsFirst)) {
      return null;
    }

    // Wenn Zählrichtung nicht passt, nicht kombinieren
    if (linksLast.isZaehlrichtung() != rechtsFirst.isZaehlrichtung()) {
      return null;
    }

    // Wenn Ende rechts schon Teil der linken Fahrstrasse, nicht kombinieren
    if (linkeFahrstrasse.elemente.contains(rechteFahrstrasse.getEnde())) {
      return null;
    }

    Fahrstrasse result = new Fahrstrasse();
    result.bereich = linkeFahrstrasse.bereich;
    result.name = createConcatName(linkeFahrstrasse.getName(), rechteFahrstrasse.getName());
    result.combi = true;
    result.rank = linkeFahrstrasse.rank + rechteFahrstrasse.rank;
    result.zaehlrichtung = linkeFahrstrasse.zaehlrichtung;

    result.elemente.addAll(linkeFahrstrasse.elemente);
    result.elemente.addAll(rechteFahrstrasse.elemente.subList(1, rechteFahrstrasse.elemente.size()));

    result.start = linkeFahrstrasse.start;
    result.ende = rechteFahrstrasse.ende;

    // Schutzsignale entfernen, die auch als normale Signale vorhanden sind
    Set<Signal> normaleSignale = new HashSet<>();
    for (Fahrstrassenelement element : result.elemente) {
      if (element instanceof FahrstrassenSignal && !element.isSchutz()) {
        Signal signal = ((FahrstrassenSignal) element).getFahrwegelement();
        if (signal != null) {
          normaleSignale.add(signal);
        }
      }
    }

    Iterator<Fahrstrassenelement> iterator = result.elemente.iterator();
    while (iterator.hasNext()) {
      Fahrstrassenelement element = iterator.next();
      if (element instanceof FahrstrassenSignal && element.isSchutz()) {
        Signal schutzSignal = ((FahrstrassenSignal) element).getFahrwegelement();
        if (schutzSignal != null && normaleSignale.contains(schutzSignal)) {
          iterator.remove();
        }
      }
    }
    return result;
  }

  private static String createConcatName(String name1, String name2) {
    String[] part1 = name1.split("-");
    String[] part2 = name2.split("-");
    int i = 0;
    if (part1[part1.length - 1].endsWith(part2[0])) {
      ++i;
    }

    StringBuilder concatName = new StringBuilder(name1);
    while (i < part2.length) {
      concatName.append("-").append(part2[i]);
      ++i;
    }

    return concatName.toString();
  }

  /**
   * Doppeleinträge in der Fahrstrasse eliminieren.
   */
  public void removeDoppeleintraege() {
    int i = 0;
    while (i < this.elemente.size()) {
      Fahrstrassenelement element = this.elemente.get(i);

      while (true) {
        int i2 = this.elemente.lastIndexOf(element);
        if (i2 <= i) {
          // kein Doppelvorkommen; weiter mit nächstem Eintrag
          ++i;
          break;
        }

        if (element instanceof FahrstrassenGeraet && element.isSchutz()) {
          // aktuelles Element ist Schutzelement; löschen, 2. Eintrag bestehen lassen, weiter mit nächstem Eintrag (der dann am
          // gleichen Index steht!)
          this.elemente.remove(i);
          break;
        } else {
          // aktuelles Element ist kein Schutzelement; 2. Eintrag löschen, weiter nach Doppelvorkommen suchen
          this.elemente.remove(i2);
        }
      }
    }
  }

  /**
   * Signalstellungen für Hauptsignale passend zu Weichenstellungen anpassen.
   *
   * Die Stellung von Hauptsignalen (erkennbar an Stellung FAHRT bzw. LANGSAMFAHRT) werden zu FAHRT bzw. LANGSAMFAHRT korrigiert, wenn bis zum nächsten
   * Hauptsignal bzw. bis zum Fahrstrassenende keine bzw. mindestens eine abzweigende Weiche befahren wird.
   */
  public void adjustLangsamfahrt() {
    FahrstrassenSignal fahrstrassenSignal = null;
    int fahrstrassenSignalIndex = -1;
    boolean langsam = false;

    int size = this.elemente.size();
    for (int i = 0; i < size; ++i) {
      Fahrstrassenelement fahrstrassenelement = this.elemente.get(i);
      if (!fahrstrassenelement.isSchutz()) {
        if (i >= size - 1 || fahrstrassenelement.isHauptsignal()) {
          if (fahrstrassenSignal != null) {
            SignalStellung neueStellung = langsam ? SignalStellung.LANGSAMFAHRT : SignalStellung.FAHRT;
            if (neueStellung != fahrstrassenSignal.getStellung()) {
              this.elemente.set(fahrstrassenSignalIndex, fahrstrassenSignal.createCopy(neueStellung));
            }
          }

          if (i >= size - 1) {
            break;
          }

          fahrstrassenSignal = (FahrstrassenSignal) fahrstrassenelement;
          fahrstrassenSignalIndex = i;
          langsam = false;
        }

        if (fahrstrassenelement instanceof FahrstrassenWeiche
            && ((FahrstrassenWeiche) fahrstrassenelement).getStellung() != WeichenStellung.GERADE) {
          langsam = true;
        }
      }
    }
  }

  /**
   * Beginnt die Fahrstrasse mit dem angegebenen Gleisabschnitt?
   *
   * @param gleisabschnitt
   *          Gleisabschnitt
   * @return <code>true</code>, wenn die Fahrstrasse mit dem angegebenen Gleisabschnitt beginnt
   */
  public boolean startsWith(Gleisabschnitt gleisabschnitt) {
    FahrstrassenGleisabschnitt start = getStart();
    return gleisabschnitt.equals(start.getFahrwegelement());
  }

  /**
   * Endet die Fahrstrasse mit dem angegebenen Gleisabschnitt?
   *
   * @param gleisabschnitt
   *          Gleisabschnitt
   * @return <code>true</code>, wenn die Fahrstrasse mit dem angegebenen Gleisabschnitt endet
   */
  public boolean endsWith(Gleisabschnitt gleisabschnitt) {
    FahrstrassenGleisabschnitt ende = getEnde();
    return gleisabschnitt.equals(ende.getFahrwegelement());
  }

  /**
   * Ist die Fahrstrasse (komplett) frei?
   *
   * @param includeStart
   *          Erstes Element der Fahrstrasse berücksichtigen?
   * @param includeEnde
   *          Letztes Element der Fahrstrasse berücksichtigen?
   * @return <code>true</code>, wenn die Gleisabschnitte der Fahrstrasse frei sind.
   */
  public boolean isFrei(boolean includeStart, boolean includeEnde) {
    int elementCount = this.elemente.size();
    for (int index = 0; index < elementCount; ++index) {
      if (!includeStart && index == 0) {
        continue;
      }

      if (!includeEnde && index == elementCount - 1) {
        continue;
      }

      Fahrstrassenelement element = this.elemente.get(index);
      Fahrwegelement fahrwegelement = element.getFahrwegelement();
      if (fahrwegelement instanceof Gleisabschnitt && ((Gleisabschnitt) fahrwegelement).isBesetzt()) {
        return false;
      }
    }

    return true;
  }

}
