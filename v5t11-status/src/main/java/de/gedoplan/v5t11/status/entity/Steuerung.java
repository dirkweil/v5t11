package de.gedoplan.v5t11.status.entity;

import de.gedoplan.v5t11.status.entity.baustein.Baustein;
import de.gedoplan.v5t11.status.entity.baustein.Besetztmelder;
import de.gedoplan.v5t11.status.entity.baustein.Funktionsdecoder;
import de.gedoplan.v5t11.status.entity.baustein.Zentrale;
import de.gedoplan.v5t11.status.entity.baustein.besetztmelder.BMMiba3;
import de.gedoplan.v5t11.status.entity.baustein.besetztmelder.SXBM1;
import de.gedoplan.v5t11.status.entity.baustein.funktionsdecoder.SD8;
import de.gedoplan.v5t11.status.entity.baustein.funktionsdecoder.STRFD1;
import de.gedoplan.v5t11.status.entity.baustein.funktionsdecoder.SXSD1;
import de.gedoplan.v5t11.status.entity.baustein.funktionsdecoder.WDMiba;
import de.gedoplan.v5t11.status.entity.baustein.funktionsdecoder.WDMiba3;
import de.gedoplan.v5t11.status.entity.fahrweg.Geraet;
import de.gedoplan.v5t11.status.entity.fahrweg.Gleisabschnitt;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Signal;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Weiche;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.weld.exceptions.IllegalArgumentException;

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
  private volatile int[] kanalWerte = new int[256];

  private Baustein[] kanalBausteine = new Baustein[256];

  @XmlElement(name = "Zentrale")
  @Getter
  private Zentrale zentrale;

  // @XmlElement(name = "Lok")
  // SortedSet<Lok> loks = new TreeSet<>();

  // @XmlElement(name = "LokController")
  // SortedSet<LokController> lokController = new TreeSet<>();

  @XmlElementWrapper(name = "Besetztmelder")
  @XmlElements({ @XmlElement(name = "BMMiba3", type = BMMiba3.class), @XmlElement(name = "SXBM1", type = SXBM1.class) })
  private SortedSet<Besetztmelder> besetztmelder = new TreeSet<>();

  @XmlElementWrapper(name = "Funktionsdecoder")
  @XmlElements({ @XmlElement(name = "SD8", type = SD8.class), @XmlElement(name = "STRFD1", type = STRFD1.class), @XmlElement(name = "SXSD1", type = SXSD1.class),
      @XmlElement(name = "WDMiba", type = WDMiba.class), @XmlElement(name = "WDMiba3", type = WDMiba3.class) })
  private SortedSet<Funktionsdecoder> funktionsdecoder = new TreeSet<>();

  @Getter
  private SortedSet<String> bereiche = new TreeSet<>();

  @Getter
  private SortedSet<Gleisabschnitt> gleisabschnitte = new TreeSet<>();

  @Getter
  private SortedSet<Signal> signale = new TreeSet<>();

  @Getter
  private SortedSet<Weiche> weichen = new TreeSet<>();

  private static final Log LOGGER = LogFactory.getLog(Steuerung.class);

  /**
   * Alle von der Steuerung belegten Selectrix-Adressen liefern.
   *
   * @return Adressen
   */
  public List<Integer> getAdressen() {
    return IntStream.range(0, this.kanalWerte.length)
        .filter(i -> this.kanalBausteine[i] != null)
        .mapToObj(Integer::valueOf)
        .collect(Collectors.toList());
  }

  // /**
  // * Alle Lokdecoder liefern.
  // *
  // * @return Lokdecoder
  // */
  // public Set<Lokdecoder> getLokdecoder() {
  // Set<Lokdecoder> lokdecoder = new HashSet<>();
  // for (Lok lok : this.loks) {
  // lokdecoder.add(lok.getDecoder());
  // }
  // return lokdecoder;
  // }

  // /**
  // * Lok liefern.
  // *
  // * @param adresse
  // * Adresse
  // * @return gefundene Lok oder <code>null</code>
  // */
  // public Lok getLok(int adresse) {
  // for (Lok lok : this.loks) {
  // if (lok.getAdresse() == adresse) {
  // return lok;
  // }
  // }
  // return null;
  // }

  // /**
  // * Wert liefern: {@link #lokController}.
  // *
  // * @return Wert
  // */
  // public SortedSet<LokController> getLokController() {
  // return this.lokController;
  // }

  // /**
  // * Wert liefern: {@link #lokController}.
  // *
  // * @return Wert
  // */
  // public LokController getLokController(String id) {
  // for (LokController l : this.lokController) {
  // if (id.equals(l.getId())) {
  // return l;
  // }
  // }
  //
  // return null;
  // }

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

    // Zentrale ergänzen.
    if (this.zentrale == null) {
      this.zentrale = new Zentrale();
      this.zentrale.afterUnmarshal(unmarshaller, this);
    }

    // Adressen der Zentrale registrieren.
    registerAdressen(this.zentrale);

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
     * Den Funktionsdecodern zugeordnete Weichen und Signale in this.weichen und
     * this.signale sammeln. Dabei auch die Bereiche in this.bereiche eintragen und Adressen
     * registrieren.
     */
    for (Funktionsdecoder fd : this.funktionsdecoder) {
      for (Geraet g : fd.getGeraete()) {
        if (g instanceof Signal) {
          this.signale.add((Signal) g);
        }

        if (g instanceof Weiche) {
          this.weichen.add((Weiche) g);
        }

        this.bereiche.add(g.getBereich());
      }

      registerAdressen(fd);
    }
  }

  private void registerAdressen(Baustein baustein) {
    baustein.getAdressen().forEach(adr -> {
      if (this.kanalBausteine[adr] != null) {
        throw new IllegalArgumentException("Adresse " + adr + " mehrfach belegt");
      }

      this.kanalBausteine[adr] = baustein;
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
    // for (Lok lok : this.loks) {
    // buf.append("\n ").append(lok.toDebugString(idOnly));
    // }

    for (Besetztmelder bm : this.besetztmelder) {
      buf.append("\n  ").append(bm);
      for (Gleisabschnitt g : bm.getGleisabschnitte()) {
        buf.append("\n    ").append(g);
      }
    }

    for (Funktionsdecoder fd : this.funktionsdecoder) {
      buf.append("\n  ").append(fd);
      for (Geraet g : fd.getGeraete()) {
        buf.append("\n    ").append(g);
      }
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

  public int getKanalWert(int adr) {
    return this.kanalWerte[adr];
  }

  public void setKanalWert(int adr, int wert) {
    setKanalWert(adr, wert, true);
  }

  public void setKanalWert(int adr, int wert, boolean updateInterface) {
    if (this.kanalWerte[adr] != wert) {
      this.kanalWerte[adr] = wert;

      if (updateInterface) {
        /* Interface aktualisieren */;
      }

      this.kanalBausteine[adr].adjustWert(adr, wert);

      // fireEvent
    }
  }
}
