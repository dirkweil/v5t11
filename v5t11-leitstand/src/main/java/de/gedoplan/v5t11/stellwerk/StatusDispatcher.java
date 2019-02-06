package de.gedoplan.v5t11.stellwerk;

import de.gedoplan.v5t11.leitstand.entity.baustein.LokController;
import de.gedoplan.v5t11.leitstand.entity.baustein.Zentrale;
import de.gedoplan.v5t11.leitstand.entity.fahrstrasse.Fahrstrasse;
import de.gedoplan.v5t11.util.domain.entity.Fahrwegelement;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.SetMultimap;

@ApplicationScoped
public class StatusDispatcher {

  private SetMultimap<Object, Runnable> listener = MultimapBuilder.hashKeys().hashSetValues().build();

  public void addListener(Object observed, Runnable action) {
    this.listener.get(observed).add(action);
  }

  /*
   * Bei Fahrstrassenänderungen werden (nur) die Actions zu darin enthaltenen Fahrwegelementen aufgerufen.
   */
  void changed(@Observes Fahrstrasse fahrstrasse) {
    fahrstrasse
        .getElemente()
        .stream()
        .map(fse -> fse.getFahrwegelement())
        .flatMap(fwe -> this.listener.get(fwe).stream())
        .forEach(x -> x.run());
  }

  void changed(@Observes Fahrwegelement fahrwegelement) {
    runActions(fahrwegelement);
  }

  void changed(@Observes LokController lokController) {
    runActions(lokController);
  }

  void changed(@Observes Zentrale zentrale) {
    runActions(zentrale);
  }

  void runActions(Object observed) {
    this.listener.get(observed).forEach(x -> x.run());
    this.listener.get(observed.getClass()).forEach(x -> x.run());
  }
}
