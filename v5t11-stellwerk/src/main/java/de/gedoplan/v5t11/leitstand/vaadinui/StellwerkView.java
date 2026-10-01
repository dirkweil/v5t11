package de.gedoplan.v5t11.leitstand.vaadinui;

import de.gedoplan.v5t11.leitstand.entity.Leitstand;
import de.gedoplan.v5t11.leitstand.entity.fahrstrasse.Fahrstrasse;
import de.gedoplan.v5t11.leitstand.entity.fahrstrasse.Fahrstrassenelement;
import de.gedoplan.v5t11.leitstand.entity.fahrweg.Gleis;
import de.gedoplan.v5t11.leitstand.entity.fahrweg.Signal;
import de.gedoplan.v5t11.leitstand.entity.fahrweg.Weiche;
import de.gedoplan.v5t11.leitstand.entity.stellwerk.Stellwerk;
import de.gedoplan.v5t11.leitstand.entity.stellwerk.StellwerkDkw2;
import de.gedoplan.v5t11.leitstand.entity.stellwerk.StellwerkEinfachWeiche;
import de.gedoplan.v5t11.leitstand.entity.stellwerk.StellwerkElement;
import de.gedoplan.v5t11.leitstand.entity.stellwerk.StellwerkGleis;
import de.gedoplan.v5t11.leitstand.entity.stellwerk.StellwerkLeer;
import de.gedoplan.v5t11.leitstand.gateway.FahrstrassenGateway;
import de.gedoplan.v5t11.leitstand.gateway.StatusGateway;
import de.gedoplan.v5t11.leitstand.persistence.SignalRepository;
import de.gedoplan.v5t11.leitstand.persistence.WeicheRepository;
import de.gedoplan.v5t11.leitstand.service.FahrstrassenManager;
import de.gedoplan.v5t11.leitstand.webui.StellwerkDrawCommandBuilder;
import de.gedoplan.v5t11.leitstand.webui.StellwerkDrawCommandBuilder.FahrstrassenVorschlag;
import de.gedoplan.v5t11.leitstand.webui.VaadinChangePushBroadcaster;
import de.gedoplan.v5t11.util.domain.attribute.BereichselementId;
import de.gedoplan.v5t11.util.domain.attribute.FahrstrassenFilter;
import de.gedoplan.v5t11.util.domain.attribute.FahrstrassenReservierungsTyp;
import de.gedoplan.v5t11.util.domain.attribute.SignalStellung;
import de.gedoplan.v5t11.util.domain.attribute.WeichenStellung;
import de.gedoplan.v5t11.vaadincommon.ui.MainLayout;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.FontIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.NotFoundException;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.json.JsonArray;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.eclipse.microprofile.rest.client.inject.RestClient;

