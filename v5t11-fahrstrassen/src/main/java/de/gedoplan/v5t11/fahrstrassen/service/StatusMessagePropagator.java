package de.gedoplan.v5t11.fahrstrassen.service;

import de.gedoplan.v5t11.fahrstrassen.entity.Parcours;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Gleisabschnitt;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Signal;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Weiche;
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
    GLEIS, SIGNAL, WEICHE
  };

  @Inject
  JMSContext jmsContext;

  @Inject
  Topic statusTopic;

  @Inject
  Parcours parcours;

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
      case GLEIS:
        Gleisabschnitt statusGleisabschnitt = JsonbWithIncludeVisibility.SHORT.fromJson(text, Gleisabschnitt.class);
        if (this.log.isDebugEnabled()) {
          this.log.debug(statusGleisabschnitt + " -> " + (statusGleisabschnitt.isBesetzt() ? "besetzt" : "frei"));
        }
        Gleisabschnitt gleisabschnitt = this.parcours.getGleisabschnitt(statusGleisabschnitt.getBereich(), statusGleisabschnitt.getName());
        if (gleisabschnitt != null) {
          gleisabschnitt.copyStatus(statusGleisabschnitt);
        }
        break;

      case SIGNAL:
        Signal statusSignal = JsonbWithIncludeVisibility.SHORT.fromJson(text, Signal.class);
        if (this.log.isDebugEnabled()) {
          this.log.debug(statusSignal + " -> " + statusSignal.getStellung());
        }
        Signal signal = this.parcours.getSignal(statusSignal.getBereich(), statusSignal.getName());
        if (signal != null) {
          signal.copyStatus(statusSignal);
        }
        break;

      case WEICHE:
        Weiche statusWeiche = JsonbWithIncludeVisibility.SHORT.fromJson(text, Weiche.class);
        if (this.log.isDebugEnabled()) {
          this.log.debug(statusWeiche + " -> " + statusWeiche.getStellung());
        }
        Weiche weiche = this.parcours.getWeiche(statusWeiche.getBereich(), statusWeiche.getName());
        if (weiche != null) {
          weiche.copyStatus(statusWeiche);
        }
        break;

      default:
        this.log.warn("Status-Message mit unbekannter Category wird ignoriert: " + message);
      }
    } catch (Exception e) {
      this.log.error("Kann Meldung nicht verarbeiten: " + e);
    }
  }
}
