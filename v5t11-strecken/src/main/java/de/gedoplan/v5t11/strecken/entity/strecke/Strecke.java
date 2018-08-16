package de.gedoplan.v5t11.strecken.entity.strecke;

import de.gedoplan.v5t11.strecken.entity.Bereichselement;
import de.gedoplan.v5t11.strecken.entity.Parcours;
import de.gedoplan.v5t11.strecken.entity.fahrweg.Signal;
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
public class Strecke extends Bereichselement {
  /**
   * In Zählrichtung orientiert?
   * Dieses Attribut dient nur als Default für die zugehörigen Streckenelemente.
   */
  @XmlAttribute
  @Getter
  private boolean zaehlrichtung;

  /**
   * Aus anderen Strecken kombiniert?
   */
  @Getter
  private boolean combi;

  /**
   * Ranking (für Auswahl aus Alternativen).
   */
  @Getter
  private int rank;

  /**
   * Liste der Streckenelemente. Beginnt und endet immer mit einem Gleisabschnitt.
   */
  @XmlElements({
      @XmlElement(name = "Gleisabschnitt", type = StreckenGleisabschnitt.class),
      @XmlElement(name = "Signal", type = StreckenSignal.class),
      @XmlElement(name = "Weiche", type = StreckenWeiche.class) })
  @Getter
  private List<Streckenelement> elemente = new ArrayList<>();

  @Getter
  private StreckenGleisabschnitt start;

  @Getter
  private StreckenGleisabschnitt ende;

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
      throw new IllegalArgumentException("Parent von Strecke muss Parcours sein");
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
    ListIterator<Streckenelement> iterator = this.elemente.listIterator();
    while (iterator.hasNext()) {
      Streckenelement streckenelement = iterator.next();
      if (streckenelement instanceof StreckenWeiche && !streckenelement.isSchutz()) {
        StreckenGleisabschnitt streckenGleisabschnitt = ((StreckenWeiche) streckenelement).createStreckenGleisabschnitt();
        if (!this.elemente.contains(streckenGleisabschnitt)) {
          iterator.add(streckenGleisabschnitt);
        }
      }
    }

    // Für alle Elemente die zugehörigen Fahrwegelemente erzeugen bzw. zuordnen
    this.elemente.forEach(element -> element.linkFahrwegelement(parcours));

    // Erstes und letztes Element müssen StreckenGleisabschnitte sein
    int count = this.elemente.size();
    if (count < 2 || !(this.elemente.get(0) instanceof StreckenGleisabschnitt) || !(this.elemente.get(count - 1) instanceof StreckenGleisabschnitt)) {
      throw new IllegalArgumentException("Strecke muss min. 2 Elemente haben und mit Gleisabschnitten beginnen und enden");
    }
    this.start = (StreckenGleisabschnitt) this.elemente.get(0);
    this.ende = (StreckenGleisabschnitt) this.elemente.get(count - 1);

    // Bereich der Stecke und des Startelements müssen gleich sein
    if (!this.bereich.equals(this.start.getBereich())) {
      throw new IllegalArgumentException("Erster Gleisabschnitte muss im gleichen Bereich wie die Strecke liegen");
    }

    // Ranking berechnen
    this.rank = this.elemente.stream().mapToInt(e -> e.getRank()).sum();

