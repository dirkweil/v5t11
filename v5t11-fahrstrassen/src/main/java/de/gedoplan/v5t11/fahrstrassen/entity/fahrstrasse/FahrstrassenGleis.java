package de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse;

import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Gleis;
import de.gedoplan.v5t11.fahrstrassen.persistence.GleisRepository;
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
public class FahrstrassenGleis extends Fahrstrassenelement {

  @Inject
  GleisRepository gleisRepository;

  @XmlAttribute
  private Boolean startErlaubt;

  @XmlAttribute
  private Boolean endeErlaubt;

  public FahrstrassenGleis(String bereich, String name, boolean zaehlrichtung) {
    super(bereich, name, zaehlrichtung);
  }

  public boolean isStartErlaubt() {
    return this.startErlaubt;
  }

  public boolean isEndeErlaubt() {
    return this.endeErlaubt;
  }

  @Override
  public Gleis getFahrwegelement() {
    Gleis gleis = this.gleisRepository.findById(getId());
    if (gleis == null) {
      throw new IllegalStateException("Gleis nicht vorhanden: " + getId());
    }
    return gleis;
  }

  @Override
  public void createFahrwegelement() {
    Gleis gleis = this.gleisRepository.findById(getId());
    if (gleis == null) {
      this.gleisRepository.persist(new Gleis(getBereich(), getName()));
    }
  }

  @Override
  public int getRank() {
    return 1;
  }

  @Override
  @JsonbInclude(full = true)
  public FahrstrassenelementTyp getTyp() {
    return FahrstrassenelementTyp.GLEIS;
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
      this.startErlaubt = !getName().startsWith(AbstractWeiche.PREFIX_WEICHEN_GLEIS);
    }
    if (this.endeErlaubt == null) {
      this.endeErlaubt = !getName().startsWith(AbstractWeiche.PREFIX_WEICHEN_GLEIS);
    }
  }
}
