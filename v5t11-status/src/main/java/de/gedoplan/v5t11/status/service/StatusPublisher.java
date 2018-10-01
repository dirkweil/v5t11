package de.gedoplan.v5t11.status.service;

import de.gedoplan.v5t11.status.entity.Kanal;
import de.gedoplan.v5t11.status.entity.baustein.Lokcontroller;
import de.gedoplan.v5t11.status.entity.baustein.Zentrale;
import de.gedoplan.v5t11.status.entity.fahrweg.Gleisabschnitt;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Signal;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Weiche;
import de.gedoplan.v5t11.status.entity.lok.Lok;
import de.gedoplan.v5t11.util.jsonb.JsonbWithIncludeVisibility;

import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSContext;

import org.apache.commons.logging.Log;

@ApplicationScoped
public class StatusPublisher {

  @Resource
  ConnectionFactory connectionFactory;

  @Resource(lookup = "java:/jms/topic/v5t11-status")
  Destination destination;

  @Inject
  Log log;

  protected void publish(String category, Object status) {

    String json = JsonbWithIncludeVisibility.SHORT.toJson(status);

    if (this.log.isDebugEnabled()) {
      this.log.debug(category + ": " + json);
    }

    try (JMSContext jmsContext = this.connectionFactory.createContext()) {
      jmsContext
          .createProducer()
          .setTimeToLive(10 * 1000)
          .setProperty("category", category)
          .send(this.destination, json);
    } catch (Exception e) {
      this.log.error("Cannot send status message", e);
    }
  }

  void publish(@Observes Zentrale zentrale) {
    publish("ZENTRALE", zentrale);
  }

  void publish(@Observes Gleisabschnitt gleisabschnitt) {
    publish("GLEIS", gleisabschnitt);
  }

  void publish(@Observes Weiche weiche) {
    publish("WEICHE", weiche);
  }

  void publish(@Observes Signal signal) {
    publish("SIGNAL", signal);
  }

  void publish(@Observes Lok lok) {
    publish("LOK", lok);
  }

  void publish(@Observes Lokcontroller lokcontroller) {
    publish("LOKCONTROLLER", lokcontroller);
  }

  void publish(@Observes Kanal kanal) {
    publish("KANAL", kanal);
  }
}
