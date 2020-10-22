package de.gedoplan.v5t11.status.service;

import de.gedoplan.v5t11.status.entity.Steuerung;
import de.gedoplan.v5t11.status.entity.autoskript.AutoSkript;
import de.gedoplan.v5t11.status.entity.fahrweg.Gleisabschnitt;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.apache.commons.logging.Log;

/**
 * Automatisierungs-Skript-Ausführung.
 * 
 * @author dw
 *
 */
@ApplicationScoped
public class AutoSkriptService {
  @Inject
  Log log;

  @Inject
  Steuerung steuerung;

  void gleisChanged(@Observes Gleisabschnitt gleisabschnitt) {
    this.steuerung.getAutoSkripte().stream()
        .filter(as -> as.getSteuerungsObjekte().contains(gleisabschnitt))
        .forEach(AutoSkript::execute);
  }
}