/**
 * Vaadin-Pendant zum bisherigen {@code view/stellwerk.xhtml}/{@code StellwerkPresenter} (Phase 3 der
 * JSF->Vaadin-Migration, siehe {@code docs/11-Agentische-migration-faces-vaadin/Phase-3/plan.md}).
 * <p>
 * Anders als {@code StellwerkSessionHolder} (JSF, {@code @SessionScoped}, ein mutable {@code stellwerk}-Feld pro
 * HTTP-Session) hält diese View den aufgelösten {@link Stellwerk} als eigenes, view-lokales Feld. <b>Achtung:</b> Bei
 * einer Client-seitigen SPA-Navigation zwischen zwei Bereichen (z. B. "HBf" -> "NBf", beides derselbe Routen-Typ
 * {@code StellwerkView}) instanziiert Vaadin KEINE neue View, sondern wiederverwendet die bereits aktive Instanz
 * ({@code AbstractNavigationStateRenderer.getRouteTarget()} findet sie über die aktive Router-Target-Chain) und ruft
 * nur {@link #setParameter} erneut auf - {@link #onAttach} feuert dabei NICHT erneut (die Komponente wird nie
 * detached). Jeglicher Bereichswechsel-Code muss daher in {@link #setParameter} laufen, nicht in {@link #onAttach}
 * (das feuert nur beim allerersten Attach bzw. nach einem vollen Browser-Reload) - und alle Felder, die pro Bereich
 * neu aufgebaut werden (hier: die drei Index-Maps in {@link #buildIndex}, sowie der gesamte Control-Panel-Zustand),
 * müssen bei jedem Aufruf explizit zurückgesetzt werden, da sie sonst über mehrere Bereichswechsel hinweg auf
 * derselben Instanz akkumulieren bzw. veraltete Gleis-/Weichen-/Signal-Referenzen eines anderen Bereichs behalten.
 * <p>
 * Teilschritt 3c: Zeichnen/Push. {@code stellwerk-draw.js} (unverändert aus {@code stellwerk.js} übernommen) wird
 * über {@code executeJs} statt über einen rohen WebSocket angestoßen. Die Bereichsfilterung braucht dafür keinen
 * eigenen Mechanismus: Diese View baut ihre eigenen, nur auf {@link #stellwerk} beschränkten Index-Maps
 * (gleis-/weichen-/signalElemente), sodass ein {@code @Changed}-Event für ein Objekt außerhalb dieses Bereichs
 * einfach keine Treffer liefert (reproduziert {@code PushService.getStellwerksBereich()}-Gruppierung implizit).
 * <p>
 * Drei Anläufe waren nötig, um {@code stellwerk-draw.js} zuverlässig zu laden (jeweils per Live-Test widerlegt, nicht
 * nur durch Code-Review - für zukünftige JS-Interop-Arbeiten im Projekt relevant):
 * <ol>
 * <li>{@code @JavaScript("stellwerk-draw.js")} - kompiliert und läuft in {@code clean compile}, scheitert aber am
 * Produktions-Build ({@code mvn package}/{@code quarkus:build}): Vaadins Frontend-Build (Vite) behandelt
 * {@code @JavaScript}/{@code @JsModule} als ES-Modul-Import und versucht, ihn zu bündeln - scheitert, weil die Datei
 * eine reine Klassenpfad-Ressource unter {@code META-INF/resources} ist (kein {@code frontend/}-Verzeichnis, kein
 * npm-Paket).
 * <li>{@code Page.addJavaScript("stellwerk-draw.js")} (später mit {@code context://}-Präfix) - kompiliert und
 * baut, aber 404 zur Laufzeit: eine unpräfigierte relative URL löst gegen die SPA-Basis-URL auf ({@code
 * V5t11VaadinServlet} läuft unter {@code /ui/*}), nicht gegen den Context-Root. Mit {@code context://}-Präfix
 * behoben - ABER nur beim Browser-Reload; bei SPA-Navigation blieb das Zeichnen aus ({@code ReferenceError: drawAll
 * is not defined}), da Vaadins Dependency-Lade-Reihenfolge-Garantie ({@code LoadMode.EAGER}) laut Javadoc nur für den
 * initialen Seitenaufbau gilt.
 * <li><b>Eigentliche Ursache dieses zweiten Symptoms</b> (erst nach dem dritten Fix-Versuch erkannt): Das Skript
 * wurde in {@link #onAttach} geladen/angestoßen - und genau das feuert bei einem Bereichswechsel per Klick gar nicht
 * (s.o., Instanz-Wiederverwendung). Der zwischenzeitliche Umbau auf ein selbst erzeugtes, in einem
 * {@code window}-Promise gecachtes {@code <script>}-Element (statt {@code Page.addJavaScript}) war zwar robuster
 * gegenüber Lade-Reihenfolge-Problemen, behob das eigentliche Problem aber nicht, solange der Aufruf weiterhin in
 * {@code onAttach} stand.
 * </ol>
 * <b>Endgültige Lösung:</b> Laden (idempotent, Promise-gecacht in {@code window}, siehe {@link #ENSURE_DRAW_JS_LOADED})
 * und initiales Zeichnen (siehe {@link #redrawAll}) werden in {@link #setParameter} angestoßen, nicht in
 * {@link #onAttach} - {@link #setParameter} feuert garantiert bei jeder Navigation, unabhängig von
 * Instanz-Wiederverwendung.
 * <p>
 * Teilschritt 3d: Interaktion. Die Fachlogik (Fahrstraßen-Reservierungs-Zustandsmaschine, Weichen-/Signalstellung)
 * ist 1:1 aus {@code StellwerkPresenter} portiert, siehe {@link #gleisClicked}/{@link #weicheClicked}/
 * {@link #signalClicked}. Zwei Vereinfachungen ergeben sich aus dem Vaadin-Modell gegenüber dem JSF-Original:
 * <ul>
 * <li>Das wsId-Korrelations-Hack (Hidden-Input, Request-Param-Roundtrip, um eine WebSocket- mit einer HTTP-Session zu
 * verknüpfen) entfällt ersatzlos - diese View ist bereits UI-gebunden und vereint Interaktion und Push im selben
 * Kontext.
 * <li>{@code StellwerkVorschlagService} (session-keyed, nur zum Finden der "eigenen" Fahrstraßen-Vorschläge beim
 * Redraw) wird nicht portiert: Der Vorschlagszustand ({@link #fahrstrasse}/{@link #fahrstrassenVorschlaege}) lebt
 * direkt als view-lokales Feld, und {@link #resolveFahrstrassenVorschlag} liefert das Overlay für
 * {@link StellwerkDrawCommandBuilder} ohne Session-Bezug.
 * </ul>
 * Das Control-Panel wird einmalig in {@link #buildControlPanel} aufgebaut und über {@link MainLayout#setExtraContent}
 * angezeigt (erste echte Nutzung dieses bisher nur dokumentierten Slots) - es überlebt (wie die View-Instanz selbst)
 * SPA-Navigationen zwischen Bereichen unverändert; nur sein Inhalt wird über {@link #updateControlPanel} je nach
 * Auswahl aktualisiert.
 */
