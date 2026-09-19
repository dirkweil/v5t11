# Phase 2 (v5t11-fahrzeuge): `fahrzeugMessung`/`gleisMessung` → Vaadin

## Context

Fortsetzung der JSF→Vaadin-Migration (Branch `vaadin`, siehe `/home/dw/.claude/plans/functional-singing-canyon.md`). In `v5t11-fahrzeuge` sind `fahrzeug-list`, `fahrzeug-control` (inkl. Basisdaten-/Position-Dialogen), `fahrzeug-function` und `fahrzeug-traktion` bereits migriert und verifiziert. Von den drei verbliebenen Cross-Links in `FahrzeugControlView.buildEditMenu()` (Programmierung/Geschwindigkeiten/Gleislängen) sind laut Migrationsplan **Geschwindigkeiten** (`fahrzeugMessung.xhtml`) und **Gleislängen** (`gleisMessung.xhtml`) als Nächstes dran; **Programmierung** (`fahrzeugProgram.xhtml`) bleibt bewusst zuletzt (höchstes Aktuationsrisiko).

Beide Views teilen ein Governance-Muster, das sich von allen bisher migrierten Views unterscheidet: Die zugrunde liegenden `@ApplicationScoped`-Services (`GeschwindigkeitsMessService`, `GleisMessService`) benachrichtigen **nicht** über den etablierten `VaadinChangePushBroadcaster`, sondern über einen eigenen, service-internen Single-Slot-Observer (`attachObserver(Runnable)`/`detachObserver()` — verifiziert in beiden Service-Klassen, Zeilen ~68-82 bzw. ~80-113). Ziel dieses Plans ist es, dieses Muster einmal sauber auf Vaadin zu bringen und für beide Views anzuwenden.

## Vorgehen

Beide Views in einer Session, aber als **zwei getrennte Commits**: zuerst `GleisMessungView` (einfacher, 3 Zustände, kein `Thread.sleep`), dann `FahrzeugMessungView` nach demselben Muster. Alte `.xhtml`/Presenter/`GeschwindigkeitsMessService`/`GleisMessService` bleiben **unangetastet** (Rollback-Pfad, wie bei jeder vorherigen Phase).

## Neue Dateien

- `v5t11-fahrzeuge/src/main/java/de/gedoplan/v5t11/fahrzeuge/vaadinui/GleisMessungView.java`
  `@Route(value = "gleis-messung", layout = MainLayout.class)`, `@PageTitle("Gleislängen - v5t11")`
- `v5t11-fahrzeuge/src/main/java/de/gedoplan/v5t11/fahrzeuge/vaadinui/FahrzeugMessungView.java`
  `@Route(value = "fahrzeug-messung", layout = MainLayout.class)`, `@PageTitle("Geschwindigkeiten - v5t11")`

Beide `extends VerticalLayout`, Struktur analog `FahrzeugFunctionView.java` (Referenz, bereits gelesen/verifiziert): `@PostConstruct init()`, `getRefreshedFahrzeug()` via `FahrzeugListPresenter`/`FahrzeugRepository.isAttached(...)`, Header mit Betriebsnummer/Bild/DecoderAdr + Speichern/zurück-Buttons (Header-Code aus `FahrzeugFunctionView.buildHeader()`/`getImage()` übernehmen).

## Bridging Single-Slot-Observer → Vaadin

Signaturen bestätigt (`GeschwindigkeitsMessService.java:106/112`, `GleisMessService.java:75/81`): `public void attachObserver(Runnable observer)` / `public void detachObserver()`, jeweils nur **ein** Slot (kein Listener-Set, vorbestehendes `// TODO mehrere Observer?` — nicht fixen, entspricht altem `@ViewScoped`-Presenter-Verhalten: jeweils nur eine offene Messungs-View gleichzeitig).

Muster (analog `FahrzeugControlView`s `onAttach`/`onDetach`, aber direkt an den Service statt an `VaadinChangePushBroadcaster` gebunden):

```java
@Override
protected void onAttach(AttachEvent attachEvent) {
  super.onAttach(attachEvent);
  refreshAll();  // initialer Sync
  this.geschwindigkeitsMessService.attachObserver(this::onServiceChanged); // bzw. gleisMessService
}

@Override
protected void onDetach(DetachEvent detachEvent) {
  this.geschwindigkeitsMessService.detachObserver();
  super.onDetach(detachEvent);
}

private void onServiceChanged() {
  getUI().ifPresent(ui -> ui.access(this::refreshAll));
}
```

