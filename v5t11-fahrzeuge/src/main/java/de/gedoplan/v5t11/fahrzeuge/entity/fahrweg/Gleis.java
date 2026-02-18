package de.gedoplan.v5t11.fahrzeuge.entity.fahrweg;

import de.gedoplan.v5t11.util.domain.attribute.BereichselementId;
import de.gedoplan.v5t11.util.domain.entity.fahrweg.AbstractGleis;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = Gleis.TABLE_NAME)
@Cacheable(true)
@NoArgsConstructor
public class Gleis extends AbstractGleis {

  public static final String TABLE_NAME = "FZ_GLEIS";

  @Getter
  @Setter
  private Integer laenge;

  @Getter
  @Setter
  private boolean verdeckt;

  public Gleis(BereichselementId id) {
    super(id);
  }

}
