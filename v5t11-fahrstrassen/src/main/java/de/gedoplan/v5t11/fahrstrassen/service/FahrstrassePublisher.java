package de.gedoplan.v5t11.fahrstrassen.service;

import javax.enterprise.context.ApplicationScoped;

//TODO JMS -> RM

@ApplicationScoped
public class FahrstrassePublisher {
  //
  // @Inject
  // JmsClient jmsClient;
  //
  // @Inject
  // Log log;
  //
  // protected void publish(MessageCategory category, Object status) {
  //
  // String json = JsonbWithIncludeVisibility.SHORT.toJson(status);
  //
  // if (this.log.isDebugEnabled()) {
  // this.log.debug(category + ": " + json);
  // }
  //
  // try {
  // this.jmsClient.send(category, json);
  // } catch (Exception e) {
  // this.log.error("Cannot send message", e);
  // }
  // }
  //
  // void publish(@Observes @Any Fahrstrasse fahrstrasse) {
  // publish(MessageCategory.FAHRSTRASSE, fahrstrasse);
  // }
}
