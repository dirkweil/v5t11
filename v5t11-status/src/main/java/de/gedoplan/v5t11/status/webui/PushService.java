package de.gedoplan.v5t11.status.webui;

import de.gedoplan.v5t11.status.entity.baustein.Zentrale;
import de.gedoplan.v5t11.status.entity.fahrweg.Gleis;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Signal;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Weiche;
import de.gedoplan.v5t11.status.entity.fahrzeug.Fahrzeug;
import de.gedoplan.v5t11.util.cdi.Changed;
import de.gedoplan.v5t11.util.jsf.AbstractPushService;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.ObservesAsync;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

@ServerEndpoint("/jakarta.faces.push/system-control")
@ApplicationScoped
public class PushService extends AbstractPushService {

  void fahrzeugChanged(@ObservesAsync @Changed Fahrzeug fahrzeug) {
    send("fahrzeug");
  }

  void gleisChanged(@ObservesAsync @Changed Gleis gleis) {
    send("gleis");
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
