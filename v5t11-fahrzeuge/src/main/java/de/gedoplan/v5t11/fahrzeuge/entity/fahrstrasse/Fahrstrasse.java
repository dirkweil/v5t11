package de.gedoplan.v5t11.fahrzeuge.entity.fahrstrasse;

import de.gedoplan.baselibs.utils.inject.InjectionUtil;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrweg.Gleis;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrweg.Weiche;
import de.gedoplan.v5t11.util.domain.attribute.BereichselementId;
import de.gedoplan.v5t11.util.domain.attribute.FahrstrassenelementTyp;
import de.gedoplan.v5t11.util.domain.entity.Bereichselement;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Access(AccessType.FIELD)
@Table(name = Fahrstrasse.TABLE_NAME)
@NoArgsConstructor
public class Fahrstrasse extends Bereichselement {

  public static final String TABLE_NAME = "FZ_FAHRSTRASSE";
  public static final String TABLE_NAME_ELEMENTE = "FZ_FAHRSTRASSE_ELEMENTE";

  /**
   * Liste der Fahrstrassenelemente. Beginnt und endet immer mit einem Gleis.
   */
  @Getter
  @Setter
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = TABLE_NAME_ELEMENTE)
  private List<Fahrstrassenelement> elemente = new ArrayList<>();

  public Fahrstrassenelement getElement(Gleis gleis) {
    return getElement(gleis.getId(), FahrstrassenelementTyp.GLEIS);
  }

  public Fahrstrassenelement getElement(Weiche weiche) {
    return getElement(weiche.getId(), FahrstrassenelementTyp.WEICHE);
  }

  private Fahrstrassenelement getElement(BereichselementId elementId, FahrstrassenelementTyp elementTyp) {
    return this.elemente
      .stream()
      .filter(fe -> fe.getTyp() == elementTyp)
      .filter(fe -> fe.getId().equals(elementId))
      .findFirst()
      .orElse(null);
  }

  @JsonbTransient
  public Fahrstrassenelement getStart() {
    return this.elemente.get(0);
  }

  public Fahrstrasse createUmkehrung() {
    Fahrstrasse fahrstrasse = new Fahrstrasse();

    this.elemente.forEach(fse -> fahrstrasse.elemente.add(0, fse.createUmkehrung()));

    fahrstrasse.setBereich(fahrstrasse.getStart().getId().getBereich());
    fahrstrasse.createName();

    return fahrstrasse;
  }

  private void createName() {
    setName(this.elemente
      .stream()
      .filter(e -> e.getTyp() == FahrstrassenelementTyp.GLEIS)
      .map(g -> g.getId().getBereich().equals(getBereich()) ? g.getId().getName() : g.getId().getBereich() + "." + g.getId().getName())
      .collect(Collectors.joining("-")));
  }

  public void injectFields() {
    InjectionUtil.injectFields(this);
    this.elemente.forEach(Fahrstrassenelement::injectFields);
  }
}
