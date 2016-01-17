package de.gedoplan.v5t11.betriebssteuerung.steuerung;

import de.gedoplan.v5t11.betriebssteuerung.event.EventFirer;
import de.gedoplan.v5t11.betriebssteuerung.qualifier.Outbound;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.Besetztmelder;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.Funktionsdecoder;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.LokController;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.Lokdecoder;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.Zentrale;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.besetztmelder.BMMiba3;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.besetztmelder.SXBM1;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.funktionsdecoder.SD8;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.funktionsdecoder.STRFD1;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.funktionsdecoder.SXSD1;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.funktionsdecoder.WDMiba;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.funktionsdecoder.WDMiba3;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrstrasse.Fahrstrasse;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrstrasse.FahrstrassenElement;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrstrasse.element.FahrstrassenGleisabschnitt;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrstrasse.element.FahrstrassenWeiche;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg.Geraet;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg.Gleisabschnitt;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg.geraet.Signal;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg.geraet.Weiche;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.konfiguration.AutoFahrstrassenKonfiguration;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.konfiguration.BahnuebergangKonfiguration;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.konfiguration.BlockstellenKonfiguration;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.konfiguration.VorsignalKonfiguration;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.lok.Lok;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.stellwerk.Stellwerk;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.stellwerk.StellwerkElement;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.stellwerk.StellwerkZeile;
import de.gedoplan.v5t11.selectrix.SelectrixMessage;
import de.gedoplan.v5t11.selectrix.SelectrixMessageListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.enterprise.inject.Typed;
import javax.enterprise.util.AnnotationLiteral;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Steuerung.
 *
 * Diese Klasse fasst alle zur Steuerung gehörenden Elemente zusammen.
 *
 * @author dw
 */
@XmlRootElement(name = "v5t11")
@XmlAccessorType(XmlAccessType.NONE)
@Typed
public class Steuerung implements SelectrixMessageListener, Serializable
{
  @XmlElement(name = "Zentrale")
  Zentrale                                            zentrale;

  @XmlElement(name = "Lok")
  SortedSet<Lok>                                      loks                            = new TreeSet<>();

  @XmlElement(name = "LokController")
  SortedSet<LokController>                            lokController                   = new TreeSet<>();

  @XmlElementWrapper(name = "Besetztmelder")
  @XmlElements({ @XmlElement(name = "BMMiba3", type = BMMiba3.class), @XmlElement(name = "SXBM1", type = SXBM1.class) })
  SortedSet<Besetztmelder>                            besetztmelder                   = new TreeSet<>();

  @XmlElementWrapper(name = "Funktionsdecoder")
  @XmlElements({ @XmlElement(name = "SD8", type = SD8.class), @XmlElement(name = "STRFD1", type = STRFD1.class), @XmlElement(name = "SXSD1", type = SXSD1.class),
      @XmlElement(name = "WDMiba", type = WDMiba.class), @XmlElement(name = "WDMiba3", type = WDMiba3.class) })
  private SortedSet<Funktionsdecoder>                 funktionsdecoder                = new TreeSet<>();

  @XmlElement(name = "Blockstelle")
  SortedSet<BlockstellenKonfiguration>                blockstellenKonfigurationen     = new TreeSet<>();

  @XmlElement(name = "Bahnuebergang")
  SortedSet<BahnuebergangKonfiguration>               bahnuebergangKonfigurationen    = new TreeSet<>();

  @XmlElement(name = "Vorsignal")
  SortedSet<VorsignalKonfiguration>                   vorsignalKonfigurationen        = new TreeSet<>();

  @XmlElementWrapper(name = "Fahrstrassen")
  @XmlElement(name = "Fahrstrasse")
  private SortedSet<Fahrstrasse>                      fahrstrassen                    = new TreeSet<>();

  private Map<Gleisabschnitt, SortedSet<Fahrstrasse>> fahrstrassenLookupMap           = new HashMap<>();

  @XmlElement(name = "Autofahrstrasse")
  SortedSet<AutoFahrstrassenKonfiguration>            autoFahrstrassenKonfigurationen = new TreeSet<>();

