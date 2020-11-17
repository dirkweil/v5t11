package de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import de.gedoplan.baselibs.utils.inject.InjectionUtil;
import de.gedoplan.v5t11.fahrstrassen.entity.Parcours;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Gleisabschnitt;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.ReservierbaresFahrwegelement;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Signal;
import de.gedoplan.v5t11.util.domain.attribute.FahrstrassenReservierungsTyp;
import de.gedoplan.v5t11.util.domain.attribute.SignalStellung;
import de.gedoplan.v5t11.util.domain.attribute.WeichenStellung;
import de.gedoplan.v5t11.util.domain.entity.Bereichselement;
import de.gedoplan.v5t11.util.jsonb.JsonbInclude;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.util.AnnotationLiteral;
import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
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
@Entity
@Table(name = Fahrstrasse.TABLE_NAME)
@Access(AccessType.FIELD)
public class Fahrstrasse extends Bereichselement {

  public static final String TABLE_NAME = "FS_FAHRSTRASSE";

  /**
   * In Zählrichtung orientiert?
   * Dieses Attribut dient nur als Default für die zugehörigen Fahrstrassenelemente.
   */
  @XmlAttribute
  @Getter(onMethod_ = @JsonbInclude(full = true))
  private boolean zaehlrichtung;

  /**
   * Aus anderen Fahrstrassen kombiniert?
   */
  @Getter
  private boolean combi;

  /**
   * Ranking (für Auswahl aus Alternativen).
   */
  @Getter(onMethod_ = @JsonbInclude(full = true))
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
  @Getter(onMethod_ = @JsonbInclude(full = true))
  // TODO
  // @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
  // @JoinColumn(name = "FAHRSTRASSE_BEREICH", referencedColumnName = "BEREICH")
  // @JoinColumn(name = "FAHRSTRASSE_NAME", referencedColumnName = "NAME")^
  // @ElementCollection(fetch = FetchType.EAGER)
  @Transient
  private List<Fahrstrassenelement> elemente = new ArrayList<>();

  @ManyToOne
  private Parcours parcours;

  /*
   * ************************************************************************************************
   * Nach dem Lesen aus XML oder DB sind im Objekt nur die oben stehenden Attribute gefüllt.
   * Es folgen nun weitere Attribute, die davon abgeleitete werden, sowie die zugehörigen
   * Initialisierungsmethoden.
   */

  /**
   * Kann diese Fahrstrasse umgekehrt genutzt werden?
   * Ist nur zum Aufbau de rDaten aus dem XML nötig und wird nicht in der DB abgelegt
   */
  @XmlAttribute
  @Getter
  @Transient
  private boolean umkehrbar;

  /**
   * Erstes Element.
   */
  @Getter
  @Transient
  private FahrstrassenGleisabschnitt start;

  /**
   * Letztes Element.
   */
  @Getter
  @Transient
  private FahrstrassenGleisabschnitt ende;

  /**
   * Falls reserviert, Typ der Reservierung, sonst <code>null</code>.
   */
  @Getter(onMethod_ = @JsonbInclude)
  private FahrstrassenReservierungsTyp reservierungsTyp = FahrstrassenReservierungsTyp.UNRESERVIERT;

  /**
   * Anzahl der bereits freigegebenen Elemente.
   */
  @Getter(onMethod_ = @JsonbInclude)
  private int teilFreigabeAnzahl = 0;

  @SuppressWarnings("unused")
  private void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
    if (!(parent instanceof Parcours)) {
      throw new IllegalArgumentException("Parent von Fahrstrasse muss Parcours sein");
    }

    this.parcours = (Parcours) parent;

