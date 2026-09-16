# Phase 2 Start: `v5t11-fahrzeuge` nach Vaadin – Aufwärmübung `gleisParameter`

## Context

Phase 1 (`v5t11-status`) ist abgeschlossen. Laut Migrationsplan (`functional-singing-canyon.md`) beginnt
jetzt Phase 2 (`v5t11-fahrzeuge`), empfohlen mit dem trivialsten View als Aufwärmübung: `gleisParameter`
(36-Zeilen-Presenter, editierbare Tabelle der Gleis-Längen/verdeckt-Flags).

`v5t11-fahrzeuge` hat bisher **keinerlei** Vaadin-Anbindung (kein `vaadinui`-Package, keine Abhängigkeit
auf `v5t11-vaadincommon`/`vaadin-quarkus-extension` in der `pom.xml`). Dieser Schritt bringt also zugleich
das Vaadin-Fundament (analog Phase 0 bei `v5t11-status`) in dieses Modul.

`gleisParameter` ist im alten JSF kein eigener Menüpunkt, sondern eine Detailseite, die per Button aus
`fahrzeugList.xhtml` erreicht wird (`action="gleisParameter"`). Zwei tote Code-Stellen wurden dabei
gefunden und laut Nutzer-Entscheidung **nicht** nachgebaut:
- Button "messen" (`action="messung"`) hat weder `navigation-case` noch Presenter-Methode.
- Button "speichern" ruft `#{gleisParameterPresenter.save()}` auf – diese Methode existiert nicht im
  Presenter. Das tatsächliche Speichern läuft bereits automatisch pro Zeile über
  `onRowEdit`/`p:ajax event="rowEdit"` → `parcoursService.saveGleis(id)`.

Die neue Vaadin-View bildet daher nur die tatsächlich funktionierende Kernfunktion nach: Tabelle mit
inline editierbaren Zeilen (Länge, verdeckt), die beim Verlassen der Editier-Zeile automatisch
gespeichert werden – plus eine einfache "zurück"-Navigation.

## Vaadin-Fundament für `v5t11-fahrzeuge` (analog Phase 0)

**`v5t11-fahrzeuge/pom.xml`**: die zwei Dependency-Blöcke ergänzt, exakt wie in `v5t11-status/pom.xml`
bereits vorhanden:
```xml
<dependency>
  <groupId>de.gedoplan</groupId>
  <artifactId>v5t11-vaadincommon</artifactId>
  <version>3.0.0-SNAPSHOT</version>
</dependency>

<dependency>
  <groupId>com.vaadin</groupId>
  <artifactId>vaadin-quarkus-extension</artifactId>
</dependency>
```
Kein eigener `V5t11VaadinServlet` nötig – die Klasse lebt zentral in `v5t11-vaadincommon`
(`de.gedoplan.v5t11.vaadincommon.ui.V5t11VaadinServlet`, `@WebServlet(urlPatterns = "/ui/*", ...)`) und
wird, wie schon bei `v5t11-status` erprobt, allein durch die Modul-Abhängigkeit automatisch mitgeladen
(Quarkus/Jandex indiziert die `vaadincommon`-Klassen im Reactor-Build). `MainLayout` wird ebenso
unverändert aus `v5t11-vaadincommon` wiederverwendet.

## Neue Datei: `GleisParameterView.java`

`v5t11-fahrzeuge/src/main/java/de/gedoplan/v5t11/fahrzeuge/vaadinui/GleisParameterView.java`
(neues Package `vaadinui`, analog zu `de.gedoplan.v5t11.status.vaadinui`).

- `@Route(value = "gleis-parameter", layout = MainLayout.class)`, `@PageTitle("Gleis-Parameter - v5t11")`.
- Extends `VerticalLayout` (Pattern von `SystemStatusView`), kein separater Presenter – injiziert
  `ParcoursService` direkt (`@Inject ParcoursService parcoursService;`), da dessen `getGleise()`/
  `saveGleis(id)` bereits die komplette benötigte Logik liefern.
- `@PostConstruct init()`: baut Toolbar (Titel "Gleis-Parameter" + "zurück"-Button) und ein
  `Grid<Gleis>` mit den Spalten Bereich (readonly), Name (readonly), Länge (editierbar, Integer),
  verdeckt (editierbar, Boolean) – über `Grid.getEditor()` (Binder-basierter Inline-Editor,
  Vaadin-Äquivalent zu `p:rowEditor`/`p:cellEditor`), Editor-Buttons-Spalte mit Edit-/Save-/Cancel-Icons.
- Datenquelle: `parcoursService.getGleise()` direkt in den Grid-Items (dieselben Entity-Instanzen wie im
  Service-internen Cache, kein Kopieren nötig – Speichern mutiert direkt die gecachten Objekte, wie im
  JSF-Original).
- Beim Speichern einer Zeile (Editor-Save-Callback): `parcoursService.saveGleis(gleis.getId())`
  aufrufen (identische Methode wie bisher `onRowEdit`), danach Grid-Zeile neu rendern.
- "zurück"-Button: navigiert per kontextrelativem Pfad zurück zur (weiterhin JSF-basierten)
  `fahrzeugList.xhtml` (`UI.getPage().setLocation("/view/fahrzeugList.xhtml")`) – Vaadin- und
  JSF-Views laufen im selben Deployment/Context, daher kein `ConfigService`-Umweg nötig.

Kein Push/Live-Update nötig (wie `SystemStatusView`: einmaliges Laden beim Öffnen reicht, Gleis-Längen
ändern sich nicht durch Fremdereignisse).

## Cutover-Verknüpfung in `fahrzeugList.xhtml` (JSF, ansonsten unverändert)

`v5t11-fahrzeuge/src/main/resources/META-INF/resources/view/fahrzeugList.xhtml`: der Button
`action="gleisParameter"` (`<p:commandButton>`, JSF-Navigation) wurde auf `<p:button href="/ui/gleis-parameter">`
umgestellt – ein direkter, kontextrelativer Link zur neuen Vaadin-Route, ohne Formular-Submit. Rest von
`fahrzeugList.xhtml` bleibt unverändert (wird erst in einem späteren Schritt von Phase 2 migriert).

Die alten Artefakte (`gleisParameter.xhtml`, `GleisParameterPresenter.java`) bleiben zunächst als
Rollback-Pfad erhalten (Soak-Periode, gleiches Muster wie in Phase 1) und werden erst nach Bestätigung
durch den Nutzer gelöscht.

## Verifikation

- `mvn -pl v5t11-fahrzeuge -am compile` — muss fehlerfrei laufen.
- Nutzer prüft in `quarkus:dev`: von `fahrzeugList.xhtml` aus auf "Gleisparameter" klicken → neue
  Vaadin-View unter `/ui/gleis-parameter` erscheint; Tabelle zeigt Bereich/Name/Länge/verdeckt korrekt;
  Zeile bearbeiten (Länge ändern, verdeckt togglen) → Speichern-Icon → Wert bleibt nach Reload
  persistiert (identisch zum bisherigen Verhalten); "zurück" führt zurück zu `fahrzeugList.xhtml`.

## Ergebnis

Umgesetzt und vom Nutzer verifiziert ("funktioniert"). Committet als `28d90f57` ("Phase 2 Start:
gleisParameter nach Vaadin migriert").
