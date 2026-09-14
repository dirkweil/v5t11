# Horizontale Toggle-Buttons für Weichen-/Signal-Stellung (Vaadin) — Fix

## Context

Im JSF-Original (`v5t11-status/.../view/systemControl.xhtml`) werden Weichen-Stellung (G/A) und
Signal-Stellung (H/F/L/R) über PrimeFaces `p:selectOneButton` dargestellt — verbundene, horizontale
Toggle-Buttons statt klassischer Radiobuttons. Die Vaadin-Migration bildet dieselben Felder mit
`RadioButtonGroup<WeichenStellung>` / `RadioButtonGroup<SignalStellung>` nach
(`v5t11-status/.../vaadinui/SystemControlView.java`).

**Erster Versuch (bereits umgesetzt, fehlerhaft):** Ein CSS-Theme `v5t11` wurde als "Theme-in-JAR"
in `v5t11-vaadincommon/src/main/resources/META-INF/resources/themes/v5t11/styles.css` angelegt und
über `@Theme("v5t11")` (`com.vaadin.flow.theme.Theme`) auf `V5t11AppShell` aktiviert.

**Beobachteter Fehler:** Die Weichen-/Signal-Stellung erscheint als reiner unformatierter Text —
nicht klickbar (kein Button-Look, keine visuelle Auswahl-Hervorhebung), aktueller Stand nicht
erkennbar.

**Root Cause (per Recherche/Dekompilierung von `flow-server-25.2.8` verifiziert):**
- `@Theme` ist ab Vaadin 25 **deprecated** und für den neuen "application theme"-Mechanismus
  (Ordner `frontend/themes/<name>/theme.json` + `styles.css`) ausgelegt.
- Ohne `theme.json` wird der Theme-Ordner zwar gefunden (`ThemeUtils.getThemeFolder` prüft nur
  Ordner-Existenz), aber die eigentliche CSS-Einbettung schlägt **still** fehl: die generierten
  `theme-v5t11.global.generated.js` / `theme-v5t11.components.generated.js` bleiben leere Stubs,
  `dev-bundle/config/stats.json` zeigt `"themeJsonContents": {}` — das CSS erreicht nie den Browser.
- Schwerwiegender: `@Theme("v5t11")` **ersetzt das Standard-Theme der App komplett** (keine
  Parent-Kette zu Lumo, da kein `theme.json`), wodurch die gesamte App ihr Lumo-Default-Styling
  verliert — das erklärt den beobachteten "reiner Text"-Effekt (nicht nur die eigene
  `toggle-buttons`-Klasse fehlt, sondern auch alle Lumo-Basisstile für `vaadin-radio-button`).
- Die CSS-Selektoren selbst (`::part(radio)`, `::part(label)`) sind korrekt und passen zur
  installierten `@vaadin/radio-group`-Version — das ist **nicht** die Fehlerursache.
- Offiziell empfohlener, nicht-deprecated Weg für genau diesen Fall (zusätzliches CSS aus einer
  JAR-Ressource laden, ohne das App-Theme zu ersetzen): `@StyleSheet` (`com.vaadin.flow.component.dependency.StyleSheet`)
  auf der `AppShellConfigurator`-Klasse, das direkt auf eine statische Ressource unter
  `META-INF/resources/...` verlinkt — kein `theme.json`, kein lokaler `frontend/themes`-Ordner nötig,
  und das bestehende Lumo-Theme bleibt aktiv.

## Fix

### 1. `V5t11AppShell.java` — `@Theme` durch `@StyleSheet` ersetzen

`v5t11-vaadincommon/src/main/java/de/gedoplan/v5t11/vaadincommon/ui/V5t11AppShell.java`:
- Import `com.vaadin.flow.theme.Theme` entfernen, `com.vaadin.flow.component.dependency.StyleSheet` ergänzen.
- `@Theme("v5t11")` ersetzen durch `@StyleSheet("themes/v5t11/styles.css")` (Pfad relativ zu
  `META-INF/resources/`, zeigt auf die bereits vorhandene, unveränderte CSS-Datei).
- `@Push` bleibt unverändert erhalten.

### 2. CSS-Datei und Java-View unverändert lassen

- `v5t11-vaadincommon/src/main/resources/META-INF/resources/themes/v5t11/styles.css` bleibt wie
  aktuell (Inhalt war nicht die Fehlerursache).
- `SystemControlView.java` (`addClassName("toggle-buttons")` auf `weichenStellungField` /
  `signalStellungField`) bleibt unverändert.

