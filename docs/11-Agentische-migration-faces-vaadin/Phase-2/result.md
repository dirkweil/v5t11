# Migration Faces -> Vaadin: Ergebnis der Phase 2

## Migration gleis-parameter
- Die View wurde im ersten Versuch gut umgesetzt.

## Migration fahrzeug-list
- Im ersten Versuch wurden die Fahrzeuge tabellarisch mit den Attributen Icon, Betriebsnummer, Adresse etc. angezeigt, obwohl in JSF Kacheln genutzt wurden, die nur Icon und Betriebsnummer zeigten.
  - Nach Übergabe eines Screenshots im Prompt baute der Agent die Anzeige korrekt mit Kacheln auf.
- Die Filter/Neu-Buttons waren oberhalb der Tabelle angeordnet, während sie in JSF in einem separaten Anzeigepart unterhalb der Navigation angezeigt wurden.
  - Im Prompt wurde das alte Verhalten beschrieben, und der Agent baute die Buttons korrekt unterhalb der Navigation ein.

## Migration fahrzeug-control
- Die View wurde im ersten Versuch schon gut umgesetzt (nur kleinere SAnpassungen von Button-Texten etc. nötig).
- Die Aktualisierung der Anzeigedaten bei Änderungen von außen funktionierte jedoch nicht.
  - Einige Fix-Schleifen brachten keinen Erfolg. Erst eine Exception ("Kontext nicht aktiv") brachte die Erkenntnis, warum die Änderungsevents nicht korrekt verarbeitet wurden.

## Migration fahrzeug-traktion
- Der Agent kannte die Vaadin-Extension, die analog zu p:picklist funktioniert, nicht. Nach Hinweis auf die Extension konnte er die View korrekt umsetzen:
  - das TwinColGrid soll vertical den verfügbaren Platz nutzen
  - die Einträge in der linken liste ("Verfügbare Fahrzeuge") sollen nach Betriebsnummer sortiert sein
  - Die Header "Betriebsnummer" mir der Checkbox zum Selektieren aller Einträge entfallen in beiden Listen
  - die Einträge rechts ("Angehängte Fahrzeuge") können nicht in ihrer Reihenfolge verändert werden

## Migration fahrzeug-function
- Die View wurde erst mit einigen Iterationen korrekt umgesetzt. Folgende Prompts führten zu einer korrekten Umsetzung:
  fahrzeug-funktionen passt so nicht; folgende Änderungen sind nötig:
  - die Checkboxen der 16 Bits erlauben keine Eingabe; bei Click sollten sie wechseln zwischen
    - Häkchen für "an"
    - "x" für "aus"
    - leer für "undefiniert"
  - Die Werte der Checkboxen korrespondieren mit den Bits der Attribute "maske" und "wert" von FahrzeugFunktion:
    - "an": Maskenbit=1, Wertbit=1
    - "aus": Maskenbit=1, Wertbit=0
    - "undefiniert": Maskenbit=0, Wertbit=0
  - Die Eingabeelemente für Funktionsgruppe sind Comboboxen
  - Die Eingabeelemente für Funktionsbeschreibung sind Textfelder
  - Die Funktionstabelle muss kompakt, aber lesbar sein; ein Screenshot aus der alten Anwendung folgt
  
  - die Anzeige der Bit-Checkboxen soll so sein:
    - "an": wie normale Checkbox im "an"-Zustand (weißer Haken auf blauem Hintergrund)
    - "aus": wie an, jedoch "x" statt Haken
    - "undefiniert": wie normale Checkbox im "aus"-Zustand (ist bereits so implementiert)
  - jeweils nach 4 Bits soll eine kleine Lücke sein, um die Nibbles optisch zu trennen
  - die Checkboxen für "Eigenschaften" sollen vertikal näher zusammen sein
  - statt des "Neu"-Buttons soll im Header der Tabelle über den Lösch-Buttons ein Button mit einem Icon für "Hinzufügen" sein (vergleiche Image vom letzten Prompt: Button mit "Plus"-Icon)

## Migration *-messung
- Die Umsetzung gelang nicht wirklich, da der Ausgangscode deutliche Schwächen enthält (Q&D-Implementierung; NPEs können auftreten etc.)
