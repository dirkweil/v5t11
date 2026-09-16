# Migration Faces -> Vaadin: Ergebnis der Phase 1b

In dieser Phase wurde BausteinProgrammierung zu Vaadin migriert.

## Gebaut wurde:
- BausteinProgrammierungView.java: Eine neue Vaadin-View, die die bestehende BausteinProgrammierung-Seite ersetzt. Sie greift direkt auf Steuerung etc. zu.
- CurrentBausteinHolder.java: Aus dem JSF-Presenter herausfaktorisierter Producer für den aktuellen Baustein. Wurde nötig, damit JSF und Vaadin gleichzeitig auf den aktuellen Baustein zugreifen können.

## Ergebnis:
- Die Gesamtanwendung kann fehlerfrei gebaut werden.
- Die neue View ui/baustein-programmierung wird korrekt angezeigt.
- Für den Baustein HEBM8 gibt es im JSF eine Sonderbehandlung:
  - Der Baustein hat keinen normalen Programmiermodus. Zu Beginn muss daher keine Taste gedrückt werden, und am Ende darf der Programmikertaster nur dann betätigt werden, wenn tatsächlich etwas programmiert wurde.
  - Im JSF wurde daher die Methode closeProgMode bei "abbrechen" nicht aufgerufen.
  - Der Agent hat diese Situation erkannt, aber als Nachlässigkeit/Fehler der JSF-Implementierung eingeschätzt. Daher wird die Methode closeProgMode nun in Vaadin trotzdem aufgerufen, was bei "abbrechen" zu der falschen Anzeige führt, dass der Programmiertaster gedrückt werden muss.
  - Eine saubere Lösung wäre vielleicht, dass closeProgMode einen Parameter bekommt, der angibt, ob die Methode aufgerufen werden soll oder nicht.
- Der Baustein SD8 hat sehr viele Konfigurationswerte, die zudem recht zeitintensiv gelesen werden müssen.
  - Die JSF-Implementierung hat die Werte der einzelnen Servos lazy geladen, also erst dann, wenn sie angezeigt werden.
  - Die Vaadin Implementierung lädt die Werte der Servos hingegen sofort beim Anzeigen der View. Das führt zu einer deutlich längeren Wartezeit, bis die View angezeigt wird.

## Fix der Behandlung von HEBM8
- Der Agent wurde angewiesen, ein neues Feld für "Baustein wurde programmiert" einzuführen, das in getCloseProgModeMessage genutzt werden kann, um unterschiedliche Texte zu erzeugen.

## Fix der Behandlung von SD8
- Der Agent wurde angewiesen, die Werte der Servos lazy zu laden, also erst dann, wenn sie angezeigt werden. Das führt zu einer deutlich kürzeren Wartezeit, bis die View angezeigt wird.