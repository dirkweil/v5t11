package de.gedoplan.v5t11.stellwerk;

import de.gedoplan.v5t11.leitstand.entity.baustein.Zentrale;
import de.gedoplan.v5t11.leitstand.entity.fahrstrasse.Fahrstrasse;
import de.gedoplan.v5t11.leitstand.persistence.GleisabschnittRepository;
import de.gedoplan.v5t11.util.cdi.Changed;
import de.gedoplan.v5t11.util.domain.attribute.FahrstrassenelementTyp;
import de.gedoplan.v5t11.util.domain.entity.Fahrwegelement;

import java.util.function.Consumer;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.event.TransactionPhase;
import javax.inject.Inject;

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

  @Inject
  GleisabschnittRepository gleisabschnittRepository;

  void changed(@Observes(during = TransactionPhase.AFTER_COMPLETION) @Changed Fahrstrasse fahrstrasse) {
    fahrstrasse
        .getElemente()
        .stream()
        .filter(fse -> fse.getTyp() == FahrstrassenelementTyp.GLEISABSCHNITT)
        .map(fse -> this.gleisabschnittRepository.findById(fse.getId()))
        .forEach(x -> changed(x));
  }

  void changed(@Observes(during = TransactionPhase.AFTER_COMPLETION) @Changed Fahrwegelement fahrwegelement) {
    runActions(fahrwegelement);
  }

  void changed(@Observes(during = TransactionPhase.AFTER_COMPLETION) @Changed Zentrale zentrale) {
    runActions(zentrale);
  }

  @SuppressWarnings("unchecked")
  void runActions(Object observed) {
    this.listener.get(observed).forEach(x -> x.accept(observed));
  }
}