@Route(value = "stellwerk", layout = MainLayout.class)
@PageTitle("Stellwerk - v5t11")
@StyleSheet("stellwerk.css")
public class StellwerkView extends VerticalLayout implements HasUrlParameter<String> {

  /**
   * Lädt {@code stellwerk-draw.js} genau einmal pro Browser-Tab (Promise in {@code window} gecacht, überlebt
   * SPA-Navigationen) und cached das Ergebnis. Jeder Zeichenaufruf (siehe {@link #redraw}) wartet über
   * {@code window.stellwerkDrawJsPromise.then(...)} auf dieses Promise, statt sich auf Vaadins eigene
   * Dependency-Lade-Reihenfolge zu verlassen (siehe Klassen-Javadoc).
   */
  private static final String ENSURE_DRAW_JS_LOADED = """
    if (!window.stellwerkDrawJsPromise) {
      window.stellwerkDrawJsPromise = new Promise((resolve, reject) => {
        const script = document.createElement('script');
        script.src = '/stellwerk-draw.js';
        script.onload = resolve;
        script.onerror = reject;
        document.head.appendChild(script);
      });
    }
    """;

  @Inject
  Leitstand leitstand;

  @Inject
  SignalRepository signalRepository;

  @Inject
  WeicheRepository weicheRepository;

  @Inject
  FahrstrassenManager fahrstrassenManager;

  @Inject
  @RestClient
  StatusGateway statusGateway;

  @Inject
  @RestClient
  FahrstrassenGateway fahrstrassenGateway;

  @Inject
  StellwerkDrawCommandBuilder drawCommandBuilder;

  @Inject
  VaadinChangePushBroadcaster pushBroadcaster;

  private Stellwerk stellwerk;
  private MainLayout mainLayout;

  private final ListMultimap<BereichselementId, StellwerkElement> gleisElemente = MultimapBuilder.hashKeys().arrayListValues().build();
  private final ListMultimap<BereichselementId, StellwerkElement> weichenElemente = MultimapBuilder.hashKeys().arrayListValues().build();
  private final ListMultimap<BereichselementId, StellwerkElement> signalElemente = MultimapBuilder.hashKeys().arrayListValues().build();

  private final Consumer<Object> changeListener = this::onChanged;

  // ---------- Control-Panel-Zustand (1:1 aus StellwerkPresenter) ----------

  private boolean controlPanelEnabled;
  private Gleis startGleis;
  private long startGleisTimeStamp = -1L;
  private BereichselementId weiche1Id;
  private BereichselementId weiche2Id;
  private BereichselementId signalId;
  private Fahrstrasse fahrstrasse;
  private List<Fahrstrasse> fahrstrassenVorschlaege = List.of();
  private boolean freigabeButtonEnabled;
  private boolean zugfahrtButtonEnabled;
  private boolean rangierfahrtEnabled;

  // ---------- Control-Panel-Komponenten (einmalig aufgebaut, siehe buildControlPanel) ----------

  private Div controlPanel;
  private FontIcon signalIcon;
  private Span signalNameSpan;
  private RadioButtonGroup<SignalStellung> signalStellungField;
  private FontIcon weiche1Icon;
  private Span weiche1NameSpan;
  private RadioButtonGroup<WeichenStellung> weiche1StellungField;
  private FontIcon weiche2Icon;
  private Span weiche2NameSpan;
  private RadioButtonGroup<WeichenStellung> weiche2StellungField;
  private FontIcon fahrstrasseIcon;
  private Span fahrstrasseNameSpan;
  private Button zugfahrtButton;
  private Button rangierfahrtButton;
  private Button freigabeButton;

