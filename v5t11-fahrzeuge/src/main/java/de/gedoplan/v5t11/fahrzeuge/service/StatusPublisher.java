package de.gedoplan.v5t11.fahrzeuge.service;

import jakarta.enterprise.context.ApplicationScoped;

//TODO JMS -> RM

@ApplicationScoped
public class StatusPublisher {
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
  // this.jmsClient.send(category, json);
  // }
  //
  // void publish(@Observes NavigationItem navigationItem) {
  // publish(MessageCategory.NAVIGATIONITEM, navigationItem);
  // }

}
