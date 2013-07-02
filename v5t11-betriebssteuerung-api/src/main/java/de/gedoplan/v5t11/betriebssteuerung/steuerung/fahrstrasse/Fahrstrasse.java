package de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrstrasse;

import de.gedoplan.v5t11.betriebssteuerung.steuerung.Bereichselement;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.Steuerung;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrstrasse.FahrstrassenElement.Rank;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrstrasse.element.FahrstrassenBahnuebergang;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrstrasse.element.FahrstrassenGleisabschnitt;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrstrasse.element.FahrstrassenSignal;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrstrasse.element.FahrstrassenWeiche;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg.Fahrwegelement;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg.Gleisabschnitt;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg.geraet.Hauptsignal;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg.geraet.Signal;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.konfiguration.VorsignalKonfiguration;
import de.gedoplan.v5t11.betriebssteuerung.util.GbsFarben;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Fahrstrasse.
 * 
 * @author dw
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@JsonAutoDetect(JsonMethod.NONE)
public class Fahrstrasse extends Bereichselement
{
  /**
   * In Zählrichtung orientiert?
   */
  @XmlAttribute
  @JsonProperty
  private boolean                   zaehlrichtung;

  /**
   * Aus anderen Fahrstrassen kombiniert?
   */
  @XmlAttribute
  @JsonProperty
  private boolean                   combi;

  /**
   * Ranking (für Auswahl aus Alternativen).
   */
  @XmlAttribute
  @JsonProperty
  private int                       rank;

  /**
   * Liste der Fahrstrassenelemente. Beginnt und endet immer mit einem FahrstrassenGleisabschnitt.
   */
  @XmlElements({ @XmlElement(name = "Bahnuebergang", type = FahrstrassenBahnuebergang.class), @XmlElement(name = "Gleisabschnitt", type = FahrstrassenGleisabschnitt.class),
      @XmlElement(name = "Signal", type = FahrstrassenSignal.class), @XmlElement(name = "Weiche", type = FahrstrassenWeiche.class) })
  @JsonProperty
  private List<FahrstrassenElement> elemente = new ArrayList<FahrstrassenElement>();

  /**
   * Falls reserviert Typ der Reservierung, sonst <code>null</code>.
   */
  private ReservierungsTyp          reservierungsTyp;

  /**
   * Wert liefern: {@link #combi}.
   * 
   * @return Wert
   */
  public boolean isCombi()
  {
    return this.combi;
  }

  /**
   * Wert liefern: {@link #rank}.
   * 
   * @return Wert
   */
  public int getRank()
  {
    return this.rank;
  }

  /**
   * Wert liefern: {@link #elemente}.
   * 
   * @return Wert
   */
  public List<FahrstrassenElement> getElemente()
  {
    return this.elemente;
  }

  /**
   * Wert liefern: {@link #reservierungsTyp}.
   * 
   * @return Wert
   */
  public ReservierungsTyp getReservierungsTyp()
  {
    return this.reservierungsTyp;
  }

  /**
   * Erstes Element liefern.
   * 
   * @return erstes Element oder <code>null</code>, wenn Fahrstrasse leer
   */
  public FahrstrassenElement getFirst()
  {
    if (this.elemente.size() != 0)
    {
      return this.elemente.get(0);
    }

    return null;
  }

  /**
   * Letztes Element liefern.
   * 
   * @return letztes Element oder <code>null</code>, wenn Fahrstrasse leer
   */
  public FahrstrassenElement getLast()
  {
    int size = this.elemente.size();
    if (size != 0)
    {
      return this.elemente.get(size - 1);
    }

    return null;
  }

  @Override
  public String toString()
  {
    StringBuilder buf = new StringBuilder(this.getClass().getSimpleName());
    buf.append("{bereich=");
    buf.append(this.bereich);
    buf.append(",name=");
    buf.append(this.name);
    buf.append("}");

    return buf.toString();
  }

  /**
   * Doppeleinträge in der Fahrstrasse eliminieren.
   */
  public void removeDoppeleintraege()
  {
    int i = 0;
    while (i < this.elemente.size())
    {
      FahrstrassenElement element = this.elemente.get(i);

      while (true)
      {
        int i2 = this.elemente.lastIndexOf(element);
        if (i2 <= i)
        {
          // kein Doppelvorkommen; weiter mit nächstem Eintrag
          ++i;
          break;
        }

        if (element.isSchutz())
        {
          // aktuelles Element ist Schutzelement; löschen, 2. Eintrag bestehen lassen, weiter mit nächstem Eintrag (der dann am
          // gleichen Index steht!)
          this.elemente.remove(i);
          break;
        }
        else
        {
          // aktuelles Element ist kein Schutzelement; 2. Eintrag löschen, weiter nach Doppelvorkommen suchen
          this.elemente.remove(i2);
        }
      }
    }
  }

