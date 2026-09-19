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
- Der Agent kannte die Vaadin-Extension, die analog zu p:picklist funktioniert, nicht. Nach Hinweis auf die Extension konnte er die View korrekt umsetzen.

## Migration *-messung
- Die Umsetzung gelang nicht wirklich, da der Ausgangscode deutliche Schwächen enthält (Q&D-Implementierung; NPEs können auftreten etc.)
