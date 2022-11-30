package de.gedoplan.v5t11.status.webui;

import de.gedoplan.v5t11.status.entity.baustein.Zentrale;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Signal;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Weiche;
import de.gedoplan.v5t11.status.entity.fahrzeug.Fahrzeug;
import de.gedoplan.v5t11.util.cdi.Changed;
import io.vertx.core.impl.ConcurrentHashSet;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.event.ObservesAsync;
import javax.faces.event.WebsocketEvent;
import javax.faces.push.Push;
import javax.faces.push.PushContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/javax.faces.push/uiRefresh")
@ApplicationScoped
public class PushService {

// Push über PushContext funktioniert mit Quarkus/MyFaces nicht
//  @Inject
//  @Push(channel = "uiRefresh")
//  PushContext uiRefresh;

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

//  void send(String event) {
//    int sessionCount = this.uiRefresh.send(event).size();
//    this.logger.debugf("Sent refresh event %s to %d browser sessions", event, sessionCount);
//  }

//  public void onOpen(@Observes @WebsocketEvent.Opened WebsocketEvent event) {
//    logger.debugf("Open websocket connection for channel %s", event.getChannel());
//  }

//  public void onClose(@Observes @WebsocketEvent.Closed WebsocketEvent event) {
//    logger.debugf("Close websocket connection for channel %s", event.getChannel());
//  }

  Set<Session> sessions = new ConcurrentHashSet<>();

  @OnOpen
  public void onOpen(Session session) {
    sessions.add(session);
    this.logger.debugf("Opened session %s", session);
  }

  @OnClose
  public void onClose(Session session) {
    sessions.remove(session);
    this.logger.debugf("Closed session %s", session);
  }

  @OnError
  public void onError(Session session, Throwable throwable) {
    sessions.remove(session);
    this.logger.debugf("Closed session %s because of %s", session, throwable);
  }

  private void send(String event) {
    this.logger.debugf("Send refresh event %s to %d browser sessions", event, sessions.size());
    String message = "{\"data\":\"" + event + "\"}";
    sessions.forEach(s -> {
      s.getAsyncRemote().sendText(message, result ->  {
        if (result.getException() != null) {
          this.logger.error("Cannot send message", result.getException());
        }
      });
    });
  }
}
