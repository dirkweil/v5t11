package de.gedoplan.v5t11.status.entity;

import de.gedoplan.baselibs.utils.inject.InjectionUtil;
import de.gedoplan.v5t11.status.entity.autoskript.AutoSkript;
import de.gedoplan.v5t11.status.entity.baustein.Baustein;
import de.gedoplan.v5t11.status.entity.baustein.Besetztmelder;
import de.gedoplan.v5t11.status.entity.baustein.Funktionsdecoder;
import de.gedoplan.v5t11.status.entity.baustein.Lokcontroller;
import de.gedoplan.v5t11.status.entity.baustein.Zentrale;
import de.gedoplan.v5t11.status.entity.baustein.besetztmelder.BMMiba3;
import de.gedoplan.v5t11.status.entity.baustein.besetztmelder.HEBM8;
import de.gedoplan.v5t11.status.entity.baustein.besetztmelder.Muet8i;
import de.gedoplan.v5t11.status.entity.baustein.besetztmelder.Muet8k;
import de.gedoplan.v5t11.status.entity.baustein.besetztmelder.SXBM1;
import de.gedoplan.v5t11.status.entity.baustein.besetztmelder.VM5262;
import de.gedoplan.v5t11.status.entity.baustein.funktionsdecoder.NoFD;
import de.gedoplan.v5t11.status.entity.baustein.funktionsdecoder.SD8;
import de.gedoplan.v5t11.status.entity.baustein.funktionsdecoder.STRFD1;
import de.gedoplan.v5t11.status.entity.baustein.funktionsdecoder.SXSD1;
import de.gedoplan.v5t11.status.entity.baustein.funktionsdecoder.WDMiba;
import de.gedoplan.v5t11.status.entity.baustein.funktionsdecoder.WDMiba3;
import de.gedoplan.v5t11.status.entity.baustein.lokcontroller.SxLokControl;
import de.gedoplan.v5t11.status.entity.baustein.zentrale.DummyZentrale;
import de.gedoplan.v5t11.status.entity.baustein.zentrale.FCC;
import de.gedoplan.v5t11.status.entity.fahrweg.Gleisabschnitt;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.FunktionsdecoderGeraet;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Schalter;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Signal;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Weiche;
import de.gedoplan.v5t11.status.entity.fahrzeug.Fahrzeug;
import de.gedoplan.v5t11.status.entity.fahrzeug.FahrzeugId;
import de.gedoplan.v5t11.status.persistence.FahrzeugRepository;
import de.gedoplan.v5t11.util.domain.attribute.SystemTyp;
import de.gedoplan.v5t11.util.domain.entity.Bereichselement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.logging.Log;

import lombok.Getter;

/**
 * Steuerung.
 *
 * Diese Klasse fasst alle zur Steuerung gehörenden Elemente zusammen.
 *
 * @author dw
 */
@XmlRootElement(name = "sx")
@XmlAccessorType(XmlAccessType.NONE)
public class Steuerung {

  @Inject
  FahrzeugRepository fahrzeugRepository;

  @Inject
  Log log;

  @XmlElements({
      @XmlElement(name = "DummyZentrale", type = DummyZentrale.class),
      @XmlElement(name = "FCC", type = FCC.class)
  })
  @Getter
  private Zentrale zentrale;

  private Map<Integer, Baustein> kanalBausteine = new ConcurrentHashMap<>();
  private Set<Integer> supressedKanaele = new CopyOnWriteArraySet<>();

  @XmlElementWrapper(name = "Lokcontroller")
  @XmlElements({
      @XmlElement(name = "SxLokControl", type = SxLokControl.class),
  })
  @Getter
  SortedSet<Lokcontroller> lokcontroller = new TreeSet<>();

