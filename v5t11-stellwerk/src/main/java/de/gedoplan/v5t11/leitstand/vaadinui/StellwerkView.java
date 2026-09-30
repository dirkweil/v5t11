package de.gedoplan.v5t11.leitstand.vaadinui;

import de.gedoplan.v5t11.leitstand.entity.Leitstand;
import de.gedoplan.v5t11.leitstand.entity.fahrstrasse.Fahrstrasse;
import de.gedoplan.v5t11.leitstand.entity.fahrweg.Gleis;
import de.gedoplan.v5t11.leitstand.entity.fahrweg.Signal;
import de.gedoplan.v5t11.leitstand.entity.fahrweg.Weiche;
import de.gedoplan.v5t11.leitstand.entity.stellwerk.Stellwerk;
import de.gedoplan.v5t11.leitstand.entity.stellwerk.StellwerkDkw2;
import de.gedoplan.v5t11.leitstand.entity.stellwerk.StellwerkEinfachWeiche;
import de.gedoplan.v5t11.leitstand.entity.stellwerk.StellwerkElement;
import de.gedoplan.v5t11.leitstand.entity.stellwerk.StellwerkGleis;
import de.gedoplan.v5t11.leitstand.entity.stellwerk.StellwerkLeer;
import de.gedoplan.v5t11.leitstand.persistence.GleisRepository;
import de.gedoplan.v5t11.leitstand.webui.StellwerkDrawCommandBuilder;
import de.gedoplan.v5t11.leitstand.webui.VaadinChangePushBroadcaster;
import de.gedoplan.v5t11.util.domain.attribute.BereichselementId;
import de.gedoplan.v5t11.util.domain.attribute.FahrstrassenelementTyp;
import de.gedoplan.v5t11.vaadincommon.ui.MainLayout;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.NotFoundException;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jakarta.inject.Inject;
import jakarta.json.JsonArray;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

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
 * neu aufgebaut werden (hier: die drei Index-Maps in {@link #buildIndex}), müssen bei jedem Aufruf explizit geleert
 * werden, da sie sonst über mehrere Bereichswechsel hinweg auf derselben Instanz akkumulieren.
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
 * Die eigentliche Interaktionslogik (Fahrstraßen-Reservierung, Weichen-/Signalstellung) folgt in Teilschritt 3d.
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
  GleisRepository gleisRepository;

  @Inject
  StellwerkDrawCommandBuilder drawCommandBuilder;

  @Inject
  VaadinChangePushBroadcaster pushBroadcaster;

  private Stellwerk stellwerk;

  private final ListMultimap<BereichselementId, StellwerkElement> gleisElemente = MultimapBuilder.hashKeys().arrayListValues().build();
  private final ListMultimap<BereichselementId, StellwerkElement> weichenElemente = MultimapBuilder.hashKeys().arrayListValues().build();
  private final ListMultimap<BereichselementId, StellwerkElement> signalElemente = MultimapBuilder.hashKeys().arrayListValues().build();

  private final Consumer<Object> changeListener = this::onChanged;

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
    redrawAll();
  }

  @Override
  protected void onAttach(AttachEvent attachEvent) {
    super.onAttach(attachEvent);
    this.pushBroadcaster.addListener(this.changeListener);
  }

  @Override
  protected void onDetach(DetachEvent detachEvent) {
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

  private void onElementClicked(StellwerkElement element) {
    // Fahrstraßen-Reservierung, Weichen-/Signalstellung: siehe Teilschritt 3d.
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
      } else if (changed instanceof Fahrstrasse fahrstrasse) {
        redrawForFahrstrasse(fahrstrasse);
      }
    }));
  }

  private void redrawForFahrstrasse(Fahrstrasse fahrstrasse) {
    List<StellwerkElement> betroffeneElemente = fahrstrasse
      .getElemente()
      .stream()
      .filter(fahrstrassenelement -> fahrstrassenelement.getTyp() == FahrstrassenelementTyp.GLEIS)
      .flatMap(fahrstrassenelement -> this.gleisRepository.findById(fahrstrassenelement.getId()).stream())
      .flatMap(gleis -> this.gleisElemente.get(gleis.getId()).stream())
      .distinct()
      .toList();
    redraw(betroffeneElemente);
  }

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

    JsonArray drawCommands = this.drawCommandBuilder.createDrawCommands(elemente);
    getElement().executeJs("window.stellwerkDrawJsPromise.then(() => drawAll(JSON.parse($0)))", drawCommands.toString());
  }
}
