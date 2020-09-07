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
import de.gedoplan.v5t11.leitstand.gateway.JmsClient;
import de.gedoplan.v5t11.leitstand.gateway.StatusGateway;
import de.gedoplan.v5t11.util.cdi.EventFirer;
import de.gedoplan.v5t11.util.jms.MessageCategory;
import de.gedoplan.v5t11.util.jsonb.JsonbWithIncludeVisibility;

import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public class StatusUpdater implements Runnable {
  private static final long RETRY_MILLIS = 10000;

  @Inject
  ConfigService configService;

  @Inject
  @RestClient
  StatusGateway statusGateway;

  @Inject
  JmsClient jmsClient;

  @Inject
  FahrstrassenManager fahrstrassenManager;

  @Inject
  EventFirer eventFirer;

  @Inject
  Log log;

  @Inject
  Leitstand leitstand;

  public void run() {

    while (true) {
      try {
        initializeStatus();

        propagateStatus();
      } catch (Exception e) {
        String msg = "Fehler beim Status-Update (Status/Fahrstrassen-Service down?)";
        if (this.log.isTraceEnabled()) {
          this.log.warn(msg, e);
        } else {
          this.log.warn(msg);
        }
      }

      try {
        Thread.sleep(RETRY_MILLIS);
      } catch (InterruptedException ie) {
      }
    }
  }

  /*
   * Aktuelle Zustände von Gleisabschnitten holen
   */
  private void initializeStatus() {
    if (this.log.isDebugEnabled()) {
      this.log.debug("Status initialisieren");
    }

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

  /*
   * JMS-Meldungen zu Gleisbelegungsänderungen verarbeiten
   */
  private void propagateStatus() throws NamingException, JMSException {
    if (this.log.isDebugEnabled()) {
      this.log.debug("Status-Änderungen überwachen");
    }

    while (true) {
      Message message = this.jmsClient.receive();
      String text = message.getBody(String.class);
      MessageCategory category = MessageCategory.valueOf(message.getStringProperty("category"));
      switch (category) {
      case FAHRSTRASSE:
        Fahrstrasse statusFahrstrasse = JsonbWithIncludeVisibility.SHORT.fromJson(text, Fahrstrasse.class);
        Fahrstrasse fahrstrasse = this.fahrstrassenManager.updateFahrstrasse(statusFahrstrasse);
        if (fahrstrasse != null) {
          if (this.log.isDebugEnabled()) {
            this.log.debug(statusFahrstrasse);
          }
          this.eventFirer.fire(fahrstrasse);
        }
        break;

      case GLEIS:
        Gleisabschnitt statusGleisabschnitt = JsonbWithIncludeVisibility.SHORT.fromJson(text, Gleisabschnitt.class);
        Gleisabschnitt gleisabschnitt = this.leitstand.getGleisabschnitt(statusGleisabschnitt.getBereich(), statusGleisabschnitt.getName());
        if (gleisabschnitt != null) {
          updateStatus(gleisabschnitt, statusGleisabschnitt);
        }
        break;

      case LOKCONTROLLER:
        LokController statusLokController = JsonbWithIncludeVisibility.SHORT.fromJson(text, LokController.class);
        LokController lokController = this.leitstand.getLokController(statusLokController.getId());
        if (lokController != null) {
          updateStatus(lokController, statusLokController);
        }
        break;

      case SIGNAL:
        Signal statusSignal = JsonbWithIncludeVisibility.SHORT.fromJson(text, Signal.class);
        Signal signal = this.leitstand.getSignal(statusSignal.getBereich(), statusSignal.getName());
        if (signal != null) {
          updateStatus(signal, statusSignal);
        }
        break;

      case WEICHE:
        Weiche statusWeiche = JsonbWithIncludeVisibility.SHORT.fromJson(text, Weiche.class);
        Weiche weiche = this.leitstand.getWeiche(statusWeiche.getBereich(), statusWeiche.getName());
        if (weiche != null) {
          updateStatus(weiche, statusWeiche);
        }
        break;

      case ZENTRALE:
        Zentrale statusZentrale = JsonbWithIncludeVisibility.SHORT.fromJson(text, Zentrale.class);
        updateStatus(this.leitstand.getZentrale(), statusZentrale);
        break;

      default:
        this.log.warn("Status-Message mit unbekannter Category wird ignoriert: " + message);
      }
    }
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
