# Migration Faces -> Vaadin: Ergebnis der Phase 1a

In dieser Phase wurde SystemControl zu Vaadin migriert.

## Gebaut wurde:
- VaadinChangePushBroadcaster.java: Ein Vaadin-Server-Push-Broadcaster, der die bestehende Föderationslogik (CDI-Events/Messaging) wiederverwendet, um Vaadin-Komponenten über Menü-Refresh-Benachrichtigungen zu informieren.
- SystemControlView.java: Eine neue Vaadin-View, die die bestehende SystemControl-Seite ersetzt. Sie greift direkt auf Steuerung etc. zu.

## Ergebnis:
- Die Gesamtanwendung kann fehlerfrei gebaut werden.
- Die neue View ui/system-control wird korrekt angezeigt, muss aber im Design noch überarbeitet werden, um die bestehende SystemControl-Seite möglichst gut zu ersetzen.

## Fix des Display-Problems für Weichen- und Signalstellungen:
- Geht in Vaadin mit CSS
- Viele lange Iteration mit dem Agenten, um die richtigen Stile zu finden.
- Plan siehe plan-fix-stellungsbuttons.md

## Fix des Display-Problems für diverse Checkboxen:
- Die Checkboxen konnten relativ unproblematisch durch ToggleButtons ersetzt werden.
- Plan siehe plan-fix-checkboxen.md

## Diverse weitere Display-Verbesserungen:
- Slider für Fahrstufe.
- Rahmen um Fieldsets bis zum rechten Rand.
- Buttons mittig beschriftet und ganzflächig anklickbar.
- Plan siehe plan-fix-diverse-display-verbesserungen.md