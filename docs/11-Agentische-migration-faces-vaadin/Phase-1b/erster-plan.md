# Fix: SX-Bus-Feld bei "Neue Bausteine" muss enabled sein

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
