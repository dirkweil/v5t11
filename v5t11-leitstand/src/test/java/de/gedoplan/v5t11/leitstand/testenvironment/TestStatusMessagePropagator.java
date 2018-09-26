package de.gedoplan.v5t11.leitstand.testenvironment;

import de.gedoplan.v5t11.leitstand.service.StatusMessagePropagator;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Specializes;

@Dependent
@Specializes
public class TestStatusMessagePropagator extends StatusMessagePropagator {

  @Override
  protected void startListener(Object obj) {
  }

}
