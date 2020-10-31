package de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse;

import de.gedoplan.v5t11.fahrstrassen.entity.Parcours;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Gleisabschnitt;
import de.gedoplan.v5t11.util.domain.entity.fahrweg.geraet.AbstractWeiche;
import de.gedoplan.v5t11.util.jsonb.JsonbInclude;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import lombok.Getter;
import lombok.NoArgsConstructor;

@XmlAccessorType(XmlAccessType.NONE)
@NoArgsConstructor
public class FahrstrassenGleisabschnitt extends Fahrstrassenelement {

  @XmlAttribute
  private Boolean startErlaubt;

  @XmlAttribute
  private Boolean endeErlaubt;

  @Getter
  private Gleisabschnitt gleisabschnitt;

  public FahrstrassenGleisabschnitt(String bereich, String name, boolean zaehlrichtung) {
    super(bereich, name, zaehlrichtung);
  }

  public boolean isStartErlaubt() {
    return this.startErlaubt;
  }

  public boolean isEndeErlaubt() {
    return this.endeErlaubt;
  }

  @Override
  public Gleisabschnitt getFahrwegelement() {
    return this.gleisabschnitt;
  }

  @Override
  public void linkFahrwegelement(Parcours parcours) {
    this.gleisabschnitt = parcours.getGleisabschnitt(getBereich(), getName());
    if (this.gleisabschnitt == null) {
      this.gleisabschnitt = new Gleisabschnitt(getBereich(), getName());
      parcours.addGleisabschnitt(this.gleisabschnitt);
    }
  }

  @Override
  public int getRank() {
    return 1;
  }

  @Override
  public void reservieren(Fahrstrasse fahrstrasse) {
    if (this.gleisabschnitt != null) {
      this.gleisabschnitt.reserviereFuerFahrstrasse(fahrstrasse, isZaehlrichtung());
    }
  }

  @Override
  @JsonbInclude(full = true)
  public String getTyp() {
    return "GLEIS";
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
    if (this.startErlaubt == null) {
      this.startErlaubt = !getName().startsWith(AbstractWeiche.PREFIX_WEICHEN_GLEISABSCHNITT);
    }
    if (this.endeErlaubt == null) {
      this.endeErlaubt = !getName().startsWith(AbstractWeiche.PREFIX_WEICHEN_GLEISABSCHNITT);
    }
  }
}
