package de.gedoplan.v5t11.leitstand.service;

import de.gedoplan.v5t11.leitstand.entity.Leitstand;
import de.gedoplan.v5t11.leitstand.entity.baustein.LokController;
import de.gedoplan.v5t11.leitstand.entity.baustein.Zentrale;
import de.gedoplan.v5t11.leitstand.entity.fahrstrasse.Fahrstrasse;
import de.gedoplan.v5t11.leitstand.entity.fahrweg.Gleisabschnitt;
import de.gedoplan.v5t11.leitstand.entity.fahrweg.Signal;
import de.gedoplan.v5t11.leitstand.entity.fahrweg.Weiche;
import de.gedoplan.v5t11.leitstand.gateway.GleisResourceClient;
import de.gedoplan.v5t11.leitstand.gateway.JmsClient;
import de.gedoplan.v5t11.leitstand.gateway.LokControllerResourceClient;
import de.gedoplan.v5t11.leitstand.gateway.LokResourceClient;
import de.gedoplan.v5t11.leitstand.gateway.SignalResourceClient;
import de.gedoplan.v5t11.leitstand.gateway.WeicheResourceClient;
import de.gedoplan.v5t11.util.cdi.Created;
import de.gedoplan.v5t11.util.cdi.EventFirer;
import de.gedoplan.v5t11.util.jms.MessageCategory;
import de.gedoplan.v5t11.util.jsonb.JsonbWithIncludeVisibility;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.ObservesAsync;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;

@ApplicationScoped
public class StatusUpdater {
  private static final long RETRY_MILLIS = 10000;

  @Inject
  ConfigService configService;

  @Inject
  GleisResourceClient gleisResourceClient;

  @Inject
  SignalResourceClient signalResourceClient;

  @Inject
  WeicheResourceClient weicheResourceClient;

  @Inject
  LokResourceClient lokResourceClient;

  @Inject
  LokControllerResourceClient lokControllerResourceClient;

  @Inject
  JmsClient jmsClient;

  @Inject
  FahrstrassenManager fahrstrassenManager;

  @Inject
  EventFirer eventFirer;

  @Inject
  Log log;

  private Leitstand leitstand;

  protected void run(@ObservesAsync @Created Leitstand leitstand) {
    this.leitstand = leitstand;

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
      } catch (InterruptedException ie) {}
    }
  }

  /*
   * Aktuelle Zustände von Gleisabschnitten holen
   */
  private void initializeStatus() {
    if (this.log.isDebugEnabled()) {
      this.log.debug("Status initialisieren");
    }

    this.gleisResourceClient.getGleisabschnitte().forEach(other -> {
      Gleisabschnitt gleisabschnitt = this.leitstand.getGleisabschnitt(other.getBereich(), other.getName());
      if (gleisabschnitt != null) {
        gleisabschnitt.copyStatus(other);
      }
    });

    this.signalResourceClient.getSignale().forEach(other -> {
      Signal signal = this.leitstand.getSignal(other.getBereich(), other.getName());
      if (signal != null) {
        signal.copyStatus(other);
      }
    });

    this.weicheResourceClient.getWeichen().forEach(other -> {
      Weiche weiche = this.leitstand.getWeiche(other.getBereich(), other.getName());
      if (weiche != null) {
        weiche.copyStatus(other);
      }
    });

    this.leitstand.getLoks().clear();
    this.leitstand.getLoks().addAll(this.lokResourceClient.getLoks());

    this.leitstand.getLokController().clear();
    this.leitstand.getLokController().addAll(this.lokControllerResourceClient.getLokController());
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
        if (this.log.isDebugEnabled()) {
          this.log.debug(statusFahrstrasse);
        }

        Fahrstrasse fahrstrasse = this.fahrstrassenManager.updateFahrstrasse(statusFahrstrasse);
        if (fahrstrasse != null) {
          this.eventFirer.fire(fahrstrasse);
        }
        break;

      case GLEIS:
        Gleisabschnitt statusGleisabschnitt = JsonbWithIncludeVisibility.SHORT.fromJson(text, Gleisabschnitt.class);
        if (this.log.isDebugEnabled()) {
          this.log.debug(statusGleisabschnitt + " -> " + (statusGleisabschnitt.isBesetzt() ? "besetzt" : "frei"));
        }
        Gleisabschnitt gleisabschnitt = this.leitstand.getGleisabschnitt(statusGleisabschnitt.getBereich(), statusGleisabschnitt.getName());
        if (gleisabschnitt != null) {
          gleisabschnitt.copyStatus(statusGleisabschnitt);
          this.eventFirer.fire(gleisabschnitt);
        }
        break;

      case LOKCONTROLLER:
        LokController statusLokController = JsonbWithIncludeVisibility.SHORT.fromJson(text, LokController.class);
        if (this.log.isDebugEnabled()) {
          this.log.debug(statusLokController);
        }
        LokController lokController = this.leitstand.getLokController(statusLokController.getId());
        if (lokController != null) {
          lokController.copyStatus(statusLokController);
          this.eventFirer.fire(lokController);
        }
        break;

      case SIGNAL:
        Signal statusSignal = JsonbWithIncludeVisibility.SHORT.fromJson(text, Signal.class);
        if (this.log.isDebugEnabled()) {
          this.log.debug(statusSignal + " -> " + statusSignal.getStellung());
        }
        Signal signal = this.leitstand.getSignal(statusSignal.getBereich(), statusSignal.getName());
        if (signal != null) {
          signal.copyStatus(statusSignal);
          this.eventFirer.fire(signal);
        }
        break;

      case WEICHE:
        Weiche statusWeiche = JsonbWithIncludeVisibility.SHORT.fromJson(text, Weiche.class);
        if (this.log.isDebugEnabled()) {
          this.log.debug(statusWeiche + " -> " + statusWeiche.getStellung());
        }
        Weiche weiche = this.leitstand.getWeiche(statusWeiche.getBereich(), statusWeiche.getName());
        if (weiche != null) {
          weiche.copyStatus(statusWeiche);
          this.eventFirer.fire(weiche);
        }
        break;

      case ZENTRALE:
        Zentrale statusZentrale = JsonbWithIncludeVisibility.SHORT.fromJson(text, Zentrale.class);
        if (this.log.isDebugEnabled()) {
          this.log.debug(statusZentrale);
        }

        this.leitstand.getZentrale().copyStatus(statusZentrale);
        this.eventFirer.fire(this.leitstand.getZentrale());
        break;

      default:
        this.log.warn("Status-Message mit unbekannter Category wird ignoriert: " + message);
      }
    }
  }

}
