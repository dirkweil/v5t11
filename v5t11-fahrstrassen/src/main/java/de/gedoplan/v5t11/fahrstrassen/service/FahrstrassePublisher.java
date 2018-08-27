package de.gedoplan.v5t11.fahrstrassen.service;

import de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse.Fahrstrasse;
import de.gedoplan.v5t11.util.jsonb.JsonbWithIncludeVisibility;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.Topic;

import org.apache.commons.logging.Log;

@ApplicationScoped
public class FahrstrassePublisher {

  @Inject
  JMSContext jmsContext;

  @Inject
  Topic statusTopic;

  @Inject
  Log log;

  protected void publish(String category, Object status) {

    String json = JsonbWithIncludeVisibility.SHORT.toJson(status);

    if (this.log.isDebugEnabled()) {
      this.log.debug(category + ": " + json);
    }

    try {
      this.jmsContext
          .createProducer()
          .setTimeToLive(10 * 1000)
          .setProperty("category", category)
          .send(this.statusTopic, json);
    } catch (Exception e) {
      this.log.error("Cannot send message", e);
    }
  }

  void publish(@Observes @Any Fahrstrasse fahrstrasse) {
    publish("FAHRSTRASSE", fahrstrasse);
  }
}
