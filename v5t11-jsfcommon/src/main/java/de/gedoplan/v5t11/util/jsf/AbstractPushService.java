package de.gedoplan.v5t11.util.jsf;

import de.gedoplan.baselibs.utils.util.ClassUtil;
import org.jboss.logging.Logger;

import javax.json.Json;
import javax.json.JsonValue;
import javax.websocket.Session;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public abstract class AbstractPushService {

  protected Logger logger = Logger.getLogger(ClassUtil.getProxiedClass(getClass()));

  protected Map<Session, String> sessions = new ConcurrentHashMap<>();

  protected void openSession(Session session) {
    openSession(session, "");
  }

  protected void openSession(Session session, String info) {
    this.logger.debugf("Open session: id=%s, info=%s", session.getId(), info);
    this.sessions.put(session, info);
  }

  protected void closeSession(Session session) {
    this.logger.debugf("Close session %s", session.getId());
    this.sessions.remove(session);
  }

  protected void abortSession(Session session, Throwable throwable) {
    this.logger.debugf("Abort session %s because of %s", session.getId(), throwable);
    this.sessions.remove(session);
  }

  protected void send(String text) {
    send(Json.createObjectBuilder().add("text", text).build());
  }

  protected void send(JsonValue message) {
    send(message, null);
  }

  protected void send(JsonValue message, Session session) {
    send(message, session, null);
  }

  protected void send(JsonValue message, Session session, Predicate<String> infoFilter) {
    // Falls keine Session angegeben, alle nehmen
    if (session == null) {
      this.sessions.keySet().forEach(s -> send(message, s, infoFilter));
      return;
    }

    // Falls Filter angegeben, nach Session-Info filtern
    if (infoFilter != null) {
      String info = this.sessions.get(session);
      if (!infoFilter.test(info)) {
        return;
      }
    }

    if (this.logger.isDebugEnabled()) {
      String messageAsString = message.toString();
      if (messageAsString.length() > 40) {
        messageAsString = messageAsString.substring(0, 39) + "...";
      }
      this.logger.debugf("Send %s to session %s", messageAsString, session.getId());
    }

    session.getAsyncRemote().sendText(message.toString(), result -> {
      if (result.getException() != null) {
        this.logger.error("Cannot send message", result.getException());
      }
    });
  }
}
