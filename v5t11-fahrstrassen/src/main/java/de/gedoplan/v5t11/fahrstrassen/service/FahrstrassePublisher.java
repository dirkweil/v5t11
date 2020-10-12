package de.gedoplan.v5t11.fahrstrassen.service;

import de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse.Fahrstrasse;
import de.gedoplan.v5t11.util.jms.JmsClient;
import de.gedoplan.v5t11.util.jms.MessageCategory;
import de.gedoplan.v5t11.util.jsonb.JsonbWithIncludeVisibility;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.inject.Inject;

import org.apache.commons.logging.Log;

@ApplicationScoped
public class FahrstrassePublisher {

  @Inject
  JmsClient jmsClient;

  @Inject
  Log log;

  protected void publish(MessageCategory category, Object status) {

    String json = JsonbWithIncludeVisibility.SHORT.toJson(status);

    if (this.log.isDebugEnabled()) {
      this.log.debug(category + ": " + json);
    }

    try {
      this.jmsClient.send(category, json);
    } catch (Exception e) {
      this.log.error("Cannot send message", e);
    }
  }

  void publish(@Observes @Any Fahrstrasse fahrstrasse) {
    publish(MessageCategory.FAHRSTRASSE, fahrstrasse);
  }
}
