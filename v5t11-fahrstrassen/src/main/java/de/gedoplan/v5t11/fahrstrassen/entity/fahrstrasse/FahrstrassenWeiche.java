package de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse;

import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Weiche;
import de.gedoplan.v5t11.fahrstrassen.persistence.WeicheRepository;
import de.gedoplan.v5t11.util.domain.attribute.FahrstrassenelementTyp;
import de.gedoplan.v5t11.util.domain.attribute.WeichenStellung;
import de.gedoplan.v5t11.util.domain.entity.fahrweg.geraet.AbstractWeiche;
import de.gedoplan.v5t11.util.jsonb.JsonbInclude;

import jakarta.inject.Inject;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;

import lombok.Getter;
import lombok.NoArgsConstructor;

@XmlAccessorType(XmlAccessType.NONE)
@NoArgsConstructor
public class FahrstrassenWeiche extends FahrstrassenGeraet {

  @Inject
  WeicheRepository weicheRepository;

  @Getter(onMethod_ = @JsonbInclude(full = true))
  @XmlAttribute
  private WeichenStellung stellung;

  @Getter(onMethod_ = @JsonbInclude(full = true))
  @XmlAttribute
  private Integer limit;

  @Override
  public Weiche getFahrwegelement() {
    return this.weicheRepository
      .findById(getId())
      .orElseThrow(() -> new IllegalStateException("Weiche nicht vorhanden: " + getId()));
  }

  @Override
  public void createFahrwegelement() {
    if (this.weicheRepository.findById(getId()).isEmpty()) {
      this.weicheRepository.persist(new Weiche(getBereich(), getName()));
    }
  }

  public FahrstrassenGleis createFahrstrassenGleis() {

    /*
     * Konvention: Eine Doppelweiche (z. B. DKW) besteht aus zwei Einzelweichen, deren Namen
     * mit der gleichen Nummer beginnen und einen eindeutigen Suffixbuchstaben haben (z. B. 11a, 11b).
     * Der zugehörige Gleis enthält nur den numerischen Teil im Namen.
     */
    String name = getName();
    boolean doppelweiche = Character.isAlphabetic(name.charAt(name.length() - 1));
    String gleisName;
    if (doppelweiche) {
      gleisName = AbstractWeiche.PREFIX_WEICHEN_GLEIS + name.substring(0, name.length() - 1);
    } else {
      gleisName = AbstractWeiche.PREFIX_WEICHEN_GLEIS + name;
    }

    return new FahrstrassenGleis(getBereich(), gleisName, this.zaehlrichtung);
  }

  @Override
  public int getRank() {
    return this.stellung == WeichenStellung.ABZWEIGEND ? 1 : 0;
  }

  //  @Override
  //  public String toString() {
  //    return super.toString() + ", stellung=" + this.stellung;
  //  }

  @Override
  @JsonbInclude(full = true)
  public FahrstrassenelementTyp getTyp() {
    return FahrstrassenelementTyp.WEICHE;
  }

  @SuppressWarnings("unused")
  private void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
    // Falls Limit nicht angegeben, für ABZWEIGEND Standardwert einsetzen
    if (this.limit == null) {
      if (this.stellung == WeichenStellung.ABZWEIGEND) {
        this.limit = 40;
      }
    } else if (this.limit <= 0) {
      // Limit 0 oder kleiner bedeutet "kein Limit"
      this.limit = null;
    }
  }

}
