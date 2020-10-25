package de.gedoplan.v5t11.fahrzeuge.service;

import de.gedoplan.v5t11.util.jms.JmsClient;
import de.gedoplan.v5t11.util.jms.MessageCategory;
import de.gedoplan.v5t11.util.jsf.NavigationItem;
import de.gedoplan.v5t11.util.jsonb.JsonbWithIncludeVisibility;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.apache.commons.logging.Log;

@ApplicationScoped
public class StatusPublisher {

  @Inject
  JmsClient jmsClient;

  @Inject
  Log log;

  protected void publish(MessageCategory category, Object status) {

    String json = JsonbWithIncludeVisibility.SHORT.toJson(status);

    if (this.log.isDebugEnabled()) {
      this.log.debug(category + ": " + json);
    }

    this.jmsClient.send(category, json);
  }

  void publish(@Observes NavigationItem navigationItem) {
    publish(MessageCategory.NAVIGATIONITEM, navigationItem);
  }

}
