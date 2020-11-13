package de.gedoplan.v5t11.status.service;

import javax.enterprise.context.ApplicationScoped;

// TODO JMS -> RM

@ApplicationScoped
public class StatusPublisher {
  //
  // @Resource
  // ConnectionFactory connectionFactory;
  //
  // @Resource(lookup = "java:/jms/topic/v5t11-status")
  // Destination destination;
  //
  // @Inject
  // @ConfigProperty(name = ConfigService.PROPERTY_ARTIFACT_ID)
  // @Getter
  // String artifactId;
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
  // try (JMSContext jmsContext = this.connectionFactory.createContext()) {
  // jmsContext
  // .createProducer()
  // .setTimeToLive(10 * 1000)
  // .setProperty("category", category.name())
  // .setProperty("origin", this.artifactId)
  // .send(this.destination, json);
  // } catch (Exception e) {
  // this.log.error("Cannot send status message", e);
  // }
  // }
  //
  // void publish(@Observes Zentrale zentrale) {
  // publish(MessageCategory.ZENTRALE, zentrale);
  // }
  //
  // void publish(@Observes Gleisabschnitt gleisabschnitt) {
  // publish(MessageCategory.GLEIS, gleisabschnitt);
  // }
  //
  // void publish(@Observes Weiche weiche) {
  // publish(MessageCategory.WEICHE, weiche);
  // }
  //
  // void publish(@Observes Signal signal) {
  // publish(MessageCategory.SIGNAL, signal);
  // }
  //
  // void publish(@Observes Fahrzeug lok) {
  // publish(MessageCategory.LOK, lok);
  // }
  //
  // void publish(@Observes Lokcontroller lokcontroller) {
  // publish(MessageCategory.LOKCONTROLLER, lokcontroller);
  // }
  //
  // void publish(@Observes NavigationItem navigationItem) {
  // publish(MessageCategory.NAVIGATIONITEM, navigationItem);
  // }
  //
}
