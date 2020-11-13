package de.gedoplan.v5t11.status.service;

//TODO JMS -> RM

//@MessageDriven(activationConfig = {
//    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
//    @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "jms/topic/v5t11-status"),
//    @ActivationConfigProperty(propertyName = "messageSelector", propertyValue = "category='NAVIGATIONITEM' and origin<>'v5t11-status'")
//})
public class StatusUpdater /* implements MessageListener */ {
  //
  // @Inject
  // NavigationPresenter navigationPresenter;
  //
  // @Inject
  // Log log;
  //
  // @Override
  // public void onMessage(Message message) {
  // try {
  // if (message instanceof TextMessage) {
  // String messageText = ((TextMessage) message).getText();
  // MessageCategory category = MessageCategory.valueOf(message.getStringProperty("category"));
  // switch (category) {
  // case NAVIGATIONITEM:
  // updateNavigationItem(messageText);
  // break;
  //
  // default:
  // this.log.warn("Status-Message mit unbekannter Category wird ignoriert: " + message);
  // }
  // } else {
  // this.log.warn("Nicht-Text-Message wird ignoriert: " + message);
  // }
  // } catch (Exception e) {
  // this.log.error("Kann Status-Message nicht verarbeiten", e);
  // throw new EJBException(e);
  // }
  //
  // }
  //
  // private void updateNavigationItem(String messageText) {
  // NavigationItem navigationItem = JsonbWithIncludeVisibility.SHORT.fromJson(messageText, NavigationItem.class);
  // this.navigationPresenter.heartBeat(navigationItem);
  // }
}
