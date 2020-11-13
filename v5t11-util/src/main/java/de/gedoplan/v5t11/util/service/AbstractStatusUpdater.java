package de.gedoplan.v5t11.util.service;

//TODO JMS -> RM

public abstract class AbstractStatusUpdater /* implements Runnable */ {
  // private static final long RETRY_MILLIS = 10000;
  //
  // @Inject
  // JmsClient jmsClient;
  //
  // @Inject
  // protected Log log;
  //
  // private Map<MessageCategory, Consumer<String>> messageHandler;
  //
  // protected AbstractStatusUpdater() {
  // this.messageHandler = getMessageHandler();
  // }
  //
  // protected abstract Map<MessageCategory, Consumer<String>> getMessageHandler();
  //
  // protected abstract void initializeStatus();
  //
  // public void run() {
  //
  // while (true) {
  // try {
  // if (this.log.isDebugEnabled()) {
  // this.log.debug("Status initialisieren");
  // }
  //
  // initializeStatus();
  //
  // if (this.log.isDebugEnabled()) {
  // this.log.debug("Status-Änderungen überwachen");
  // }
  //
  // propagateStatus();
  // } catch (Exception e) {
  // String msg = "Fehler beim Status-Update (Status-Service down?)";
  // if (this.log.isTraceEnabled()) {
  // this.log.warn(msg, e);
  // } else {
  // this.log.warn(msg);
  // }
  // }
  //
  // try {
  // Thread.sleep(RETRY_MILLIS);
  // } catch (InterruptedException ie) {
  // }
  // }
  // }
  //
  // private void propagateStatus() throws NamingException, JMSException {
  // JMSConsumer jmsConsumer = this.jmsClient.getConsumer(this.messageHandler.keySet());
  //
  // while (true) {
  // Message message = jmsConsumer.receive();
  // if (message != null) {
  // String text = message.getBody(String.class);
  // MessageCategory category = MessageCategory.valueOf(message.getStringProperty("category"));
  // Consumer<String> handler = this.messageHandler.get(category);
  // if (handler != null) {
  // handler.accept(text);
  // } else {
  // this.log.warn("Status-Message mit unbekannter Category wird ignoriert: " + message);
  // }
  // }
  // }
  // }
}