    // Fuer alle Elemente Defaults übernehmen wenn nötig
    this.elemente.forEach(element -> {
      if (element.getBereich() == null) {
        element.setBereich(getBereich());
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
    this.elemente.forEach(element -> element.linkFahrwegelement(this.parcours));

    // Erstes und letztes Element müssen FahrstrassenGleisabschnitte sein
    int count = this.elemente.size();
    if (count < 2 || !(this.elemente.get(0) instanceof FahrstrassenGleisabschnitt) || !(this.elemente.get(count - 1) instanceof FahrstrassenGleisabschnitt)) {
      throw new IllegalArgumentException("Fahrstrasse muss min. 2 Elemente haben und mit Gleisabschnitten beginnen und enden");
    }
    this.start = (FahrstrassenGleisabschnitt) this.elemente.get(0);
    this.ende = (FahrstrassenGleisabschnitt) this.elemente.get(count - 1);

    // Bereich der Stecke und des Startelements müssen gleich sein
    if (!getBereich().equals(this.start.getBereich())) {
      throw new IllegalArgumentException("Erster Gleisabschnitte muss im gleichen Bereich wie die Fahrstrasse liegen");
    }

    // Ranking berechnen
    this.rank = this.elemente.stream().mapToInt(e -> e.getRank()).sum();

    createName();
  }

  /*
   * Fahrstrassen-Name aus enthaltenen Gleisabschnitten zusammenstellen.
   */
  private void createName() {
    setName(this.elemente
        .stream()
        .filter(e -> e instanceof FahrstrassenGleisabschnitt)
        .map(e -> (FahrstrassenGleisabschnitt) e)
        // .filter(g -> !g.isWeichenGleisabschnitt())
        .map(g -> g.getBereich().equals(getBereich()) ? g.getName() : g.getName() + "@" + g.getBereich())
        .collect(Collectors.joining("-")));
  }

  /*
   * ************************************************************************************************
   * Es folgen nun weitere Methoden, die bei einem Neuaufbau des Parcours aus dem XML benötigt werden,
   * um Combi-Fahrstrassen zu erzeugen, Doppelte zu entfernen etc.
   * Diese Methoden werden nur von Parcours.completeFahrstrassen genutzt.
   */

  /**
   * Fahrstrassen kombinieren.
   *
   * Die angegebenen Fahrstrassen werden in der Reihenfolge links-rechts kombiniert, wenn dies möglich ist.
   *
   * @param linkeFahrstrasse
   *        Linke Fahrstrasse (Einfahrt)
   * @param rechteFahrstrasse
   *        Rechte Fahrstrasse (Ausfahrt)
   * @return Kombi-Fahrstrasse, wenn Kombination möglich, sonst <code>null</code>
   */
  public static Fahrstrasse concat(Fahrstrasse linkeFahrstrasse, Fahrstrasse rechteFahrstrasse) {
    // Wenn verschiedene Bereiche, nicht kombinieren
    if (!linkeFahrstrasse.getBereich().equals(rechteFahrstrasse.getBereich())) {
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

    // Wenn Zyklen entstehen würden, nicht kombinieren
    Set<Fahrstrassenelement> rechteFahrstrassenGleisabschnitte = rechteFahrstrasse
        .getElemente()
        .stream()
        .skip(1)
        .filter(e -> e instanceof FahrstrassenGleisabschnitt)
        .collect(Collectors.toSet());
    for (Fahrstrassenelement fe : linkeFahrstrasse.getElemente()) {
      if (rechteFahrstrassenGleisabschnitte.contains(fe)) {
        return null;
      }
    }

    Fahrstrasse result = new Fahrstrasse();
    result.setBereich(linkeFahrstrasse.getBereich());
    result.setName(createConcatName(linkeFahrstrasse.getName(), rechteFahrstrasse.getName()));
    result.combi = true;
    result.rank = linkeFahrstrasse.rank + rechteFahrstrasse.rank;
    result.zaehlrichtung = linkeFahrstrasse.zaehlrichtung;

    result.elemente.addAll(linkeFahrstrasse.elemente);
    result.elemente.addAll(rechteFahrstrasse.elemente.subList(1, rechteFahrstrasse.elemente.size()));

    result.start = linkeFahrstrasse.start;
    result.ende = rechteFahrstrasse.ende;

    result.parcours = linkeFahrstrasse.parcours;

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
   * Umgekehrte Fahrstrasse erzeugen.
   *
   * Es wird eine Fahrstrasse mit umgekehrten Elementen in umgekehrter Reihung erzeugt.
   *
   * @return umgekehrte Fahrstrasse
   */
  public Fahrstrasse createUmkehrung() {
    Fahrstrasse fahrstrasse = new Fahrstrasse();

    this.elemente.forEach(fse -> fahrstrasse.elemente.add(0, fse.createUmkehrung()));

    fahrstrasse.rank = this.rank;

    fahrstrasse.zaehlrichtung = !this.zaehlrichtung;

    fahrstrasse.start = (FahrstrassenGleisabschnitt) fahrstrasse.elemente.get(0);
    fahrstrasse.ende = (FahrstrassenGleisabschnitt) fahrstrasse.elemente.get(fahrstrasse.elemente.size() - 1);

    fahrstrasse.setBereich(fahrstrasse.start.getBereich());
    fahrstrasse.createName();

    return fahrstrasse;
  }

  /*
   * ************************************************************************************************
   * Ab hier folgt die normale Geschäftslogik.
   */

  /**
   * Beginnt die Fahrstrasse mit dem angegebenen Gleisabschnitt?
   *
   * @param gleisabschnitt
   *        Gleisabschnitt
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
   *        Gleisabschnitt
   * @return <code>true</code>, wenn die Fahrstrasse mit dem angegebenen Gleisabschnitt endet
   */
  public boolean endsWith(Gleisabschnitt gleisabschnitt) {
    FahrstrassenGleisabschnitt ende = getEnde();
    return gleisabschnitt.equals(ende.getFahrwegelement());
  }

  /**
   * Ist die Fahrstrasse (komplett) frei?
   *
   * Die Fahrstrasse ist dann frei,
   * - wenn sie mit keiner bereits reservierten Fahrstrasse kollidiert,
   * - wenn keiner ihrer Gleisabschnitte besetzt ist, wobei für diese Prüfung der Start und das Ende per Parameter ausgenommen werden können.
   *
   * @param includeStart
   *        Erstes Element der Fahrstrasse in Besetztprüfung berücksichtigen?
   * @param includeEnde
   *        Letztes Element der Fahrstrasse in Besetztprüfung berücksichtigen?
   * @return <code>true</code>, wenn die Fahrstrasse frei ist.
   */
  public boolean isFrei(boolean includeStart, boolean includeEnde) {
    synchronized (Fahrstrasse.class) {

      int elementCount = this.elemente.size();
      for (int index = 0; index < elementCount; ++index) {
        Fahrstrassenelement element = this.elemente.get(index);
        ReservierbaresFahrwegelement fahrwegelement = element.getFahrwegelement();

        if (!element.isSchutz() && fahrwegelement.getReserviertefahrstrasse() != null) {
          return false;
        }

        if (!includeStart && index == 0) {
          continue;
        }

        if (!includeEnde && index == elementCount - 1) {
          continue;
        }

        if (fahrwegelement instanceof Gleisabschnitt && ((Gleisabschnitt) fahrwegelement).isBesetzt()) {
          return false;
        }
      }
    }

    return true;
  }

  /**
   * Fahrstrasse reservieren oder freigeben.
   *
   * @see {@link #reservieren(FahrstrassenReservierungsTyp, boolean, boolean) reservieren(FahrstrassenReservierungsTyp, false, false)}
   */
  public boolean reservieren(FahrstrassenReservierungsTyp reservierungsTyp) {
    return reservieren(reservierungsTyp, false, false);
  }

  /**
   * Fahrstrasse reservieren oder freigeben.
   *
   * Wird als reservierungsTyp {@link FahrstrassenReservierungsTyp#UNRESERVIERT} übergeben, wird die Fahrstrasse freigegeben.
   * Bei anderem reservierungsTyp wird die Fahrstrasse entsprechend reserviert, wenn sie noch frei ist.
   *
   * Zur Freigabe kann aber auch {@link #freigeben(Gleisabschnitt)} genutzt werden.
   *
   * @param reservierungsTyp
   *        Art der Fahrstrassenreservierung, <code>UNRESERVIERT</code> für Freigabe
   * @param includeStart
   *        Erstes Element der Fahrstrasse in Besetztprüfung berücksichtigen?
   * @param includeEnde
   *        Letztes Element der Fahrstrasse in Besetztprüfung berücksichtigen?
   * @return <code>true</code>, wenn die Fahrstrasse reserviert bzw. freigegeben werden konnte
   */
  public boolean reservieren(FahrstrassenReservierungsTyp reservierungsTyp, boolean includeStart, boolean includeEnde) {

    if (reservierungsTyp == null || reservierungsTyp == FahrstrassenReservierungsTyp.UNRESERVIERT) {
      return freigeben(null);

    }

    synchronized (Fahrstrasse.class) {
      if (!isFrei(includeStart, includeEnde)) {
        return false;
      }

      this.reservierungsTyp = reservierungsTyp;
      this.teilFreigabeAnzahl = 0;
      this.elemente.forEach(fe -> fe.reservieren(this));
    }

    this.eventFirer.fire(this, Reserviert.Literal.INSTANCE);
    return true;

  }

  /**
   * Fahrstrasse komplett oder teilweise freigeben.
   *
   * @param teilFreigabeEnde
   *        <code>null</code> für Komplettfreigabe oder erster Gleisabschnitt, der nicht freigegeben wird
   * @return <code>true</code>, wenn die Fahrstrasse freigegeben werden konnte
   */
  public boolean freigeben(Gleisabschnitt teilFreigabeEnde) {

    if (this.reservierungsTyp == FahrstrassenReservierungsTyp.UNRESERVIERT) {
      return false;
    }

    int bisherigeTeilFreigabeAnzahl = this.teilFreigabeAnzahl;
    int neueTeilFreigabeAnzahl = this.teilFreigabeAnzahl;

    synchronized (Fahrstrasse.class) {
      while (neueTeilFreigabeAnzahl < this.elemente.size()) {
        Fahrstrassenelement element = this.elemente.get(neueTeilFreigabeAnzahl);
        if (teilFreigabeEnde != null && element instanceof FahrstrassenGleisabschnitt && teilFreigabeEnde.equals(element.getFahrwegelement())) {
          break;
        }
        element.reservieren(null);
        ++neueTeilFreigabeAnzahl;
      }

      if (neueTeilFreigabeAnzahl >= this.elemente.size()) {
        this.reservierungsTyp = FahrstrassenReservierungsTyp.UNRESERVIERT;
      }

      this.teilFreigabeAnzahl = neueTeilFreigabeAnzahl;
    }

    this.eventFirer.fire(this, Freigegeben.Literal.of(bisherigeTeilFreigabeAnzahl, neueTeilFreigabeAnzahl));

    return neueTeilFreigabeAnzahl != bisherigeTeilFreigabeAnzahl;
  }

  @Qualifier
  @Target({ TYPE, METHOD, PARAMETER, FIELD })
  @Retention(RUNTIME)
  @Documented
  public static @interface Reserviert {
    public static final class Literal extends AnnotationLiteral<Reserviert> implements Reserviert {
      public static final Literal INSTANCE = new Literal();
    }
  }

  @Qualifier
  @Target({ TYPE, METHOD, PARAMETER, FIELD })
  @Retention(RUNTIME)
  @Documented
  public static @interface Freigegeben {
    @Nonbinding
    int bisher() default 0;

    @Nonbinding
    int neu() default 0;

    public static final class Literal extends AnnotationLiteral<Freigegeben> implements Freigegeben {

      public static Literal of(int bisher, int neu) {
        return new Literal(bisher, neu);
      }

      private int bisher;
      private int neu;

      private Literal(int bisher, int neu) {
        this.bisher = bisher;
        this.neu = neu;
      }

      @Override
      public int bisher() {
        return this.bisher;
      }

      @Override
      public int neu() {
        return this.neu;
      }
    }
  }

  /**
   * Sind alle Gleisabschnitte ab dem übergebenen Index besetzt?
   * Wird in der Freigabesteuerung benutzt: Ist die restliche Fahrstrasse komplett besetzt, kann sie freigegeben werden.
   * 
   * @param startIndex Start-Index
   * @return alles besetzt?
   */
  public boolean isKomplettBesetzt(int startIndex) {
    for (int i = startIndex; i < this.elemente.size(); ++i) {
      Fahrstrassenelement fe = this.elemente.get(i);
      if (fe instanceof FahrstrassenGleisabschnitt) {
        Gleisabschnitt g = ((FahrstrassenGleisabschnitt) fe).getFahrwegelement();
        if (!g.isBesetzt()) {
          return false;
        }
      }
      // TODO Sind Nicht-Gleisabschnitte überhaupt relevant?
      // else {
      // if (!fe.isSchutz()) {
      // return false;
      // }
      // }

    }

    return true;
  }

  /**
   * Folgen ab dem übergebenen Index nur noch Gleisabschnitte?
   * Wird in der Freigabesteuerung benutzt: Falls die restliche Fahtrstrasse nur aus Gleisabschnitten besteht,
   * kann sie freigegeben werden.
   * 
   * @param startIndex Start-Index
   * @return nur noch Gleisabschnitte?
   */
  public boolean isNurGleisabschnitte(int startIndex) {
    for (int i = startIndex; i < this.elemente.size(); ++i) {
      Fahrstrassenelement fe = this.elemente.get(i);
      if (!(fe instanceof FahrstrassenGleisabschnitt) && !fe.isSchutz()) {
        return false;
      }
    }

    return true;
  }

  public void injectFields() {
    InjectionUtil.injectFields(this);
  }

}
