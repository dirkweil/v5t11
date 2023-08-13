package de.gedoplan.v5t11.status.entity.baustein;

import de.gedoplan.baselibs.utils.inject.InjectionUtil;
import de.gedoplan.v5t11.status.entity.fahrweg.Gleis;

import java.util.SortedSet;
import java.util.TreeSet;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
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
public abstract class Besetztmelder extends Baustein implements Encoder {

  /**
   * Zugeordnete Gleise.
   */
  @XmlElement(name = "Gleis", type = Gleis.class)
  protected SortedSet<Gleis> gleise = new TreeSet<>();

  /**
   * Konstruktor.
   * <p>
   * Wird nur während des JAXB-Unmarshalling aufgerufen.
   *
   * @param byteAnzahl Anzahl belegter Bytes (Adressen)
   */
  protected Besetztmelder(int byteAnzahl) {
    super(byteAnzahl);
  }

  @Override
  public String getLabelPrefix() {
    return "Besetzmelder";
  }

  @Override
  public void adjustStatus() {
    this.gleise.forEach(Gleis::adjustStatus);
  }

  public void injectFields() {
    InjectionUtil.injectFields(this);
    this.gleise.forEach(Gleis::injectFields);
  }
}
