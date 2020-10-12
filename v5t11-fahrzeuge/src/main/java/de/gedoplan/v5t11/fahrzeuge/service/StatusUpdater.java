package de.gedoplan.v5t11.fahrzeuge.service;

import de.gedoplan.v5t11.fahrzeuge.entity.baustein.Zentrale;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrweg.Fahrweg;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrweg.Gleisabschnitt;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrweg.StatusUpdateable;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrweg.Weiche;
import de.gedoplan.v5t11.fahrzeuge.gateway.StatusGateway;
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
  Fahrweg statusCache;

  @Inject
  EventFirer eventFirer;

  @Override
  protected Map<MessageCategory, Consumer<String>> getMessageHandler() {
    Map<MessageCategory, Consumer<String>> messageHandler = new EnumMap<MessageCategory, Consumer<String>>(MessageCategory.class);
    messageHandler.put(MessageCategory.GLEIS, this::updateGleisabschnitt);
    messageHandler.put(MessageCategory.WEICHE, this::updateWeiche);
    messageHandler.put(MessageCategory.ZENTRALE, this::updateZentrale);
    return messageHandler;
  }

  /*
   * Aktuelle Zustände von Gleisabschnitten holen
   */
  @Override
  protected void initializeStatus() {
    this.statusGateway.getGleisabschnitte().forEach(statusGleisabschnitt -> {
      Gleisabschnitt gleisabschnitt = this.statusCache.getOrCreateGleisabschnitt(statusGleisabschnitt.getBereich(), statusGleisabschnitt.getName());
      updateStatus(gleisabschnitt, statusGleisabschnitt);
    });

    this.statusGateway.getWeichen().forEach(statusWeiche -> {
      Weiche weiche = this.statusCache.getOrCreateWeiche(statusWeiche.getBereich(), statusWeiche.getName());
      updateStatus(weiche, statusWeiche);
    });

    Zentrale statusZentrale = this.statusGateway.getZentrale();
    updateStatus(this.statusCache.getZentrale(), statusZentrale);

  }

  private void updateGleisabschnitt(String messageText) {
    Gleisabschnitt statusGleisabschnitt = JsonbWithIncludeVisibility.SHORT.fromJson(messageText, Gleisabschnitt.class);
    Gleisabschnitt gleisabschnitt = this.statusCache.getGleisabschnitt(statusGleisabschnitt.getBereich(), statusGleisabschnitt.getName());
    if (gleisabschnitt != null) {
      updateStatus(gleisabschnitt, statusGleisabschnitt);
    }
  }

  private void updateWeiche(String messageText) {
    Weiche statusWeiche = JsonbWithIncludeVisibility.SHORT.fromJson(messageText, Weiche.class);
    Weiche weiche = this.statusCache.getWeiche(statusWeiche.getBereich(), statusWeiche.getName());
    if (weiche != null) {
      updateStatus(weiche, statusWeiche);
    }
  }

  private void updateZentrale(String messageText) {
    Zentrale statusZentrale = JsonbWithIncludeVisibility.SHORT.fromJson(messageText, Zentrale.class);
    updateStatus(this.statusCache.getZentrale(), statusZentrale);
  }

  private <T extends StatusUpdateable<T>> void updateStatus(T object, T statusObject) {
    if (this.log.isDebugEnabled()) {
      this.log.debug(statusObject);
    }
    if (object != statusObject) {
      object.copyStatus(statusObject);
    }
    this.eventFirer.fire(object);
  }

}