  /**
   * Ist die Fahrstrasse (komplett) frei?
   * 
   * @param includeFirst Erstes Element der Fahrstrasse berücksichtigen?
   * @param includeLast Letztes Element der Fahrstrasse berücksichtigen?
   * @return <code>true</code>, wenn die Gleisabschnitte der Fahrstrasse frei sind.
   */
  public boolean isFrei(boolean includeFirst, boolean includeLast)
  {
    int elementCount = this.elemente.size();
    for (int index = 0; index < elementCount; ++index)
    {
      if (!includeFirst && index == 0)
      {
        continue;
      }

      if (!includeLast && index == elementCount - 1)
      {
        continue;
      }

      FahrstrassenElement element = this.elemente.get(index);
      Fahrwegelement fahrwegelement = element.getFahrwegelement();
      if (fahrwegelement instanceof Gleisabschnitt && ((Gleisabschnitt) fahrwegelement).isBesetzt())
      {
        return false;
      }
    }

    return true;
  }

  /**
   * Ist die Fahrstrasse reservierbar?
   * 
   * @return <code>true</code>, wenn keines der zugeordneten Fahrwegelemente durch eine andere Fahrstrasse reserviert ist,
   *         Schutzelemente ausgenommen.
   */
  public boolean isReservierbar()
  {
    for (FahrstrassenElement element : this.elemente)
    {
      if (!element.isSchutz())
      {
        if (element.getFahrwegelement().getReservierteFahrstrasse() != null)
        {
          return false;
        }
      }
    }

    return true;
  }

  // /**
  // * Kollidiert die Fahrstrasse mit einer anderen?
  // *
  // * @param andereFahrstrasse andere Fahrstrasse
  // * @return <code>true</code>, wenn eine Kollision vorliegt
  // */
  // public boolean kollidiertMit(Fahrstrasse andereFahrstrasse)
  // {
  // for (FahrstrassenElement element : this.elemente)
  // {
  // int i = andereFahrstrasse.elemente.indexOf(element);
  // if (i >= 0)
  // {
  // FahrstrassenElement anderesElement = andereFahrstrasse.elemente.get(i);
  // if (!element.isSchutz() && !anderesElement.isSchutz())
  // {
  // return true;
  // }
  // }
  // }
  // return false;
  // }

  /**
   * Beginnt die Fahrstrasse mit dem angegebenen Gleisabschnitt?
   * 
   * @param gleisabschnitt Gleisabschnitt
   * @return <code>true</code>, wenn die Fahrstrasse mit dem angegebenen Gleisabschnitt beginnt
   */
  public boolean startsWith(Gleisabschnitt gleisabschnitt)
  {
    FahrstrassenElement first = getFirst();
    return first instanceof FahrstrassenGleisabschnitt && gleisabschnitt.equals(first.getFahrwegelement());
  }

  /**
   * Endet die Fahrstrasse mit dem angegebenen Gleisabschnitt?
   * 
   * @param gleisabschnitt Gleisabschnitt
   * @return <code>true</code>, wenn die Fahrstrasse mit dem angegebenen Gleisabschnitt endet
   */
  public boolean endsWith(Gleisabschnitt gleisabschnitt)
  {
    FahrstrassenElement last = getLast();
    return last instanceof FahrstrassenGleisabschnitt && gleisabschnitt.equals(last.getFahrwegelement());
  }

  /**
   * Konstruktor für interne Zwecke.
   */
  protected Fahrstrasse()
  {
  }

  /**
   * Fahrstrasse reservieren oder freigeben.
   * 
   * Wird als reservierungsTyp <code>null</code> übergeben, wird die Fahrstrasse freigegeben. Die Methode ist ähnlich aufrufbar
   * wie {@link FahrstrassenElement#reservieren(Fahrstrasse)}. Zur Freigabe kann aber auch {@link #freigeben(Gleisabschnitt)}
   * genutzt werden.
   * 
   * @param reservierungsTyp Art der Fahrstrassenreservierung, <code>null</code> für Freigabe
   */
  public void reservieren(ReservierungsTyp reservierungsTyp)
  {
    if (reservierungsTyp == null)
    {
      freigeben(null);
    }
    else
    {
      this.reservierungsTyp = reservierungsTyp;
      for (FahrstrassenElement element : this.elemente)
      {
        element.reservieren(this);
      }
    }
  }

