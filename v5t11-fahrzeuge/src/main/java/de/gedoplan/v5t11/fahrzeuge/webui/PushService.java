package de.gedoplan.v5t11.fahrzeuge.webui;

import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.Fahrzeug;
import de.gedoplan.v5t11.util.cdi.Changed;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.ObservesAsync;
import javax.faces.push.Push;
import javax.faces.push.PushContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import de.gedoplan.v5t11.util.jsf.AbstractPushService;
import org.jboss.logging.Logger;

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
  @Override
  protected void onOpen(Session session) {
    super.onOpen(session);
  }

  @OnClose
  @Override
  protected void onClose(Session session) {
    super.onClose(session);
  }

  @OnError
  @Override
  protected void onError(Session session, Throwable throwable) {
    super.onError(session, throwable);
  }
}
