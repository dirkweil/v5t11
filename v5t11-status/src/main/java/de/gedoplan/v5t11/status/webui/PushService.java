package de.gedoplan.v5t11.status.webui;

import de.gedoplan.v5t11.status.entity.baustein.Zentrale;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Signal;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Weiche;
import de.gedoplan.v5t11.status.entity.fahrzeug.Fahrzeug;
import de.gedoplan.v5t11.util.cdi.Changed;
import io.vertx.core.impl.ConcurrentHashSet;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.ObservesAsync;
import javax.inject.Inject;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.util.Set;

@ServerEndpoint("/javax.faces.push/uiRefresh")
@ApplicationScoped
public class PushService {

  @Inject
  Logger logger;

  void fahrzeugChanged(@ObservesAsync @Changed Fahrzeug fahrzeug) {
    send("fahrzeug");
  }

  void signalChanged(@ObservesAsync @Changed Signal signal) {
    send("signal");
  }

  void weicheChanged(@ObservesAsync @Changed Weiche weiche) {
    send("weiche");
  }

  void zentraleChanged(@ObservesAsync @Changed Zentrale zentrale) {
    send("zentrale");
  }

  private Set<Session> sessions = new ConcurrentHashSet<>();

  @OnOpen
  void onOpen(Session session) {
    sessions.add(session);
    this.logger.debugf("Opened session %s", session);
  }

  @OnClose
  void onClose(Session session) {
    sessions.remove(session);
    this.logger.debugf("Closed session %s", session);
  }

  @OnError
  void onError(Session session, Throwable throwable) {
    sessions.remove(session);
    this.logger.debugf("Closed session %s because of %s", session, throwable);
  }

  private void send(String event) {
    this.logger.debugf("Send refresh event %s to %d browser sessions", event, sessions.size());
    String message = "{\"data\":\"" + event + "\"}";
    sessions.forEach(s -> {
      s.getAsyncRemote().sendText(message, result -> {
        if (result.getException() != null) {
          this.logger.error("Cannot send message", result.getException());
        }
      });
    });
  }
}
