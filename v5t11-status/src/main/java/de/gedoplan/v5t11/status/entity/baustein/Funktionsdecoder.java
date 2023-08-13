package de.gedoplan.v5t11.status.entity.baustein;

import de.gedoplan.baselibs.utils.inject.InjectionUtil;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Bahnuebergang;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.FunktionsdecoderGeraet;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.HauptsignalRtGe;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.HauptsignalRtGn;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.HauptsignalRtGnGe;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Hauptsperrsignal;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Schalter;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Sperrsignal;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Vorsignal;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Weiche;

import java.util.SortedSet;
import java.util.TreeSet;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;
import jakarta.xml.bind.annotation.XmlRootElement;

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
  @XmlElements({
    @XmlElement(name = "Bahnuebergang", type = Bahnuebergang.class),
    @XmlElement(name = "HauptsignalRtGnGe", type = HauptsignalRtGnGe.class),
    @XmlElement(name = "HauptsignalRtGe", type = HauptsignalRtGe.class),
    @XmlElement(name = "HauptsignalRtGn", type = HauptsignalRtGn.class),
    @XmlElement(name = "Hauptsperrsignal", type = Hauptsperrsignal.class),
    @XmlElement(name = "Sperrsignal", type = Sperrsignal.class),
    @XmlElement(name = "Vorsignal", type = Vorsignal.class),
    @XmlElement(name = "Schalter", type = Schalter.class),
    @XmlElement(name = "Weiche", type = Weiche.class) })
  protected SortedSet<FunktionsdecoderGeraet> geraete = new TreeSet<>();

  /**
   * Konstruktor.
   * <p>
   * Wird nur während des JAXB-Unmarshalling aufgerufen.
   *
   * @param byteAnzahl Anzahl belegter Bytes (Adressen)
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
    this.geraete.forEach(FunktionsdecoderGeraet::adjustStatus);
  }

  public void injectFields() {
    InjectionUtil.injectFields(this);
    this.geraete.forEach(FunktionsdecoderGeraet::injectFields);
  }
}
