package de.gedoplan.v5t11.leitstand.webui;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;
import de.gedoplan.v5t11.leitstand.entity.Leitstand;
import de.gedoplan.v5t11.leitstand.entity.fahrweg.Gleis;
import de.gedoplan.v5t11.leitstand.entity.fahrweg.Signal;
import de.gedoplan.v5t11.leitstand.entity.fahrweg.Weiche;
import de.gedoplan.v5t11.leitstand.entity.stellwerk.StellwerkDkw2;
import de.gedoplan.v5t11.leitstand.entity.stellwerk.StellwerkEinfachWeiche;
import de.gedoplan.v5t11.leitstand.entity.stellwerk.StellwerkElement;
import de.gedoplan.v5t11.leitstand.entity.stellwerk.StellwerkGleis;
import de.gedoplan.v5t11.leitstand.entity.stellwerk.StellwerkRichtung;
import de.gedoplan.v5t11.util.cdi.Changed;
import de.gedoplan.v5t11.util.domain.attribute.BereichselementId;
import de.gedoplan.v5t11.util.jsf.AbstractPushService;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.event.TransactionPhase;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.util.List;

@ServerEndpoint("/javax.faces.push/stellwerk")
@ApplicationScoped
public class PushService extends AbstractPushService {

  private ListMultimap<BereichselementId, StellwerkElement> gleisElemente = MultimapBuilder.hashKeys().arrayListValues().build();
  private ListMultimap<BereichselementId, StellwerkElement> weichenElemente = MultimapBuilder.hashKeys().arrayListValues().build();
  private ListMultimap<BereichselementId, StellwerkElement> signalElemente = MultimapBuilder.hashKeys().arrayListValues().build();

  public PushService(Leitstand leitstand) {
    leitstand
      .getStellwerke()
      .stream()
      .flatMap(x -> x.getZeilen().stream())
      .flatMap(x -> x.getElemente().stream())
      .forEach(element -> {
        if (element instanceof StellwerkGleis stellwerkGleis) {
          this.gleisElemente.put(stellwerkGleis.getId(), stellwerkGleis);
        }
        if (element instanceof StellwerkEinfachWeiche stellwerkEinfachWeiche) {
          this.weichenElemente.put(element.getId(), element);
          if (stellwerkEinfachWeiche.getGleisId() != null) {
            this.gleisElemente.put(stellwerkEinfachWeiche.getGleisId(), element);
          }
        }
        if (element instanceof StellwerkDkw2 stellwerkDkw2) {
          this.weichenElemente.put(stellwerkDkw2.getWeicheAId(), element);
          this.weichenElemente.put(stellwerkDkw2.getWeicheBId(), element);
          if (stellwerkDkw2.getGleisId() != null) {
            this.gleisElemente.put(stellwerkDkw2.getGleisId(), element);
          }
        }
        if (element.getSignalId() != null) {
          this.signalElemente.put(element.getSignalId(), element);
        }
      });
  }

  void gleisChanged(@Observes(during = TransactionPhase.AFTER_COMPLETION) @Changed Gleis gleis) {
    this.gleisElemente.get(gleis.getId()).forEach(e -> {
      send(e);
    });
  }

  void signalChanged(@Observes(during = TransactionPhase.AFTER_COMPLETION) @Changed Signal signal) {
    this.signalElemente.get(signal.getId()).forEach(e -> {
      send(e);
    });
  }

  void weicheChanged(@Observes(during = TransactionPhase.AFTER_COMPLETION) @Changed Weiche weiche) {
    this.weichenElemente.get(weiche.getId()).forEach(e -> {
      send(e);
    });
  }

  private void send(StellwerkElement element) {
    JsonObjectBuilder builder = Json.createObjectBuilder();
    builder.add("uiId", element.getUiId());

    String gleisName = null;
    Boolean gleisBesetzt = null;

    List<StellwerkRichtung> aktiveRichtungen = null;

    if (element instanceof StellwerkGleis stellwerkGleis) {
      if (stellwerkGleis.isLabel()) {
        gleisName = stellwerkGleis.getName();
      }

      aktiveRichtungen = stellwerkGleis.getRichtungen();

      gleisBesetzt = stellwerkGleis.findGleis().isBesetzt();
    }

    if (gleisName != null) {
      builder.add("g", gleisName);
    }

    if (gleisBesetzt != null) {
      builder.add("b", gleisBesetzt);
    }

    if (aktiveRichtungen != null) {
      List<String> richtungsNamen = aktiveRichtungen.stream().map(x -> x.name()).toList();
      builder.add("a", Json.createArrayBuilder(richtungsNamen));
    }

    send(builder.build());
  }

  private static void addJsonObject(String name, Gleis gleis, JsonObjectBuilder builder) {
    builder.add(name, Json.createObjectBuilder()
      .add("name", gleis.getName())
      .add("besetzt", gleis.isBesetzt()));
  }

  private static void addJsonObject(String name, Weiche weiche, JsonObjectBuilder builder) {
    builder.add(name, Json.createObjectBuilder()
      .add("name", weiche.getName())
      .add("stellung", weiche.getStellung().toString()));
  }

  private static void addJsonObject(String name, Signal signal, JsonObjectBuilder builder) {
    builder.add(name, Json.createObjectBuilder()
      .add("name", signal.getName())
      .add("stellung", signal.getStellung().toString()));
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