  @PostConstruct
  void init() {
    this.controlPanel = buildControlPanel();
  }

  @Override
  public void setParameter(BeforeEvent event, String bereich) {
    this.stellwerk = this.leitstand.getStellwerk(bereich);
    if (this.stellwerk == null) {
      event.rerouteToError(NotFoundException.class, "Unbekannter Stellwerksbereich: " + bereich);
      return;
    }

    setSizeFull();
    getStyle().set("overflow", "auto");

    buildIndex();

    removeAll();
    add(buildGrid());

    // Bewusst hier und nicht in onAttach() - siehe Klassen-Javadoc (Instanz-Wiederverwendung bei SPA-Navigation).
    getElement().executeJs(ENSURE_DRAW_JS_LOADED);

    // Muss NACH buildGrid()/ENSURE_DRAW_JS_LOADED laufen: clearControlPanel() löst selbst schon ein redrawAll() aus
    // (siehe dort) - vorher wäre weder das Canvas-Grid noch das window.stellwerkDrawJsPromise bereit.
    this.startGleis = null;
    this.startGleisTimeStamp = -1L;
    clearControlPanel();
    updateControlPanel();
  }

  @Override
  protected void onAttach(AttachEvent attachEvent) {
    super.onAttach(attachEvent);
    this.pushBroadcaster.addListener(this.changeListener);

    this.mainLayout = (MainLayout) getParent().orElseThrow();
    this.mainLayout.setExtraContent(this.controlPanel);
  }

  @Override
  protected void onDetach(DetachEvent detachEvent) {
    this.mainLayout.setExtraContent();
    this.pushBroadcaster.removeListener(this.changeListener);
    super.onDetach(detachEvent);
  }

  private Div buildGrid() {
    Div grid = new Div();
    grid.addClassName("stellwerk");

    this.stellwerk
      .getZeilen()
      .stream()
      .flatMap(zeile -> zeile.getElemente().stream())
      .forEach(element -> {
        StellwerkCanvas canvas = new StellwerkCanvas(element);
        canvas.addClickListener(event -> onElementClicked(element));
        grid.add(canvas);
      });

    return grid;
  }

