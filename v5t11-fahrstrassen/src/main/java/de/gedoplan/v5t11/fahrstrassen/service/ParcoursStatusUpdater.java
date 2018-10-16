package de.gedoplan.v5t11.fahrstrassen.service;

import de.gedoplan.v5t11.fahrstrassen.entity.Parcours;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Gleisabschnitt;
import de.gedoplan.v5t11.fahrstrassen.gateway.GleisResourceClient;
import de.gedoplan.v5t11.fahrstrassen.gateway.JmsClient;
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
public class ParcoursStatusUpdater {
  private static final long RETRY_MILLIS = 10000;

  @Inject
  ConfigService configService;

  @Inject
  GleisResourceClient gleisResourceClient;

  @Inject
  JmsClient jmsClient;

  @Inject
  EventFirer eventFirer;

  @Inject
  Log log;

  private Parcours parcours;

  protected void run(@ObservesAsync @Created Parcours parcours) {
    this.parcours = parcours;

    while (true) {
      try {
        initializeGleisStatus();

        propagateGleisStatus();
      } catch (Exception e) {
        String msg = "Fehler beim Status-Update (Status-Service down?)";
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
  private void initializeGleisStatus() {
    if (this.log.isDebugEnabled()) {
      this.log.debug("Status der Gleisbelegungen initialisieren");
    }

    this.gleisResourceClient.getGleisabschnitte().forEach(other -> {
      Gleisabschnitt gleisabschnitt = this.parcours.getGleisabschnitt(other.getBereich(), other.getName());
      if (gleisabschnitt != null) {
        if (this.log.isDebugEnabled()) {
          this.log.debug(other);
        }
        gleisabschnitt.copyStatus(other);
        this.eventFirer.fire(gleisabschnitt);
      }
    });
  }

  /*
   * JMS-Meldungen zu Gleisbelegungsänderungen verarbeiten
   */
  private void propagateGleisStatus() throws NamingException, JMSException {
    if (this.log.isDebugEnabled()) {
      this.log.debug("Änderungen der Gleisbelegungen überwachen");
    }

    while (true) {
      Message message = this.jmsClient.receive();
      String text = message.getBody(String.class);
      String category = message.getStringProperty("category");
      if (MessageCategory.GLEIS.toString().equals(category)) {
        Gleisabschnitt statusGleisabschnitt = JsonbWithIncludeVisibility.SHORT.fromJson(text, Gleisabschnitt.class);
        if (this.log.isDebugEnabled()) {
          this.log.debug(statusGleisabschnitt);
        }
        Gleisabschnitt gleisabschnitt = this.parcours.getGleisabschnitt(statusGleisabschnitt.getBereich(), statusGleisabschnitt.getName());
        if (gleisabschnitt != null) {
          gleisabschnitt.copyStatus(statusGleisabschnitt);
          this.eventFirer.fire(gleisabschnitt);
        }
      } else {
        this.log.warn("Status-Message mit unbekannter Category wird ignoriert: " + message);
      }
    }
  }

}
