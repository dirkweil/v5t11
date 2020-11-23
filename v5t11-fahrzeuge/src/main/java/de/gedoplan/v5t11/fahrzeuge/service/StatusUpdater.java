package de.gedoplan.v5t11.fahrzeuge.service;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.reactive.messaging.Incoming;

//TODO JMS -> RM

@ApplicationScoped
public class StatusUpdater {

  @Incoming("weiche-changed")
  void weicheChanged(byte[] msg) {
    String json = new String(msg);
    System.out.println("msg: " + json);
  }

  // @Inject
  // @RestClient
  // StatusGateway statusGateway;
  //
  // @Inject
  // Fahrweg statusCache;
  //
  // @Inject
  // EventFirer eventFirer;
  //
  // @Inject
  // NavigationPresenter navigationPresenter;
  //
  // @Override
  // protected Map<MessageCategory, Consumer<String>> getMessageHandler() {
  // Map<MessageCategory, Consumer<String>> messageHandler = new EnumMap<MessageCategory, Consumer<String>>(MessageCategory.class);
  // messageHandler.put(MessageCategory.GLEIS, this::updateGleisabschnitt);
  // messageHandler.put(MessageCategory.NAVIGATIONITEM, this::updateNavigationItem);
  // messageHandler.put(MessageCategory.WEICHE, this::updateWeiche);
  // messageHandler.put(MessageCategory.ZENTRALE, this::updateZentrale);
  // return messageHandler;
  // }
  //
  // /*
  // * Aktuelle Zustände von Gleisabschnitten holen
  // */
  // @Override
  // protected void initializeStatus() {
  // this.statusGateway.getGleisabschnitte().forEach(statusGleisabschnitt -> {
  // Gleisabschnitt gleisabschnitt = this.statusCache.getOrCreateGleisabschnitt(statusGleisabschnitt.getBereich(), statusGleisabschnitt.getName());
  // updateStatus(gleisabschnitt, statusGleisabschnitt);
  // });
  //
  // this.statusGateway.getWeichen().forEach(statusWeiche -> {
  // Weiche weiche = this.statusCache.getOrCreateWeiche(statusWeiche.getBereich(), statusWeiche.getName());
  // updateStatus(weiche, statusWeiche);
  // });
  //
  // Zentrale statusZentrale = this.statusGateway.getZentrale();
  // updateStatus(this.statusCache.getZentrale(), statusZentrale);
  //
  // }
  //
  // private void updateGleisabschnitt(String messageText) {
  // Gleisabschnitt statusGleisabschnitt = JsonbWithIncludeVisibility.SHORT.fromJson(messageText, Gleisabschnitt.class);
  // Gleisabschnitt gleisabschnitt = this.statusCache.getGleisabschnitt(statusGleisabschnitt.getBereich(), statusGleisabschnitt.getName());
  // if (gleisabschnitt != null) {
  // updateStatus(gleisabschnitt, statusGleisabschnitt);
  // }
  // }
  //
  // private void updateNavigationItem(String messageText) {
  // NavigationItem navigationItem = JsonbWithIncludeVisibility.SHORT.fromJson(messageText, NavigationItem.class);
  // this.navigationPresenter.heartBeat(navigationItem);
  // }
  //
  // private void updateWeiche(String messageText) {
  // Weiche statusWeiche = JsonbWithIncludeVisibility.SHORT.fromJson(messageText, Weiche.class);
  // Weiche weiche = this.statusCache.getWeiche(statusWeiche.getBereich(), statusWeiche.getName());
  // if (weiche != null) {
  // updateStatus(weiche, statusWeiche);
  // }
  // }
  //
  // private void updateZentrale(String messageText) {
  // Zentrale statusZentrale = JsonbWithIncludeVisibility.SHORT.fromJson(messageText, Zentrale.class);
  // updateStatus(this.statusCache.getZentrale(), statusZentrale);
  // }
  //
  // private <T extends StatusUpdateable<T>> void updateStatus(T object, T statusObject) {
  // if (this.log.isDebugEnabled()) {
  // this.log.debug(statusObject);
  // }
  // if (object != statusObject) {
  // object.copyStatus(statusObject);
  // }
  // this.eventFirer.fire(object);
  // }

}
