package de.gedoplan.v5t11.leitstand.webui;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;
import de.gedoplan.v5t11.leitstand.entity.Leitstand;
import de.gedoplan.v5t11.leitstand.entity.fahrstrasse.Fahrstrasse;
import de.gedoplan.v5t11.leitstand.entity.fahrstrasse.Fahrstrassenelement;
import de.gedoplan.v5t11.leitstand.entity.fahrweg.Gleis;
import de.gedoplan.v5t11.leitstand.entity.fahrweg.Signal;
import de.gedoplan.v5t11.leitstand.entity.fahrweg.Weiche;
import de.gedoplan.v5t11.leitstand.entity.stellwerk.StellwerkDkw2;
import de.gedoplan.v5t11.leitstand.entity.stellwerk.StellwerkEinfachWeiche;
import de.gedoplan.v5t11.leitstand.entity.stellwerk.StellwerkElement;
import de.gedoplan.v5t11.leitstand.entity.stellwerk.StellwerkGleis;
import de.gedoplan.v5t11.leitstand.entity.stellwerk.StellwerkLeer;
import de.gedoplan.v5t11.leitstand.entity.stellwerk.StellwerkRichtung;
import de.gedoplan.v5t11.leitstand.persistence.GleisRepository;
import de.gedoplan.v5t11.leitstand.service.FahrstrassenManager;
import de.gedoplan.v5t11.util.cdi.Changed;
import de.gedoplan.v5t11.util.domain.attribute.BereichselementId;
import de.gedoplan.v5t11.util.domain.attribute.FahrstrassenelementTyp;
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
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ServerEndpoint("/javax.faces.push/stellwerk/{bereich}")
@ApplicationScoped
public class PushService extends AbstractPushService {

  public static final List<String> FARBEN_SPERR_SH0 = List.of("r");
  public static final List<String> FARBEN_SPERR_SH1 = List.of("w");
  public static final List<String> FARBEN_HAUPT_HP0 = List.of("r", "-");
  public static final List<String> FARBEN_HAUPT_HP1 = List.of("g", "-");
  public static final List<String> FARBEN_HAUPT_HP2 = List.of("g", "y");
  public static final List<String> FARBEN_HAUPT_HP0_SH1 = List.of("r", "w");
  public static final List<String> FARBEN_BLOCK_HP0 = List.of("-", "r");
  public static final List<String> FARBEN_BLOCK_HP1 = List.of("-", "g");

  public static final String ATTR_UIID = "uiId";
  public static final String ATTR_GLEIS_BESETZT = "b";
  public static final String ATTR_AKTIVE_RICHTUNGEN = "a";
  public static final String ATTR_INAKTIVE_RICHTUNGEN = "i";
  public static final String ATTR_NAME = "n";
  public static final String ATTR_NAMENS_POSITION = "p";
  public static final String ATTR_FAHRSTRASSEN_TYP = "f";
  public static final String ATTR_FAHRSTRASSEN_ZAEHLRICHTUNG = "z";
  public static final String ATTR_SIGNAL_LICHTER = "l";
  public static final String ATTR_SIGNAL_POSITION = "s";

  private Leitstand leitstand;

  private ListMultimap<BereichselementId, StellwerkElement> gleisElemente = MultimapBuilder.hashKeys().arrayListValues().build();
  private ListMultimap<BereichselementId, StellwerkElement> weichenElemente = MultimapBuilder.hashKeys().arrayListValues().build();
  private ListMultimap<BereichselementId, StellwerkElement> signalElemente = MultimapBuilder.hashKeys().arrayListValues().build();

  FahrstrassenManager fahrstrassenManager;

  GleisRepository gleisRepository;

  StellwerkVorschlagService stellwerkVorschlagService;

