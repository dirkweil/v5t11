package de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import de.gedoplan.baselibs.utils.inject.InjectionUtil;
import de.gedoplan.v5t11.fahrstrassen.entity.Parcours;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Gleis;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.ReservierbaresFahrwegelement;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Signal;
import de.gedoplan.v5t11.fahrstrassen.persistence.FahrstrassenStatusRepository;
import de.gedoplan.v5t11.util.domain.attribute.BereichselementId;
import de.gedoplan.v5t11.util.domain.attribute.FahrstrassenReservierungsTyp;
import de.gedoplan.v5t11.util.domain.attribute.SignalStellung;
import de.gedoplan.v5t11.util.domain.attribute.WeichenStellung;
import de.gedoplan.v5t11.util.domain.entity.Bereichselement;
import de.gedoplan.v5t11.util.jsonb.JsonbInclude;
import de.gedoplan.v5t11.util.transaction.TransactionChecker;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.util.AnnotationLiteral;
import javax.enterprise.util.Nonbinding;
import javax.inject.Inject;
import javax.inject.Qualifier;
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

  @Inject
  FahrstrassenStatusRepository fahrstrassenStatusRepository;

  @Inject
  TransactionChecker transactionChecker;

  /**
   * In Zählrichtung orientiert?
   * Dieses Attribut dient nur als Default für die zugehörigen Fahrstrassenelemente.
   */
  @XmlAttribute
  @Getter(onMethod_ = @JsonbInclude(full = true))
  private boolean zaehlrichtung;

  /**
   * Zielsignalbereich.
   * Wenn gesetzt, endet die Fahrstrasse vor diesem Signal.
   */
  @XmlAttribute(name = "ziel-bereich")
  @Getter(onMethod_ = @JsonbInclude(full = true))
  private String zielSignalBereich;

  /**
   * Zielsignalname.
   * Wenn gesetzt, endet die Fahrstrasse vor diesem Signal.
   */
  @XmlAttribute(name = "ziel-name")
  @Getter(onMethod_ = @JsonbInclude(full = true))
  private String zielSignalName;

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
   * Liste der Fahrstrassenelemente. Beginnt und endet immer mit einem Gleis.
   */
  @XmlElements({
      @XmlElement(name = "Gleis", type = FahrstrassenGleis.class),
      @XmlElement(name = "Hauptsignal", type = FahrstrassenHauptsignal.class),
      @XmlElement(name = "Vorsignal", type = FahrstrassenVorsignal.class),
      @XmlElement(name = "Sperrsignal", type = FahrstrassenSperrsignal.class),
      @XmlElement(name = "Weiche", type = FahrstrassenWeiche.class) })
  @Getter(onMethod_ = @JsonbInclude(full = true))
  private List<Fahrstrassenelement> elemente = new ArrayList<>();

  /*
   * ************************************************************************************************
   * Nach dem Lesen aus XML oder DB sind im Objekt nur die oben stehenden Attribute gefüllt.
   * Es folgen nun weitere Attribute, die davon abgeleitete werden, sowie die zugehörigen
   * Initialisierungsmethoden.
   */

  /**
   * Kann diese Fahrstrasse umgekehrt genutzt werden?
   * Ist nur zum Aufbau der Daten aus dem XML nötig und wird nicht in der DB abgelegt
   */
  @XmlAttribute
  @Getter
  private boolean umkehrbar;

  /**
   * Erstes Element.
   */
  private FahrstrassenGleis start;

  public FahrstrassenGleis getStart() {
    if (this.start == null) {
      this.start = (FahrstrassenGleis) this.elemente.get(0);
    }
    return this.start;
  }

  /**
   * Letztes Element.
   */
  private FahrstrassenGleis ende;

  public FahrstrassenGleis getEnde() {
    if (this.ende == null) {
      this.ende = (FahrstrassenGleis) this.elemente.get(this.elemente.size() - 1);
    }
    return this.ende;
  }

  @SuppressWarnings("unused")
  private void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
    if (!(parent instanceof Parcours)) {
      throw new IllegalArgumentException("Parent von Fahrstrasse muss Parcours sein");
    }

    Parcours parcours = (Parcours) parent;

    // Fuer alle Elemente Defaults übernehmen wenn nötig
    if (this.zielSignalName != null) {
      if (this.zielSignalBereich == null) {
        this.zielSignalBereich = getBereich();
      }
    } else {
      this.zielSignalBereich = null;
    }

    this.elemente.forEach(element -> {
      if (element.getBereich() == null) {
        element.setBereich(getBereich());
      }

      if (element.zaehlrichtung == null) {
        element.zaehlrichtung = this.zaehlrichtung;
      }
    });

    // Zu Weichen die entsprechenden Gleise ergänzen, wenn es nicht nur Schutzweichen sind
    ListIterator<Fahrstrassenelement> iterator = this.elemente.listIterator();
    while (iterator.hasNext()) {
      Fahrstrassenelement fahrstrassenelement = iterator.next();
      if (fahrstrassenelement instanceof FahrstrassenWeiche && !fahrstrassenelement.isSchutz()) {
        FahrstrassenGleis fahrstrassenGleis = ((FahrstrassenWeiche) fahrstrassenelement).createFahrstrassenGleis();
        if (!this.elemente.contains(fahrstrassenGleis)) {
          iterator.add(fahrstrassenGleis);
        }
      }
    }

    // Erstes und letztes Element müssen FahrstrassenGleise sein
    int count = this.elemente.size();
    if (count < 2 || !(this.elemente.get(0) instanceof FahrstrassenGleis) || !(this.elemente.get(count - 1) instanceof FahrstrassenGleis)) {
      throw new IllegalArgumentException("Fahrstrasse muss min. 2 Elemente haben und mit Gleisen beginnen und enden");
    }

    // Bereich der Stecke und des Startelements müssen gleich sein
    if (!getBereich().equals(this.getStart().getBereich())) {
      throw new IllegalArgumentException("Erster Gleise muss im gleichen Bereich wie die Fahrstrasse liegen");
    }

    // Doppelte entfernen
    removeDoppeleintraege();

    createName();
    createRank();
  }

  /*
   * Fahrstrassen-Name aus enthaltenen Gleisen zusammenstellen.
   */
  private void createName() {
    setName(this.elemente
        .stream()
        .filter(e -> e instanceof FahrstrassenGleis)
        .map(e -> (FahrstrassenGleis) e)
        // .filter(g -> !g.isWeichenGleis())
        .map(g -> g.getBereich().equals(getBereich()) ? g.getName() : g.getBereich() + "." + g.getName())
        .collect(Collectors.joining("-")));
  }

  /**
   * Rang als Summe der Einzelwerte erstellen.
   */
  private void createRank() {
    this.rank = this.elemente.stream().mapToInt(e -> e.getRank()).sum();
  }

  /*
   * ************************************************************************************************
   * Es folgen nun weitere Methoden, die bei einem Neuaufbau des Parcours aus dem XML benötigt werden,
   * um Combi-Fahrstrassen zu erzeugen, Doppelte zu entfernen etc.
   * Diese Methoden werden nur von Parcours.completeFahrstrassen und Parcours.addPersistentEntries genutzt.
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

    // Wenn Ende links nicht Anfang rechts oder unterschiedliche Zählrichtung, nicht kombinieren
    Fahrstrassenelement linksLast = linkeFahrstrasse.getEnde();
    Fahrstrassenelement rechtsFirst = rechteFahrstrasse.getStart();
    if (!linksLast.equals(rechtsFirst)
        || linksLast.isZaehlrichtung() != rechtsFirst.isZaehlrichtung()) {
      return null;
    }

    // Wenn Zyklen entstehen würden, nicht kombinieren
    Set<ReservierbaresFahrwegelement> linkeGleise = linkeFahrstrasse
        .getElemente()
        .stream()
        .filter(e -> e instanceof FahrstrassenGleis)
        .map(e -> e.getFahrwegelement())
        .collect(Collectors.toSet());
    boolean zyklus = rechteFahrstrasse
        .getElemente()
        .stream()
        .skip(1)
        .filter(e -> e instanceof FahrstrassenGleis)
        .map(e -> e.getFahrwegelement())
        .anyMatch(linkeGleise::contains);
    if (zyklus) {
      return null;
    }

    Fahrstrasse result = new Fahrstrasse();
    result.setBereich(linkeFahrstrasse.getBereich());
    result.combi = true;
    result.zaehlrichtung = linkeFahrstrasse.zaehlrichtung;
    result.zielSignalBereich = rechteFahrstrasse.zielSignalBereich;
    result.zielSignalName = rechteFahrstrasse.zielSignalName;

    linkeFahrstrasse.elemente.stream().map(Fahrstrassenelement::createKopie).forEach(result.elemente::add);
    rechteFahrstrasse.elemente.stream().skip(1).map(Fahrstrassenelement::createKopie).forEach(result.elemente::add);

    result.removeDoppeleintraege();

    result.start = linkeFahrstrasse.getStart();
    result.ende = rechteFahrstrasse.getEnde();

    // Schutzsignale entfernen, die auch als normale Signale vorhanden sind
    Set<Signal> normaleSignale = result.elemente
        .stream()
        .filter(e -> !e.isSchutz())
        .filter(e -> e instanceof FahrstrassenSignal)
        .map(e -> ((FahrstrassenSignal) e).getFahrwegelement())
        .collect(Collectors.toSet());

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

    result.createName();
    result.createRank();

    return result;
  }

  /**
   * Doppeleinträge in der Fahrstrasse eliminieren.
   */
  public void removeDoppeleintraege() {
    int i = 0;
    while (i < this.elemente.size()) {
      Fahrstrassenelement element = this.elemente.get(i);

      while (true) {
        int i2 = findLastSame(element);
        if (i2 <= i) {
          // kein Doppelvorkommen; weiter mit nächstem Eintrag
          ++i;
          break;
        }

        Fahrstrassenelement element2 = this.elemente.get(i2);

        if (i == 0 || element2.isSchutz()) {
          // erstes Element ist am Anfang ==> muss stehen bleiben, da FS sonst ggf. nicht mit einem Gleis beginnt
          // zweites Element ist Schutzelement ==> zweites Element löschen, weiter nach Doppelvorkommen suchen
          this.elemente.remove(i2);
        } else {
          // zweites Element ist kein Schutzelement ==> erstes Element löschen, weiter mit nächstem Eintrag (der dann am gleichen Index i steht!)
          this.elemente.remove(i);
          break;
        }
      }
    }
  }

  private int findLastSame(Fahrstrassenelement element) {
    int i = this.elemente.size();
    while (true) {
      --i;
      if (i < 0) {
        return -1;
      }
      if (this.elemente.get(i).equals(element)) {
        return i;
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

    fahrstrasse.setBereich(fahrstrasse.getStart().getBereich());
    fahrstrasse.createName();

    return fahrstrasse;
  }

  public void createFahrstrassenStatus() {
    FahrstrassenStatus fahrstrassenStatus = this.fahrstrassenStatusRepository.findById(getId());
    if (fahrstrassenStatus == null) {
      this.fahrstrassenStatusRepository.persist(new FahrstrassenStatus(getBereich(), getName()));
    }
  }

  /*
   * ************************************************************************************************
   * Ab hier folgt die normale Geschäftslogik.
   */

  /**
   * Beginnt die Fahrstrasse mit dem angegebenen Gleis?
   *
   * @param gleisId
   *        Id des Gleises
   * @return <code>true</code>, wenn die Fahrstrasse mit dem angegebenen Gleis beginnt
   */
  public boolean startsWith(BereichselementId gleisId) {
    return gleisId.equals(getStart().getId());
  }

  /**
   * Endet die Fahrstrasse mit dem angegebenen Gleis?
   *
   * @param gleisId
   *        Id des Gleises
   * @return <code>true</code>, wenn die Fahrstrasse mit dem angegebenen Gleis endet
   */
  public boolean endsWith(BereichselementId gleisId) {
    return gleisId.equals(getEnde().getId());
  }

  /**
   * Status der Fahrstrasse liefern.
   *
   * @return Fahrstrassen-Sstatus
   */
  public FahrstrassenStatus getFahrstrassenStatus() {
    FahrstrassenStatus fahrstrassenStatus = this.fahrstrassenStatusRepository.findById(getId());
    if (fahrstrassenStatus == null) {
      throw new IllegalStateException("FahrstrassenStatus nicht vorhanden: " + getId());
      // fahrstrassenStatus = new FahrstrassenStatus(getBereich(), getName());
      // this.fahrstrassenStatusRepository.persist(fahrstrassenStatus);
    }
    return fahrstrassenStatus;
  }

  /**
   * Reservierungstyp liefern (Conveniance-Methode).
   *
   * @return Reservierungstyp
   */
  @JsonbInclude
  public FahrstrassenReservierungsTyp getReservierungsTyp() {
    return getFahrstrassenStatus().getReservierungsTyp();
  }

  /**
   * Teilfreigabeanzahl liefern (Conveniance-Methode).
   *
   * @return Teilfreigabeanzahl
   */
  @JsonbInclude
  public int getTeilFreigabeAnzahl() {
    return getFahrstrassenStatus().getTeilFreigabeAnzahl();
  }

  /**
   * Ist die Fahrstrasse (komplett) frei?
   *
   * Die Fahrstrasse ist dann frei,
   * - wenn sie mit keiner bereits reservierten Fahrstrasse kollidiert,
   * - wenn keiner ihrer Gleise besetzt ist, wobei für diese Prüfung der Start und das Ende per Parameter ausgenommen werden können.
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

        if (!element.isSchutz() && fahrwegelement.getReserviertefahrstrasseId() != null) {
          return false;
        }

        if (!includeStart && index == 0) {
          continue;
        }

        if (!includeEnde && index == elementCount - 1) {
          continue;
        }

        if (fahrwegelement instanceof Gleis && ((Gleis) fahrwegelement).isBesetzt()) {
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
   * Zur Freigabe kann aber auch {@link #freigeben(Gleis)} genutzt werden.
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

    this.transactionChecker.assureActiveTransaction();

    synchronized (Fahrstrasse.class) {
      if (!isFrei(includeStart, includeEnde)) {
        return false;
      }

      FahrstrassenStatus fahrstrassenStatus = getFahrstrassenStatus();
      fahrstrassenStatus.setReservierungsTyp(reservierungsTyp);
      fahrstrassenStatus.setTeilFreigabeAnzahl(0);
      this.elemente.forEach(fe -> fe.reservieren(getId()));
    }

    this.eventFirer.fire(this, Reserviert.Literal.INSTANCE);
    return true;

  }

  /**
   * Fahrstrasse komplett oder teilweise freigeben.
   *
   * @param teilFreigabeEnde
   *        <code>null</code> für Komplettfreigabe oder erster Gleis, der nicht freigegeben wird
   * @return <code>true</code>, wenn die Fahrstrasse freigegeben werden konnte
   */
  public boolean freigeben(Gleis teilFreigabeEnde) {

    this.transactionChecker.assureActiveTransaction();

    FahrstrassenStatus fahrstrassenStatus = getFahrstrassenStatus();

    if (fahrstrassenStatus.getReservierungsTyp() == FahrstrassenReservierungsTyp.UNRESERVIERT) {
      return false;
    }

    int bisherigeTeilFreigabeAnzahl = fahrstrassenStatus.getTeilFreigabeAnzahl();
    int neueTeilFreigabeAnzahl = bisherigeTeilFreigabeAnzahl;

    synchronized (Fahrstrasse.class) {
      while (neueTeilFreigabeAnzahl < this.elemente.size()) {
        Fahrstrassenelement element = this.elemente.get(neueTeilFreigabeAnzahl);
        if (teilFreigabeEnde != null && element instanceof FahrstrassenGleis && teilFreigabeEnde.equals(element.getFahrwegelement())) {
          break;
        }
        element.reservieren(null);
        ++neueTeilFreigabeAnzahl;
      }

      if (neueTeilFreigabeAnzahl >= this.elemente.size()) {
        fahrstrassenStatus.setReservierungsTyp(FahrstrassenReservierungsTyp.UNRESERVIERT);
      }

      fahrstrassenStatus.setTeilFreigabeAnzahl(neueTeilFreigabeAnzahl);
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
    int bisher()

    default 0;

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
   * Sind alle Gleise ab dem übergebenen Index besetzt?
   * Wird in der Freigabesteuerung benutzt: Ist die restliche Fahrstrasse komplett besetzt, kann sie freigegeben werden.
   *
   * @param startIndex
   *        Start-Index
   * @return alles besetzt?
   */
  public boolean isKomplettBesetzt(int startIndex) {
    for (int i = startIndex; i < this.elemente.size(); ++i) {
      Fahrstrassenelement fe = this.elemente.get(i);
      if (fe instanceof FahrstrassenGleis) {
        Gleis g = ((FahrstrassenGleis) fe).getFahrwegelement();
        if (!g.isBesetzt()) {
          return false;
        }
      }
      // TODO Sind Nicht-Gleise überhaupt relevant?
      // else {
      // if (!fe.isSchutz()) {
      // return false;
      // }
      // }

    }

    return true;
  }

  /**
   * Folgen ab dem übergebenen Index nur noch Gleise?
   * Wird in der Freigabesteuerung benutzt: Falls die restliche Fahtrstrasse nur aus Gleisen besteht,
   * kann sie freigegeben werden.
   *
   * @param startIndex
   *        Start-Index
   * @return nur noch Gleise?
   */
  public boolean isNurGleise(int startIndex) {
    for (int i = startIndex; i < this.elemente.size(); ++i) {
      Fahrstrassenelement fe = this.elemente.get(i);
      if (!(fe instanceof FahrstrassenGleis) && !fe.isSchutz()) {
        return false;
      }
    }

    return true;
  }

  public void injectFields() {
    InjectionUtil.injectFields(this);
    this.elemente.forEach(Fahrstrassenelement::injectFields);
  }

}
