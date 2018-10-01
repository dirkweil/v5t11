package de.gedoplan.v5t11.stellwerk;

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

  void changed(@Observes Fahrstrasse fahrstrasse) {
    fahrstrasse
        .getElemente()
        .stream()
        .map(fse -> fse.getFahrwegelement())
        .peek(System.out::println)
        .flatMap(fwe -> this.listener.get(fwe).stream())
        .forEach(x -> x.run());
  }

  void changed(@Observes Fahrwegelement fahrwegelement) {
    this.listener.get(fahrwegelement).forEach(x -> x.run());
  }

  void changed(@Observes Zentrale zentrale) {
    this.listener.get(zentrale).forEach(x -> x.run());
  }
}