  /**
   * Baut die auf {@link #stellwerk} beschränkten Lookup-Strukturen (Gleis-/Weichen-/Signal-Id -> betroffene
   * {@link StellwerkElement}e), 1:1 aus {@code PushService}s Konstruktor übernommen, aber nur über die Zeilen dieses
   * einen Bereichs statt über {@code leitstand.getStellwerke()} (alle Bereiche).
   */
  private void buildIndex() {
    // Muss bei jedem Aufruf geleert werden: setParameter() (und damit buildIndex()) kann bei einer
    // SPA-Navigation zwischen zwei Bereichen mehrfach auf derselben View-Instanz laufen (siehe Kommentar in
    // setParameter()) - ohne clear() würden sich Einträge früher besuchter Bereiche unbegrenzt ansammeln.
    this.gleisElemente.clear();
    this.weichenElemente.clear();
    this.signalElemente.clear();

    this.stellwerk
      .getZeilen()
      .stream()
      .flatMap(zeile -> zeile.getElemente().stream())
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

  private void onChanged(Object changed) {
    getUI().ifPresent(ui -> ui.access(() -> {
      if (changed instanceof Gleis gleis) {
        redraw(this.gleisElemente.get(gleis.getId()));
      } else if (changed instanceof Signal signal) {
        redraw(this.signalElemente.get(signal.getId()));
      } else if (changed instanceof Weiche weiche) {
        redraw(this.weichenElemente.get(weiche.getId()));
      } else if (changed instanceof Fahrstrasse) {
        // Siehe Javadoc von redrawAll(): ein voller Bereichs-Redraw statt eines gezielten, indexbasierten
        // Redraws nur der betroffenen Gleis-Zellen (ersetzt die frühere, fehleranfällige redrawFahrstrassenGleise()).
        redrawAll();
      }
    }));
  }

  /**
   * Redrawt sämtliche Zellen des Bereichs - verwendet sowohl für das initiale Zeichnen (siehe
   * {@link #setParameter}) als auch für jede Fahrstraßen-Zustandsänderung (Vorschlag gesetzt/gewechselt,
   * tatsächlich reserviert/freigegeben, Control-Panel geschlossen). Letzteres war ursprünglich über eine gezielte,
   * indexbasierte Auswahl der betroffenen Gleis-Zellen gelöst (analog {@code PushService.sendFahrstrassen}); das
   * verursachte jedoch einen live beobachteten Bug: reine Gleis-Zellen (ohne eigene Weiche/Signal-Stellungsänderung,
   * die sie unabhängig davon ohnehin redrawen würde) bekamen ihr Fahrstraßen-Overlay nicht live aktualisiert, nur
   * nach einem vollen Reload. Da ein Redraw aller Zellen eines Bereichs (typischerweise wenige Dutzend Canvas-Element)
   * unkritisch günstig ist, wird hier bewusst auf die gezielte Auswahl verzichtet statt die genaue Ursache in der
   * Index-Logik weiterzuverfolgen.
   */
  private void redrawAll() {
    List<StellwerkElement> alleElemente = this.stellwerk
      .getZeilen()
      .stream()
      .flatMap(zeile -> zeile.getElemente().stream())
      .filter(element -> !(element instanceof StellwerkLeer) || element.getSignalId() != null)
      .toList();
    redraw(alleElemente);
  }

  private void redraw(Collection<StellwerkElement> elemente) {
    if (elemente.isEmpty()) {
      return;
    }

    JsonArray drawCommands = this.drawCommandBuilder.createDrawCommands(elemente, this::resolveFahrstrassenVorschlag);
    getElement().executeJs("window.stellwerkDrawJsPromise.then(() => drawAll(JSON.parse($0)))", drawCommands.toString());
  }

  /**
   * Liefert das Fahrstraßen-Vorschlags-Overlay für ein Gleis anhand des view-lokalen Auswahlzustands
   * ({@link #fahrstrasse}/{@link #fahrstrassenVorschlaege}) - Ersatz für das session-keyed
   * {@code StellwerkVorschlagService} des JSF-Originals (siehe Klassen-Javadoc).
   */
  private FahrstrassenVorschlag resolveFahrstrassenVorschlag(Gleis gleis) {
    if (this.fahrstrasse != null) {
      Fahrstrassenelement element = this.fahrstrasse.getElement(gleis, false);
      if (element != null) {
        return new FahrstrassenVorschlag("V", element.isZaehlrichtung());
      }
    }

    for (Fahrstrasse alternative : this.fahrstrassenVorschlaege) {
      if (!alternative.equals(this.fahrstrasse)) {
        Fahrstrassenelement element = alternative.getElement(gleis, false);
        if (element != null) {
          return new FahrstrassenVorschlag("A", element.isZaehlrichtung());
        }
      }
    }

    return null;
  }

  // ---------- Interaktion (Teilschritt 3d), 1:1 aus StellwerkPresenter portiert ----------

  private void onElementClicked(StellwerkElement element) {
    this.weiche1Id = null;
    this.weiche2Id = null;
    this.signalId = null;

    if (element instanceof StellwerkGleis stellwerkGleis) {
      gleisClicked(stellwerkGleis.findGleis());
    } else if (element instanceof StellwerkEinfachWeiche stellwerkEinfachWeiche) {
      gleisClicked(stellwerkEinfachWeiche.findGleis());
      weicheClicked(stellwerkEinfachWeiche.getId(), null);
    } else if (element instanceof StellwerkDkw2 stellwerkDkw2) {
      gleisClicked(stellwerkDkw2.findGleis());
      weicheClicked(stellwerkDkw2.getWeicheAId(), stellwerkDkw2.getWeicheBId());
    }

    signalClicked(element.getSignalId());

    updateControlPanel();
  }

  private void gleisClicked(Gleis gleis) {
    long now = System.currentTimeMillis();

    Fahrstrasse reservierteFahrstrasse = this.fahrstrassenManager.getReservierteFahrstrasse(gleis);
    if (reservierteFahrstrasse != null) {
      // Gleis ist in aktiver Fahrstrasse
      this.fahrstrasse = reservierteFahrstrasse;

      this.freigabeButtonEnabled = true;
      this.zugfahrtButtonEnabled = false;
      this.rangierfahrtEnabled = false;

      this.controlPanelEnabled = true;
      return;
    }

    Map<Boolean, List<Fahrstrasse>> partitionierteVorschlaege = this.fahrstrassenVorschlaege
      .stream()
      .collect(Collectors.partitioningBy(fs -> fs.getElement(gleis, false) != null));
    List<Fahrstrasse> vorschlaegeMitGleis = partitionierteVorschlaege.get(true);
    if (vorschlaegeMitGleis != null && !vorschlaegeMitGleis.isEmpty()) {
      // Gleis ist in vorgeschlagener Fahrstrasse

      /*
       Vorschläge so umsortieren, dass die Fahrstrassen, die das Gleis enthalten, vorne stehen
       und die Ordnung ansonsten erhalten bleibt.
       */
      List<Fahrstrasse> neueVorschlaege = new ArrayList<>(vorschlaegeMitGleis);
      neueVorschlaege.addAll(partitionierteVorschlaege.get(false));
      this.fahrstrassenVorschlaege = neueVorschlaege;

      // Die erste FS ist nun die gerade angewählte, andere mit gleichem Gleis folgen danach
      this.fahrstrasse = this.fahrstrassenVorschlaege.get(0);

      this.freigabeButtonEnabled = false;
      this.zugfahrtButtonEnabled = true;
      this.rangierfahrtEnabled = true;

      this.controlPanelEnabled = true;

      redrawAll();
      return;
    }

    if (this.startGleis == null || (now - this.startGleisTimeStamp) > 5000) {
      // Erstes Gleis innerhalb der Wartezeit => als Start einer Fahrstrasse merken
      this.startGleisTimeStamp = now;
      this.startGleis = gleis;

      return;
    }

    // Zweites Gleis innerhalb Wartezeit => Fahrstrassen suchen
    try {
      List<Fahrstrasse> fahrstrassen = this.fahrstrassenGateway.getFahrstrassen(
        this.startGleis.getBereich(), this.startGleis.getName(),
        gleis.getBereich(), gleis.getName(),
        FahrstrassenFilter.FREI);

      if (fahrstrassen.isEmpty()) {
        showWarning(String.format("Keine Fahrstrasse von %s nach %s gefunden", this.startGleis.getName(), gleis.getName()));
      } else {
        this.fahrstrassenVorschlaege = fahrstrassen;
        this.fahrstrasse = fahrstrassen.get(0);

        this.freigabeButtonEnabled = false;
        this.zugfahrtButtonEnabled = true;
        this.rangierfahrtEnabled = true;

        this.controlPanelEnabled = true;

        redrawAll();
      }
    } catch (Exception e) {
      showError("Fahrstrassen können nicht ermittelt werden", "Fahrstrassen-Service", e);
    }
  }

  private void weicheClicked(BereichselementId weiche1Id, BereichselementId weiche2Id) {
    this.weiche1Id = weiche1Id;
    this.weiche2Id = weiche2Id;

    this.controlPanelEnabled = true;
  }

  private void signalClicked(BereichselementId signalId) {
    if (signalId != null) {
      this.signalId = signalId;

      this.controlPanelEnabled = true;
    }
  }

  private void setWeicheXStellung(BereichselementId weicheId, WeichenStellung stellung) {
    if (stellung != null && weicheId != null) {
      try {
        this.statusGateway.weicheStellen(weicheId.getBereich(), weicheId.getName(), stellung);
      } catch (Exception e) {
        showError("Weiche kann nicht gestellt werden", "Status-Service", e);
      }
    }
  }

  private void setSignalStellung(SignalStellung stellung) {
    if (stellung != null && this.signalId != null) {
      try {
        this.statusGateway.signalStellen(this.signalId.getBereich(), this.signalId.getName(), stellung);
      } catch (Exception e) {
        showError("Signal kann nicht gestellt werden", "Status-Service", e);
      }
    }
  }

  private void fahrstrasseReservieren(FahrstrassenReservierungsTyp fahrstrassenReservierungsTyp) {
    Fahrstrasse fahrstrasse = this.fahrstrasse;

    clearControlPanel();

    if (fahrstrasse != null) {
      try {
        this.fahrstrassenGateway.reserviereFahrstrasse(fahrstrasse.getId(), fahrstrassenReservierungsTyp);
      } catch (Exception e) {
        String operation = fahrstrassenReservierungsTyp == FahrstrassenReservierungsTyp.UNRESERVIERT ? "freigegeben" : "reserviert";
        showError(String.format("Fahrstrasse %s kann nicht %s werden", fahrstrasse.getShortName(), operation), "Fahrstrassen-Service", e);
      }
    }

    updateControlPanel();
  }

  private void clearControlPanel() {
    this.controlPanelEnabled = false;
    this.weiche1Id = null;
    this.weiche2Id = null;
    this.signalId = null;
    this.fahrstrasse = null;
    this.fahrstrassenVorschlaege = List.of();

    // Redraw, damit ein zuvor gezeichnetes Vorschlags-Overlay (siehe resolveFahrstrassenVorschlag) verschwindet.
    redrawAll();
  }

  private void showError(String message, String serviceName, Exception e) {
    StringBuilder summary = new StringBuilder(message);
    if (e.getMessage() != null && e.getMessage().contains("Connection refused")) {
      summary.append("\n(").append(serviceName).append(" nicht erreichbar)");
    }

    Notification notification = Notification.show(summary.toString(), 3000, Notification.Position.BOTTOM_START);
    notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
  }

  private void showWarning(String message) {
    Notification notification = Notification.show(message, 3000, Notification.Position.BOTTOM_START);
    notification.addThemeVariants(NotificationVariant.LUMO_WARNING);
  }

  // ---------- Control-Panel-Aufbau/-Aktualisierung ----------

  /**
   * Baut das Control-Panel einmalig auf (Komponenten-Mapping aus {@code stellwerk.xhtml}s {@code extra}-Facette,
   * Grid-Layout aus {@code stellwerk.css}s {@code .controlPanel}-Klasse unverändert übernommen). Die Komponente
   * überlebt - wie die View-Instanz selbst - SPA-Navigationen zwischen Bereichen; nur ihr Inhalt wird über
   * {@link #updateControlPanel} aktualisiert.
   */
  private Div buildControlPanel() {
    Div panel = new Div();
    panel.addClassName("controlPanel");
    // stellwerk.css' .controlPanel-Klasse (bewusst unverändert, siehe Klassen-Javadoc) definiert
    // "grid-template-columns: 5% 15% auto" - das war für den breiten "extra"-Bereich des JSF-Originals bemessen.
    // MainLayout.extraArea ist dagegen ein schmaler Drawer-Bereich; 5%/15% davon sind schmaler als ein Icon bzw.
    // eine Bezeichnung, wodurch beide sich überlappen. Per Inline-Style (gewinnt gegenüber der Klasse) auf für den
    // Drawer passende, inhaltsbasierte Spaltenbreiten umgestellt.
    // "auto" statt "1fr" für Spalte 2: "1fr" reserviert immer den vollen Anteil des freien Platzes für die
    // Bezeichnung, auch wenn ihr Text (z. B. "Sbk2") viel kürzer ist - das schiebt Spalte 3 (die Buttons) an den
    // rechten Rand. "auto" bemisst die Spalte stattdessen am tatsächlichen Inhalt, sodass die Buttons direkt nach
    // der Bezeichnung sitzen.
    panel.getStyle().set("grid-template-columns", "1.5em auto auto").set("gap", "0.25em 0.5em");

    Button closeButton = new Button(new FontIcon("pi", "pi-times"), event -> {
      clearControlPanel();
      updateControlPanel();
    });
    // Jedes Element bekommt explizit grid-row UND grid-column: bei nur grid-column (auto-placement für die Zeile)
    // weicht der CSS-Grid-Algorithmus bereits belegte Zellen aus - da je nach Auswahl nicht alle Zeilen sichtbar
    // sind (setVisible(false) nimmt die Zeile aus der Grid-Berechnung heraus), würden sichtbare Zeilen sonst
    // unvorhersehbar durcheinandergewürfelt (z. B. das Signal-Radio-Feld landet dann in der Weiche1-Zeile).
    closeButton.getStyle().set("grid-row", "1").set("grid-column", "3").set("justify-self", "end");
    panel.add(closeButton);

    this.signalIcon = new FontIcon("pi", "pi-sun");
    this.signalIcon.getStyle().set("grid-row", "2").set("grid-column", "1");
    this.signalNameSpan = new Span();
    this.signalNameSpan.getStyle().set("grid-row", "2").set("grid-column", "2");
    this.signalStellungField = new RadioButtonGroup<>();
    this.signalStellungField.addClassName("toggle-buttons");
    this.signalStellungField.getStyle().set("grid-row", "2").set("grid-column", "3").set("justify-self", "start");
    this.signalStellungField.addValueChangeListener(event -> {
      if (event.isFromClient() && event.getValue() != null) {
        setSignalStellung(event.getValue());
      }
    });
    panel.add(this.signalIcon, this.signalNameSpan, this.signalStellungField);

    this.weiche1Icon = new FontIcon("pi", "pi-share-alt");
    this.weiche1Icon.getStyle().set("grid-row", "3").set("grid-column", "1");
    this.weiche1NameSpan = new Span();
    this.weiche1NameSpan.getStyle().set("grid-row", "3").set("grid-column", "2");
    this.weiche1StellungField = new RadioButtonGroup<>();
    this.weiche1StellungField.addClassName("toggle-buttons");
    this.weiche1StellungField.setItems(WeichenStellung.values());
    this.weiche1StellungField.getStyle().set("grid-row", "3").set("grid-column", "3").set("justify-self", "start");
    this.weiche1StellungField.addValueChangeListener(event -> {
      if (event.isFromClient() && event.getValue() != null) {
        setWeicheXStellung(this.weiche1Id, event.getValue());
      }
    });
    panel.add(this.weiche1Icon, this.weiche1NameSpan, this.weiche1StellungField);

    this.weiche2Icon = new FontIcon("pi", "pi-share-alt");
    this.weiche2Icon.getStyle().set("grid-row", "4").set("grid-column", "1");
    this.weiche2NameSpan = new Span();
    this.weiche2NameSpan.getStyle().set("grid-row", "4").set("grid-column", "2");
    this.weiche2StellungField = new RadioButtonGroup<>();
    this.weiche2StellungField.addClassName("toggle-buttons");
    this.weiche2StellungField.setItems(WeichenStellung.values());
    this.weiche2StellungField.getStyle().set("grid-row", "4").set("grid-column", "3").set("justify-self", "start");
    this.weiche2StellungField.addValueChangeListener(event -> {
      if (event.isFromClient() && event.getValue() != null) {
        setWeicheXStellung(this.weiche2Id, event.getValue());
      }
    });
    panel.add(this.weiche2Icon, this.weiche2NameSpan, this.weiche2StellungField);

    this.fahrstrasseIcon = new FontIcon("pi", "pi-arrows-alt");
    this.fahrstrasseIcon.getStyle().set("grid-row", "5").set("grid-column", "1");
    this.fahrstrasseNameSpan = new Span();
    this.fahrstrasseNameSpan.getStyle().set("grid-row", "5").set("grid-column", "2 / span 2");

    this.zugfahrtButton = new Button("Zugfahrt", event -> fahrstrasseReservieren(FahrstrassenReservierungsTyp.ZUGFAHRT));
    this.rangierfahrtButton = new Button("Rangierfahrt", event -> fahrstrasseReservieren(FahrstrassenReservierungsTyp.RANGIERFAHRT));
    this.freigabeButton = new Button("Freigabe", event -> fahrstrasseReservieren(FahrstrassenReservierungsTyp.UNRESERVIERT));
    Div fahrstrasseButtonsRow = new Div(this.zugfahrtButton, this.rangierfahrtButton, this.freigabeButton);
    fahrstrasseButtonsRow.getStyle().set("grid-row", "6").set("grid-column", "2 / span 2");

    panel.add(this.fahrstrasseIcon, this.fahrstrasseNameSpan, fahrstrasseButtonsRow);

    return panel;
  }

  private void updateControlPanel() {
    this.controlPanel.setVisible(this.controlPanelEnabled);

    boolean hasSignal = this.signalId != null;
    this.signalIcon.setVisible(hasSignal);
    this.signalNameSpan.setVisible(hasSignal);
    this.signalStellungField.setVisible(hasSignal);
    if (hasSignal) {
      Signal signal = this.signalRepository.findById(this.signalId).get();
      this.signalNameSpan.setText(this.signalId.getName());
      this.signalStellungField.setItems(signal.getTyp().getErlaubteStellungen());
      this.signalStellungField.setValue(signal.getStellung());
    }

    updateWeicheRow(this.weiche1Id, this.weiche1Icon, this.weiche1NameSpan, this.weiche1StellungField);
    updateWeicheRow(this.weiche2Id, this.weiche2Icon, this.weiche2NameSpan, this.weiche2StellungField);

    boolean hasFahrstrasse = this.fahrstrasse != null;
    this.fahrstrasseIcon.setVisible(hasFahrstrasse);
    this.fahrstrasseNameSpan.setVisible(hasFahrstrasse);
    if (hasFahrstrasse) {
      this.fahrstrasseNameSpan.setText(this.fahrstrasse.getShortName());
    }
    this.zugfahrtButton.setVisible(this.zugfahrtButtonEnabled);
    this.rangierfahrtButton.setVisible(this.rangierfahrtEnabled);
    this.freigabeButton.setVisible(this.freigabeButtonEnabled);
  }

  private void updateWeicheRow(BereichselementId weicheId, FontIcon icon, Span nameSpan, RadioButtonGroup<WeichenStellung> stellungField) {
    boolean has = weicheId != null;
    icon.setVisible(has);
    nameSpan.setVisible(has);
    stellungField.setVisible(has);
    if (has) {
      nameSpan.setText(weicheId.getName());
      stellungField.setValue(this.weicheRepository.findById(weicheId).get().getStellung());
    }
  }
}