  /**
   * Fahrstrasse komplett oder teilweise freigeben.
   * 
   * @param teilFreigabeEnde <code>null</code> für Komplettfreigabe oder erster Gleisabschnitt, der nicht freigegeben wird
   */
  public void freigeben(Gleisabschnitt teilFreigabeEnde)
  {
    if (teilFreigabeEnde == null)
    {
      this.reservierungsTyp = null;
    }

    for (FahrstrassenElement element : this.elemente)
    {
      if (teilFreigabeEnde != null && element instanceof FahrstrassenGleisabschnitt && teilFreigabeEnde.equals(element.getFahrwegelement()))
      {
        break;
      }
      element.reservieren(null);
    }

  }

  /**
   * Fahrstrasse vorschlagen.
   * 
   * @param vorgeschlagen vorgeschlagen?
   */
  public void vorschlagen(boolean vorgeschlagen)
  {
    for (FahrstrassenElement element : this.elemente)
    {
      element.vorschlagen(vorgeschlagen ? this : null);
    }
  }

  /**
   * Art der Fahrstrassenreservierung.
   * 
   * @author dw
   */
  public static enum ReservierungsTyp
  {
    /**
     * Zugfahrt.
     */
    ZUGFAHRT
    {
      @Override
      public Color getGbsFarbe()
      {
        return GbsFarben.GLEIS_IN_ZUGFAHRSTRASSE;
      }
    },

    /**
     * Rangierfahrt.
     */
    RANGIERFAHRT
    {
      @Override
      public Color getGbsFarbe()
      {
        return GbsFarben.GLEIS_IN_RANGIERFAHRSTRASSE;
      }
    };

    public abstract Color getGbsFarbe();
  }

  /**
   * Weichengleisabschnitte und Vorsignale hinzufügen.
   * 
   * Zu jeder Weiche, die nicht nur Schutzweiche ist, den zugehörigen Gleisabschnitt hinzufügen, wenn vorhanden.
   * 
   * Zu jedem Hauptsignal, das nicht Schutzsignal ist und das ein Vorsignal am gleichen Mast hat, dieses Vorsignal hinzufügen.
   * 
   * @param steuerung Steuerung
   */
  public void addWeichenGleisabschnitteUndVorsignale(Steuerung steuerung)
  {
    ListIterator<FahrstrassenElement> listIterator = this.elemente.listIterator();
    while (listIterator.hasNext())
    {
      FahrstrassenElement fahrstrassenElement = listIterator.next();

      if (fahrstrassenElement instanceof FahrstrassenWeiche && !fahrstrassenElement.isSchutz())
      {
        String weichenGleisabschnittName = "W" + fahrstrassenElement.getName();
        Gleisabschnitt gleisabschnitt = steuerung.getGleisabschnitt(fahrstrassenElement.getBereich(), weichenGleisabschnittName);
        if (gleisabschnitt != null)
        {
          FahrstrassenGleisabschnitt fahrstrassenGleisabschnitt = new FahrstrassenGleisabschnitt(gleisabschnitt, true);
          fahrstrassenGleisabschnitt.setZaehlrichtungIfNull(fahrstrassenElement.isZaehlrichtung());
          listIterator.add(fahrstrassenGleisabschnitt);
        }
      }

      if (fahrstrassenElement instanceof FahrstrassenSignal && !fahrstrassenElement.isSchutz())
      {
        Signal signal = ((FahrstrassenSignal) fahrstrassenElement).getFahrwegelement();
        if (signal != null && signal instanceof Hauptsignal)
        {
          for (VorsignalKonfiguration vorsignalKonfiguration : steuerung.getVorsignalKonfigurationen())
          {
            if (signal.equals(vorsignalKonfiguration.getHauptsignalAmGleichenMast()))
            {
              FahrstrassenSignal fahrstrassenSignal = new FahrstrassenSignal(vorsignalKonfiguration.getVorsignal());
              fahrstrassenSignal.setZaehlrichtungIfNull(fahrstrassenElement.isZaehlrichtung());
              listIterator.add(fahrstrassenSignal);
            }
          }
        }
      }
    }
  }