### 3. Stale Build-Artefakte bereinigen

Der vorherige fehlerhafte `@Theme`-Versuch hat in `v5t11-status/src/main/frontend/generated/`
leere `theme-v5t11.*.generated.js`-Stubs sowie einen entsprechenden Eintrag im Dev-Bundle
(`target/dev-bundle`) hinterlassen. Da `frontend/generated/` und der Vaadin-Build-Output
git-ignored bzw. generiert sind, reicht ein sauberer Rebuild (`mvn compile` bzw. Neustart von
`quarkus:dev`, das Vaadin bei geänderten Annotationen automatisch neu bundelt) — keine manuelle
Bereinigung nötig, aber ggf. hilfreich, `target/` im betroffenen Modul zu löschen, falls der
Dev-Server einen stale Bundle-Zustand cached.

## Verifikation

- `mvn -pl v5t11-vaadincommon,v5t11-status -am compile` — muss fehlerfrei laufen.
- Prüfen, dass `v5t11-status/src/main/frontend/generated/jar-resources/themes/v5t11/styles.css`
  weiterhin existiert und dass kein leerer `theme-v5t11.*.generated.js`-Stub mehr erzeugt wird
  (stattdessen sollte die Stylesheet-Ressource über den `@StyleSheet`-Mechanismus eingebunden werden,
  z. B. sichtbar als `<link>`/injizierter Style im generierten Bootstrap-HTML).
- App im Dev-Mode starten (User startet `quarkus:dev` selbst, siehe Memory-Regel — nicht proaktiv
  starten) und `/ui/system-control` aufrufen:
  - Restliche App (Lumo-Styling von Buttons, ComboBoxen, Checkboxen etc.) muss weiterhin normal
    aussehen (Regressionscheck für den durch `@Theme` verursachten Lumo-Ausfall).
  - Weichen-Stellung (G/A) und Signal-Stellung (H/F/L/R) erscheinen als verbundene, horizontale
    Buttons, aktiver Wert farblich hervorgehoben, Klick ändert Auswahl und stellt Weiche/Signal.
  - Disabled-Zustand (kein Gleis/keine Weiche/kein Signal ausgewählt) sichtbar gedimmt.

## Nachtrag: "Weiterhin vertikal & Text" nach dem Fix — Diagnose

Nach dem `@StyleSheet`-Fix meldete der Nutzer, die Weichen-Stellung erscheine weiterhin als reiner
Text und nun sogar vertikal statt horizontal gestapelt. Direkte Live-Untersuchung des laufenden
`quarkus:dev`-Prozesses (PID-Zugriff, `curl` auf den Server) ergab:

- `/themes/v5t11/styles.css` wird vom Server korrekt mit **aktuellem** Inhalt ausgeliefert (200 OK).
- Der generierte Bootstrap-HTML-`<link>`-Tag verweist korrekt darauf.
- Der kompilierte Frontend-Bundle (`target/classes/META-INF/VAADIN/webapp/VAADIN/build/*.js`) ist
  frisch gebaut (Zeitstempel nach dem Fix) und enthält sowohl Lumo- als auch
  `vaadin-radio-group`-Code.
- Keine Konfiguration (`application.properties`, `pom.xml`) beeinflusst Theme/Stylesheet-Auflösung;
  `V5t11VaadinServlet` mischt sich nicht ein.

**Es gibt serverseitig kein verbleibendes Code-/Konfigurationsproblem.** Die wahrscheinlichste
Ursache ist ein **veralteter Browser-Zustand**: Die App nutzt `@Push` (persistente
Server-Session/WebSocket) und einen vorgebauten (nicht Hot-Reload-fähigen) JS-Bundle mit
Hash-Dateinamen. Ein bereits offener Browser-Tab von vor dem Fix hält weiterhin die alte JS-Version
im Speicher (ein Server-Neustart erzwingt kein Neuladen); auch ein einfaches Neuladen kann ein
gecachtes altes `index.html` liefern, das dann auf nicht mehr existierende alte Bundle-Hash-Dateien
verweist (stille Ladefehler → Custom Elements werden nie "upgraded" → Ergebnis: reiner Text, keine
Interaktivität, kein Lumo-Flex-Layout mehr, daher vertikal statt horizontal).

### Nächster Schritt (kein Code-Fix nötig)

