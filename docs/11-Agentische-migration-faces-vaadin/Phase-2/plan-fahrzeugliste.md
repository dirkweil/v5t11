# Bugfix: ContextNotActiveException bei Push-Update in FahrzeugControlView

## Context

Der Log zeigt die tatsächliche Ursache dafür, dass Live-Updates nicht ankommen:

```
jakarta.enterprise.context.ContextNotActiveException: SessionScoped context was not active when
trying to obtain a bean instance for a client proxy of CLASS bean
[class=de.gedoplan.v5t11.fahrzeuge.webui.FahrzeugListPresenter, ...]
```

Stacktrace: `VaadinChangePushBroadcaster.onChanged` (läuft auf einem `@ObservesAsync`-Executor-Thread,
kein HTTP-Request) → `FahrzeugControlView.onChanged` → `ui.access(...)` → `refreshAll()` →
`this.fahrzeugListPresenter.setCurrentFahrzeug(...)`. `FahrzeugListPresenter` ist `@SessionScoped`
(CDI). Quarkus aktiviert den `@SessionScoped`-Kontext nur innerhalb eines echten HTTP-Requests mit
Session (Undertow-Session-Filter) – **nicht** in einem `UI.access(...)`-Callback, der von einem
Hintergrund-Thread (CDI-Async-Event) aus angestoßen wurde. Der Zugriff auf den `@SessionScoped`-Bean
wirft daher eine Exception, die den **gesamten** `refreshAll()`-Aufruf abbricht, bevor irgendein
UI-Feld aktualisiert wird – daher "kommt nichts an".

## Zur Nutzerfrage: `@Observes(during = TransactionPhase.AFTER_SUCCESS)` statt `@ObservesAsync`?

Guter Gedanke, löst aber ein **anderes** Problem: Er würde die (bereits separat diskutierte) Race
Condition schließen, bei der das Event noch vor dem Commit der `StatusUpdater`-Transaktion gefeuert
wird (`EventFirer.fire()` ruft `es.fire(event)` synchron – das ist die Variante, auf die sich
`@Observes(during=...)` bezieht – **und zusätzlich** `es.fireAsync(event)`, worauf `@ObservesAsync`
reagiert). Ein transaktionaler Observer würde erst nach erfolgreichem Commit benachrichtigt, garantiert
aktuelle Daten.

**Er behebt aber nicht den `ContextNotActiveException`-Fehler:** Auch ein `@Observes(during=
AFTER_SUCCESS)`-Observer läuft auf dem Thread, der die Transaktion abschließt (i. d. R. der
Kafka-Consumer-/Vert.x-Worker-Thread aus `IncomingHandler`) – ebenfalls kein HTTP-Request mit aktiver
Session. Der `@SessionScoped`-Zugriff würde exakt genauso scheitern.

## Fix (zwei unabhängige Verbesserungen)

1. **Den eigentlichen Crash beheben:** `FahrzeugControlView.refreshAll()` ruft aktuell
   `this.fahrzeugListPresenter.setCurrentFahrzeug(this.fahrzeug)` auf – das ist der einzige
   `@SessionScoped`-Zugriff im Push-Pfad. Diese Zeile wird aus `refreshAll()` entfernt. Die
   Session-Bridge (`FahrzeugListPresenter.currentFahrzeug`, gebraucht für die Cross-Links zu den
   weiterhin-JSF-Views) wird stattdessen **direkt vor jeder Navigation** gesetzt – an der Stelle ist
   immer ein echter Klick/Request aktiv, der Session-Kontext also verfügbar:
   - `navigateToJsf(String path)`: setzt `fahrzeugListPresenter.setCurrentFahrzeug(this.fahrzeug)` vor
     `ui.getPage().setLocation(path)`.
   - `saveFahrzeug()` behält seinen bestehenden, expliziten `setCurrentFahrzeug(...)`-Aufruf (läuft
     synchron im Dialog-Save-Klick, also unverändert unproblematisch).
   - `getRefreshedFahrzeug()` (im `@PostConstruct`, ebenfalls echter Request) bleibt unverändert.

2. **Die Race Condition schließen (Nutzervorschlag übernehmen):**
   `VaadinChangePushBroadcaster.onChanged` wird von `@ObservesAsync @Changed Object changed` auf
   `@Observes(during = TransactionPhase.AFTER_SUCCESS) @Changed Object changed` umgestellt. Das
   garantiert, dass die Benachrichtigung erst nach dem Commit der `StatusUpdater`-Transaktion kommt –
   das im vorherigen (zurückgenommenen) Fix-Versuch behandelte Problem wird damit sauber auf
   CDI-Ebene gelöst, statt es in der View wegzuarbeiten. Kein Einfluss auf `PushService`
   (nutzt weiterhin unverändert `@ObservesAsync` für den alten JSF-Websocket-Push).

## Verifikation

- `mvn -pl v5t11-fahrzeuge -am compile` — muss fehlerfrei laufen.
- Nutzer prüft in `quarkus:dev`: externe Änderung eines Fahrzeugdecoders erscheint jetzt ohne
  manuellen Reload in `FahrzeugControlView`; kein `ContextNotActiveException` mehr im Log; Cross-Links
  (Position/Traktion/Funktionen/Programmierung/Messungen) übergeben weiterhin das korrekte, aktuelle
  Fahrzeug an die JSF-Views.
