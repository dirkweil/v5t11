# Fix: HEBM8-Meldung beim Schließen des Programmiermodus ohne vorheriges Programmieren

## Context

Die neue `BausteinProgrammierungView` (Vaadin) zeigt den closeProgMode-Text unified sowohl nach Klick
auf "programmieren" als auch nach Klick auf "abbrechen" (`buildParameterFieldset()`, beide Buttons
rufen `showCloseProgMode()` auf, siehe `BausteinProgrammierungView.java:272-277`). Für die meisten
Bausteintypen ist der Text generisch ("Bitte Programmiertaster am Baustein drücken!") und daher auch
bei "abbrechen" unproblematisch.

Für HEBM8 liefert `HEBM8RuntimeService.getCloseProgModeMessage()` (`HEBM8RuntimeService.java:71-74`)
aber den spezifischen Text "Programmiertaster am Baustein so lange drücken, bis die LED aufleuchtet!" –
das ist eine Handlungsanweisung, die nur nach tatsächlich erfolgtem Programmieren (`program()` wurde
aufgerufen) sinnvoll/korrekt ist. Im alten JSF überspringt HEBM8 daher bei "abbrechen" closeProgMode
komplett (eigener xhtml-Fragment-Button, der direkt zur Landing-Seite navigiert) – das ist laut Nutzer
kein Bug, sondern beabsichtigt.

Statt diesen Sonderfall (unterschiedliche Navigation je Baustein-Typ) zu replizieren, wählt der Nutzer
einen saubereren Ansatz: `ConfigurationRuntimeService` merkt sich, ob `program()` aufgerufen wurde;
`getCloseProgModeMessage()` kann dieses Flag auswerten, um den Meldungstext zu bestimmen. Nur HEBM8
nutzt das aktuell, um zwischen "programmiert" (bisheriger Text) und "nicht programmiert" (neuer Text
"Programmiertaster am Baustein bitte NICHT drücken!") zu unterscheiden.

## Fix

**`v5t11-status/src/main/java/de/gedoplan/v5t11/status/service/ConfigurationRuntimeService.java`**
- Neues Feld `protected boolean programmed;` (Default `false`).
- In `program()` (Zeile 61-64) am Ende `this.programmed = true;` setzen.

**`v5t11-status/src/main/java/de/gedoplan/v5t11/status/service/besetztmelder/hebm8/HEBM8RuntimeService.java`**
- `getCloseProgModeMessage()` (Zeile 71-74) anpassen:
  ```java
  @Override
  public String getCloseProgModeMessage() {
    return this.programmed
      ? "Programmiertaster am Baustein so lange drücken, bis die LED aufleuchtet!"
      : "Programmiertaster am Baustein bitte NICHT drücken!";
  }
  ```

Keine Änderung an `BausteinProgrammierungView.java` nötig – die unified Navigation (beide Buttons →
`showCloseProgMode()`) bleibt bestehen; nur der angezeigte Text unterscheidet sich für HEBM8 je nach
`programmed`-Flag. Kein Dependent-Scope-Problem: `ConfigurationRuntimeService`-Instanzen werden pro
Baustein-Auswahl neu erzeugt (`CDI.current().select(...).get()` in `selectBaustein()`), daher startet
`programmed` für jede Programmier-Session korrekt bei `false`.

## Verifikation

- `mvn -pl v5t11-status -am compile` — muss fehlerfrei laufen.
- Nutzer prüft in `quarkus:dev`: HEBM8-Baustein auswählen → "abbrechen" ohne vorheriges
  "programmieren" → Meldung "Programmiertaster am Baustein bitte NICHT drücken!". Dann erneut: Werte
  ändern, "programmieren" klicken → Meldung "...bis die LED aufleuchtet!". Andere Bausteintypen
  weiterhin mit generischer Meldung, unverändert.

---

# (Erledigt) Fix: SX-Bus-Feld bei "Neue Bausteine" muss enabled sein

## Context

In `BausteinProgrammierungView.edit()` wurde `this.busNrFixed = true;` unconditional gesetzt, sobald
der openProgMode-Bestätigungsschritt mit "ok" verlassen wird — unabhängig davon, ob der Baustein
bereits eine feste Adresse hat oder nicht. Dadurch war das SX-Bus-ComboBox-Feld im Geräteformular
immer disabled, auch für Bausteine aus dem Tab "Neue Bausteine" (Adresse 0, `busNrFixed` in
`selectBaustein()` korrekt auf `false` berechnet). Für neue, noch nicht am Bus vorhandene Bausteine
muss der Nutzer aber den SX-Bus auswählen können, auf dem programmiert werden soll.

## Fix (bereits angewendet)

`edit()` setzt `busNrFixed` nicht mehr selbst — der in `selectBaustein()` anhand der Baustein-Adresse
ermittelte Wert (`adr != 0`) bleibt maßgeblich und wird nicht überschrieben.

Datei: `v5t11-status/src/main/java/de/gedoplan/v5t11/status/vaadinui/BausteinProgrammierungView.java`

## Verifikation

- `mvn -pl v5t11-status -am compile` — muss fehlerfrei laufen.
- Nutzer prüft in `quarkus:dev`: Baustein über "Neue Bausteine" auswählen → im Geräteformular ist
  das SX-Bus-Feld editierbar; ein bereits konfigurierter Baustein (Tab "Vorkonfigurierte Bausteine")
  zeigt das Feld weiterhin disabled (feste Adresse).
