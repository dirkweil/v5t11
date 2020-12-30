package de.gedoplan.v5t11.fahrstrassen.testenvironment;

import de.gedoplan.v5t11.fahrstrassen.service.StatusUpdater;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;

//TODO JMS -> RM

@ApplicationScoped
@Alternative
@Priority(1)
public class TestStatusUpdater extends StatusUpdater {
  //
  // @Override
  // public void run() {
  // }

}
