package de.gedoplan.v5t11.fahrstrassen.testenvironment;

import de.gedoplan.v5t11.fahrstrassen.entity.Parcours;
import de.gedoplan.v5t11.fahrstrassen.service.ParcoursStatusUpdater;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Specializes;

@Dependent
@Specializes
public class TestParcoursStatusUpdater extends ParcoursStatusUpdater {

  @Override
  protected void run(Parcours parcours) {
  }

}
