package de.gedoplan.v5t11.status.entity.baustein;

import de.gedoplan.v5t11.status.entity.fahrweg.Geraet;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Bahnuebergang;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.HauptsignalRtGe;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.HauptsignalRtGn;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.HauptsignalRtGnGe;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Hauptsperrsignal;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Sperrsignal;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Vorsignal;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Weiche;

import java.util.SortedSet;
import java.util.TreeSet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Besetztmelder.
 *
 * @author dw
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Funktionsdecoder extends Baustein implements Decoder {
  /**
   * Zugeordnete Geräte.
   */
  @XmlElements({ @XmlElement(name = "Bahnuebergang", type = Bahnuebergang.class), @XmlElement(name = "HauptsignalRtGnGe", type = HauptsignalRtGnGe.class),
      @XmlElement(name = "HauptsignalRtGe", type = HauptsignalRtGe.class), @XmlElement(name = "HauptsignalRtGn", type = HauptsignalRtGn.class),
      @XmlElement(name = "Hauptsperrsignal", type = Hauptsperrsignal.class), @XmlElement(name = "Sperrsignal", type = Sperrsignal.class), @XmlElement(name = "Vorsignal", type = Vorsignal.class),
      @XmlElement(name = "Weiche", type = Weiche.class) })
  protected SortedSet<Geraet> geraete = new TreeSet<>();

  /**
   * Konstruktor.
   *
   * Wird nur während des JAXB-Unmarshalling aufgerufen.
   *
   * @param byteAnzahl
   *          Anzahl belegter Bytes (Adressen)
   */
  protected Funktionsdecoder(int byteAnzahl) {
    super(byteAnzahl);
  }

  @Override
  public String getLabelPrefix() {
    return "Funktionsdecoder";
  }

  @Override
  public void adjustStatus() {
    this.geraete.forEach(Geraet::adjustStatus);
  }

}
