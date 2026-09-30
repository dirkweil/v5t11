# Migration JSF (PrimeFaces) → Vaadin Flow für v5t11 — Phase 3: `v5t11-stellwerk`

## Context

Die v5t11-Anwendung wird schrittweise pro Microservice von JSF/PrimeFaces auf Vaadin Flow migriert (Branch `vaadin`). Phase 0 (Fundament), Phase 1/1a (`v5t11-status`) und Phase 2 (`v5t11-fahrzeuge`) sind abgeschlossen und vom Nutzer verifiziert. **Phase 3 (`v5t11-stellwerk`)** ist die letzte und laut Gesamtplan risikoreichste Phase: ein einziger, aber komplexer canvas-basierter Gleisplan-("Leitstand"-)View mit rohem WebSocket-Push, einer nicht-trivialen Fahrstraßen-Reservierungs-Logik und echter Hardware-Aktuierung (Weichen/Signale stellen, Fahrstraßen reservieren). Dieser Plan detailliert die fünf im Gesamtplan skizzierten Teilschritte (3a–3e) auf Basis einer vollständigen Code-Analyse des aktuellen `v5t11-stellwerk`-Moduls sowie der bereits etablierten Vaadin-Infrastruktur aus Phase 0–2, damit die Umsetzung – wie schon bei Phase 1a (`SystemControlView`) – kontrolliert und mit klaren Exit-Kriterien je Teilschritt erfolgen kann.

**Zentrales Ergebnis der Analyse:** Alle drei ursprünglich offenen Risikopunkte (Layout-Frage, JS-Integration, Session-Korrelations-Hack) sind lösbar, ohne neue Infrastruktur zu erfinden – zwei der vier im Gesamtplan offenen Entscheidungen (Chrome-arme Layout-Variante, Canvas-Ansatz) werden hiermit abschließend geklärt. Der wsId-Korrelations-Mechanismus entfällt in Vaadin sogar vollständig, was eine der größten Komplexitätsquellen des Originals eliminiert.

## Bestandsaufnahme (verifiziert)