  @XmlElementWrapper(name = "Stellwerke")
  @XmlElement(name = "Stellwerk")
  private SortedSet<Stellwerk>                        stellwerke                      = new TreeSet<>();

  private SortedSet<String>                           bereiche                        = new TreeSet<>();
  private SortedSet<Gleisabschnitt>                   gleisabschnitte                 = new TreeSet<>();
  private SortedSet<Signal>                           signale                         = new TreeSet<>();
  private SortedSet<Weiche>                           weichen                         = new TreeSet<>();

  private static final Log                            LOGGER                          = LogFactory.getLog(Steuerung.class);

  /**
   * Alle von der Steuerung belegten Selectrix-Adressen liefern.
   *
   * @return Adressen
   */
  public List<Integer> getAdressen()
  {
    List<Integer> adressen = new ArrayList<>();

    adressen.addAll(this.zentrale.getAdressen());

    for (Lok lok : this.loks)
    {
      adressen.add(lok.getAdresse());
    }

    for (LokController lokController : this.lokController)
    {
      adressen.add(lokController.getAdresse());
    }

    for (Besetztmelder bm : this.besetztmelder)
    {
      adressen.addAll(bm.getAdressen());
    }

    for (Funktionsdecoder fd : this.funktionsdecoder)
    {
      adressen.addAll(fd.getAdressen());
    }

    return adressen;
  }

  /**
   * Wert liefern: {@link #zentrale}.
   *
   * @return Wert
   */
  public Zentrale getZentrale()
  {
    return this.zentrale;
  }

  /**
   * Wert liefern: {@link #bereiche}.
   *
   * @return Wert
   */
  public Set<String> getBereiche()
  {
    return this.bereiche;
  }

  /**
   * Wert liefern: {@link #loks}.
   *
   * @return Wert
   */
  public Set<Lok> getLoks()
  {
    return this.loks;
  }

  /**
   * Alle Lokdecoder liefern.
   *
   * @return Lokdecoder
   */
  public Set<Lokdecoder> getLokdecoder()
  {
    Set<Lokdecoder> lokdecoder = new HashSet<>();
    for (Lok lok : this.loks)
    {
      lokdecoder.add(lok.getDecoder());
    }
    return lokdecoder;
  }

