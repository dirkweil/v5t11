package de.gedoplan.v5t11.status.entity.baustein;

import de.gedoplan.baselibs.utils.inject.InjectionUtil;
import de.gedoplan.v5t11.status.entity.Kanal;
import de.gedoplan.v5t11.status.entity.fahrzeug.Fahrzeug;
import de.gedoplan.v5t11.util.domain.attribute.FahrzeugId;
import de.gedoplan.v5t11.util.jsonb.JsonbInclude;

import java.util.ArrayList;
import java.util.List;

import javax.json.bind.annotation.JsonbNillable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Lokcontroller.
 *
 * @author dw
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonbNillable
public abstract class Lokcontroller extends Baustein implements Encoder {

  /**
   * Zugewiesene Lok oder <code>null</code>.
   */
  @Getter
  protected Fahrzeug lok;

  protected Lokcontroller(int byteAnzahl) {
    super(byteAnzahl);
  }

  @JsonbInclude
  public FahrzeugId getLokId() {
    return this.lok != null ? this.lok.getId() : null;
  }

  /**
   * Lok zuweisen.
   *
   * @param lok Lok
   * @param hornBits Bits für das Signalhorn
   */
  public abstract void setLok(Fahrzeug lok, int hornBits);

  @Override
  public String getLabelPrefix() {
    return "Lokcontroller";
  }

  // TODO Ohne diese Methode wird die ID nicht ins JSON aufgenommen; warum nicht?
  @Override
  @JsonbInclude
  public String getId() {
    return super.getId();
  }

  @Override
  public void adjustStatus() {
  }

  public void injectFields() {
    InjectionUtil.injectFields(this);
  }

  @Override
  public List<Integer> getAdressen() {
    // Ein LokController horcht auf allen SX-Bussen
    // TODO Kann die Busanzahl dynamisch ermittelt werden?
    if (this.adressen.get() == null) {
      List<Integer> adressen = new ArrayList<>();
      for (int busNr = 0; busNr < 2; ++busNr) {
        adressen.add(Kanal.toAdr(busNr, this.adresse));
      }
      this.adressen.set(adressen);
    }
    return this.adressen.get();
  }

  @Override
  protected void setWert(long wert, boolean updateInterface) {
    // Wert kann nur im Objekt gesetzt werden; ein Update über das Interface ist nicht möglich
    this.wert = wert;
  }

  @Override
  public void adjustWert(int adr, int kanalWert) {
    // Wert wird gesetzt, wenn Adresse eine der registrierten ist; eine Verschiebung des Wertes unterbleibt
    long mask = 0b11111111;
    long teilWert = (kanalWert) & mask;

    for (int bausteinAdresse : getAdressen()) {
      if (bausteinAdresse == adr) {
        this.wert = (this.wert & ~mask) | teilWert;

        adjustStatus();

        break;
      }
    }
  }

}