Wichtig:
- `Runnable` liefert **kein** Änderungsobjekt — `refreshAll()` liest alle Anzeigewerte frisch aus den Service-Gettern (`getStatusDescription()`, `isAktiv()`, `getGeschwindigkeit()`/`getMessungen()`).
- `attachObserver`/`detachObserver` ausschließlich in `onAttach`/`onDetach` (nicht `@PostConstruct`), analog zu `FahrzeugControlView`.
- `refreshAll()` darf (wie in `FahrzeugControlView.refreshAll()` dokumentiert) keine `@SessionScoped`-Bridges anfassen — nur den injizierten `@ApplicationScoped`-Service und das lokale `fahrzeug`-Feld lesen.
- Simulationsmodus (`v5t11.host` unset): Service ruft den Observer ggf. **synchron** noch während des Button-Click-Handlers auf, bevor `attachObserver`/Click zurückkehren — `ui.access()` innerhalb desselben UI-Request ist unkritisch (dokumentierter Vaadin-Fall), aber beim manuellen Test explizit auf `UIDetachedException`/`IllegalStateException` prüfen.

## UI-Mapping (PrimeFaces → Vaadin)

| PrimeFaces | Vaadin |
|---|---|
| `p:toolbar`-Header | `HorizontalLayout` wie `FahrzeugFunctionView.buildHeader()` (Betriebsnummer/Bild/DecoderAdr + Speichern/zurück) |
| `h:panelGrid` Messgleis/Länge/Nachbargleise (nur fahrzeugMessung) | `FormLayout` mit `addFormItem(new Span(...), "Label:")`, statisch (service-seitig fix nach `@PostConstruct`) |
| Aktions-/Abbrechen-Buttons | `Button`, `setEnabled(...)` in `refreshAll()` nach `isAktiv()` |
| Statustext | `Span`, gebunden in `refreshAll()` an `getStatusDescription()` |
| `p:dataTable` Fahrstufe/vorwärts/rückwärts | `Grid<GeschwindigkeitsEntry>` (Presenter-Inner-Class-Form übernehmen), `setItems(...)` in `refreshGeschwindigkeiten()`, Formatierung `String.format(Locale.GERMAN, "%,d µm/s (%,d km/h)", ...)`, je zwei `addComponentColumn(this::buildRemoveButton)` für vorwärts/rückwärts-Löschen |
| `p:dataTable` Bereich/Name/Länge-bisher/Länge-neu | `Grid` gebunden an `gleisMessService.getMessungen()` (`SortedMap<Gleis,Integer>`), Spalten-Header-Button "alle Overrides entfernen" analog `FahrzeugFunctionView`s `removeColumn.setHeader(addButton)`-Pattern, Trash-Button pro Zeile |
| `p:dialog`-Confirm | `ConfirmDialog` wie `FahrzeugControlView.openRemoveConfirmDialog()`: `setHeader(beschreibung)`, `setText(anleitung)`, `setCancelable(true)`, `addConfirmListener(...)` |
| `p:remoteCommand` + JSF-Websocket-Workaround-Script | entfällt komplett — ersetzt durch `attachObserver`/`ui.access()` |
| `FacesMessage`-Fehlermeldung | `Notification.show(msg).addThemeVariants(NotificationVariant.LUMO_ERROR)` wie `FahrzeugControlView.saveFahrzeug()` |

**Speichern**: `geschwindigkeitsMessService.save()`/`gleisMessService.save()` (unverändert `@Transactional`), danach `getUI().ifPresent(ui -> ui.getPage().setLocation("/ui/fahrzeug-control"))` wie `FahrzeugFunctionView.navigateToControl()`.
**Zurück**: gleiche Navigation ohne `save()` (entspricht altem `action="finished" immediate="true"`).
**Abbrechen**: direkter Aufruf `abbrechen()`, keine Bestätigung nötig (wie im Original).
**Confirm-vor-Messung**: view-lokales `private Runnable selectedAktion`-Feld statt Presenter-Feld; Klick auf Aktions-Button setzt es + öffnet `ConfirmDialog`; `addConfirmListener` führt `selectedAktion.run()` in try/catch aus, zeigt bei Fehler `Notification` (LUMO_ERROR) — inhaltlich identisch zu `FahrzeugMessungPresenter.execSelectedAktion()`/`GleisMessungPresenter.execSelectedAktion()`, nur ohne die PrimeFaces-`ajax().update(...)`-Aufrufe.

