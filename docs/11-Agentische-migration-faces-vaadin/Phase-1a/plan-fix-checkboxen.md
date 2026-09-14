# Anlagenverbindung (`connectedField`) als Toggle-Button mit dynamischem Text

## Context

Nach der Umstellung der übrigen Checkboxen (Gleisspannung, besetzt, aktiv, rückwärts sowie
Lok-Funktionen) auf Toggle-Buttons mit Zustandstext (`SystemControlView.java`, CSS-Klasse
`toggle-buttons` in `v5t11-vaadincommon/.../themes/v5t11/styles.css`) fehlt noch `connectedField`
("Anlagenverbindung"). Dieses Feld wurde beim vorigen Durchgang bewusst ausgenommen, weil sein
Text — anders als bei den anderen vier Feldern — nicht aus einem festen Wortpaar besteht, sondern
im eingeschalteten Zustand den tatsächlichen Port-Namen anzeigt.

**JSF-Original** (`systemControl.xhtml`, `p:selectBooleanButton`):
- `onLabel="#{systemControlPresenter.zentralePort}"` → dynamisch
- `offLabel="nicht verbunden"` → fest
- `disabled="#{systemControlPresenter.zentraleEchtbetrieb}"`

**`getZentralePort()`** (alter Presenter, `SystemControlPresenter.java:162-164`):
```java
public String getZentralePort() {
  return isZentraleEchtbetrieb() ? this.steuerung.getZentrale().getPortName() : "Dummyport";
}
```
`isEchtbetrieb()` (`Zentrale.java:265-267`): `true`, wenn ein echter Port konfiguriert ist (nicht
`null`/`"none"`), sonst reiner Dummy-/Entwicklungsbetrieb.

**Aktueller Vaadin-Stand** (`SystemControlView.java`, `buildAllgemeinSection()` /
`refreshZentrale()`): Der Text wird bereits dynamisch berechnet, aber **unabhängig vom
Checked-Zustand immer gleich** gesetzt:
```java
this.connectedField.setLabel("Verbunden (" + (echtbetrieb ? zentrale.getPortName() : "Dummyport") + ")");
```
Das entspricht nicht dem Original — es fehlt die Fallunterscheidung „verbunden → Portname“ vs.
„nicht verbunden → fester Text“.

`refreshZentrale()` wird bei jeder relevanten Zustandsänderung aufgerufen (initial in
`buildAllgemeinSection()`, aus `setZentraleConnected(boolean)` nach Client-Klick im Dummy-Betrieb,
und über den Push-Listener `onChanged(Zentrale)`), berechnet `echtbetrieb`/`connected` bereits neu
und ist damit die richtige und einzige Stelle, an der der Label-Text aktualisiert werden muss —
anders als bei den vorherigen vier Feldern braucht es hier **keine** Label-Logik zusätzlich im
`ValueChangeListener`, da jeder Client-Klick (nur im Dummy-Betrieb möglich, sonst ist das Feld
disabled) über `setZentraleConnected()` ohnehin `refreshZentrale()` nachzieht.

## Fix

Alles in `v5t11-status/src/main/java/de/gedoplan/v5t11/status/vaadinui/SystemControlView.java`
(`buildAllgemeinSection()` / `refreshZentrale()`):

1. Konstruktor-Text auf den Default-/Off-Zustand setzen und CSS-Klasse ergänzen:
   ```java
   this.connectedField = new Checkbox("nicht verbunden");
   this.connectedField.addClassName("toggle-buttons");
   ```
   (Listener selbst bleibt unverändert — reine Domänen-Logik, kein Label-Update nötig.)

2. In `refreshZentrale()` die Label-Zeile durch eine Fallunterscheidung nach Connected-Zustand
   ersetzen:
   ```java
   private void refreshZentrale() {
     Zentrale zentrale = this.steuerung.getZentrale();
     boolean echtbetrieb = zentrale.isEchtbetrieb();
     boolean connected = zentrale.isConnected();
     this.connectedField.setValue(connected);
     this.connectedField.setEnabled(!echtbetrieb);
     this.connectedField.setLabel(connected ? (echtbetrieb ? zentrale.getPortName() : "Dummyport") : "nicht verbunden");
     this.gleisspannungField.setValue(zentrale.isGleisspannung());
   }
   ```

Keine Änderung an `setZentraleConnected(boolean)` oder dem `ValueChangeListener` von
`connectedField` nötig.

## Verifikation

- `mvn -pl v5t11-vaadincommon,v5t11-status -am compile` — muss fehlerfrei laufen.
- Nutzer prüft in `quarkus:dev` unter `/ui/system-control`:
  - Im Dummy-Betrieb (kein echter Port konfiguriert): Button zeigt „nicht verbunden“ (nicht
    hervorgehoben), nach Klick „Dummyport“ (hervorgehoben, Lumo-Primärfarbe); erneuter Klick
    schaltet zurück auf „nicht verbunden“.
  - Im Echtbetrieb (falls testbar): Button ist deaktiviert/gedimmt und zeigt je nach echtem
    Connected-Zustand entweder den konfigurierten Portnamen oder „nicht verbunden“.
  - Optik (abgerundeter Button statt Kästchen) konsistent mit den bereits umgestellten restlichen
    Feldern.