  public PushService(Leitstand leitstand, FahrstrassenManager fahrstrassenManager, GleisRepository gleisRepository, StellwerkVorschlagService stellwerkVorschlagService) {

    this.leitstand = leitstand;
    this.fahrstrassenManager = fahrstrassenManager;
    this.gleisRepository = gleisRepository;
    this.stellwerkVorschlagService = stellwerkVorschlagService;

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

  void fahrstrasseChanged(@Observes(during = TransactionPhase.AFTER_COMPLETION) @Changed Fahrstrasse fahrstrasse) {
    send(fahrstrasse
      .getElemente()
      .stream()
      .filter(fse -> fse.getTyp() == FahrstrassenelementTyp.GLEIS)
      .map(fse -> this.gleisRepository.findById(fse.getId()))
      .flatMap(g -> this.gleisElemente.get(g.getId()).stream())
      .toList());
  }

  private void sendAll(Session session, String bereich) {
    JsonArrayBuilder builder = Json.createArrayBuilder();
    this.leitstand
      .getStellwerk(bereich)
      .getZeilen()
      .stream()
      .flatMap(zeile -> zeile.getElemente().stream())
      .filter(element -> !(element instanceof StellwerkLeer) || element.getSignalId() != null)
      .forEach(element -> builder.add(createDrawCommand(element, session.getId())));
    send(builder.build(), session);
  }

  private void send(Collection<StellwerkElement> elemente) {
    this.sessions.keySet().forEach(session -> {
      Map<String, List<StellwerkElement>> elementeProBereich = elemente.stream().collect(Collectors.groupingBy(e -> e.getStellwerksBereich()));
      elementeProBereich.forEach((stellwerksBereich, elementeDesBereichs) -> {
        JsonArrayBuilder builder = Json.createArrayBuilder();
        for (StellwerkElement element : elementeDesBereichs) {
          builder.add(createDrawCommand(element, session.getId()));
        }
        send(builder.build(), session, info -> stellwerksBereich.equals(info));
      });
    });
  }

  /**
   * Zeichenbefehl für die UI zu einem Stellwerkselement erzeugen.
   *
   * Das erzeugte Objekt ist ein JSON-Objekt mit den folgenden Attributen:
   *
   * Die UI-Id ist stets vorhanden:
   * <dl>
   *   <dt>uiId</dt><dd>Id des Canvas-Elements auf der Webseite</dd>
   * </dl>
   *
   * Falls ein Gleis gezeichnet werden soll, gibt es diese Attribute:
   * <dl>
   *   <dt>b</dt><dd>Gleis besetzt?</dd>
   *   <dt>a</dt><dd>Aktive Gleissegmente als Array der Länge 2 aus den Richtungen N, NO, O, SO, S, SW, W, NW</dd>
   * </dl>
   *
   *
   * Für Weichen gibt es diese Attribute:
   * <dl>
   *   <dt>i</dt><dd>Inaktive Gleissegmente als Array aus den Richtungen N, NO, O, SO, S, SW, W, NW</dd>
   * </dl>
   *
   * Für Signale gibt es diese Attribute:
   * <dl>
   *   <dt>l</dt><dd>Anzuzeigende Lichter als Array aus r, g, y, w, -; ein oder zwei Elemente von oben nach unten</dd>
   *   <dt>s</dt><dd>Position des Signals als Richtung N, NO, O, SO, S, SW, W, NW</dd>
   * </dl>
   *
   * Ist das Gleis Teil einer Fahrstrasse, gibt es diese Attribute:
   * <dl>
   *   <dt>f</dt><dd>Reservierungstyp als Kürzel Z, R</dd>
   *   <dt>z</dt><dd>In Zählrichtung?</dd>
   * </dl>
   *
   * Soll ein Name angezeigt werden (Gleis, Weiche, Signal), gibt es diese Attribute:
   * <dl>
   *   <dt>n</dt><dd>Name</dd>
   *   <dt>p</dt><dd>Position des Namens als Richtung N, NO, O, SO, S, SW, W, NW</dd>
   * </dl>
   *
   * @param element
   * @return
   */
  private JsonObject createDrawCommand(StellwerkElement element, String sessionId) {
    JsonObjectBuilder builder = Json.createObjectBuilder();
    builder.add(ATTR_UIID, element.getUiId());

    Gleis gleis = null;
    String name = null;
    StellwerkRichtung namensPosition = null;

    List<StellwerkRichtung> aktiveRichtungen = null;
    List<StellwerkRichtung> inaktiveRichtungen = null;

    if (element instanceof StellwerkGleis stellwerkGleis) {
      if (stellwerkGleis.isLabel()) {
        name = stellwerkGleis.getName();
        namensPosition = stellwerkGleis.getLabelPos();
      }

      aktiveRichtungen = stellwerkGleis.getRichtungen();

      gleis = stellwerkGleis.findGleis();
    } else if (element instanceof StellwerkEinfachWeiche stellwerkEinfachWeiche) {
      name = stellwerkEinfachWeiche.getName();
      namensPosition = stellwerkEinfachWeiche.getLabelPos();

      aktiveRichtungen = new ArrayList<>();
      inaktiveRichtungen = new ArrayList<>();
      addRichtungen(stellwerkEinfachWeiche.findWeiche(), stellwerkEinfachWeiche.getGeradeRichtung(), stellwerkEinfachWeiche.getAbzweigendRichtung(), aktiveRichtungen, inaktiveRichtungen);
      if (stellwerkEinfachWeiche.isStammIstEinfahrt()) {
        aktiveRichtungen.add(0, stellwerkEinfachWeiche.getStammRichtung());
      } else {
        aktiveRichtungen.add(stellwerkEinfachWeiche.getStammRichtung());
      }

      gleis = stellwerkEinfachWeiche.findGleis();
    } else if (element instanceof StellwerkDkw2 stellwerkDkw2) {
      name = stellwerkDkw2.getName();
      namensPosition = stellwerkDkw2.getLabelPos();

      aktiveRichtungen = new ArrayList<>();
      inaktiveRichtungen = new ArrayList<>();
      addRichtungen(stellwerkDkw2.findWeicheA(), stellwerkDkw2.getGeradeRichtung()[0], stellwerkDkw2.getAbzweigRichtung()[0], aktiveRichtungen, inaktiveRichtungen);
      addRichtungen(stellwerkDkw2.findWeicheB(), stellwerkDkw2.getGeradeRichtung()[1], stellwerkDkw2.getAbzweigRichtung()[1], aktiveRichtungen, inaktiveRichtungen);

      gleis = stellwerkDkw2.findGleis();
    }

    if (gleis != null) {
      builder.add(ATTR_GLEIS_BESETZT, gleis.isBesetzt());

      addFahrstrasse(gleis, sessionId, builder);

    }

    if (aktiveRichtungen != null) {
      List<String> richtungsNamen = aktiveRichtungen.stream().map(x -> x.name()).toList();
      builder.add(ATTR_AKTIVE_RICHTUNGEN, Json.createArrayBuilder(richtungsNamen));
    }

    if (inaktiveRichtungen != null) {
      List<String> richtungsNamen = inaktiveRichtungen.stream().map(x -> x.name()).toList();
      builder.add(ATTR_INAKTIVE_RICHTUNGEN, Json.createArrayBuilder(richtungsNamen));
    }

    if (element.getSignalId() != null) {
      addSignal(element.findSignal(), element.getSignalPosition(), builder);
    }

    if (name != null && namensPosition != null) {
      builder.add(ATTR_NAME, name);
      builder.add(ATTR_NAMENS_POSITION, namensPosition.toString());
    }

    return builder.build();
  }

  private void addFahrstrasse(Gleis gleis, String sessionId, JsonObjectBuilder builder) {
    String f = null;
    Fahrstrassenelement fahrstrassenelement = null;

    Fahrstrasse fahrstrasse = this.fahrstrassenManager.getReservierteFahrstrasse(gleis);
    if (fahrstrasse != null) {
      // Gleis ist in aktiver Fahrstrasse
      f = fahrstrasse.getReservierungsTyp().toString();
      fahrstrassenelement = fahrstrasse.getElement(gleis, true);
    } else {
      fahrstrasse = this.stellwerkVorschlagService.getVorgeschlageneFahrstrasse(gleis, sessionId);
      if (fahrstrasse != null) {
        // Gleis ist in vorgeschlagener Fahrstrasse
        f = "V";
      } else {
        fahrstrasse = this.stellwerkVorschlagService.getAlternativeFahrstrasse(gleis, sessionId);
        // Gleis könnte in alternativ vorgeschlagener Fahrstrasse sein
        // (Prüfung überflüssig wegen nächster Abfrage)
        f = "A";
      }

      if (fahrstrasse != null) {
        fahrstrassenelement = fahrstrasse.getElement(gleis, false);
      }
    }

    if (f != null && fahrstrassenelement != null) {
      builder.add(ATTR_FAHRSTRASSEN_TYP, f);
      builder.add(ATTR_FAHRSTRASSEN_ZAEHLRICHTUNG, fahrstrassenelement.isZaehlrichtung());
    }
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

  private static void addSignal(Signal signal, String signalPosition, JsonObjectBuilder builder) {

    List<String> lichter = switch (signal.getTyp()) {
      case SPERRSIGNAL -> switch (signal.getStellung()) {
        default -> FARBEN_SPERR_SH0;
        case FAHRT, LANGSAMFAHRT, RANGIERFAHRT -> FARBEN_SPERR_SH1;
      };
      case HAUPTSPERRSIGNAL -> switch (signal.getStellung()) {
        default -> FARBEN_HAUPT_HP0;
        case FAHRT -> FARBEN_HAUPT_HP1;
        case LANGSAMFAHRT -> FARBEN_HAUPT_HP2;
        case RANGIERFAHRT -> FARBEN_HAUPT_HP0_SH1;
      };
      case HAUPTSIGNAL_RT_GE -> switch (signal.getStellung()) {
        default -> FARBEN_HAUPT_HP0;
        case FAHRT, LANGSAMFAHRT -> FARBEN_HAUPT_HP2;
      };
      case HAUPTSIGNAL_RT_GN -> switch (signal.getStellung()) {
        default -> FARBEN_BLOCK_HP0;
        case FAHRT, LANGSAMFAHRT -> FARBEN_BLOCK_HP1;
      };
      case HAUPTSIGNAL_RT_GE_GN -> switch (signal.getStellung()) {
        default -> FARBEN_HAUPT_HP0;
        case FAHRT -> FARBEN_BLOCK_HP1;
        case LANGSAMFAHRT -> FARBEN_HAUPT_HP2;
      };
      default -> List.of();
    };

    if (!lichter.isEmpty()) {
      builder.add(ATTR_SIGNAL_LICHTER, Json.createArrayBuilder(lichter));
      builder.add(ATTR_SIGNAL_POSITION, signalPosition != null ? signalPosition : "N");
    }
  }

  @Inject
  ManagedExecutor managedExecutor;

  @OnOpen
  protected void onOpen(Session session, @PathParam("bereich") String bereich) {
    super.openSession(session, bereich);
    this.managedExecutor.runAsync(() -> {
      send(Json.createObjectBuilder().add("wsid", session.getId()).build(), session);
      sendAll(session, bereich);
    });
  }

  @OnClose
  protected void onClose(Session session) {
    super.closeSession(session);
  }

  @OnError
  protected void onError(Session session, Throwable throwable) {
    super.abortSession(session, throwable);
  }
}
