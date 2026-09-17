# FahrzeugControlView: Checkboxen als Toggle-Buttons (wie SystemControlView)

## Context

`SystemControlView` (`v5t11-status`, Phase 1a) stellt boolesche Felder (Aktiv, Gleisspannung, Rückwärts,
Licht, Funktionen F1-F16, ...) nicht als normale Checkboxen dar, sondern als Toggle-Buttons – über die
CSS-Klasse `toggle-buttons`, definiert im **gemeinsamen** Theme
`v5t11-vaadincommon/src/main/resources/META-INF/resources/themes/v5t11/styles.css` (`vaadin-checkbox.
toggle-buttons`, `vaadin-checkbox.toggle-buttons-fn` für gleich breite Ziffern-Buttons). Dieses
Stylesheet wird bereits global über `V5t11AppShell` (`@StyleSheet("themes/v5t11/styles.css")`,
`v5t11-vaadincommon`) geladen – **für alle** Services mit Vaadin-GUI, also auch `v5t11-fahrzeuge`. Es ist
also **keine neue CSS-Datei** nötig, nur die Anwendung der bereits vorhandenen Klasse.

Der Nutzer möchte dasselbe jetzt für `FahrzeugControlView` (`v5t11-fahrzeuge`).

## Betroffene Checkbox-Felder in `FahrzeugControlView.java`

Alle aktuellen `Checkbox`-Felder bekommen `.addClassName("toggle-buttons")`, analog zu
`SystemControlView`s `connectedField`/`gleisspannungField`/`gleisBesetztField`/`fahrzeugAktivField`/
`fahrzeugRueckwaertsField`/`fahrzeugLichtField`/Funktions-Checkboxen:

- `aktivField`: hat aktuell **keinen Label-Text** (`new Checkbox()`) – wird analog zu
  `SystemControlView.fahrzeugAktivField` auf dynamischen Text umgestellt ("inaktiv"/"aktiv" je nach
  Wert, in `addValueChangeListener` und initial in `applyFahrzeug`/`refreshAll`).
- `rueckwaertsField`: hat aktuell den statischen Label "rückwärts" – wird analog zu
  `SystemControlView.fahrzeugRueckwaertsField` auf dynamischen Text umgestellt ("vorwärts"/"rückwärts").
- `lichtCheckbox`: statischer Label "Licht" bleibt (wie `SystemControlView.fahrzeugLichtField`, dort
  ebenfalls statisch) – nur `toggle-buttons`-Klasse ergänzen.
- Funktions-Checkboxen (`funktionCheckboxes`, Label = `funktion.getBeschreibung()`): nur
  `toggle-buttons`-Klasse ergänzen (kein `toggle-buttons-fn`, da hier variable Textbeschreibungen statt
  einheitlicher Ziffern stehen – `toggle-buttons-fn` ist speziell für kurze, gleich breite
  Ziffern-Labels wie F1..F16 gedacht).
- Lok-Control-1/2-Checkboxen (`buildLokControlCheckbox`): ebenfalls boolesche Toggle-Felder, bekommen
  aus Konsistenzgründen ebenfalls `toggle-buttons`, obwohl es in `SystemControlView` kein direktes
  Gegenstück gibt.

Die dynamische Label-Aktualisierung für `aktivField`/`rueckwaertsField` erfolgt an drei Stellen: beim
initialen Aufbau (Konstruktion), im `addValueChangeListener` (bei Client-Änderung) und in
`applyFahrzeug`/`refreshAll` (bei Server-seitigem Refresh/Push) – exakt das in `SystemControlView`
etablierte Pattern (`setLabel(event.getValue() ? "aktiv" : "inaktiv")`).

## Verifikation

- `mvn -pl v5t11-fahrzeuge -am compile` — muss fehlerfrei laufen.
- Nutzer prüft in `quarkus:dev`: Aktiv/Rückwärts/Licht/Funktionen/Lok-Control 1/2 erscheinen als
  Toggle-Buttons (wie in System-Control), Klickverhalten und Live-Update-Anzeige unverändert korrekt.