  /**
   * Enthält die Fahrstrasse das angegebene Fahrwegelement?
   * 
   * @param fahrwegelement Fahrwegelement
   * @return <code>true</code>, wenn die Fahrstrasse das angegebene Fahrwegelement enthält
   */
  public boolean enthaelt(Fahrwegelement fahrwegelement)
  {
    for (FahrstrassenElement fahrstrassenElement : this.elemente)
    {
      if (fahrwegelement.equals(fahrstrassenElement.getFahrwegelement()))
      {
        return true;
      }
    }
    return false;
  }

  /**
   * Fahrstrassen kombinieren.
   * 
   * Die angegebenen Fahrstrassen werden in der Reihenfolge links-rechts kombiniert, wenn dies möglich ist.
   * 
   * @param linkeFahrstrasse Linke Fahrstrasse (Einfahrt)
   * @param rechteFahrstrasse Rechte Fahrstrasse (Ausfahrt)
   * @return Kombi-Fahrstrasse, wenn Kombination möglich, sonst <code>null</code>
   */
  public static Fahrstrasse concat(Fahrstrasse linkeFahrstrasse, Fahrstrasse rechteFahrstrasse)
  {
    // Wenn Ende links nicht Anfang rechts, nicht kombinieren
    FahrstrassenElement linksLast = linkeFahrstrasse.getLast();
    FahrstrassenElement rechtsFirst = rechteFahrstrasse.getFirst();
    if (!linksLast.equals(rechtsFirst))
    {
      return null;
    }

    // Wenn Zählrichtung nicht passt, nicht kombinieren
    if (linksLast.isZaehlrichtung() != rechtsFirst.isZaehlrichtung())
    {
      return null;
    }

    // Wenn Ende rechts schon Teil der linken Fahrstrasse, nicht kombinieren
    if (linkeFahrstrasse.elemente.contains(rechteFahrstrasse.getLast()))
    {
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

    // Schutzsignale entfernen, die auch als normale Signale vorhanden sind
    Set<Signal> normaleSignale = new HashSet<>();
    for (FahrstrassenElement element : result.elemente)
    {
      if (element instanceof FahrstrassenSignal && !element.isSchutz())
      {
        Signal signal = ((FahrstrassenSignal) element).getFahrwegelement();
        if (signal != null)
        {
          normaleSignale.add(signal);
        }
      }
    }

    Iterator<FahrstrassenElement> iterator = result.elemente.iterator();
    while (iterator.hasNext())
    {
      FahrstrassenElement element = iterator.next();
      if (element instanceof FahrstrassenSignal && element.isSchutz())
      {
        Signal schutzSignal = ((FahrstrassenSignal) element).getFahrwegelement();
        if (schutzSignal != null && normaleSignale.contains(schutzSignal))
        {
          iterator.remove();
        }
      }
    }
    return result;
  }

  private static String createConcatName(String name1, String name2)
  {
    String[] part1 = name1.split("-");
    String[] part2 = name2.split("-");
    int i = 0;
    if (part1[part1.length - 1].endsWith(part2[0]))
    {
      ++i;
    }

    StringBuilder concatName = new StringBuilder(name1);
    while (i < part2.length)
    {
      concatName.append("-").append(part2[i]);
      ++i;
    }

    return concatName.toString();
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
    FahrstrassenBahnuebergang fahrstrassenBahnuebergang = null;
    FahrstrassenGleisabschnitt fahrstrassenGleisabschnitt = null;
    for (FahrstrassenElement element : getElemente())
    {
      // Bereich und Zählrichtung in Elemente übernehmen, wenn dort noch leer
      if (element.getBereich() == null)
      {
        element.setBereich(this.bereich);
      }

      element.setZaehlrichtungIfNull(this.zaehlrichtung);
    }

    // Falls nötig, Ranking berechnen
    if (this.rank <= 0)
    {
      this.rank = 0;
      for (FahrstrassenElement element : getElemente())
      {
        Rank elementRank = element.getRank();
        if (elementRank != null)
        {
          this.rank += elementRank.intValue();
        }
      }
    }

    // Namen zusammensetzen im Format Start-Ziel
    if (getName() == null)
    {
      StringBuilder name = new StringBuilder(getFirst().getName());
      String startBereich = getFirst().getBereich();
      if (!startBereich.equals(this.bereich))
      {
        name.append("(").append(startBereich).append(")");
      }
      name.append("-").append(getLast().getName());
      String endBereich = getLast().getBereich();
      if (!endBereich.equals(this.bereich))
      {
        name.append("(").append(endBereich).append(")");
      }
      setName(name.toString());
    }
  }
}
