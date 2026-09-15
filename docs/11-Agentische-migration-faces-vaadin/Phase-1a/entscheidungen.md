# Migration Faces -> Vaadin: Entwurfsentscheidungen Phase 1a

Zusammenfassung der wichtigsten Entwurfsentscheidungen bei der Vaadin-Migration von
`SystemControlView` (Details siehe `plan-fix-stellungsbuttons.md`, `plan-fix-checkboxen.md`,
`plan-fix-diverse-display-verbesserungen.md`).

## Toggle-Button-Optik (CSS-Strategie)

- Statt eines Vaadin-`@Theme` (in Vaadin 25 deprecated, deaktiviert stillschweigend Lumo komplett)
  wird das gemeinsame Stylesheet per `@StyleSheet(Lumo.STYLESHEET)` + `@StyleSheet("themes/v5t11/styles.css")`
  auf `V5t11AppShell` eingebunden — Lumo bleibt aktiv, unser CSS überschreibt gezielt.
- Für `RadioButtonGroup` (Weichen-/Signal-Stellung) wird das Light-DOM-`<label>` direkt gestylt,
  nicht `::part(label)` — dort existiert kein `part="label"`, nur `slot="label"`.
- Für `vaadin-checkbox` wurde zunächst `::part(label)` verwendet (existiert dort tatsächlich),
  später aber korrigiert: dieser Shadow-Part ist nur ein inerter Wrapper ohne Klick-Semantik.
  Endgültig wird auch hier das echte Light-DOM-`<label>` (mit `for`-Attribut) gestylt — konsistent
  mit der Radio-Button-Lösung und mit voller Klickfläche.
- Die Klasse `toggle-buttons` wird direkt auf jede einzelne `Checkbox` gesetzt (nicht auf einen
  Container), weil eine Checkbox selbst das Toggle-Element ist — anders als bei `RadioButtonGroup`,
  wo die Klasse auf der Gruppe sitzt.

## Fahrstufen-Slider

- `IntegerField` mit +/- ersetzt durch Vaadins `IntegerSlider` (transitiv bereits über
  `vaadin-quarkus-extension` verfügbar, keine neue Abhängigkeit).
- Die eingebaute „Wert immer sichtbar“-Sprechblase wurde verworfen (fest positioniertes Overlay
  ohne Rücksicht auf andere Elemente, keine offizielle Positionierungs-API) — stattdessen ein
  eigenes `Span`-Element mit fester Breite, links vom Slider, vertikal zentriert.

## Layout

- Alle `FieldSet`s bekommen `setWidthFull()`; zusätzlich global
  `fieldset { box-sizing: border-box; margin: 0; }`, da native `<fieldset>`-Elemente sonst per
  Browser-Default über die verfügbare Breite hinausragen.
- Alle mehrelementigen Formularzeilen bekommen explizit `setAlignItems(CENTER)`, statt sich auf
  Lumo-Defaults zu verlassen.

## Umbenennung „Lok“ -> „Fahrzeug“

- Bewusst auf `SystemControlView.java` beschränkt (Rückfrage per Nutzerdialog) — JSF-Code,
  Domänenklassen (`Lokcontroller`, `SxLokControl`) und das separate `v5t11-fahrzeuge`-Modul (eigene
  „Fahrzeug“-Entity, Kollisionsgefahr) bleiben unverändert.

## Git-Hygiene

- Vaadin-Frontend-Build-Dateien (`package.json`, `tsconfig.json`, `types.d.ts`, `vite.config.ts`,
  `src/main/bundles/`) eingecheckt gemäß Vaadins eigener Konvention; `node_modules/` und
  `vite.generated.ts` ignoriert.
