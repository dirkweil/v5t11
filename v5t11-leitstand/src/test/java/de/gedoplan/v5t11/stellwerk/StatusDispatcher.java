package de.gedoplan.v5t11.stellwerk;

import de.gedoplan.v5t11.leitstand.entity.fahrstrasse.Fahrstrasse;
import de.gedoplan.v5t11.leitstand.entity.fahrweg.Gleisabschnitt;
import de.gedoplan.v5t11.leitstand.entity.fahrweg.Signal;
import de.gedoplan.v5t11.leitstand.entity.fahrweg.Weiche;
import de.gedoplan.v5t11.util.domain.entity.Fahrwegelement;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.SetMultimap;

@ApplicationScoped
public class StatusDispatcher {

  private SetMultimap<Fahrwegelement, GbsElement> listener = MultimapBuilder.hashKeys().hashSetValues().build();

  public void addListener(Fahrwegelement fahrwegelement, GbsElement gbsElement) {
    this.listener.get(fahrwegelement).add(gbsElement);
  }

  void fahrstrasseChanged(@Observes Fahrstrasse fahrstrasse) {
    fahrstrasse
        .getElemente()
        .stream()
        .map(fse -> fse.getFahrwegelement())
        .peek(System.out::println)
        .flatMap(fwe -> this.listener.get(fwe).stream())
        .forEach(GbsElement::repaint);
  }

  void gleisabschnittChanged(@Observes Gleisabschnitt gleisabschnitt) {
    this.listener.get(gleisabschnitt).forEach(GbsElement::repaint);
  }

  void signalChanged(@Observes Signal signal) {
    this.listener.get(signal).forEach(GbsElement::repaint);
  }

  void weicheChanged(@Observes Weiche weiche) {
    this.listener.get(weiche).forEach(GbsElement::repaint);
  }
}
