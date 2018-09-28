package de.gedoplan.v5t11.stellwerk;

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

  private static SetMultimap<Fahrwegelement, GbsElement> listener = MultimapBuilder.hashKeys().hashSetValues().build();

  public static void addListener(Fahrwegelement fahrwegelement, GbsElement gbsElement) {
    listener.get(fahrwegelement).add(gbsElement);
  }

  void gleisabschnittChanged(@Observes Gleisabschnitt gleisabschnitt) {
    listener.get(gleisabschnitt).forEach(GbsElement::repaint);
  }

  void signalChanged(@Observes Signal signal) {
    listener.get(signal).forEach(GbsElement::repaint);
  }

  void weicheChanged(@Observes Weiche weiche) {
    listener.get(weiche).forEach(GbsElement::repaint);
  }
}