  @XmlElementWrapper(name = "Besetztmelder")
  @XmlElements({
      @XmlElement(name = "BMMiba3", type = BMMiba3.class),
      @XmlElement(name = "HEBM8", type = HEBM8.class),
      @XmlElement(name = "Muet8i", type = Muet8i.class),
      @XmlElement(name = "Muet8k", type = Muet8k.class),
      @XmlElement(name = "SXBM1", type = SXBM1.class),
      @XmlElement(name = "VM5262", type = VM5262.class),
  })
  @Getter
  private SortedSet<Besetztmelder> besetztmelder = new TreeSet<>();

  @XmlElementWrapper(name = "Funktionsdecoder")
  @XmlElements({
      @XmlElement(name = "NoFD", type = NoFD.class),
      @XmlElement(name = "SD8", type = SD8.class),
      @XmlElement(name = "STRFD1", type = STRFD1.class),
      @XmlElement(name = "SXSD1", type = SXSD1.class),
      @XmlElement(name = "WDMiba", type = WDMiba.class),
      @XmlElement(name = "WDMiba3", type = WDMiba3.class),
  })
  @Getter
  private SortedSet<Funktionsdecoder> funktionsdecoder = new TreeSet<>();

  @Getter
  private SortedSet<String> bereiche = new TreeSet<>();

  @Getter
  private SortedSet<Gleisabschnitt> gleisabschnitte = new TreeSet<>();

  @Getter
  private SortedSet<Schalter> schalter = new TreeSet<>();

  @Getter
  private SortedSet<Signal> signale = new TreeSet<>();

  @Getter
  private SortedSet<Weiche> weichen = new TreeSet<>();

  private SortedMap<FahrzeugId, Fahrzeug> fahrzeuge = new TreeMap<>();

  @XmlElementWrapper(name = "AutoSkripte")
  @XmlElement(name = "AutoSkript")
  @Getter
  private List<AutoSkript> autoSkripte = new ArrayList<>();

  public Collection<Fahrzeug> getFahrzeuge() {
    return this.fahrzeuge.values();
  }

  public Fahrzeug getFahrzeug(FahrzeugId id) {
    return this.fahrzeuge.get(id);
  }

  public void addFahrzeug(Fahrzeug fahrzeug) {
    if (this.fahrzeuge.containsKey(fahrzeug.getId())) {
      return;
    }

    this.fahrzeuge.put(fahrzeug.getId(), fahrzeug);
    // TODO Fahrzeug in Zentrale anmelden
  }

  public void removeFahrzeug(FahrzeugId id) {
    if (this.fahrzeuge.remove(id) == null) {
      return;
    }

    // TODO Fahrzeug in Zentrale abmelden
  }

  /**
   * Wert liefern: {@link #lokController}.
   *
   * @return Wert
   */
  public Lokcontroller getLokcontroller(String id) {
    for (Lokcontroller lc : this.lokcontroller) {
      if (id.equals(lc.getId())) {
        return lc;
      }
    }

    return null;
  }

