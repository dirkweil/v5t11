package de.gedoplan.v5t11.leitstand.testenvironment;

import de.gedoplan.v5t11.leitstand.service.StatusUpdater;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;

@ApplicationScoped
@Alternative
@Priority(1)
public class TestStatusUpdater extends StatusUpdater {

  @Override
  public void run() {
  }

}