Nutzer bitten, einen **echten Hard-Reload** durchzuführen: DevTools öffnen, "Disable cache"
aktivieren, dann Strg+Shift+R — oder den Tab komplett schließen und `/ui/system-control` neu
öffnen. Falls das Problem danach weiterhin besteht: Browser-Konsole/Netzwerk-Tab auf 404-Fehler bei
`/ui/VAADIN/build/*.js` prüfen (Bestätigung eines gecachten alten `index.html`) und diese Info
zurückmelden, bevor weitere Code-Änderungen vorgenommen werden.

## Zweiter Nachtrag: Hard-Reload (Chrome + Firefox) ohne Wirkung — echter Root Cause gefunden

Hard-Reload in beiden Browsern brachte keine Änderung (Browser-Cache damit ausgeschlossen). Neue
Beobachtung: Die Stellungsfelder sind voll funktional (Klick löst ValueChangeListener/Fachaktion
korrekt aus), aber **komplett ungestylt** — weder Lumo-Optik (Radio-Kreise, Checked-Hervorhebung)
noch die eigene `toggle-buttons`-CSS wirkt; Stapelung vertikal statt horizontal.

**Tatsächliche Root Cause (per Dekompilierung von `flow-server-25.2.8` verifiziert):** In Vaadin
25.x hat das ersatzlose Weglassen von `@Theme` NICHT den bisher angenommenen Effekt "Lumo bleibt
als Default aktiv". Laut Javadoc von `com.vaadin.flow.theme.NoTheme` (jetzt ebenfalls deprecated):
*"no theme is applied to the application unless it is explicitly requested. Omitting `@Theme` has
the same effect as using this annotation"*. D. h. unser `@StyleSheet`-Fix hat zwar den vorherigen
`@Theme("v5t11")`-Fehler behoben, aber gleichzeitig **jegliches Theme (auch Lumo) komplett
deaktiviert** — die App lief seitdem faktisch themenlos. Bestätigt durch Live-Check des laufenden
Servers: Das einzige `<link rel="stylesheet">` im gerenderten `<head>` ist unser eigenes
`themes/v5t11/styles.css` — kein `lumo/lumo.css` wird geladen. Vaadin-Komponenten wie
`vaadin-radio-group` ziehen sich ihre Lumo-Färbung/Ausrichtung über einen Injection-Mechanismus
(`LumoInjectionMixin`), der nur reagiert, wenn Lumos globales Stylesheet tatsächlich geladen ist;
ohne es bleibt nur das rohe, ungefärbte Basis-Layout (u. a. `flex-direction: column`, daher
vertikal).

Die im vorigen Fix-Kommentar getroffene Annahme *"ohne das Standard-Lumo-Theme zu ersetzen"* war
falsch — in Vaadin 25.x muss Lumo nach dem Umstieg auf `@StyleSheet` explizit mit angegeben werden.

### Korrigierter Fix

`v5t11-vaadincommon/src/main/java/de/gedoplan/v5t11/vaadincommon/ui/V5t11AppShell.java`:
- Zusätzlichen Import `com.vaadin.flow.theme.lumo.Lumo` ergänzen.
- Vor dem bestehenden `@StyleSheet("themes/v5t11/styles.css")` ein weiteres
  `@StyleSheet(Lumo.STYLESHEET)` ergänzen (Reihenfolge wichtig: Lumo zuerst laden, damit die
  eigene `toggle-buttons`-CSS in der Kaskade danach kommt und Lumo-Defaults gezielt überschreiben
  kann).
- Javadoc-Kommentar korrigieren: nicht mehr behaupten, dass das Standard-Lumo-Theme automatisch
  erhalten bleibt — stattdessen dokumentieren, dass Lumo seit Vaadin 25 explizit per
  `@StyleSheet(Lumo.STYLESHEET)` angefordert werden muss.

Kein weiterer Dateiaustausch nötig — `styles.css` (eigene `toggle-buttons`-Klasse) und
`SystemControlView.java` bleiben unverändert; sie waren nie die Fehlerursache.

### Verifikation (ergänzend)

- Recompile (`mvn -pl v5t11-vaadincommon,v5t11-status -am compile`).
- Live-`<head>` erneut prüfen (curl auf laufenden `quarkus:dev`, falls verfügbar): jetzt müssen
  zwei `<link rel="stylesheet">`-Einträge erscheinen — `lumo/lumo.css` und
  `themes/v5t11/styles.css`.
- Nutzer bittet erneut um Hard-Reload und Sichtprüfung: Restliche App weiterhin normal (Lumo
  zurück), Weichen-/Signal-Stellung jetzt horizontal, als verbundene Buttons, mit farblich
  hervorgehobenem aktivem Wert.
