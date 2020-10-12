package de.gedoplan.v5t11.leitstand.service;

import de.gedoplan.v5t11.leitstand.entity.Leitstand;
import de.gedoplan.v5t11.leitstand.entity.baustein.LokController;
import de.gedoplan.v5t11.leitstand.entity.baustein.Zentrale;
import de.gedoplan.v5t11.leitstand.entity.fahrstrasse.Fahrstrasse;
import de.gedoplan.v5t11.leitstand.entity.fahrweg.Gleisabschnitt;
import de.gedoplan.v5t11.leitstand.entity.fahrweg.Signal;
import de.gedoplan.v5t11.leitstand.entity.fahrweg.StatusUpdateable;
import de.gedoplan.v5t11.leitstand.entity.fahrweg.Weiche;
import de.gedoplan.v5t11.leitstand.entity.lok.Lok;
import de.gedoplan.v5t11.leitstand.gateway.StatusGateway;
import de.gedoplan.v5t11.util.cdi.EventFirer;
import de.gedoplan.v5t11.util.jms.MessageCategory;
import de.gedoplan.v5t11.util.jsonb.JsonbWithIncludeVisibility;
import de.gedoplan.v5t11.util.service.AbstractStatusUpdater;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
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
  FahrstrassenManager fahrstrassenManager;

  @Inject
  Leitstand leitstand;

  @Inject
  EventFirer eventFirer;

  @Override
  protected Map<MessageCategory, Consumer<String>> getMessageHandler() {
    Map<MessageCategory, Consumer<String>> messageHandler = new EnumMap<MessageCategory, Consumer<String>>(MessageCategory.class);
    messageHandler.put(MessageCategory.FAHRSTRASSE, this::updateFahrstrasse);
    messageHandler.put(MessageCategory.GLEIS, this::updateGleisabschnitt);
    messageHandler.put(MessageCategory.LOKCONTROLLER, this::updateLokcontroller);
    messageHandler.put(MessageCategory.GLEIS, this::updateGleisabschnitt);
    messageHandler.put(MessageCategory.SIGNAL, this::updateSignal);
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
      Gleisabschnitt gleisabschnitt = this.leitstand.getGleisabschnitt(statusGleisabschnitt.getBereich(), statusGleisabschnitt.getName());
      if (gleisabschnitt != null) {
        updateStatus(gleisabschnitt, statusGleisabschnitt);
      }
    });

    this.statusGateway.getSignale().forEach(statusSignal -> {
      Signal signal = this.leitstand.getSignal(statusSignal.getBereich(), statusSignal.getName());
      if (signal != null) {
        updateStatus(signal, statusSignal);
      }
    });

    this.statusGateway.getWeichen().forEach(statusWeiche -> {
      Weiche weiche = this.leitstand.getWeiche(statusWeiche.getBereich(), statusWeiche.getName());
      if (weiche != null) {
        updateStatus(weiche, statusWeiche);
      }
    });

    Zentrale statusZentrale = this.statusGateway.getZentrale();
    updateStatus(this.leitstand.getZentrale(), statusZentrale);

    Set<Lok> statusLoks = this.statusGateway.getLoks();
    this.leitstand.getLoks().retainAll(statusLoks);
    statusLoks.forEach(statusLok -> {
      Lok lok = this.leitstand.getLok(statusLok.getId());
      if (lok == null) {
        this.leitstand.getLoks().add(statusLok);
        lok = statusLok;
      }
      updateStatus(lok, statusLok);
    });

    Set<LokController> statusLokControllers = this.statusGateway.getLokcontroller();
    this.leitstand.getLokController().retainAll(statusLokControllers);
    statusLokControllers.forEach(statusLokController -> {
      LokController lokController = this.leitstand.getLokController(statusLokController.getId());
      if (lokController == null) {
        this.leitstand.getLokController().add(statusLokController);
        lokController = statusLokController;
      }
      updateStatus(lokController, statusLokController);
    });
  }

  private void updateFahrstrasse(String messageText) {
    Fahrstrasse statusFahrstrasse = JsonbWithIncludeVisibility.SHORT.fromJson(messageText, Fahrstrasse.class);
    Fahrstrasse fahrstrasse = this.fahrstrassenManager.updateFahrstrasse(statusFahrstrasse);
    if (fahrstrasse != null) {
      if (this.log.isDebugEnabled()) {
        this.log.debug(statusFahrstrasse);
      }
      this.eventFirer.fire(fahrstrasse);
    }
  }

  private void updateLokcontroller(String messageText) {
    LokController statusLokController = JsonbWithIncludeVisibility.SHORT.fromJson(messageText, LokController.class);
    LokController lokController = this.leitstand.getLokController(statusLokController.getId());
    if (lokController != null) {
      updateStatus(lokController, statusLokController);
    }
  }

  private void updateGleisabschnitt(String messageText) {
    Gleisabschnitt statusGleisabschnitt = JsonbWithIncludeVisibility.SHORT.fromJson(messageText, Gleisabschnitt.class);
    Gleisabschnitt gleisabschnitt = this.leitstand.getGleisabschnitt(statusGleisabschnitt.getBereich(), statusGleisabschnitt.getName());
    if (gleisabschnitt != null) {
      updateStatus(gleisabschnitt, statusGleisabschnitt);
    }
  }

  private void updateSignal(String messageText) {
    Signal statusSignal = JsonbWithIncludeVisibility.SHORT.fromJson(messageText, Signal.class);
    Signal signal = this.leitstand.getSignal(statusSignal.getBereich(), statusSignal.getName());
    if (signal != null) {
      updateStatus(signal, statusSignal);
    }
  }

  private void updateWeiche(String messageText) {
    Weiche statusWeiche = JsonbWithIncludeVisibility.SHORT.fromJson(messageText, Weiche.class);
    Weiche weiche = this.leitstand.getWeiche(statusWeiche.getBereich(), statusWeiche.getName());
    if (weiche != null) {
      updateStatus(weiche, statusWeiche);
    }
  }

  private void updateZentrale(String messageText) {
    Zentrale statusZentrale = JsonbWithIncludeVisibility.SHORT.fromJson(messageText, Zentrale.class);
    updateStatus(this.leitstand.getZentrale(), statusZentrale);
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
