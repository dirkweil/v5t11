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
import de.gedoplan.v5t11.leitstand.entity.stellwerk.StellwerkLeer;
import de.gedoplan.v5t11.leitstand.entity.stellwerk.StellwerkRichtung;
import de.gedoplan.v5t11.util.cdi.Changed;
import de.gedoplan.v5t11.util.domain.attribute.BereichselementId;
import de.gedoplan.v5t11.util.domain.attribute.WeichenStellung;
import de.gedoplan.v5t11.util.jsf.AbstractPushService;
import org.eclipse.microprofile.context.ManagedExecutor;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.event.TransactionPhase;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@ServerEndpoint("/javax.faces.push/stellwerk")
@ApplicationScoped
public class PushService extends AbstractPushService {

  private Leitstand leitstand;

  private ListMultimap<BereichselementId, StellwerkElement> gleisElemente = MultimapBuilder.hashKeys().arrayListValues().build();
  private ListMultimap<BereichselementId, StellwerkElement> weichenElemente = MultimapBuilder.hashKeys().arrayListValues().build();
  private ListMultimap<BereichselementId, StellwerkElement> signalElemente = MultimapBuilder.hashKeys().arrayListValues().build();

  public PushService(Leitstand leitstand) {

    this.leitstand = leitstand;

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
    send(this.gleisElemente.get(gleis.getId()));
  }

  void signalChanged(@Observes(during = TransactionPhase.AFTER_COMPLETION) @Changed Signal signal) {
    send(this.signalElemente.get(signal.getId()));
  }

  void weicheChanged(@Observes(during = TransactionPhase.AFTER_COMPLETION) @Changed Weiche weiche) {
    send(this.weichenElemente.get(weiche.getId()));
  }

  private void sendAll() {
    send(this.leitstand
      .getStellwerke()
      .stream()
      .flatMap(x -> x.getZeilen().stream())
      .flatMap(x -> x.getElemente().stream())
      .filter(x -> !(x instanceof StellwerkLeer) || x.getSignalId() != null)
      .toList());
  }

  private void send(Collection<StellwerkElement> elemente) {
    JsonArrayBuilder builder = Json.createArrayBuilder();
    for (StellwerkElement element : elemente) {
      builder.add(createJsonObject(element));
    }
    send(builder.build());
  }

  private static JsonObject createJsonObject(StellwerkElement element) {
    JsonObjectBuilder builder = Json.createObjectBuilder();
    builder.add("uiId", element.getUiId());

    String gleisName = null;
    Boolean gleisBesetzt = null;

    String weichenName = null;

    List<StellwerkRichtung> aktiveRichtungen = null;
    List<StellwerkRichtung> inaktiveRichtungen = null;

    if (element instanceof StellwerkGleis stellwerkGleis) {
      if (stellwerkGleis.isLabel()) {
        weichenName = stellwerkGleis.getName();
      }

      aktiveRichtungen = stellwerkGleis.getRichtungen();

      gleisBesetzt = stellwerkGleis.findGleis().isBesetzt();
    } else if (element instanceof StellwerkEinfachWeiche stellwerkEinfachWeiche) {
      weichenName = stellwerkEinfachWeiche.getName();

      aktiveRichtungen = new ArrayList<>();
      inaktiveRichtungen = new ArrayList<>();
      addRichtungen(stellwerkEinfachWeiche.findWeiche(), stellwerkEinfachWeiche.getGeradeRichtung(), stellwerkEinfachWeiche.getAbzweigendRichtung(), aktiveRichtungen, inaktiveRichtungen);
      if (stellwerkEinfachWeiche.isStammIstEinfahrt()) {
        aktiveRichtungen.add(stellwerkEinfachWeiche.getStammRichtung());
      } else {
        aktiveRichtungen.add(0, stellwerkEinfachWeiche.getStammRichtung());
      }
    } else if (element instanceof StellwerkDkw2 stellwerkDkw2) {
      weichenName = stellwerkDkw2.getName();

      aktiveRichtungen = new ArrayList<>();
      inaktiveRichtungen = new ArrayList<>();
      addRichtungen(stellwerkDkw2.findWeicheA(), stellwerkDkw2.getGeradeRichtung()[0], stellwerkDkw2.getAbzweigRichtung()[0], aktiveRichtungen, inaktiveRichtungen);
      addRichtungen(stellwerkDkw2.findWeicheB(), stellwerkDkw2.getGeradeRichtung()[1], stellwerkDkw2.getAbzweigRichtung()[1], aktiveRichtungen, inaktiveRichtungen);
    }

    if (gleisName != null) {
      builder.add("g", gleisName);
    }

    if (gleisBesetzt != null) {
      builder.add("b", gleisBesetzt);
    }

    if (weichenName != null) {
      builder.add("w", weichenName);
    }

    if (aktiveRichtungen != null) {
      List<String> richtungsNamen = aktiveRichtungen.stream().map(x -> x.name()).toList();
      builder.add("a", Json.createArrayBuilder(richtungsNamen));
    }

    if (inaktiveRichtungen != null) {
      List<String> richtungsNamen = inaktiveRichtungen.stream().map(x -> x.name()).toList();
      builder.add("i", Json.createArrayBuilder(richtungsNamen));
    }

    return builder.build();
  }

  private static void addRichtungen(Weiche weiche, StellwerkRichtung geradeRichtung, StellwerkRichtung abzweigendRichtung, List<StellwerkRichtung> aktiveRichtungen,
    List<StellwerkRichtung> inaktiveRichtungen) {
    if (weiche.getStellung().equals(WeichenStellung.GERADE)) {
      aktiveRichtungen.add(geradeRichtung);
      inaktiveRichtungen.add(abzweigendRichtung);
    } else {
      aktiveRichtungen.add(abzweigendRichtung);
      inaktiveRichtungen.add(geradeRichtung);
    }
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

  @Inject
  ManagedExecutor managedExecutor;

  @OnOpen
  protected void onOpen(Session session) {
    super.onOpen(session);
    this.logger.debugf("sendAll");
    this.managedExecutor.runAsync(this::sendAll);
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