## Änderungen in `FahrzeugControlView.java`

`buildEditMenu()`:
```java
subMenu.addItem("Geschwindigkeiten", event -> navigateToFahrzeugMessung());
subMenu.addItem("Gleislängen", event -> navigateToGleisMessung());
```
plus zwei neue private Methoden analog `navigateToFunction()`/`navigateToTraktion()`:
```java
private void navigateToFahrzeugMessung() {
  this.fahrzeugListPresenter.setCurrentFahrzeug(this.fahrzeug);
  getUI().ifPresent(ui -> ui.getPage().setLocation("/ui/fahrzeug-messung"));
}
private void navigateToGleisMessung() {
  this.fahrzeugListPresenter.setCurrentFahrzeug(this.fahrzeug);
  getUI().ifPresent(ui -> ui.getPage().setLocation("/ui/gleis-messung"));
}
```
Klassen-Javadoc anpassen: Satz über "Programmierung/Geschwindigkeiten/Gleislängen bleiben Cross-Links" auf nur noch "Programmierung" reduzieren. `faces-config.xml` bleibt unverändert (alte JSF-Views bleiben nur noch per Direkt-URL erreichbar, nicht mehr verlinkt — das ist der Rollback-Pfad).

## Risiken / Was sich NICHT ändern darf

- `GeschwindigkeitsMessService`/`GleisMessService` selbst werden **nicht** angefasst (State-Machine, `Thread.sleep(5000)` auf dem CDI-Async-Event-Thread, Interpolationslogik in `save()`, Simulationszweig via `v5t11.host`) — die neue View ist ein reiner Drop-in-Ersatz für den Presenter, liest nur die bestehenden Getter.
- `onServiceChanged()`-Callback darf vor `ui.access(...)` keine blockierende Arbeit ausführen (läuft ggf. auf dem CDI-Async-Thread).
- Single-Observer-Slot: nur eine `Gleislängen`/`Geschwindigkeiten`-View gleichzeitig offen halten (bestehende Einschränkung, nicht neu einführen oder "fixen").

## Verifikation

1. `./mvnw -pl v5t11-fahrzeuge -am compile` (Root: `/opt/prj/gedoplan/v5t11`) — muss grün sein.
2. Manueller Test in `quarkus:dev` (Simulationsmodus zuerst, `v5t11.host` nicht gesetzt):
   - `fahrzeug-list` → Fahrzeug wählen → `fahrzeug-control` → `bearbeiten` → `Gleislängen`: Route `/ui/gleis-messung`, Header korrekt, "Gleislängen messen" → Confirm-Dialog mit korrektem Text → bestätigen → Grid füllt sich, `Speichern` persistiert, `zurück` navigiert ohne Nebenwirkungen zurück zu `fahrzeug-control`.
   - Gleiches für `Geschwindigkeiten` → `/ui/fahrzeug-messung`: beide Aktions-Buttons, Confirm-Text, Tabelle füllt sich, Zeilen-Löschen funktioniert, `Speichern` persistiert über `FahrzeugRepository.merge`.
   - Alte JSF-Views (`/view/gleisMessung.xhtml`, `/view/fahrzeugMessung.xhtml`) parallel öffnen und bestätigen, dass sie unverändert funktionieren (Rollback-Pfad-Nachweis).
3. Live-Update: Status/Tabelle aktualisieren sich ohne manuelles Neuladen, wenn Events feuern (nur eine Messungs-View gleichzeitig offen, wie bisher).
4. Falls verfügbar: ein echter Messvorgang gegen reale Hardware/Zentrale, um Timing-Verhalten (`Thread.sleep`-Interaktion) zu bestätigen.
5. Bestätigen, dass `FahrzeugControlView`'s Menü nicht mehr auf die JSF-Seiten verlinkt, diese aber per Direkt-URL weiter erreichbar sind.
