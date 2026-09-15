# Checkbox-Toggle-Buttons: gesamte Fläche klickbar machen

## Context

Bei den Checkbox-basierten Toggle-Buttons (Licht, F1–F16, Gleisspannung, besetzt, aktiv,
rückwärts, Anlagenverbindung — alle mit CSS-Klasse `toggle-buttons`) lässt sich aktuell nur exakt
der Text anklicken, nicht die umgebende gepolsterte Button-Fläche. Bei den verbundenen
Weichen-/Signal-Stellung-Buttons (`RadioButtonGroup`, ebenfalls `toggle-buttons`) tritt das
Problem nicht auf.

**Root Cause** (per Recherche im `@vaadin/checkbox`/`@vaadin/field-base`-Quellcode verifiziert):
Das aktuelle CSS stylt `vaadin-checkbox.toggle-buttons::part(label)` — das ist ein **Shadow-DOM
`<div part="label">`**, das nur als visueller Wrapper um das eigentliche `<slot name="label">`
dient. Der wirklich klickbare/aktivierende Bestandteil ist jedoch das echte Light-DOM
`<label for="...">`-Element (von `LabelledInputController` erzeugt, mit `for`-Attribut auf die
Input-ID), das eng um den Text sitzt und **keine** Polsterung erhält. Ein Klick auf die per
`::part(label)`-Padding aufgeblasene Fläche trifft daher nur das inerte Shadow-`<div>` (kein
`for`-Attribut, kein Klick-Handler) und aktiviert nichts.

Bei `vaadin-radio-button` wurde dieses Problem bereits korrekt gelöst (siehe vorhandener
CSS-Kommentar in `styles.css`): dort hat `<label>` gar keinen `part="label"`, weshalb schon jetzt
das echte Light-DOM-`<label>`-Element direkt gestylt wird (`.toggle-buttons vaadin-radio-button
label { padding: ...; }`) — genau dieses Muster fehlt bisher bei `vaadin-checkbox`.

Es gibt keinen alternativen, klickbaren Shadow-Part, der die gesamte Button-Fläche abdeckt (der
interne `.vaadin-checkbox-container` ist weder als Part exportiert noch (wegen `display: contents`)
selbst eine Klickfläche).

## Fix

`v5t11-vaadincommon/src/main/resources/META-INF/resources/themes/v5t11/styles.css`: Im
`vaadin-checkbox`-Block alle `::part(label)`-Selektoren durch das echte Light-DOM-Element `label`
ersetzen (analog zu `vaadin-radio-button`), inkl. `display: inline-block`, damit Padding/Border
eine echte Box um den Text bilden:

```css
vaadin-checkbox.toggle-buttons label {
  padding: var(--lumo-space-xs) var(--lumo-space-m);
  border: 1px solid var(--lumo-contrast-30pct);
  border-radius: var(--lumo-border-radius-m);
  cursor: pointer;
  display: inline-block;
}

vaadin-checkbox.toggle-buttons[checked] label {
  background-color: var(--lumo-primary-color);
  color: var(--lumo-primary-contrast-color);
  border-color: var(--lumo-primary-color);
}

vaadin-checkbox.toggle-buttons[disabled] label {
  opacity: var(--lumo-disabled-text-opacity, 0.4);
  cursor: default;
}
```

`vaadin-checkbox.toggle-buttons::part(checkbox) { display: none; }` bleibt unverändert (das ist
weiterhin der korrekte, echte Shadow-Part für die zu versteckende native Box).

Die `toggle-buttons-fn`-Sonderregel (einheitliche Breite/Zentrierung für die Funktions-Buttons)
ebenfalls auf das Light-DOM-Element umstellen; da `label` jetzt (wie beim Radio-Button) ein
`inline-block`-Element ist, funktioniert dafür wieder simples `text-align: center` statt
`justify-content` (das war nur wegen Lumos `display: flex` auf dem Shadow-Div nötig):
```css
vaadin-checkbox.toggle-buttons-fn label {
  min-width: 1.5rem;
  text-align: center;
}
```

Keine Java-Änderungen nötig — betrifft ausschließlich das gemeinsame Stylesheet, wirkt sich aber
auf alle Checkbox-Toggle-Buttons in `SystemControlView.java` gleichzeitig aus.

## Verifikation

- `mvn -pl v5t11-vaadincommon compile` — muss fehlerfrei laufen.
- Nutzer prüft in `quarkus:dev` unter `/ui/system-control`: Bei allen Checkbox-Toggle-Buttons
  (Licht, F1–F16, Gleisspannung, besetzt, aktiv, rückwärts, Anlagenverbindung) lässt sich die
  gesamte sichtbare Button-Fläche anklicken, nicht nur der Text. Optik unverändert (gleiche
  Polsterung/Breite/Zentrierung wie zuvor). Weichen-/Signal-Stellung (RadioButtonGroup) weiterhin
  unverändert korrekt.
