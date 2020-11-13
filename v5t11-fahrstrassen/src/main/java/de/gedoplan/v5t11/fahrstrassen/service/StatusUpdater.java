package de.gedoplan.v5t11.fahrstrassen.service;

import de.gedoplan.v5t11.util.service.AbstractStatusUpdater;

import javax.enterprise.context.ApplicationScoped;

//TODO JMS -> RM

@ApplicationScoped
public class StatusUpdater extends AbstractStatusUpdater {
  //
  // @Inject
  // @RestClient
  // StatusGateway statusGateway;
  //
  // @Inject
  // Parcours parcours;
  //
  // @Inject
  // EventFirer eventFirer;
  //
  // @Override
  // protected Map<MessageCategory, Consumer<String>> getMessageHandler() {
  // Map<MessageCategory, Consumer<String>> messageHandler = new EnumMap<MessageCategory, Consumer<String>>(MessageCategory.class);
  // messageHandler.put(MessageCategory.GLEIS, this::updateGleisabschnitt);
  // return messageHandler;
  // }
  //
  // /*
  // * Aktuelle Zustände von Gleisabschnitten holen
  // */
  // @Override
  // protected void initializeStatus() {
  // this.statusGateway.getGleisabschnitte().forEach(statusGleisabschnitt -> {
  // Gleisabschnitt gleisabschnitt = this.parcours.getGleisabschnitt(statusGleisabschnitt.getBereich(), statusGleisabschnitt.getName());
  // if (gleisabschnitt != null) {
  // updateGleisabschnitt(gleisabschnitt, statusGleisabschnitt);
  // }
  // });
  // }
  //
  // private void updateGleisabschnitt(String messageText) {
  // Gleisabschnitt statusGleisabschnitt = JsonbWithIncludeVisibility.SHORT.fromJson(messageText, Gleisabschnitt.class);
  // Gleisabschnitt gleisabschnitt = this.parcours.getGleisabschnitt(statusGleisabschnitt.getBereich(), statusGleisabschnitt.getName());
  // if (gleisabschnitt != null) {
  // updateGleisabschnitt(gleisabschnitt, statusGleisabschnitt);
  // }
  // }
  //
  // private void updateGleisabschnitt(Gleisabschnitt gleisabschnitt, Gleisabschnitt statusGleisabschnitt) {
  // if (this.log.isDebugEnabled()) {
  // this.log.debug(statusGleisabschnitt);
  // }
  // if (gleisabschnitt != statusGleisabschnitt) {
  // gleisabschnitt.copyStatus(statusGleisabschnitt);
  // }
  // this.eventFirer.fire(gleisabschnitt);
  // }

}
