package de.gedoplan.v5t11.stellwerk;

import de.gedoplan.v5t11.leitstand.entity.baustein.Zentrale;
import de.gedoplan.v5t11.leitstand.entity.fahrstrasse.Fahrstrasse;
import de.gedoplan.v5t11.util.domain.entity.Fahrwegelement;

import java.util.function.Consumer;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.SetMultimap;

@ApplicationScoped
public class StatusDispatcher {

  @SuppressWarnings("rawtypes")
  private SetMultimap<Object, Consumer> listener = MultimapBuilder.hashKeys().hashSetValues().build();

  public <T> void addListener(T observed, Consumer<T> action) {
    this.listener.get(observed).add(action);
  }

  public <T> void removeListener(Consumer<T> action) {
    this.listener.values().remove(action);
  }

  /*
   * Bei Fahrstrassenänderungen werden (nur) die Actions zu darin enthaltenen Fahrwegelementen aufgerufen.
   */
  void changed(@Observes Fahrstrasse fahrstrasse) {
    // TODO FahrStrasse
    // fahrstrasse
    // .getElemente()
    // .stream()
    // .map(fse -> fse.getFahrwegelement())
    // .flatMap(fwe -> this.listener.get(fwe).stream())
    // .forEach(x -> x.run());
  }

  void changed(@Observes Fahrwegelement fahrwegelement) {
    runActions(fahrwegelement);
  }

  void changed(@Observes Zentrale zentrale) {
    runActions(zentrale);
  }

  @SuppressWarnings("unchecked")
  void runActions(Object observed) {
    this.listener.get(observed).forEach(x -> x.accept(observed));
    this.listener.get(observed.getClass()).forEach(x -> x.accept(observed));
  }
}
