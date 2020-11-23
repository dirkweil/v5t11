package de.gedoplan.v5t11.fahrstrassen.service;

import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Gleisabschnitt;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Signal;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Weiche;
import de.gedoplan.v5t11.util.jsonb.JsonbWithIncludeVisibility;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.reactive.messaging.Incoming;

//TODO JMS -> RM

@ApplicationScoped
public class StatusUpdater {

  @Incoming("gleis-changed")
  void gleisabschnittChanged(byte[] msg) {
    String json = new String(msg);
    Gleisabschnitt gleisabschnitt = JsonbWithIncludeVisibility.SHORT.fromJson(json, Gleisabschnitt.class);
    System.out.println("msg: " + gleisabschnitt);
  }

  @Incoming("signal-changed")
  void signalChanged(byte[] msg) {
    String json = new String(msg);
    Signal signal = JsonbWithIncludeVisibility.SHORT.fromJson(json, Signal.class);
    System.out.println("msg: " + signal);
  }

  @Incoming("weiche-changed")
  void weicheChanged(byte[] msg) {
    String json = new String(msg);
    Weiche weiche = JsonbWithIncludeVisibility.SHORT.fromJson(json, Weiche.class);
    System.out.println("msg: " + weiche);
  }

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