- **`leitstand.xhtml`** (7 Zeilen) ist ein reiner No-op-Wrapper um das gemeinsame `v5t11.xhtml`-Template (aus `v5t11-jsfcommon`) – keine eigene Chrome. Dieses Template ist in Phase 0 bereits 1:1 durch `MainLayout` (`v5t11-vaadincommon`) abgelöst worden (Navbar + Drawer mit `VaadinNavigationMenu` + `extraArea`-Slot + Content).
- **`stellwerk.xhtml`** (117 Zeilen): `content`-Facette = verschachteltes `ui:repeat` (`stellwerk.zeilen` → `zeile.elemente`), pro `StellwerkElement` ein `<canvas id="uiId" width="1000" height="1000" style="grid-row-start:..;grid-column-start:..">` mit `onclick`, das ein PrimeFaces-`remoteCommand` `elementClicked` (uiId + wsId) auslöst. `extra`-Facette = Control-Panel (Close-Button, Signal-/Weiche1-/Weiche2-Auswahl als `p:selectOneButton`, Fahrstraßen-Buttons "Zugfahrt"/"Rangierfahrt"/"Freigabe").
- **`stellwerk.css`**: `.stellwerk` (CSS-Grid, 30 Spalten × 40px, responsive 35px ab ≤1500px, grauer Rand je Zelle), `.controlPanel` (Grid `5% 15% auto`). Bleibt unverändert wiederverwendbar.
- **`stellwerk.js`** (335 Zeilen, **komplett framework-frei**, kein jQuery/PrimeFaces): Zeilen 1–32 = reiner WebSocket-Transport (entfällt in Vaadin), Zeilen 34–335 = zustandslose Canvas2D-Zeichenfunktionen (`drawElement`, `drawInaktiv`, `drawAktiv`, `drawFahrstrasse`, `drawSignal`, `drawName` + 3 Lookup-Helfer) – bleiben unverändert übernehmbar.
- **`StellwerkPresenter.java`** (387 Zeilen, `@Named @ViewScoped`): `elementClicked()` (Klick-Dispatch), `gleisClicked(Gleis)` (Fahrstraßen-Zustandsmaschine: reservierte FS → nur Freigabe; vorgeschlagene FS → Zugfahrt/Rangierfahrt; sonst 2-Klick-Start/Ziel-Puffer mit 5s-Fenster → `fahrstrassenGateway.getFahrstrassen(...)`), `weicheClicked`/`signalClicked`, `get/setWeicheXStellung`/`get/setSignalStellung` (REST via `statusGateway`, aktuiert echte Hardware), `fahrstrasseReservieren(typ)` (REST via `fahrstrassenGateway`), `clearControlPanel()`.
- **`PushService.java`** (529 Zeilen, `@ApplicationScoped extends AbstractPushService`, `@ServerEndpoint("/jakarta.faces.push/stellwerk/{bereich}")`): vier separate CDI-Observer `gleisChanged`/`signalChanged`/`weicheChanged`/`fahrstrasseChanged`, jeweils `@Observes(during = TransactionPhase.AFTER_COMPLETION) @Changed` (verifiziert im Code, Zeilen 133/142/151/160 – **nicht** `@ObservesAsync` wie bei status/fahrzeuge). Bereichsfilterung: gruppiert geänderte `StellwerkElement`e nach `getStellwerksBereich()` (kann von `Gleis.getBereich()` abweichen!) und sendet pro offener Session nur an die zum verbundenen `bereich`-Pfadparameter passende Gruppe. Baut Draw-Commands (`createDrawCommand`, Felder `uiId/b/a/i/f/z/l/s/n/p`, exakt passend zu `stellwerk.js`).
- **`StellwerkVorschlagService.java`**: hält pro WebSocket-Session (`sessionId`-String) eine vorgeschlagene + alternative Fahrstraßen-Liste; wird **ausschließlich** von `PushService` (zum Lesen beim Redraw der jeweils eigenen Session) und `StellwerkPresenter` (zum Schreiben nach Klick) verwendet – kein Cross-Session-Broadcast des Vorschlags selbst.
- **`StellwerkSessionHolder.java`**: hält das JAXB-geladene `Stellwerk`-Objekt pro Bereich; einziger Konsument ist `StellwerkPresenter`.
- **wsId-Korrelation**: Server pusht beim WS-`onOpen` `{"wsId": session.getId()}`; Client schreibt das in ein verstecktes Formularfeld; jeder `elementClicked`-Request (normaler JSF-AJAX-POST) schickt diese Id zurück, damit `StellwerkPresenter` weiß, welche WebSocket-Session (und damit welchen `StellwerkVorschlagService`-Zustand) der aktuelle HTTP-Request repräsentiert – ein reiner Workaround, weil JSF-AJAX und rohes WebSocket sonst unkorreliert sind.
- **`NavigationProducer.java`** (41 Zeilen): erzeugt **dynamisch pro Bereich** (`leitstand.getBereiche()`) einen `NavigationItem` (`name=bereich`, `category="Stellwerk"`, `url=.../view/stellwerk.xhtml?bereich=<b>"`, `icon="pi pi-map"`, `order=0`).
- **web.xml**: keine explizite Servlet-Konfiguration (MyFaces-Autoregistrierung); **keine** Vaadin-Servlet-/Dependency-Spuren im Modul – `v5t11-stellwerk/pom.xml` hängt aktuell nur an `v5t11-jsfcommon`+`v5t11-util`.
- **Domänenklassen** (`Stellwerk`/`StellwerkZeile`/`StellwerkElement`+Subtypen, `StellwerkRichtung`-Enum, lokale `Gleis`/`Signal`/`Weiche`/`Fahrstrasse`-Entities) sind JAXB-geladen bzw. JPA-Entities, komplett Vaadin/JSF-agnostisch – **keine Änderungen nötig**, werden 1:1 weiterverwendet.
- **Bereits etablierte Vaadin-Infrastruktur** (aus `v5t11-vaadincommon`, wiederverwendbar): `V5t11VaadinServlet` (`/ui/*`), `MainLayout` (Navbar/Drawer/Content, `setExtraContent(...)`-Slot bisher unbenutzt), `V5t11AppShell` (`@Push`, Lumo-Theme via `@StyleSheet`), zwei service-lokale `VaadinChangePushBroadcaster`-Varianten (kein Filter-Parameter, Consumer self-filtert), `RadioButtonGroup`/Toggle-Button-CSS-Pattern aus `SystemControlView`. Guava (`ListMultimap` u. a.) ist bereits Projektabhängigkeit (u. a. in `PushService.java` selbst genutzt).