  /**
   * Lok liefern.
   *
   * @param adresse Adresse
   * @return gefundene Lok oder <code>null</code>
   */
  public Lok getLok(int adresse)
  {
    for (Lok lok : this.loks)
    {
      if (lok.getAdresse() == adresse)
      {
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
  public SortedSet<LokController> getLokController()
  {
    return this.lokController;
  }

  /**
   * Wert liefern: {@link #lokController}.
   *
   * @return Wert
   */
  public LokController getLokController(String id)
  {
    for (LokController l : this.lokController)
    {
      if (id.equals(l.getId()))
      {
        return l;
      }
    }

    return null;
  }

  /**
   * Wert liefern: {@link #besetztmelder}.
   *
   * @return Wert
   */
  public Set<Besetztmelder> getBesetztmelder()
  {
    return this.besetztmelder;
  }

  /**
   * Wert liefern: {@link #funktionsdecoder}.
   *
   * @return Wert
   */
  public Set<Funktionsdecoder> getFunktionsdecoder()
  {
    return this.funktionsdecoder;
  }

  /**
   * Wert liefern: {@link #gleisabschnitte}.
   *
   * @return Wert
   */
  public Set<Gleisabschnitt> getGleisabschnitte()
  {
    return this.gleisabschnitte;
  }

  /**
   * Wert liefern: {@link #gleisabschnitte}.
   *
   * @return Wert
   */
  public Set<Gleisabschnitt> getGleisabschnitte(String bereich)
  {
    return getBereichselemente(bereich, this.gleisabschnitte);
  }

  /**
   * Gleisabschnitt liefern.
   *
   * @param bereich Bereich
   * @param name Name
   * @return gefundener Gleisabschnitt oder <code>null</code>
   */
  public Gleisabschnitt getGleisabschnitt(String bereich, String name)
  {
    return getBereichselement(bereich, name, this.gleisabschnitte);
  }

  /**
   * Wert liefern: {@link #signale}.
   *
   * @return Wert
   */
  public Set<Signal> getSignale()
  {
    return this.signale;
  }

  /**
   * Wert liefern: {@link #signale}.
   *
   * @return Wert
   */
  public Set<Signal> getSignale(String bereich)
  {
    return getBereichselemente(bereich, this.signale);
  }

  /**
   * Signal liefern.
   *
   * @param bereich Bereich
   * @param name Name
   * @return gefundenes Signal oder <code>null</code>
   */
  public Signal getSignal(String bereich, String name)
  {
    return getBereichselement(bereich, name, this.signale);
  }

  /**
   * Wert liefern: {@link #weichen}.
   *
   * @return Wert
   */
  public Set<Weiche> getWeichen()
  {
    return this.weichen;
  }

  /**
   * Wert liefern: {@link #weichen}.
   *
   * @return Wert
   */
  public Set<Weiche> getWeichen(String bereich)
  {
    return getBereichselemente(bereich, this.weichen);
  }

  /**
   * Weiche liefern.
   *
   * @param bereich Bereich
   * @param name Name
   * @return gefundene Weiche oder <code>null</code>
   */
  public Weiche getWeiche(String bereich, String name)
  {
    return getBereichselement(bereich, name, this.weichen);
  }

  /**
   * Wert liefern: {@link #blockstellenKonfigurationen}.
   *
   * @return Wert
   */
  public SortedSet<BlockstellenKonfiguration> getBlockstellenKonfigurationen()
  {
    return this.blockstellenKonfigurationen;
  }

  /**
   * Wert liefern: {@link #bahnuebergangKonfigurationen}.
   *
   * @return Wert
   */
  public SortedSet<BahnuebergangKonfiguration> getBahnuebergangKonfigurationen()
  {
    return this.bahnuebergangKonfigurationen;
  }

  /**
   * Wert liefern: {@link #vorsignalKonfigurationen}.
   *
   * @return Wert
   */
  public SortedSet<VorsignalKonfiguration> getVorsignalKonfigurationen()
  {
    return this.vorsignalKonfigurationen;
  }

  /**
   * Wert liefern: {@link #fahrstrassen}.
   *
   * @return Wert
   */
  public SortedSet<Fahrstrasse> getFahrstrassen()
  {
    return this.fahrstrassen;
  }

  public SortedSet<Fahrstrasse> getFahrstrassen4Start(Gleisabschnitt gleisabschnitt)
  {
    SortedSet<Fahrstrasse> fahrstrassen = this.fahrstrassenLookupMap.get(gleisabschnitt);
    if (fahrstrassen != null)
    {
      return fahrstrassen;
    }
    else
    {
      return Collections.emptySortedSet();
    }
  }

  void add2FahrstrassenLookupMap(Iterable<Fahrstrasse> fahrstrassen)
  {
    fahrstrassen.forEach(f -> add2FahrstrassenLookupMap(f));
  }

  void add2FahrstrassenLookupMap(Fahrstrasse fahrstrasse)
  {
    Gleisabschnitt start = fahrstrasse.getFirst().getFahrwegelement();

    SortedSet<Fahrstrasse> fahrstrassen = this.fahrstrassenLookupMap.get(start);
    if (fahrstrassen == null)
    {
      fahrstrassen = new TreeSet<>();
      this.fahrstrassenLookupMap.put(start, fahrstrassen);
    }

    fahrstrassen.add(fahrstrasse);
  }

  void removeFromFahrstrassenLookupMap(Iterable<Fahrstrasse> fahrstrassen)
  {
    fahrstrassen.forEach(f -> removeFromFahrstrassenLookupMap(f));
  }

  void removeFromFahrstrassenLookupMap(Fahrstrasse fahrstrasse)
  {
    Gleisabschnitt start = fahrstrasse.getFirst().getFahrwegelement();

    SortedSet<Fahrstrasse> fahrstrassen = this.fahrstrassenLookupMap.get(start);
    if (fahrstrassen != null)
    {
      fahrstrassen.remove(fahrstrasse);
    }
  }

  /**
   * Wert liefern: {@link #autoFahrstrassenKonfigurationen}.
   *
   * @return Wert
   */
  public SortedSet<AutoFahrstrassenKonfiguration> getAutoFahrstrassenKonfigurationen()
  {
    return this.autoFahrstrassenKonfigurationen;
  }

  /**
   * Fahrstrasse liefern.
   *
   * @param bereich Bereich
   * @param name Name
   * @return gefundene Fahrstrasse oder <code>null</code>
   */
  public Fahrstrasse getFahrstrasse(String bereich, String name)
  {
    return getBereichselement(bereich, name, this.fahrstrassen);
  }

  /**
   * Stellwerk liefern.
   *
   * @param bereich Bereich
   * @return Stellwerk
   */
  public Stellwerk getStellwerk(String bereich)
  {
    for (Stellwerk stellwerk : this.stellwerke)
    {
      if (bereich.equals(stellwerk.getBereich()))
      {
        return stellwerk;
      }
    }

    return null;
  }

  private static <E extends Bereichselement> SortedSet<E> getBereichselemente(String bereich, SortedSet<E> bereichselemente)
  {
    SortedSet<E> result = new TreeSet<>();
    for (E element : bereichselemente)
    {
      if (bereich.equals(element.getBereich()))
      {
        result.add(element);
      }
    }

    return result;
  }

  private static <T extends Bereichselement> T getBereichselement(String bereich, String name, Collection<T> set)
  {
    for (T element : set)
    {
      if (element.getBereich().equals(bereich) && element.getName().equals(name))
      {
        return element;
      }
    }

    return null;
  }

  @Override
  public void onMessage(SelectrixMessage message)
  {
    this.zentrale.onMessage(message);

    for (Besetztmelder bm : this.besetztmelder)
    {
      bm.onMessage(message);
    }

    for (Funktionsdecoder fd : this.funktionsdecoder)
    {
      fd.onMessage(message);
    }

    for (Lok lok : this.loks)
    {
      lok.onMessage(message);
    }

    for (LokController lc : this.lokController)
    {
      lc.onMessage(message);
    }
  }

  /**
   * Wert setzen.
   *
   * @param wert Wert
   * @param adresse Adresse
   */
  public void setWert(int adresse, int wert)
  {
    if (LOGGER.isTraceEnabled())
    {
      LOGGER.trace(String.format("setWert(%d, 0x%02x)", adresse, wert));
    }

    EventFirer.fireEvent(new SelectrixMessage(adresse, wert), new AnnotationLiteral<Outbound>()
    {});
  }

  /**
   * Nachbearbeitung nach JAXB-Unmarshal.
   *
   * @param unmarshaller Unmarshaller
   * @param parent Parent
   */
  @SuppressWarnings("unused")
  private void afterUnmarshal(Unmarshaller unmarshaller, Object parent)
  {
    if (this.zentrale == null)
    {
      this.zentrale = new Zentrale();
      this.zentrale.afterUnmarshal(unmarshaller, this);
    }

    /*
     * Den Besetztmeldern zugeordnete Gleisabschnitte in this.gleisabschnitte sammeln. Dabei auch die Bereiche in this.bereiche
     * eintragen.
     */
    for (Besetztmelder bm : this.besetztmelder)
    {
      for (Gleisabschnitt g : bm.getGleisabschnitte())
      {
        this.gleisabschnitte.add(g);
        this.bereiche.add(g.getBereich());
      }
    }

    /*
     * Den Funktionsdecodern zugeordnete Weichen und Signale in this.weichen und this.signale sammeln. Dabei auch die Bereiche in
     * this.bereiche eintragen.
     */
    for (Funktionsdecoder fd : this.funktionsdecoder)
    {
      for (Geraet g : fd.getGeraete())
      {
        if (g instanceof Signal)
        {
          this.signale.add((Signal) g);
        }

        if (g instanceof Weiche)
        {
          this.weichen.add((Weiche) g);
        }

        this.bereiche.add(g.getBereich());
      }
    }

    // Blocksignale als solche markieren. Dabei ungültige Konfigurationen entfernen.
    Iterator<BlockstellenKonfiguration> blockstellenIterator = this.blockstellenKonfigurationen.iterator();
    while (blockstellenIterator.hasNext())
    {
      BlockstellenKonfiguration blockstellenKonfiguration = blockstellenIterator.next();
      Signal signal = blockstellenKonfiguration.getSignal();
      Gleisabschnitt gleisabschnitt = blockstellenKonfiguration.getGleisabschnitt();
      if (signal == null || gleisabschnitt == null)
      {
        blockstellenIterator.remove();
      }
      else
      {
        signal.setBlock(true);
      }
    }

    // Ungültige Bahnuebergangskonfigurationen entfernen.
    Iterator<BahnuebergangKonfiguration> bahnuebergangIterator = this.bahnuebergangKonfigurationen.iterator();
    while (bahnuebergangIterator.hasNext())
    {
      BahnuebergangKonfiguration bahnuebergangKonfiguration = bahnuebergangIterator.next();
      if (bahnuebergangKonfiguration.getBahnuebergang() == null || bahnuebergangKonfiguration.getGleisabschnitte().isEmpty())
      {
        bahnuebergangIterator.remove();
      }
    }

    // Fahrstrassen komplettieren. Dabei auch die Bereiche in this.bereiche eintragen.
    for (Fahrstrasse fahrstrasse : this.fahrstrassen)
    {
      String bereich = fahrstrasse.getBereich();
      for (FahrstrassenElement fahrstrassenElement : fahrstrasse.getElemente())
      {
        // Zugehöriges Fahrwegelement setzen
        fahrstrassenElement.setFahrwegelement(this);

        this.bereiche.add(fahrstrassenElement.getBereich());
      }

      this.bereiche.add(bereich);
    }

    // Fahrstrassen-Lookup-Map erstellen
    add2FahrstrassenLookupMap(this.fahrstrassen);

    // Aus Fahrstrassen Vorgänger/Nachfolger für die enthaltenen Gleisabschnitte errechnen
    this.fahrstrassen.forEach(f -> deriveFolgeGleisabschnitte(f));

    // Fahrstrassen kombinieren
    concatFahrstrassen();

    // Nicht nutzbare Fahrstrassen entfernen
    Set<Fahrstrasse> unusableFahrstrassen = this.fahrstrassen
        .stream()
        .filter(f -> !f.isZugfahrtGeeignet() && !f.isRangierGeeignet())
        .collect(Collectors.toSet());
    this.fahrstrassen.removeAll(unusableFahrstrassen);
    removeFromFahrstrassenLookupMap(unusableFahrstrassen);

    // Doppeleinträge in Fahrstrassen eliminieren und Signale auf Langsamfahrt korrigieren, wenn nötig..
    this.fahrstrassen.forEach(f -> {
      f.removeDoppeleintraege();
      f.adjustLangsamfahrt();
    });

    // Vorsignale hinzufügen.
    this.fahrstrassen.forEach(f -> f.addVorsignale(this));

    // Ungültige Autofahrstrassen-Konfigurationen entfernen.
    Iterator<AutoFahrstrassenKonfiguration> autoFahrstrassenIterator = this.autoFahrstrassenKonfigurationen.iterator();
    while (autoFahrstrassenIterator.hasNext())
    {
      AutoFahrstrassenKonfiguration autoFahrstrassenKonfiguration = autoFahrstrassenIterator.next();
      if (autoFahrstrassenKonfiguration.getGleisabschnitt() == null || autoFahrstrassenKonfiguration.getZielGleisabschnitte().isEmpty())
      {
        autoFahrstrassenIterator.remove();
      }
    }

    for (String bereich : getBereiche())
    {
      Stellwerk stellwerk = getStellwerk(bereich);
      for (StellwerkZeile zeile : stellwerk.getZeilen())
      {
        for (StellwerkElement element : zeile.getElemente())
        {
          if (element.getBereich() == null)
          {
            element.setBereich(bereich);
          }
        }
      }
    }

    // Alle Listener registrieren
    addValueChangedListeners();
  }

  private void concatFahrstrassen()
  {
    SortedSet<Fahrstrasse> zuPruefendeFahrstrassen = this.fahrstrassen;
    while (true)
    {
      SortedSet<Fahrstrasse> weitereFahrstrassen = new TreeSet<>();
      for (Fahrstrasse fahrstrasse1 : zuPruefendeFahrstrassen)
      {
        for (Fahrstrasse fahrstrasse2 : getFahrstrassen4Start(fahrstrasse1.getLast().getFahrwegelement()))
        {
          Fahrstrasse kombiFahrstrasse = Fahrstrasse.concat(fahrstrasse1, fahrstrasse2);
          if (kombiFahrstrasse != null && !this.fahrstrassen.contains(kombiFahrstrasse))
          {
            weitereFahrstrassen.add(kombiFahrstrasse);
          }
        }
      }

      if (weitereFahrstrassen.isEmpty())
      {
        break;
      }

      this.fahrstrassen.addAll(weitereFahrstrassen);
      add2FahrstrassenLookupMap(weitereFahrstrassen);

      zuPruefendeFahrstrassen = weitereFahrstrassen;
    }
  }

  void deriveFolgeGleisabschnitte(Fahrstrasse fahrstrasse)
  {
    FahrstrassenGleisabschnitt fahrstrassenGleisabschnitt = null;
    FahrstrassenWeiche fahrstrassenWeiche1 = null;
    FahrstrassenWeiche fahrstrassenWeiche2 = null;

    for (FahrstrassenElement fahrstrassenElement : fahrstrasse.getElemente())
    {
      if (fahrstrassenElement instanceof FahrstrassenGleisabschnitt)
      {
        // Nächster Gleisabschnitt
        FahrstrassenGleisabschnitt nextFahrstrassenGleisabschnitt = (FahrstrassenGleisabschnitt) fahrstrassenElement;

        // Gibt es schon einen vorigen Gleisabschnitt?
        if (fahrstrassenGleisabschnitt != null)
        {
          if (fahrstrassenWeiche2 == null)
          {
            fahrstrassenWeiche2 = fahrstrassenWeiche1;
          }

          // Routing in Richtung des Gleisabschnitts eintragen
          fahrstrassenGleisabschnitt.getFahrwegelement().addFolgeGleisabschnitt(
              fahrstrassenGleisabschnitt.isZaehlrichtung(),
              fahrstrassenWeiche1,
              nextFahrstrassenGleisabschnitt);

          // Routing in Gegenrichtung des Folge-Gleisabschnitts eintragen
          nextFahrstrassenGleisabschnitt.getFahrwegelement().addFolgeGleisabschnitt(
              !nextFahrstrassenGleisabschnitt.isZaehlrichtung(),
              fahrstrassenWeiche2,
              fahrstrassenGleisabschnitt);
        }

        // Abschnitt für's nächste Mal merken
        fahrstrassenGleisabschnitt = nextFahrstrassenGleisabschnitt;
        fahrstrassenWeiche1 = null;
        fahrstrassenWeiche2 = null;
      }

      if (fahrstrassenElement instanceof FahrstrassenWeiche && !fahrstrassenElement.isSchutz())
      {
        // Weiche merken
        if (fahrstrassenWeiche1 == null)
        {
          fahrstrassenWeiche1 = (FahrstrassenWeiche) fahrstrassenElement;
        }
        else if (fahrstrassenWeiche2 == null)
        {
          fahrstrassenWeiche2 = (FahrstrassenWeiche) fahrstrassenElement;
        }
        else
        {
          // Es dürfen maximal zwei Weichen zwischen zwei Gleisabschnitten liegen
          throw new IllegalArgumentException(fahrstrasse + " hat zu viele Weichen nach " + fahrstrassenGleisabschnitt);
        }
      }
    }

  }

  /**
   * Textliche Repräsentation der Steuerung erstellen.
   *
   * @param idOnly nur IDs?
   * @return Steuerung als String
   */
  public String toDebugString(boolean idOnly)
  {
    StringBuilder buf = new StringBuilder("Steuerung");
    for (Lok lok : this.loks)
    {
      buf.append("\n  ").append(lok.toDebugString(idOnly));
    }

    for (Besetztmelder bm : this.besetztmelder)
    {
      buf.append("\n  ").append(bm);
      for (Gleisabschnitt g : bm.getGleisabschnitte())
      {
        buf.append("\n    ").append(g);
      }
      buf.append("\n    Properties: ").append(bm.getProperties());
    }

    for (Funktionsdecoder fd : this.funktionsdecoder)
    {
      buf.append("\n  ").append(fd);
      for (Geraet g : fd.getGeraete())
      {
        buf.append("\n    ").append(g);
      }
      buf.append("\n    Properties: ").append(fd.getProperties());
    }

    for (Fahrstrasse fahrstrasse : this.fahrstrassen)
    {
      buf.append("\n  ").append(fahrstrasse);
      for (FahrstrassenElement fahrstrassenElement : fahrstrasse.getElemente())
      {
        buf.append("\n    ").append(fahrstrassenElement);
      }
    }

    for (Stellwerk stellwerk : this.stellwerke)
    {
      buf.append("\n  ").append(stellwerk);
      for (StellwerkZeile zeile : stellwerk.getZeilen())
      {
        buf.append("\n    Zeile");
        for (StellwerkElement element : zeile.getElemente())
        {
          buf.append("\n      " + element);
        }
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
  public String toString()
  {
    return toDebugString(true);
  }

  /**
   * Freie Fahrstrassen suchen.
   *
   * Die angegebenen Gleisabschnitte dürfen belegt sein, alle anderen müssen frei sein.
   *
   * @param beginn Beginn-Gleisabschnitt
   * @param ende Ende-Gleisabschnitt
   * @return Liste freie Fahrstrassen
   */
  public List<Fahrstrasse> getFreieFahrstrassen(Gleisabschnitt beginn, Gleisabschnitt ende)
  {
    return getFreieFahrstrassen(beginn, false, ende, false);
  }

  /**
   * Freie Fahrstrassen suchen.
   *
   * Die angegebenen Gleisabschnitte dürfen belegt sein, alle anderen müssen frei sein.
   *
   * @param beginn Beginn-Gleisabschnitt
   * @param beginnMussFreiSein Beginn-Gleisabschnitt muss frei sein
   * @param ende Ende-Gleisabschnitt
   * @param endeMussFreiSein Ende-Gleisabschnitt muss frei sein
   * @return Liste freie Fahrstrassen
   */
  public List<Fahrstrasse> getFreieFahrstrassen(Gleisabschnitt beginn, boolean beginnMussFreiSein, Gleisabschnitt ende, boolean endeMussFreiSein)
  {
    List<Fahrstrasse> freieFahrstrassen = new ArrayList<>();
    for (Fahrstrasse fahrstrasse : this.fahrstrassen)
    {
      if (fahrstrasse.startsWith(beginn) && fahrstrasse.endsWith(ende))
      {
        if (fahrstrasse.isFrei(beginnMussFreiSein, endeMussFreiSein) && fahrstrasse.isReservierbar())
        {
          freieFahrstrassen.add(fahrstrasse);
        }
      }
    }

    Collections.sort(freieFahrstrassen, new Comparator<Fahrstrasse>()
    {
      @Override
      public int compare(Fahrstrasse f1, Fahrstrasse f2)
      {
        return f1.getRank() - f2.getRank();
      }
    });

    return freieFahrstrassen;
  }

  /**
   * Alle ValueChangedListener registrieren.
   */
  public void addValueChangedListeners()
  {
    for (Funktionsdecoder funktionsdecoder : this.funktionsdecoder)
    {
      funktionsdecoder.addValueChangedListeners();
    }

    for (Besetztmelder besetztmelder : this.besetztmelder)
    {
      besetztmelder.addValueChangedListeners();
    }
  }

}
