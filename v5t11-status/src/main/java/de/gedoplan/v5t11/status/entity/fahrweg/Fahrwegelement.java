package de.gedoplan.v5t11.status.entity.fahrweg;

import de.gedoplan.v5t11.status.entity.Bereichselement;

import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;

/**
 * Element eines Fahrwegs.
 *
 * Ein solches Element kann ein Gerät sein (Signal, Weiche etc.) oder ein Gleisabschnitt.
 *
 * @author dw
 */
public abstract class Fahrwegelement extends Bereichselement {
  private transient BeanManager beanManager;

  protected void publishStatus() {
    if (this.beanManager == null) {
      this.beanManager = CDI.current().select(BeanManager.class).get();
    }

    this.beanManager.fireEvent(this);
  }
}