## Teilschritt 3a — Layout/Menü

**Entscheidung (löst Gesamtplan-Punkt "Stellwerk-Layout"): keine eigene chrome-arme Layout-Variante.** `leitstand.xhtml` hatte nie eigene Struktur — `MainLayout` wird unverändert wiederverwendet, exakt wie bei status/fahrzeuge. Volle Bildschirmhöhe/-breite für das Canvas-Grid über das etablierte Pattern (`setSizeFull()` + `overflow: auto` auf der View selbst), keine neue CSS-Regel auf `MainLayout`-Ebene nötig.

**`v5t11-stellwerk/pom.xml`**: Dependencies auf `v5t11-vaadincommon` (Version `3.0.0-SNAPSHOT`) und `vaadin-quarkus-extension` (versionslos, über Root-BOM verwaltet) ergänzen — exakt das bei `v5t11-fahrzeuge/pom.xml` etablierte Muster. `v5t11-jsfcommon` bleibt vorerst bestehen (wird während der Koexistenzphase noch für `NavigationItem`/`NavigationPresenter`/`AbstractPushService` gebraucht).

**Neue Route mit Bereichs-Parameter** (erste Nutzung von `HasUrlParameter` im Projekt):
```java
@Route(value = "stellwerk", layout = MainLayout.class)
@PageTitle("Stellwerk - v5t11")
public class StellwerkView extends VerticalLayout implements HasUrlParameter<String> {
  @Override
  public void setParameter(BeforeEvent event, String bereich) {
    this.stellwerk = this.stellwerkSessionHolder.getStellwerk(bereich); // oder Äquivalent
    if (this.stellwerk == null) {
      event.rerouteToError(NotFoundException.class, "Unbekannter Stellwerksbereich: " + bereich);
      return;
    }
    ...
  }
}
```
Pflichtparameter (kein `@OptionalParameter`) — unbekannter Bereich führt zu sichtbarem 404 statt stillem Fallback wie im JSF-Original.

**`NavigationProducer`**: "(alt)"-Konvention auf die **gesamte Pro-Bereich-Schleife** anwenden (nicht nur einen Eintrag):
```java
for (String bereich : this.leitstand.getBereiche()) {
  items.add(new NavigationItem(bereich + " (alt)", "Stellwerk", urlPrefix + "view/stellwerk.xhtml?bereich=" + bereich, "pi pi-map", 0));
  items.add(new NavigationItem(bereich, "Stellwerk", urlPrefix + "ui/stellwerk/" + bereich, "pi pi-map", -1));
}
```

**Exit-Kriterien 3a:** Build grün; `v5t11-stellwerk` hängt an `vaadincommon`; `/ui/stellwerk/<bereich>` ist für jeden bekannten Bereich erreichbar (leere Platzhalter-View reicht); Menü zeigt pro Bereich beide Einträge ("(alt)" + neu).

## Teilschritt 3b — Struktur (Canvas-Grid als Vaadin-Komponente)

Vaadin Flow hat keine eingebaute `Canvas`-Komponente (verifiziert: `flow-html-components` enthält keine Canvas-Klasse) und im Projekt existiert noch keine handgebaute Custom-Component. **Entscheidung:** ein schlanker Wrapper pro Zelle:

```java
public class StellwerkCanvas extends Component implements ClickNotifier<StellwerkCanvas> {
  @Getter
  private final StellwerkElement stellwerkElement;

  public StellwerkCanvas(StellwerkElement stellwerkElement) {
    super(new Element("canvas"));
    this.stellwerkElement = stellwerkElement;
    getElement().setAttribute("id", stellwerkElement.getUiId());
    getElement().setAttribute("width", "1000");
    getElement().setAttribute("height", "1000");
    getElement().getStyle().set("grid-row-start", String.valueOf(stellwerkElement.getZeilenNr()));
    getElement().getStyle().set("grid-column-start", String.valueOf(stellwerkElement.getSpaltenNr()));
  }
}
```
`ClickNotifier` ist ein Standard-Vaadin-Interface (verifiziert vorhanden in `flow-server`) und liefert `addClickListener(...)` über das native DOM-`click`-Event — der Listener referenziert direkt das zugehörige `StellwerkElement`-Objekt (kein `uiId`-String-Lookup mehr nötig, anders als bisher über `StellwerkSessionHolder.getElementByUiUd(uiId)`).

`StellwerkView` baut den Grid-Container als `Div` mit CSS-Klasse `stellwerk` (unverändert aus `stellwerk.css` übernommen) und füllt ihn analog zum alten verschachtelten `ui:repeat` — **alle** Elemente inkl. `StellwerkLeer`, da Positionierung ohnehin über explizite `grid-row-start`/`grid-column-start` erfolgt (1:1-DOM-Parität, minimiertes Risiko):
```java
this.stellwerk.getZeilen().stream()
    .flatMap(z -> z.getElemente().stream())
    .forEach(element -> {
      StellwerkCanvas canvas = new StellwerkCanvas(element);
      canvas.addClickListener(e -> onElementClicked(element));
      this.canvasByUiId.put(element.getUiId(), canvas); // für gezielte Redraws in 3c
      grid.add(canvas);
    });
```
`Stellwerk`/`StellwerkZeile`/`StellwerkElement` (inkl. `uiId`/`zeilenNr`/`spaltenNr`) bleiben dabei vollständig unverändert.

**Exit-Kriterien 3b:** `/ui/stellwerk/<bereich>` zeigt das korrekte Grid-Layout (Zellenanzahl/-position identisch zum JSF-Original, Canvas noch leer/ungezeichnet); Klick auf eine Zelle löst nachweislich `onElementClicked` mit dem richtigen `StellwerkElement` aus (Log-Ausgabe reicht als Nachweis vor 3d).

## Teilschritt 3c — Zeichnen/Push

**JS-Interop (löst Gesamtplan-Punkt "Canvas-Ansatz"): `@JavaScript` + `executeJs`, kein `@JsModule`/Lit.** Neue Datei `stellwerk-draw.js` = Zeilen 34–335 des Originals unverändert + eine zusätzliche Hilfsfunktion:
```javascript
function drawAll(drawCommands) {
  drawCommands.forEach(drawElement);
}
```
Geladen via `@JavaScript("stellwerk-draw.js")` auf `StellwerkView` — derselbe klassenpfad-basierte Lademechanismus, den `V5t11AppShell` bereits für `@StyleSheet("themes/v5t11/styles.css")` nutzt (verifiziert: beide Annotationen existieren in `com.vaadin.flow.component.dependency`, kein `frontend/`-Verzeichnis/npm-Zusatzbuild nötig). Redraw-Aufruf pro Push-Batch:
```java
getElement().executeJs("drawAll(JSON.parse($0))", drawCommandsJson);
```
**Hinweis für die Umsetzung:** Da dies das erste JS-Interop im Projekt ist, sollte der `@JavaScript`-Ladepfad (klassenpfad-relativ, analog `@StyleSheet`) als allererster Spike verifiziert werden, bevor der Rest von 3c gebaut wird — geringes, aber neues Risiko.

