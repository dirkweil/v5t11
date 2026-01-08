package de.gedoplan.v5t11.fahrzeuge.entity.fahrstrasse;

import de.gedoplan.baselibs.utils.exception.BugException;
import de.gedoplan.baselibs.utils.inject.InjectionUtil;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrweg.Gleis;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrweg.Weiche;
import de.gedoplan.v5t11.fahrzeuge.persistence.GleisRepository;
import de.gedoplan.v5t11.fahrzeuge.persistence.WeicheRepository;
import de.gedoplan.v5t11.util.domain.attribute.BereichselementId;
import de.gedoplan.v5t11.util.domain.attribute.FahrstrassenelementTyp;
import de.gedoplan.v5t11.util.domain.attribute.WeichenStellung;
import de.gedoplan.v5t11.util.jsonb.JsonbShort;

import jakarta.inject.Inject;
import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Convert;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Transient;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Access(AccessType.FIELD)
@NoArgsConstructor
public class Fahrstrassenelement implements Cloneable {

  @Transient
  @Inject
  GleisRepository gleisRepository;

  @Transient
  @Inject
  WeicheRepository weicheRepository;

  @Getter
  private BereichselementId id;

  @Getter
  @Setter
  protected boolean zaehlrichtung;

  @Getter
  @Setter
  @Transient
  protected boolean schutz;

  @Getter
  @Setter
  @Convert(converter = FahrstrassenelementTyp.Adapter4Jpa.class)
  protected FahrstrassenelementTyp typ;

  @Getter
  @Setter
  protected String stellung;

  @JsonbShort
  public void setKey(BereichselementId id) {
    this.id = id;
  }

  /**
   * Conveniance-Methode: Zugeordnete Weichenstellung liefern.
   * Darf nur aufgerufen werden, wenn es sich um ein Weichenelement handelt!
   *
   * @return Weichenstellung
   */
  public WeichenStellung getWeichenstellung() {
    assert this.typ == FahrstrassenelementTyp.WEICHE;
    return WeichenStellung.fromString(this.stellung);
  }

  /**
   * Kopie erzeugen.
   * <p>
   * Es wird eine Kopie des Elementes mit neuer ID erzeugt.
   *
   * @return umgekehrtes Element
   */
  public Fahrstrassenelement createKopie() {

    try {
      return (Fahrstrassenelement) this.clone();
    } catch (CloneNotSupportedException e) {
      throw new BugException(e);
    }
  }

  /**
   * Umgekehrtes Element erzeugen.
   * <p>
   * Wie {@link #createKopie()}, aber mit umgekehrter Zählrichtung
   *
   * @return umgekehrtes Element
   */
  public Fahrstrassenelement createUmkehrung() {

    Fahrstrassenelement fahrstrassenelement = createKopie();
    fahrstrassenelement.zaehlrichtung = !fahrstrassenelement.isZaehlrichtung();
    return fahrstrassenelement;
  }

  public void injectFields() {
    InjectionUtil.injectFields(this);
  }

  public void associateFahrwegelement() {
    switch (this.typ) {
    case GLEIS -> associateGleis();
    case WEICHE -> associateWeiche();
    }
  }

  private void associateGleis() {
    if (this.gleisRepository.findById(id).isEmpty()) {
      this.gleisRepository.persist(guessGleis(id));
    }
  }

  private Gleis guessGleis(BereichselementId id) {
    Gleis gleis = new Gleis(id);
    gleis.setVerdeckt(id.getBereich().equals("SBf"));
    return gleis;
  }

  private void associateWeiche() {
    if (this.weicheRepository.findById(id).isEmpty()) {
      this.weicheRepository.persist(new Weiche(id));
    }
  }
}
