# Phase 2 (v5t11-fahrzeuge): `fahrzeugTraktion` — Korrektur nach erster Umsetzung

## Context

`FahrzeugTraktionView` (Route `fahrzeug-traktion`, TwinColGrid-Add-on) ist bereits implementiert und kompiliert grün (siehe vorheriger Plan-Durchlauf). Der Nutzer hat nach Test in `quarkus:dev` vier konkrete Korrekturen gemeldet. Dieser Plan beschreibt ausschließlich diese Korrekturen an der bestehenden Datei `v5t11-fahrzeuge/src/main/java/de/gedoplan/v5t11/fahrzeuge/vaadinui/FahrzeugTraktionView.java`, kein Neubau.

Für Punkt 4 (Umsortieren funktioniert nicht) wurde die Ursache im Quellcode des Add-ons (`FlowingCode/TwinColGridAddon`, GitHub) verifiziert: `setSelectionGridReorderingAllowed(true)` setzt nur ein internes Flag (`selection.allowReordering = value`); dieses Flag wird ausschließlich innerhalb der Drag&Drop-Listener ausgewertet, die **nur** durch `withDragAndDropSupport()` registriert werden (`configDragAndDrop(...)`). Ohne diesen Aufruf hat das Flag schlicht keine Wirkung — das ist der Bug. `withDragAndDropSupport()` aktiviert als Nebeneffekt zusätzlich Drag&Drop-Transfer zwischen den beiden Grids (nicht nur Button-Transfer) — das ist eine sinnvolle Ergänzung, keine unerwünschte Nebenwirkung.

Für Punkt 3 wurde ebenfalls im Quellcode verifiziert: `addColumn(...)` setzt keinen Standard-Header (muss explizit per `.setHeader(...)` gesetzt werden — Weglassen genügt also, um den Text zu entfernen), und beide Grids nutzen intern `Grid.SelectionMode.MULTI` mit Vaadins eingebauter Auswahlspalte (nicht Teil der Add-on-API) — die "Alle auswählen"-Checkbox in deren Header lässt sich über Vaadins Standard-API `GridMultiSelectionModel.setSelectAllCheckboxVisibility(HIDDEN)` ausblenden, ohne die Transfer-Funktion zu beeinträchtigen (Transfer läuft über Buttons/Drag&Drop auf Basis von `grid.getSelectedItems()`, nicht über Klick auf die einzelnen Zeilen-Checkboxen).

## Änderungen in `FahrzeugTraktionView.java`

Alle vier Punkte betreffen nur `buildTwinColGrid()` (und `init()` für Punkt 1); der Rest der Datei (Kopfbereich, `saveFahrzeug()`, `getRefreshedFahrzeug()`) bleibt unverändert.

1. **Vertikalen Platz nutzen:**
   - In `init()`: `add(buildHeader(), buildTwinColGrid());` bleibt, zusätzlich `setFlexGrow(1, this.twinColGrid);` danach (View selbst ist `VerticalLayout` mit `setSizeFull()` — das TwinColGrid muss den nach dem Kopfbereich verbleibenden Platz per Flex-Grow einnehmen).
   - In `buildTwinColGrid()`: `this.twinColGrid.setSizeFull();` statt nur `setWidthFull()`, plus `this.twinColGrid.forEachGrid(grid -> grid.setSizeFull());` (stellt sicher, dass auch die beiden inneren `Grid<Fahrzeug>` selbst die Höhe ausnutzen, `forEachGrid` ist bestehende Add-on-API).

2. **Linke Liste nach Betriebsnummer sortiert:**
   - Nach dem Zusammenbau von `candidates` (verfügbare ∪ bereits gezogene, minus sich selbst) zusätzlich sortieren, bevor die Liste an den `TwinColGrid`-Konstruktor geht:
     ```java
     candidates.sort(Comparator.comparing(Fahrzeug::getBetriebsnummer));
     ```
     (Bisher war nur die Verfügbaren-Teilmenge sortiert, die angehängten wurden unsortiert angehängt — dadurch war die Gesamtreihenfolge, aus der sich die linke Liste speist, nicht durchgängig sortiert.) Neuer Import `java.util.Comparator`.

3. **Spaltenkopf "Betriebsnummer" und "Alle auswählen"-Checkbox entfernen (beide Listen):**
   - `.setHeader("Betriebsnummer")` beim `addColumn(...)`-Aufruf ersatzlos streichen (kein Ersatztext, Header bleibt leer):
     ```java
     this.twinColGrid.addColumn(Fahrzeug::getBetriebsnummer);
     ```
   - Zusätzlich die "Alle auswählen"-Checkbox der eingebauten Vaadin-Auswahlspalte auf beiden Grids ausblenden:
     ```java
     this.twinColGrid.forEachGrid(grid -> {
       if (grid.getSelectionModel() instanceof GridMultiSelectionModel<Fahrzeug> multiSelectionModel) {
         multiSelectionModel.setSelectAllCheckboxVisibility(SelectAllCheckboxVisibility.HIDDEN);
       }
     });
     ```
     Neue Imports: `com.vaadin.flow.component.grid.GridMultiSelectionModel`, `com.vaadin.flow.component.grid.GridMultiSelectionModel.SelectAllCheckboxVisibility`.

4. **Umsortieren rechts aktivieren (Bugfix):**
   - Zusätzlich zu bereits vorhandenem `this.twinColGrid.setSelectionGridReorderingAllowed(true);` neu: `this.twinColGrid.withDragAndDropSupport();` aufrufen (Reihenfolge egal, beide sind unabhängige Setter/Fluent-Aufrufe auf derselben Instanz) — erst dieser Aufruf verdrahtet die Drag&Drop-Listener, die das Reordering-Flag überhaupt auswerten.

## Nicht betroffen

- Speichern-Logik (`saveFahrzeug()`), Navigation, `FahrzeugControlView`-Integration — alle bereits korrekt aus dem vorherigen Durchlauf.
- `fahrzeugTraktion.xhtml`/`FahrzeugTraktionPresenter.java` bleiben unverändert als Rollback-Pfad.

## Verifikation

- `mvn -pl v5t11-fahrzeuge -am compile` — muss fehlerfrei laufen.
- Nutzer prüft in `quarkus:dev`: TwinColGrid füllt die verfügbare Höhe unterhalb des Kopfbereichs; linke Liste ("Verfügbare Fahrzeuge") ist nach Betriebsnummer sortiert; keine Spaltenkopf-Beschriftung "Betriebsnummer" und keine "Alle auswählen"-Checkbox in beiden Listen sichtbar; Fahrzeuge im rechten Grid ("Angehängte Fahrzeuge") lassen sich jetzt per Drag&Drop umsortieren; Transfer zwischen den Listen (Button und/oder Drag&Drop) funktioniert weiterhin wie zuvor.