**Bereichsgefiltertes Push ohne neuen Filter-Mechanismus:** Die bestehenden `VaadinChangePushBroadcaster`-Varianten haben absichtlich keinen Filter-Parameter (jeder Listener self-filtert). Das reicht hier: `StellwerkView` baut beim Aufbau (in `setParameter`) eigene, **nur auf ihren Bereich beschränkte** Index-Maps (`ListMultimap<BereichselementId, StellwerkElement>` für Gleis/Weiche/Signal — Guava ist bereits Projektabhängigkeit), reproduziert also `PushService`s `getStellwerksBereich()`-Gruppierung implizit über "kommt das geänderte Objekt in meinem Index vor". Neue, service-lokale Klasse `VaadinChangePushBroadcaster` (Package `leitstand.webui`, 1:1 Kopie des fahrzeuge-Musters: `@Observes(during = TransactionPhase.AFTER_SUCCESS) @Changed Object`) — bewusst `AFTER_SUCCESS` statt des im alten `PushService` verwendeten `AFTER_COMPLETION` (verifiziert: `PushService` nutzt tatsächlich `AFTER_COMPLETION`, welches auch nach fehlgeschlagenem Rollback feuert; `AFTER_SUCCESS` ist strikter und entspricht der in Phase 1a/2 etablierten, bewusst gewählten Konvention).

`onChanged(Object changed)` dispatcht per `instanceof` auf die passende Index-Map, redrawt nur die betroffenen `StellwerkCanvas`-Zellen und aktualisiert danach das Control-Panel (Ersatz für das alte, globale `updateControlPanel()` nach jedem Batch). Die ~200 Zeilen Draw-Command-Aufbau-Logik aus `PushService` (`createDrawCommand`/`addFahrstrasse`/`addRichtungen`/`addSignal`, Farbkonstanten) werden **unverändert** in eine neue, framework-neutrale Klasse `StellwerkDrawCommandBuilder` extrahiert; `PushService` selbst bleibt bis 3e unangetastet (Rollback-Pfad), die kurzzeitige Code-Duplikation ist akzeptabel und verschwindet mit der Löschung in 3e.

**`StellwerkVorschlagService` wird aufgelöst, nicht ersetzt:** Analyse zeigt, dass der Vorschlags-Zustand einer Session **nie an andere Sessions verteilt** wird — jede Session sieht nur ihren eigenen Overlay auf einem gemeinsam sichtbaren Gleis. Der Broadcast-Umweg existierte nur, weil rohes WebSocket keinen anderen Weg kennt, gezielt die eigene Session neu zu zeichnen. In Vaadin berechnet ein Klick (`gleisClicked`) den Vorschlag und redrawt **sofort und ausschließlich** die eigenen Canvas-Zellen des aufrufenden Views — kein Service, kein CDI-Event nötig. Die zwei Felder (`vorschlag`, `weitereVorschlaege`) werden private Felder direkt auf `StellwerkView`; damit entfällt zugleich das komplette wsId-Korrelations-Hack (Hidden-Input, Request-Param-Roundtrip) ersatzlos, da `StellwerkView` als Vaadin-`UI`-gebundene Instanz Interaktion und Push bereits im selben Kontext vereint.

**Exit-Kriterien 3c:** Initiales Redraw beim Öffnen einer Bereichs-View zeigt optisch identische Gleispläne wie das JSF-Original (Stichprobe mehrerer Bereiche); Änderung von außen (zweite Session, Sensor-Feedback) löst ein gezieltes Redraw nur der betroffenen Zellen aus, ohne globalen Reload; Fahrstraßen-Vorschlag-Overlay erscheint nur in der klickenden Session.

## Teilschritt 3d — Interaktion

1:1-Portierung der Fachlogik aus `StellwerkPresenter`, nur Transport/State-Ort ändert sich:

