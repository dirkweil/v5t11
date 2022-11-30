package de.gedoplan.v5t11.util.jsf;

import de.gedoplan.baselibs.utils.util.ClassUtil;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.websocket.Session;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public abstract class AbstractPushService {

  Logger logger = Logger.getLogger(ClassUtil.getProxiedClass(getClass()));

  private Set<Session> sessions = new CopyOnWriteArraySet<>();

  protected void onOpen(Session session) {
    sessions.add(session);
    this.logger.debugf("Opened session %s", session);
  }

  protected void onClose(Session session) {
    sessions.remove(session);
    this.logger.debugf("Closed session %s", session);
  }

  protected void onError(Session session, Throwable throwable) {
    sessions.remove(session);
    this.logger.debugf("Closed session %s because of %s", session, throwable);
  }

  protected void send(String event) {
    this.logger.debugf("Send event %s to %d browser sessions", event, sessions.size());
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
