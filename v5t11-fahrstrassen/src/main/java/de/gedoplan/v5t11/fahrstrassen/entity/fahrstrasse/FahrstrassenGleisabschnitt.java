package de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse;

import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Gleisabschnitt;
import de.gedoplan.v5t11.fahrstrassen.persistence.GleisabschnittRepository;
import de.gedoplan.v5t11.util.domain.attribute.FahrstrassenelementTyp;
import de.gedoplan.v5t11.util.domain.entity.fahrweg.geraet.AbstractWeiche;
import de.gedoplan.v5t11.util.jsonb.JsonbInclude;

import javax.inject.Inject;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import lombok.NoArgsConstructor;

@XmlAccessorType(XmlAccessType.NONE)
@NoArgsConstructor
public class FahrstrassenGleisabschnitt extends Fahrstrassenelement {

  @Inject
  GleisabschnittRepository gleisabschnittRepository;

  @XmlAttribute
  private Boolean startErlaubt;

  @XmlAttribute
  private Boolean endeErlaubt;

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
    Gleisabschnitt gleisabschnitt = this.gleisabschnittRepository.findById(getId());
    if (gleisabschnitt == null) {
      throw new IllegalStateException("Gleisabschnitt nicht vorhanden: " + getId());
    }
    return gleisabschnitt;
  }

  @Override
  public void createFahrwegelement() {
    Gleisabschnitt gleisabschnitt = this.gleisabschnittRepository.findById(getId());
    if (gleisabschnitt == null) {
      this.gleisabschnittRepository.persist(new Gleisabschnitt(getBereich(), getName()));
    }
  }

  @Override
  public int getRank() {
    return 1;
  }

  @Override
  @JsonbInclude(full = true)
  public FahrstrassenelementTyp getTyp() {
    return FahrstrassenelementTyp.GLEISABSCHNITT;
  }

  /**
   * Nachbearbeitung nach JAXB-Unmarshal.
   *
   * @param unmarshaller
   *        Unmarshaller
   * @param parent
   *        Parent
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