    // Strecken-Name aus enthaltenen Gleisabschnitten zusammenstellen
    this.name = this.elemente
        .stream()
        .filter(e -> e instanceof StreckenGleisabschnitt)
        .map(e -> (StreckenGleisabschnitt) e)
        .filter(g -> !g.isWeichenGleisabschnitt())
        .map(g -> g.getBereich().equals(this.bereich) ? g.getName() : g.getBereich() + "/" + g.getName())
        .collect(Collectors.joining("-"));
  }

  /**
   * Strecken kombinieren.
   *
   * Die angegebenen Strecken werden in der Reihenfolge links-rechts kombiniert, wenn dies möglich ist.
   *
   * @param linkeStrecke
   *          Linke Strecke (Einfahrt)
   * @param rechteStrecke
   *          Rechte Strecke (Ausfahrt)
   * @return Kombi-Strecke, wenn Kombination möglich, sonst <code>null</code>
   */
  public static Strecke concat(Strecke linkeStrecke, Strecke rechteStrecke) {
    // Wenn verschiedene Bereiche, nicht kombinieren
    if (!linkeStrecke.bereich.equals(rechteStrecke.bereich)) {
      return null;
    }

    // Wenn Ende links nicht Anfang rechts, nicht kombinieren
    Streckenelement linksLast = linkeStrecke.getEnde();
    Streckenelement rechtsFirst = rechteStrecke.getStart();
    if (!linksLast.equals(rechtsFirst)) {
      return null;
    }

    // Wenn Zählrichtung nicht passt, nicht kombinieren
    if (linksLast.isZaehlrichtung() != rechtsFirst.isZaehlrichtung()) {
      return null;
    }

    // Wenn Ende rechts schon Teil der linken Strecke, nicht kombinieren
    if (linkeStrecke.elemente.contains(rechteStrecke.getEnde())) {
      return null;
    }

    Strecke result = new Strecke();
    result.bereich = linkeStrecke.bereich;
    result.name = createConcatName(linkeStrecke.getName(), rechteStrecke.getName());
    result.combi = true;
    result.rank = linkeStrecke.rank + rechteStrecke.rank;
    result.zaehlrichtung = linkeStrecke.zaehlrichtung;

    result.elemente.addAll(linkeStrecke.elemente);
    result.elemente.addAll(rechteStrecke.elemente.subList(1, rechteStrecke.elemente.size()));

    result.start = linkeStrecke.start;
    result.ende = rechteStrecke.ende;

    // Schutzsignale entfernen, die auch als normale Signale vorhanden sind
    Set<Signal> normaleSignale = new HashSet<>();
    for (Streckenelement element : result.elemente) {
      if (element instanceof StreckenSignal && !element.isSchutz()) {
        Signal signal = ((StreckenSignal) element).getFahrwegelement();
        if (signal != null) {
          normaleSignale.add(signal);
        }
      }
    }

    Iterator<Streckenelement> iterator = result.elemente.iterator();
    while (iterator.hasNext()) {
      Streckenelement element = iterator.next();
      if (element instanceof StreckenSignal && element.isSchutz()) {
        Signal schutzSignal = ((StreckenSignal) element).getFahrwegelement();
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
   * Doppeleinträge in der Strecke eliminieren.
   */
  public void removeDoppeleintraege() {
    int i = 0;
    while (i < this.elemente.size()) {
      Streckenelement element = this.elemente.get(i);

      while (true) {
        int i2 = this.elemente.lastIndexOf(element);
        if (i2 <= i) {
          // kein Doppelvorkommen; weiter mit nächstem Eintrag
          ++i;
          break;
        }

        if (element instanceof StreckenGeraet && element.isSchutz()) {
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
   * Hauptsignal bzw. bis zum Streckenende keine bzw. mindestens eine abzweigende Weiche befahren wird.
   */
  public void adjustLangsamfahrt() {
    StreckenSignal streckenSignal = null;
    int streckenSignalIndex = -1;
    boolean langsam = false;

    int size = this.elemente.size();
    for (int i = 0; i < size; ++i) {
      Streckenelement streckenelement = this.elemente.get(i);
      if (!streckenelement.isSchutz()) {
        if (i >= size - 1 || isHauptsignal(streckenelement)) {
          if (streckenSignal != null) {
            SignalStellung neueStellung = langsam ? SignalStellung.LANGSAMFAHRT : SignalStellung.FAHRT;
            if (neueStellung != streckenSignal.getStellung()) {
              this.elemente.set(streckenSignalIndex, streckenSignal.createCopy(neueStellung));
            }
          }

          if (i >= size - 1) {
            break;
          }

          streckenSignal = (StreckenSignal) streckenelement;
          streckenSignalIndex = i;
          langsam = false;
        }

        if (streckenelement instanceof StreckenWeiche
            && ((StreckenWeiche) streckenelement).getStellung() != WeichenStellung.GERADE) {
          langsam = true;
        }
      }
    }
  }

  private static boolean isHauptsignal(Streckenelement streckenelement) {
    if (streckenelement instanceof StreckenSignal) {
      StreckenSignal streckenSignal = (StreckenSignal) streckenelement;

      // TODO Das sollte eine richtige Eigenschaft sein!
      return streckenSignal.getFahrwegelement().getTyp().startsWith("Haupt");
    }

    return false;
  }
}
