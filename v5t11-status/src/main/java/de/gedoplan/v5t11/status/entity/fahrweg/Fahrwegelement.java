package de.gedoplan.v5t11.status.entity.fahrweg;

import de.gedoplan.v5t11.status.entity.Bereichselement;

import javax.enterprise.inject.spi.BeanManager;
import javax.json.bind.annotation.JsonbTransient;

/**
 * Element eines Fahrwegs.
 *
 * Ein solches Element kann ein Gerät sein (Signal, Weiche etc.) oder ein Gleisabschnitt.
 *
 * @author dw
 */
public abstract class Fahrwegelement extends Bereichselement {
  @JsonbTransient
  private transient BeanManager beanManager;
}