  /**
   * Wert liefern: {@link #gleisabschnitte}.
   *
   * @return Wert
   */
  public Set<Gleisabschnitt> getGleisabschnitte(String bereich) {
    return getBereichselemente(bereich, this.gleisabschnitte);
  }

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
   * Wert liefern: {@link #signale}.
   *
   * @return Wert
   */
  public Set<Signal> getSignale(String bereich) {
    return getBereichselemente(bereich, this.signale);
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
   * Wert liefern: {@link #schalter}.
   *
   * @return Wert
   */
  public Set<Schalter> getSchalter(String bereich) {
    return getBereichselemente(bereich, this.schalter);
  }

  /**
   * Schalter liefern.
   *
   * @param bereich
   *          Bereich
   * @param name
   *          Name
   * @return gefundener Schalter oder <code>null</code>
   */
  public Schalter getSchalter(String bereich, String name) {
    return getBereichselement(bereich, name, this.schalter);
  }

  /**
   * Wert liefern: {@link #weichen}.
   *
   * @return Wert
   */
  public Set<Weiche> getWeichen(String bereich) {
    return getBereichselemente(bereich, this.weichen);
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

  private static <E extends Bereichselement> SortedSet<E> getBereichselemente(String bereich, SortedSet<E> bereichselemente) {
    SortedSet<E> result = new TreeSet<>();
    for (E element : bereichselemente) {
      if (bereich.equals(element.getBereich())) {
        result.add(element);
      }
    }

    return result;
  }

  private static <T extends Bereichselement> T getBereichselement(String bereich, String name, Collection<T> set) {
    for (T element : set) {
      if (element.getBereich().equals(bereich) && element.getName().equals(name)) {
        return element;
      }
    }

    return null;
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

    /*
     * Default-Zentrale ist FCC.
     */
    if (this.zentrale == null) {
      this.zentrale = new FCC();
    }

    /*
     * Den Besetztmeldern zugeordnete Gleisabschnitte in this.gleisabschnitte
     * sammeln. Dabei auch die Bereiche in this.bereiche eintragen und Adressen
     * registrieren.
     */
    for (Besetztmelder bm : this.besetztmelder) {
      for (Gleisabschnitt g : bm.getGleisabschnitte()) {
        this.gleisabschnitte.add(g);
        this.bereiche.add(g.getBereich());
      }

      registerAdressen(bm);
    }

    /*
     * Den Funktionsdecodern zugeordnete Geräte in this.schalter/signale/weichen sammeln. Dabei auch die Bereiche in
     * this.bereiche eintragen und Adressen registrieren.
     */
    for (Funktionsdecoder fd : this.funktionsdecoder) {
      for (FunktionsdecoderGeraet g : fd.getGeraete()) {
        if (g instanceof Schalter) {
          Schalter s = (Schalter) g;
          this.schalter.add(s);
          this.bereiche.add(s.getBereich());
        }

        if (g instanceof Signal) {
          Signal s = (Signal) g;
          this.signale.add(s);
          this.bereiche.add(s.getBereich());
        }

        if (g instanceof Weiche) {
          Weiche w = (Weiche) g;
          this.weichen.add(w);
          this.bereiche.add(w.getBereich());
        }

      }

      registerAdressen(fd);
    }

    for (Lokcontroller lc : this.lokcontroller) {
      registerAdressen(lc);
    }

    // Skript-Objekte mit zugehörigen Steuerungsobkjekten verknüpfen
    this.autoSkripte.forEach(as -> as.linkSteuerungsObjekte(this));
  }

  public void assignLokcontroller(String lokcontrollerId, FahrzeugId fahrzeugId) {
    Lokcontroller lokcontroller = getLokcontroller(lokcontrollerId);
    if (lokcontroller == null) {
      throw new IllegalArgumentException("Lokcontroller nicht gefunden: " + lokcontrollerId);
    }

    Fahrzeug lok = null;
    if (fahrzeugId != null) {
      lok = getFahrzeug(fahrzeugId);
      if (lok == null) {
        throw new IllegalArgumentException("Lok nicht gefunden: " + fahrzeugId);
      }

      for (Lokcontroller lc : getLokcontroller()) {
        if (!lc.equals(lokcontroller) && lok.equals(lc.getLok())) {
          lc.setLok(null);
        }
      }
    }

    lokcontroller.setLok(lok);
  }

  private void registerAdressen(Baustein baustein) {
    baustein.getAdressen().forEach(adr -> {
      if (this.kanalBausteine.containsKey(adr)) {
        throw new IllegalArgumentException("Adresse " + adr + " mehrfach belegt");
      }

      this.kanalBausteine.put(adr, baustein);
    });

  }

  /**
   * Textliche Repräsentation der Steuerung erstellen.
   *
   * @param idOnly
   *          nur IDs?
   * @return Steuerung als String
   */
  public String toDebugString(boolean idOnly) {
    StringBuilder buf = new StringBuilder("Steuerung");

    buf.append("\n  ").append(this.zentrale);

    for (Besetztmelder bm : this.besetztmelder) {
      buf.append("\n  ").append(bm);
      for (Gleisabschnitt g : bm.getGleisabschnitte()) {
        buf.append("\n    ").append(g);
      }
    }

    for (Funktionsdecoder fd : this.funktionsdecoder) {
      buf.append("\n  ").append(fd);
      for (FunktionsdecoderGeraet g : fd.getGeraete()) {
        buf.append("\n    ").append(g);
      }
    }

    for (Lokcontroller ld : this.lokcontroller) {
      buf.append("\n ").append(ld);
    }

    for (AutoSkript as : this.autoSkripte) {
      buf.append("\n ").append(as);
    }

    return buf.toString();
  }

  /**
   * {@inheritDoc}
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return toDebugString(true);
  }

  public void open(ExecutorService executorService) {
    this.zentrale.open(executorService);
  }

  public void close() {
    this.zentrale.close();
  }

  public void injectFields() {
    InjectionUtil.injectFields(this);
    this.zentrale.injectFields();
    this.besetztmelder.forEach(Besetztmelder::injectFields);
    this.funktionsdecoder.forEach(Funktionsdecoder::injectFields);
    this.lokcontroller.forEach(Lokcontroller::injectFields);

    this.fahrzeugRepository.findAll().forEach(f -> {
      this.fahrzeuge.put(f.getId(), f);
      f.injectFields();
    });
  }

  public void postConstruct() {
    this.zentrale.postConstruct();

    // Besetztmelder etc. haben derzeit kein postConstruct; nachrüsten, falls erforderlich
    // this.besetztmelder.forEach(Besetztmelder::postConstruct);
    // this.funktionsdecoder.forEach(Funktionsdecoder::postConstruct);
    // this.lokcontroller.forEach(Lokcontroller::postConstruct);
    //
    // this.loks.values().forEach(lok -> {
    // lok.postConstruct();
    // });

    /*
     * Bei Betrieb ohne Port Zentrale nur simulieren.
     */
    if ("none".equalsIgnoreCase(this.zentrale.getPortName())) {
      this.zentrale = new DummyZentrale();
      this.zentrale.injectFields();
      this.zentrale.postConstruct();
    }

  }

  public int getSX1Kanal(int adr) {
    return this.zentrale.getSX1Kanal(adr);
  }

  public void setSX1Kanal(int adr, int wert) {
    this.zentrale.setSX1Kanal(adr, wert);
  }

  public void adjustTo(Kanal kanal) {
    int adr = kanal.getAdresse();

    if (!this.supressedKanaele.contains(adr)) {
      Baustein baustein = this.kanalBausteine.get(adr);
      if (baustein != null) {
        int wert = kanal.getWert();
        baustein.adjustWert(adr, wert);
        return;
      }

      Fahrzeug lok = this.fahrzeuge.get(new FahrzeugId(SystemTyp.SX1, adr));
      if (lok != null) {
        lok.adjustTo(kanal);
      }
    }
  }

  public void adjustTo(SX2Kanal kanal) {
    Fahrzeug lok = this.fahrzeuge.get(new FahrzeugId(kanal.getSystemTyp(), kanal.getAdresse()));
    if (lok != null) {
      lok.adjustTo(kanal);
    }

  }

  public void awaitSync() {
    this.zentrale.awaitSync();
  }

  public void addSuppressedKanal(int adr) {
    if (this.log.isDebugEnabled()) {
      this.log.debug("addSuppressedKanal: " + adr);
    }
    this.supressedKanaele.add(adr);
  }

  public void removeSuppressedKanal(int adr) {
    if (this.log.isDebugEnabled()) {
      this.log.debug("removeSuppressedKanal: " + adr);
    }
    this.supressedKanaele.remove(adr);
  }

}