| Alt | Neu |
|---|---|
| `elementClicked()` (String-Lookup über `uiId`) | `onElementClicked(StellwerkElement element)` direkt aus `StellwerkCanvas`-Klick, kein Lookup |
| `gleisClicked(Gleis)` (Fahrstraßen-Zustandsmaschine, `startGleis`/`startGleisTimeStamp`, 5s-Fenster, `fahrstrassenGateway.getFahrstrassen(...)`) | **unverändert**, gleiche Felder, gleiche Gateway-Aufrufe |
| `weicheClicked`/`signalClicked` | unverändert |
| `get/setWeicheXStellung`, `get/setSignalStellung` (REST via `statusGateway`, `FacesMessage` bei Fehler) | unverändert; Fehler über `Notification.show(...)` + `NotificationVariant.LUMO_ERROR` (Muster aus `SystemControlView`) |
| `fahrstrasseReservierenZugfahrt/Rangierfahrt/fahrstrasseFreigeben` | unverändert |
| `clearControlPanel()` | unverändert, minus `stellwerkVorschlagService.clear(...)` (entfällt, siehe 3c) |
| `h:inputText wsId` (Debug-Feld) | entfällt ersatzlos |

**Control-Panel über `MainLayout.setExtraContent(...)`** (erste echte Nutzung dieses bisher nur dokumentierten Slots), aufgebaut in `onAttach()`/zurückgesetzt in `onDetach()`. Komponenten-Mapping (Grid `5% 15% auto` aus `stellwerk.css` wiederverwendet): Close-`Button` (PrimeIcons-Klasse `pi pi-times`, bereits app-weit via `V5t11AppShell` verfügbar) → `clearControlPanel()`; Signal-/Weiche1-/Weiche2-Gruppen als Icon+Name+`RadioButtonGroup<SignalStellung|WeichenStellung>` (exaktes Muster aus `SystemControlView`); Fahrstraßen-Gruppe als Icon+Name+bis zu drei `Button`s, Sichtbarkeit über `setVisible(...)` an die `*ButtonEnabled`-Flags gekoppelt.

**Risikoeinstufung:** identische Risikoklasse wie Phase 1a (`SystemControlView`) — `statusGateway.weicheStellen/signalStellen` aktuieren echte Hardware, `fahrstrassenGateway.reserviereFahrstrasse` reserviert Fahrwege exklusiv. Kein Confirm-Dialog, jede Aktion wirkt sofort (1:1 wie Original). **Manueller Smoke-Test gegen echte/simulierte Zentrale vor Cutover zwingend**, inkl. kompletter Fahrstraßen-Zustandsmaschine (2-Klick-Fenster) und Live-Redraw bei Fremdänderung.

**Exit-Kriterien 3d:** Alle Interaktionen (Weichen-/Signalstellung, Fahrstraßen-Reservierung/-Freigabe, Vorschlags-Overlay) lösen dieselbe Fachlogik aus wie im JSF-Original; Control-Panel erscheint/verschwindet korrekt je nach Auswahl; Hardware-Smoke-Test durch den Nutzer bestanden.

## Teilschritt 3e — Cleanup

Löschung **erst nach Soak-Periode** und verifiziertem Cutover (alle Bereichs-URLs in `NavigationProducer` auf `ui/stellwerk/{bereich}` umgestellt, "(alt)"-Einträge entfernt). Verifiziert: `PushService` und `StellwerkVorschlagService` referenzieren sich zwar gegenseitig, aber **keine** der vier Alt-Klassen hat weitere Konsumenten außerhalb von `leitstand.webui` bzw. `StellwerkPresenter` — daher kein gestufter Umbau nötig, alle vier können **in einem Schritt** gelöscht werden:
- `PushService.java` (529 Zeilen, inkl. `@ServerEndpoint`)
- `StellwerkPresenter.java` (387 Zeilen)
- `StellwerkVorschlagService.java`
- `StellwerkSessionHolder.java`
- `leitstand.xhtml`, `view/stellwerk.xhtml`, alte `stellwerk.js` (WebSocket-Variante — **nicht** identisch mit neuer `stellwerk-draw.js`)

