package de.gedoplan.v5t11.parcours.testenvironment.service;

import de.gedoplan.v5t11.parcours.service.StatusUpdater;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;

@ApplicationScoped
@Alternative
@Priority(1)
public class StatusUpdaterMock extends StatusUpdater {
  //
  // @Override
  // public void run() {
  // }

}
