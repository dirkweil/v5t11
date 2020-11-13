package de.gedoplan.v5t11.util.jms;

import javax.enterprise.context.ApplicationScoped;

//TODO JMS -> RM

@ApplicationScoped
public class JmsClient {
  //
  // @Inject
  // ConfigService configService;
  //
  // @Inject
  // Log log;
  //
  // private JMSContext jmsContext;
  // private Topic topic;
  //
  // public JMSConsumer getConsumer(Collection<MessageCategory> categories) {
  // if (this.jmsContext == null) {
  // init();
  // }
  //
  // String selector = categories.stream()
  // .map(Object::toString)
  // .collect(Collectors.joining("','", "category in ('", "')"));
  //
  // if (this.log.isTraceEnabled()) {
  // this.log.trace("JMS consumer selector: " + selector);
  // }
  //
  // try {
  // return this.jmsContext.createConsumer(this.topic, selector, true);
  // } catch (JMSRuntimeException e) {
  // reset();
  // throw e;
  // }
  // }
  //
  // public void send(MessageCategory category, String text) {
  // if (this.jmsContext == null) {
  // init();
  // }
  //
  // try {
  // this.jmsContext
  // .createProducer()
  // .setTimeToLive(10 * 1000)
  // .setDeliveryMode(DeliveryMode.NON_PERSISTENT)
  // .setProperty("category", category.toString())
  // .send(this.topic, text);
  // } catch (JMSRuntimeException e) {
  // reset();
  // throw e;
  // }
  //
  // }
  //
  // private void init() {
  //
  // Context jndiContext = null;
  // ConnectionFactory connectionFactory = null;
  // try {
  // String statusJmsUrl = this.configService.getStatusJmsUrl();
  //
  // if (this.log.isDebugEnabled()) {
  // this.log.debug("JMS URL: " + statusJmsUrl);
  // }
  //
  // if (statusJmsUrl.startsWith("http-remoting")) {
  //
  // // Verbindung über WildFly
  // jndiContext = JNDIContextFactory.getInitialContext(this.configService.getStatusJmsUrl(), null, null);
  // connectionFactory = (ConnectionFactory) jndiContext.lookup(LookupHelper.getDefaultJmsConnectionFactoryLookupName());
  //
  // } else {
  //
  // // Direkte Verbindung zu Artemis
  // Properties prop = new Properties();
  // prop.setProperty("java.naming.factory.initial", "org.apache.activemq.artemis.jndi.ActiveMQInitialContextFactory");
  // prop.setProperty("connectionFactory.ConnectionFactory", statusJmsUrl);
  // prop.setProperty("topic.jms/topic/v5t11-status", "jms.topic.v5t11-status");
  //
  // jndiContext = new InitialContext(prop);
  // connectionFactory = (ConnectionFactory) jndiContext.lookup("ConnectionFactory");
  // }
  //
  // this.topic = (Topic) jndiContext.lookup("jms/topic/v5t11-status");
  //
  // if (this.log.isTraceEnabled()) {
  // this.log.trace("jndiContext: " + jndiContext);
  // this.log.trace("connectionFactory: " + connectionFactory);
  // this.log.trace("topic: " + this.topic);
  // }
  //
  // this.jmsContext = connectionFactory.createContext("anonymous", "anonymous_123");
  // } catch (NamingException e) {
  // throw new JMSRuntimeException("Cannot connect to JNDI", null, e);
  // } finally {
  // try {
  // jndiContext.close();
  // } catch (Exception e) {
  // }
  // }
  // }
  //
  // private void reset() {
  // if (this.jmsContext != null) {
  // try {
  // this.jmsContext.close();
  // } catch (Exception e) {
  // }
  // }
  //
  // this.jmsContext = null;
  // }

}
