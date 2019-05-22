package de.gedoplan.v5t11.status.entity;

import de.gedoplan.v5t11.selectrix.SelectrixMessage;
import de.gedoplan.v5t11.status.entity.baustein.Baustein;
import de.gedoplan.v5t11.status.entity.baustein.Besetztmelder;
import de.gedoplan.v5t11.status.entity.baustein.Funktionsdecoder;
import de.gedoplan.v5t11.status.entity.baustein.Lokcontroller;
import de.gedoplan.v5t11.status.entity.baustein.Lokdecoder;
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
import de.gedoplan.v5t11.status.entity.baustein.lokdecoder.DH05;
import de.gedoplan.v5t11.status.entity.baustein.lokdecoder.DH10;
import de.gedoplan.v5t11.status.entity.baustein.lokdecoder.DHL050;
import de.gedoplan.v5t11.status.entity.baustein.lokdecoder.DHL100;
import de.gedoplan.v5t11.status.entity.baustein.lokdecoder.Tr66825;
import de.gedoplan.v5t11.status.entity.baustein.lokdecoder.Tr66830;
import de.gedoplan.v5t11.status.entity.baustein.lokdecoder.Tr66832;
import de.gedoplan.v5t11.status.entity.baustein.lokdecoder.Tr66835;
import de.gedoplan.v5t11.status.entity.fahrweg.Gleisabschnitt;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.FunktionsdecoderGeraet;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Signal;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Weiche;
import de.gedoplan.v5t11.status.entity.lok.Lok;
import de.gedoplan.v5t11.status.service.SelectrixGateway;
import de.gedoplan.v5t11.util.cdi.EventFirer;
import de.gedoplan.v5t11.util.domain.entity.Bereichselement;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.enterprise.inject.Vetoed;
import javax.inject.Inject;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

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
@Vetoed // Steuerung wird durch einen Producer bereitgestellt
public class Steuerung {

  @Inject
  SelectrixGateway selectrixGateway;

  private volatile int[] kanalWerte = new int[256];

  private Baustein[] kanalBausteine = new Baustein[256];

  @XmlElement(name = "Interface")
  @Getter
  private SxInterface sxInterface;

  @XmlElement(name = "Zentrale")
  @Getter
  private Zentrale zentrale;

  @XmlElementWrapper(name = "Lokdecoder")
  @XmlElements({
      @XmlElement(name = "DH05", type = DH05.class),
      @XmlElement(name = "DH10", type = DH10.class),
      @XmlElement(name = "DHL050", type = DHL050.class),
      @XmlElement(name = "DHL100", type = DHL100.class),
      @XmlElement(name = "Tr66825", type = Tr66825.class),
      @XmlElement(name = "Tr66830", type = Tr66830.class),
      @XmlElement(name = "Tr66832", type = Tr66832.class),
      @XmlElement(name = "Tr66835", type = Tr66835.class),
  })
  @Getter
  SortedSet<Lokdecoder> lokdecoder = new TreeSet<>();

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
  private SortedSet<Signal> signale = new TreeSet<>();

  @Getter
  private SortedSet<Weiche> weichen = new TreeSet<>();

  @Getter
  private SortedSet<Lok> loks = new TreeSet<>();

  /**
   * Alle von der Steuerung belegten Selectrix-Adressen liefern.
   *
   * @return Adressen
   */
  public List<Integer> getAdressen() {
    List<Integer> adressen = IntStream.range(0, this.kanalWerte.length)
        .filter(i -> i < 10 || this.kanalBausteine[i] != null)
        .mapToObj(Integer::valueOf)
        .collect(Collectors.toList());

    return adressen;
  }

  /**
   * Lok liefern.
   *
   * @param id
   *          Id
   * @return gefundene Lok oder <code>null</code>
   */
  public Lok getLok(String id) {
    for (Lok lok : this.loks) {
      if (id.equals(lok.getId())) {
        return lok;
      }
    }
    return null;
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

    // Interface ergänzen
    if (this.sxInterface == null) {
      this.sxInterface = new SxInterface();
    }

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
      for (FunktionsdecoderGeraet g : fd.getGeraete()) {
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

    for (Lokdecoder ld : this.lokdecoder) {
      Lok lok = ld.getLok();
      if (lok != null) {
        if (!this.loks.add(lok)) {
          throw new IllegalArgumentException("Lok " + lok.getId() + " ist mehreren Lokdecodern zugeordnet");
        }

        registerAdressen(ld);
      }
    }

    for (Lokcontroller lc : this.lokcontroller) {
      registerAdressen(lc);
    }
  }

  public void assignLokcontroller(String lokcontrollerId, String lokId) {
    Lokcontroller lokcontroller = getLokcontroller(lokcontrollerId);
    if (lokcontroller == null) {
      throw new IllegalArgumentException("Lokcontroller nicht gefunden: " + lokcontrollerId);
    }

    Lok lok = null;
    if (lokId != null) {
      lok = getLok(lokId);
      if (lok == null) {
        throw new IllegalArgumentException("Lok nicht gefunden: " + lokId);
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

    for (Lokdecoder ld : this.lokdecoder) {
      buf.append("\n  ").append(ld);
      if (ld.getLok() != null) {
        buf.append("\n    ").append(ld.getLok());
      }
    }

    for (Lokcontroller ld : this.lokcontroller) {
      buf.append("\n  ").append(ld);
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
        this.selectrixGateway.setValue(adr, wert);
      }

      if (this.kanalBausteine[adr] != null) {
        this.kanalBausteine[adr].adjustWert(adr, wert);
      }

      EventFirer.getInstance().fire(new Kanal(adr, wert));
    }
  }

  public void onMessage(SelectrixMessage message) {
    setKanalWert(message.getAddress(), message.getValue(), false);
  }

  @XmlAccessorType(XmlAccessType.NONE)
  @Getter
  public static class SxInterface {
    @XmlAttribute
    private String typ;

    @XmlAttribute
    private int speed;
  }
}