`stellwerk.css` bleibt unverändert (wird von altem und neuem View während der Soak-Zeit gemeinsam genutzt, keine Duplizierung nötig).

## Offene Punkte für die Umsetzung (bewusst nicht vorab entschieden)

- Push-Transport-Latenz vor Cutover mit realer/simulierter Hardware verifizieren (gezielte Redraws sollen nicht spürbar langsamer sein als der bisherige volle WebSocket-Push).
- Ob der initiale Vollzeichnen-Redraw beim View-Attach synchron reicht oder (wie `PushService.onOpen` es per `ManagedExecutor` tut) asynchron entkoppelt werden sollte, bei sehr großen Bereichen im Praxistest prüfen.
- PrimeIcons-Klassen (`pi pi-*`) statt Vaadin-`Icon` weiterverwenden, für optische Konsistenz mit den bestehenden `NavigationItem`-Icons.

## Kritische Dateien

**Alt (Referenz/Rollback-Pfad, bis 3e unangetastet):**
- `v5t11-stellwerk/src/main/java/de/gedoplan/v5t11/leitstand/webui/StellwerkPresenter.java`
- `v5t11-stellwerk/src/main/java/de/gedoplan/v5t11/leitstand/webui/PushService.java`
- `v5t11-stellwerk/src/main/java/de/gedoplan/v5t11/leitstand/webui/StellwerkVorschlagService.java`
- `v5t11-stellwerk/src/main/java/de/gedoplan/v5t11/leitstand/webui/StellwerkSessionHolder.java`
- `v5t11-stellwerk/src/main/resources/META-INF/resources/view/stellwerk.xhtml`
- `v5t11-stellwerk/src/main/resources/META-INF/resources/stellwerk.js`
- `v5t11-stellwerk/src/main/java/de/gedoplan/v5t11/leitstand/webui/NavigationProducer.java` (wird geändert, nicht gelöscht)

**Neu anzulegen:**
- `v5t11-stellwerk/pom.xml` (Vaadin-Dependencies)
- `v5t11-stellwerk/src/main/java/de/gedoplan/v5t11/leitstand/vaadinui/StellwerkView.java`
- `v5t11-stellwerk/src/main/java/de/gedoplan/v5t11/leitstand/vaadinui/StellwerkCanvas.java`
- `v5t11-stellwerk/src/main/java/de/gedoplan/v5t11/leitstand/webui/VaadinChangePushBroadcaster.java`
- `v5t11-stellwerk/src/main/java/de/gedoplan/v5t11/leitstand/webui/StellwerkDrawCommandBuilder.java` (extrahiert aus `PushService`)
- `v5t11-stellwerk/src/main/resources/META-INF/resources/stellwerk-draw.js`

**Referenz-/Vorlagen-Dateien (bereits verifizierte Muster):**
- `v5t11-vaadincommon/src/main/java/de/gedoplan/v5t11/vaadincommon/ui/MainLayout.java`
- `v5t11-status/src/main/java/de/gedoplan/v5t11/status/vaadinui/SystemControlView.java`
- `v5t11-fahrzeuge/src/main/java/de/gedoplan/v5t11/fahrzeuge/webui/VaadinChangePushBroadcaster.java`

## Verifikation

Je Teilschritt (3a–3d) manueller Smoke-Test vor Fortschritt zum nächsten, analog zum bisherigen Vorgehen bei Phase 1a/2: `mvn -pl v5t11-stellwerk -am clean compile` nach jeder neuen Datei (nicht nur `compile` ohne `clean` — siehe bekannter Stolperstein aus Phase 2, wo das einen echten Compile-Fehler in einer neuen Datei verschleiert hat); Dev-Server wird nur nach expliziter Freigabe durch den Nutzer gestartet (echte Postgres/Kafka-Infrastruktur läuft nur in dessen eigener Dev-Umgebung). Vor dem endgültigen Cutover (3d→3e-Übergang) manueller Hardware-Smoke-Test durch den Nutzer gegen echte/simulierte Zentrale, wie in Phase 1a etabliert.
