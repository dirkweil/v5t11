package de.gedoplan.v5t11.leitstand.service;

import de.gedoplan.v5t11.leitstand.entity.Leitstand;
import de.gedoplan.v5t11.leitstand.entity.baustein.LokController;
import de.gedoplan.v5t11.leitstand.entity.baustein.Zentrale;
import de.gedoplan.v5t11.leitstand.entity.fahrstrasse.Fahrstrasse;
import de.gedoplan.v5t11.leitstand.entity.fahrweg.Gleisabschnitt;
import de.gedoplan.v5t11.leitstand.entity.fahrweg.Signal;
import de.gedoplan.v5t11.leitstand.entity.fahrweg.Weiche;
import de.gedoplan.v5t11.util.cdi.EventFirer;
import de.gedoplan.v5t11.util.jsonb.JsonbWithIncludeVisibility;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.Message;
import javax.jms.Topic;

import org.apache.commons.logging.Log;

@ApplicationScoped
public class StatusMessagePropagator {

  private static enum Category {
    FAHRSTRASSE, GLEIS, LOKCONTROLLER, SIGNAL, WEICHE, ZENTRALE
  };

  @Inject
  JMSContext jmsContext;

  @Inject
  Topic statusTopic;

  @Inject
  Leitstand leitstand;

  @Inject
  FahrstrassenManager fahrstrassenManager;

  @Inject
  EventFirer eventFirer;

  @Inject
  Log log;

  protected void startListener(@Observes @Initialized(ApplicationScoped.class) Object obj) {
    String selector = Stream.of(Category.values())
        .map(Object::toString)
        .collect(Collectors.joining("','", "category in ('", "')"));

    if (this.log.isDebugEnabled()) {
      this.log.debug("selector: " + selector);
    }

    this.jmsContext
        .createConsumer(this.statusTopic, selector)
        .setMessageListener(this::propagateMessage);
  }

  void propagateMessage(Message message) {
    try {
      String text = message.getBody(String.class);
      String category = message.getStringProperty("category");
      switch (Category.valueOf(category)) {
      case FAHRSTRASSE:
        Fahrstrasse statusFahrstrasse = JsonbWithIncludeVisibility.SHORT.fromJson(text, Fahrstrasse.class);
        if (this.log.isDebugEnabled()) {
          this.log.debug(statusFahrstrasse);
        }

        Fahrstrasse fahrstrasse = this.fahrstrassenManager.updateFahrstrasse(statusFahrstrasse);
        if (fahrstrasse != null) {
          this.eventFirer.fire(fahrstrasse);
        }
        break;

      case GLEIS:
        Gleisabschnitt statusGleisabschnitt = JsonbWithIncludeVisibility.SHORT.fromJson(text, Gleisabschnitt.class);
        if (this.log.isDebugEnabled()) {
          this.log.debug(statusGleisabschnitt + " -> " + (statusGleisabschnitt.isBesetzt() ? "besetzt" : "frei"));
        }
        Gleisabschnitt gleisabschnitt = this.leitstand.getGleisabschnitt(statusGleisabschnitt.getBereich(), statusGleisabschnitt.getName());
        if (gleisabschnitt != null) {
          gleisabschnitt.copyStatus(statusGleisabschnitt);
          this.eventFirer.fire(gleisabschnitt);
        }
        break;

      case LOKCONTROLLER:
        LokController statusLokController = JsonbWithIncludeVisibility.SHORT.fromJson(text, LokController.class);
        if (this.log.isDebugEnabled()) {
          this.log.debug(statusLokController);
        }
        LokController lokController = this.leitstand.getLokController(statusLokController.getId());
        if (lokController != null) {
          lokController.copyStatus(statusLokController);
          this.eventFirer.fire(lokController);
        }
        break;

      case SIGNAL:
        Signal statusSignal = JsonbWithIncludeVisibility.SHORT.fromJson(text, Signal.class);
        if (this.log.isDebugEnabled()) {
          this.log.debug(statusSignal + " -> " + statusSignal.getStellung());
        }
        Signal signal = this.leitstand.getSignal(statusSignal.getBereich(), statusSignal.getName());
        if (signal != null) {
          signal.copyStatus(statusSignal);
          this.eventFirer.fire(signal);
        }
        break;

      case WEICHE:
        Weiche statusWeiche = JsonbWithIncludeVisibility.SHORT.fromJson(text, Weiche.class);
        if (this.log.isDebugEnabled()) {
          this.log.debug(statusWeiche + " -> " + statusWeiche.getStellung());
        }
        Weiche weiche = this.leitstand.getWeiche(statusWeiche.getBereich(), statusWeiche.getName());
        if (weiche != null) {
          weiche.copyStatus(statusWeiche);
          this.eventFirer.fire(weiche);
        }
        break;

      case ZENTRALE:
        Zentrale statusZentrale = JsonbWithIncludeVisibility.SHORT.fromJson(text, Zentrale.class);
        if (this.log.isDebugEnabled()) {
          this.log.debug(statusZentrale);
        }

        this.leitstand.getZentrale().copyStatus(statusZentrale);
        this.eventFirer.fire(this.leitstand.getZentrale());
        break;

      default:
        this.log.warn("Status-Message mit unbekannter Category wird ignoriert: " + message);
      }
    } catch (Exception e) {
      this.log.error("Kann Meldung nicht verarbeiten: " + e);
    }
  }
}
