package de.gedoplan.v5t11.fahrzeuge.webui;

import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.Fahrzeug;
import de.gedoplan.v5t11.util.cdi.Changed;
import de.gedoplan.v5t11.util.jsf.AbstractPushService;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.ObservesAsync;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/javax.faces.push/fahrzeug-control")
@ApplicationScoped
public class PushService extends AbstractPushService {

  void fahrzeugChanged(@ObservesAsync @Changed Fahrzeug fahrzeug) {
    send(getEventName(fahrzeug));
  }

  public String getEventName(Fahrzeug fahrzeug) {
    return "_" + fahrzeug.getId().getAdresse() + "_" + fahrzeug.getId().getSystemTyp().name();
  }

  @OnOpen
  protected void onOpen(Session session) {
    openSession(session);
  }

  @OnClose
  protected void onClose(Session session) {
    closeSession(session);
  }

  @OnError
  protected void onError(Session session, Throwable throwable) {
    abortSession(session, throwable);
  }
}
