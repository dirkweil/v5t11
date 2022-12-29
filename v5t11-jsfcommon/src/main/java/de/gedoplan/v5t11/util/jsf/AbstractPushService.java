package de.gedoplan.v5t11.util.jsf;

import de.gedoplan.baselibs.utils.util.ClassUtil;
import org.jboss.logging.Logger;

import javax.json.Json;
import javax.json.JsonValue;
import javax.websocket.Session;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public abstract class AbstractPushService {

  protected Logger logger = Logger.getLogger(ClassUtil.getProxiedClass(getClass()));

  private Set<Session> sessions = new CopyOnWriteArraySet<>();

  protected void onOpen(Session session) {
    this.sessions.add(session);
    this.logger.debugf("Opened session %s", session);
  }

  protected void onClose(Session session) {
    this.sessions.remove(session);
    this.logger.debugf("Closed session %s", session);
  }

  protected void onError(Session session, Throwable throwable) {
    this.sessions.remove(session);
    this.logger.debugf("Closed session %s because of %s", session, throwable);
  }

  protected void send(String text) {
    send(Json.createObjectBuilder().add("text", text).build());
  }

  protected void send(JsonValue message) {
    this.logger.debugf("Send %s to %d browser sessions!", message, this.sessions.size());
    this.sessions.forEach(s -> {
      s.getAsyncRemote().sendText(message.toString(), result -> {
        if (result.getException() != null) {
          this.logger.error("Cannot send message", result.getException());
        }
      });
    });
  }
}
