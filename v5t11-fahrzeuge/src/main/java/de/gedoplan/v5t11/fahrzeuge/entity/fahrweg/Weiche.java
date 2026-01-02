package de.gedoplan.v5t11.fahrzeuge.entity.fahrweg;

import de.gedoplan.v5t11.util.domain.attribute.BereichselementId;
import de.gedoplan.v5t11.util.domain.entity.fahrweg.geraet.AbstractWeiche;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import lombok.NoArgsConstructor;

@Entity
@Table(name = Weiche.TABLE_NAME)
@Cacheable(true)
@NoArgsConstructor
public class Weiche extends AbstractWeiche {

  public static final String TABLE_NAME = "FZ_WEICHE";

  public Weiche(BereichselementId id) {
    super(id);
  }

}
