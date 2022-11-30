package de.gedoplan.v5t11.status.webui;

import de.gedoplan.v5t11.status.entity.baustein.Zentrale;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Signal;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Weiche;
import de.gedoplan.v5t11.status.entity.fahrzeug.Fahrzeug;
import de.gedoplan.v5t11.util.cdi.Changed;
import de.gedoplan.v5t11.util.jsf.AbstractPushService;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.ObservesAsync;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/javax.faces.push/system-control")
@ApplicationScoped
public class PushService extends AbstractPushService {

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
