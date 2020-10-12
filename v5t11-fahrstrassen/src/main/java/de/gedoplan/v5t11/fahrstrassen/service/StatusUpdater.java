package de.gedoplan.v5t11.fahrstrassen.service;

import de.gedoplan.v5t11.fahrstrassen.entity.Parcours;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Gleisabschnitt;
import de.gedoplan.v5t11.fahrstrassen.gateway.StatusGateway;
import de.gedoplan.v5t11.util.cdi.EventFirer;
import de.gedoplan.v5t11.util.jms.MessageCategory;
import de.gedoplan.v5t11.util.jsonb.JsonbWithIncludeVisibility;
import de.gedoplan.v5t11.util.service.AbstractStatusUpdater;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Consumer;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public class StatusUpdater extends AbstractStatusUpdater {

  @Inject
  @RestClient
  StatusGateway statusGateway;

  @Inject
  Parcours parcours;

  @Inject
  EventFirer eventFirer;

  @Override
  protected Map<MessageCategory, Consumer<String>> getMessageHandler() {
    Map<MessageCategory, Consumer<String>> messageHandler = new EnumMap<MessageCategory, Consumer<String>>(MessageCategory.class);
    messageHandler.put(MessageCategory.GLEIS, this::updateGleisabschnitt);
    return messageHandler;
  }

  /*
   * Aktuelle Zustände von Gleisabschnitten holen
   */
  @Override
  protected void initializeStatus() {
    this.statusGateway.getGleisabschnitte().forEach(statusGleisabschnitt -> {
      Gleisabschnitt gleisabschnitt = this.parcours.getGleisabschnitt(statusGleisabschnitt.getBereich(), statusGleisabschnitt.getName());
      if (gleisabschnitt != null) {
        updateGleisabschnitt(gleisabschnitt, statusGleisabschnitt);
      }
    });
  }

  private void updateGleisabschnitt(String messageText) {
    Gleisabschnitt statusGleisabschnitt = JsonbWithIncludeVisibility.SHORT.fromJson(messageText, Gleisabschnitt.class);
    Gleisabschnitt gleisabschnitt = this.parcours.getGleisabschnitt(statusGleisabschnitt.getBereich(), statusGleisabschnitt.getName());
    if (gleisabschnitt != null) {
      updateGleisabschnitt(gleisabschnitt, statusGleisabschnitt);
    }
  }

  private void updateGleisabschnitt(Gleisabschnitt gleisabschnitt, Gleisabschnitt statusGleisabschnitt) {
    if (this.log.isDebugEnabled()) {
      this.log.debug(statusGleisabschnitt);
    }
    if (gleisabschnitt != statusGleisabschnitt) {
      gleisabschnitt.copyStatus(statusGleisabschnitt);
    }
    this.eventFirer.fire(gleisabschnitt);
  }

}
