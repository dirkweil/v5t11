# Migration JSF (PrimeFaces) → Vaadin Flow für v5t11

## Context

Die Anwendung besteht aus vier Quarkus-Microservices (`v5t11-status`, `v5t11-parcours`, `v5t11-fahrzeuge`, `v5t11-stellwerk`) plus den Shared-Modulen `v5t11-jsfcommon` und `v5t11-util`. Drei der vier Services (`status`, `fahrzeuge`, `stellwerk`) bieten heute ein Jakarta-Faces-GUI auf Basis von PrimeFaces/MyFaces; `parcours` hat keine eigene UI. Der Nutzer möchte dieses JSF-GUI durch **Vaadin** (Vaadin Flow, serverseitiges Java-UI) ablösen, **schrittweise pro Microservice** statt in einem großen Wurf, um Risiko und Aufwand pro Release überschaubar zu halten.

Grundlage dieses Plans ist eine Bestandsaufnahme des aktuellen GUI (Views, Backing-Beans, Komponentenbibliothek, REST-Schnittstellen) sowie eine vertiefte Analyse der Menü-Föderation, des Push-Mechanismus und der Stellwerk-Canvas-Logik (siehe Detailbefunde unten).

## Zentrale Befunde aus der Analyse

- **Korrektur (nach Praxistest in Phase 0):** Die ursprüngliche Annahme "keine Servlet-Mapping-Kollision" war **falsch** und die zunächst gewählte Gegenmaßnahme (Faces-Servlet in `status` von `*.xhtml` auf `/view/*` + `/index.xhtml` umzumappen) hat den Konflikt **nicht** behoben – die Faces-Views blieben unerreichbar. Ursache: `vaadin-quarkus-extension` (genauer: die zugrundeliegende `com.vaadin:vaadin-quarkus`) registriert standardmäßig einen `QuarkusVaadinServlet`, der laut [offizieller Vaadin-Doku](https://vaadin.com/docs/latest/flow/integrations/quarkus#integrating-vaadin-with-existing-quarkus-application) **jeden Request auf die Root (`/`) der Anwendung** an sich zieht – nicht (nur) über ein normales `/*`-Servlet-Mapping, sondern so, dass er andere Routen/Pfade "shadowt". Die Doku beschreibt genau dieses Szenario (Vaadin-Dashboard unter einem Sub-Pfad in einer bestehenden Anwendung) und empfiehlt als Lösung **nicht** ein Umbiegen des anderen Servlets, sondern einen **eigenen `QuarkusVaadinServlet`**, der den Default-Servlet ersetzt:
  ```java
  @WebServlet(urlPatterns = "/ui/*", name = "V5t11VaadinServlet", asyncSupported = true)
  public class V5t11VaadinServlet extends QuarkusVaadinServlet {
  }
  ```
  Damit übernimmt **dieser** Servlet die `/ui/*`-Pfade, der Default-Vaadin-Servlet (der sonst die Root/`/*` beansprucht) entfällt, und die Faces Servlet bleibt unangetastet auf ihrem ursprünglichen `*.xhtml`-Mapping. `@Route`-Werte müssen dabei relativ zum neuen Servlet-Pfad angegeben werden (z. B. `@Route("system-status")` statt `@Route("ui/system-status")`, da der Servlet selbst schon auf `/ui/*` sitzt).
- Die fehlgeschlagene `/view/*`-Änderung an `v5t11-status/src/main/resources/META-INF/web.xml` wird zurückgenommen (Faces Servlet wieder auf `*.xhtml`).
- **Menü-Föderation ist bereits transportunabhängig von JSF:** `NavigationProducer` je Service liefert absolute URLs, verteilt über CDI-Events + SmallRye Reactive Messaging an `NavigationPresenter` der anderen Services. Nur die URL-Strings und die Rendering-Komponente müssen migriert werden – **Menüpunkte können unabhängig von der View-Migrationsreihenfolge einzeln umgestellt werden.**
- **Stellwerk-Canvas ist einfacher als der historische BootsFaces-Hinweis in `webui.adoc` suggeriert:** pro Gleisplan-Zelle ein eigenes `<canvas>`-Element, angesteuert über ein schlankes, bereits von PrimeFaces entkoppeltes JS-Zeichenprotokoll (`stellwerk.js`) über rohes WebSocket. Das ist direkt als Vaadin-Client-Modul portierbar; nur der Transport wechselt zu Vaadin-Push.
- **`fahrzeuge` und `stellwerk` haben keine eigene REST-API** – das ist unkritisch, da Vaadin Flow serverseitig im selben JVM-Prozess läuft: Views rufen die bestehenden Service-/Gateway-Klassen direkt in-process auf. REST bleibt ausschließlich für Cross-Service-Aufrufe (bestehende Gateways) nötig.
- Root-`pom.xml` verwaltet bereits alle Framework-Versionen zentral im `dependencyManagement` – der natürliche Ort für die Vaadin-BOM/Extension.

## Phase 0 — Fundament: Reactor-Integration, Shared Module, Koexistenz-Pilot

**Ziel:** Vaadin Flow + Quarkus lauffähig neben dem bestehenden JSF-Stack nachweisen, ohne bestehende Views anzufassen; `v5t11-vaadincommon` als Grundgerüst aufbauen.

1. **Build:** Vaadin-BOM (`com.vaadin:vaadin-bom`) + `com.vaadin:vaadin-quarkus-extension` im Root-`pom.xml`-`dependencyManagement` ergänzen (Version: aktuelle stabile 24.x-Patch-Version, vor Implementierung gegen Kompatibilität mit Quarkus 3.39.3 / Java 25 prüfen). Bestehende PrimeFaces/MyFaces-Einträge bleiben unverändert bis Phase 4.
2. **Neues Modul `v5t11-vaadincommon`** (Geschwistermodul zu `v5t11-jsfcommon`, gleiche Poms-Struktur), in Root-`modules` aufgenommen. Löst `v5t11-jsfcommon` Stück für Stück ab; Services hängen während ihrer Migrationsphase an **beiden** Modulen.
3. **Routing-Koexistenz (korrigiert, siehe Befunde oben):** Es gibt **keine** Konfigurationsproperty für Vaadins URL-Mapping. Stattdessen wird in `v5t11-vaadincommon` ein eigener Servlet `V5t11VaadinServlet extends QuarkusVaadinServlet`, annotiert mit `@WebServlet(urlPatterns = "/ui/*", name = "V5t11VaadinServlet", asyncSupported = true)`, angelegt. Das ersetzt automatisch den von der Extension sonst root-greifenden Default-Servlet (Vaadin-Quarkus erkennt eigene `VaadinServlet`-Subklassen mit `@WebServlet` und registriert dann keinen zusätzlichen Default-Servlet mehr). Die Faces Servlet bleibt unverändert auf `*.xhtml`. `@Route`-Werte sind relativ zu `/ui/*` anzugeben (z. B. `@Route("system-status")`). Kein Reverse-Proxy nötig. Dieses Muster (eigener `V5t11VaadinServlet`) muss in **jedem** Service übernommen werden, der Vaadin bekommt (status jetzt, fahrzeuge/stellwerk in Phase 2/3) – da der Servlet pro Quarkus-Deployment (nicht global) registriert wird, aber die Klasse selbst aus dem gemeinsamen `v5t11-vaadincommon`-Modul wiederverwendet werden kann.
4. **Shared-Komponenten-Mapping (`v5t11-jsfcommon` → `v5t11-vaadincommon`):**
   - `v5t11.xhtml`-Template → `MainLayout extends AppLayout` (gemeinsames Layout für alle 3 Services).
   - `NavigationItem` → Klasse unverändert weiterverwenden (Kandidat für Verschiebung nach `v5t11-util`, damit beide Module sie referenzieren können).
   - `NavigationPresenter` → `VaadinNavigationMenu`-Komponente: identische Föderationslogik (CDI-Events/Messaging unverändert übernehmen), Rendering in `SideNav`, Live-Refresh über `UI.access()`/`@Push` statt rohem `@ServerEndpoint`.
   - `AbstractPushService` → **Empfehlung: durch Vaadins eingebauten Server-Push ersetzen** (`@Push`, `UI.access(Runnable)`) statt Portierung; ein kleiner `VaadinPushBroadcaster`-Helper bildet die bisherige `send(message, filterPredicate)`-Semantik nach (wird für Stellwerks bereichsgefiltertes Push benötigt).
   - `SessionConfigurator` → kleines Äquivalent, niedriges Risiko.
   - `ViewExpiredExceptionHandler(Factory)` → eigene `SessionExpiredView` (kein 1:1-Äquivalent in Vaadin, da Session-Handling grundsätzlich anders funktioniert).
   - CSS/Branding (`v5t11.css`, `v5t11.png`) → Lumo-Theme-Customization (`frontend/themes/v5t11/`), Logo weiterverwenden.
5. **Pilot-View: `v5t11-status` / `SystemStatusPresenter` / `systemStatus.xhtml`.** Begründung: kleinster Presenter (45 Zeilen), reine Statusanzeige (kein Aktuator, also risikoarm für Live-Hardware-Tests), Service hat bereits eigene REST-Schicht.

**Exit-Kriterien Phase 0:**
- `v5t11-vaadincommon` baut, `v5t11-status` hängt daran.
- Alte (`/view/systemStatus.xhtml`) und neue (`/ui/system-status`) Ansicht laufen gleichzeitig in derselben Instanz/Session.
- Gemeinsames Layout/Menü zeigt mind. einen **föderierten** Menüeintrag aus einem noch rein-JSF-Service.
- Produktions-Build (inkl. Node/npm als Build-Time-Dependency) funktioniert; in CI/README dokumentieren.
- Manueller Smoke-Test (echte/simulierte Hardware) bestätigt Datengleichheit alt/neu.
- Rollback nachgewiesen: neue Route lässt sich ohne Auswirkung auf JSF deaktivieren.

## Phasen 1–3 — Migration pro Service (empfohlene Reihenfolge: status → fahrzeuge → stellwerk)

Migration **routenweise**, nicht atomar pro Service. Je View:

1. Neue `@Route("ui/<name>")`-View erstellen, ruft dieselben Service-/Domänenklassen wie der alte Presenter direkt auf (keine neue REST-Schicht nötig).
2. `NavigationProducer`-Eintrag ergänzen/umstellen.
3. Manueller Smoke-Test alt/neu nebeneinander.
4. **Cutover:** `NavigationProducer`-URL auf `/ui/<name>` umstellen (eine Zeile) – wirkt sofort föderiert in allen laufenden Services. Alte `.xhtml`+Presenter bleiben als Rollback-Pfad kurz erhalten (Soak-Periode, z. B. ein Release-Zyklus).
5. **Löschung** der alten Artefakte nach der Soak-Periode.

### Zwischenschritt — Navigationsmenü: alte/neue Einträge unterscheiden

**Ziel:** Solange JSF- und Vaadin-Views pro Service parallel existieren (Migrationsfenster), müssen beide im föderierten Menü klar unterscheidbar sein, bevor der eigentliche routenweise Cutover (siehe Ablauf oben) beginnt.

- In `NavigationProducer#getStatusNavigationItem()` (`v5t11-status/src/main/java/de/gedoplan/v5t11/status/webui/NavigationProducer.java`) erhalten die drei bestehenden (JSF-)Einträge den Namenszusatz `" (alt)"`:
  - `"System-Status (alt)"` (unverändert `view/systemStatus.xhtml`, order 810)
  - `"System-Control (alt)"` (unverändert `view/systemControl.xhtml`, order 820)
  - `"Baustein-Programmierung (alt)"` (unverändert `view/bausteinProgrammierung.xhtml`, order 830)
- Für jede bereits existierende Vaadin-View wird ein **neuer** `NavigationItem`-Eintrag ohne Zusatz ergänzt, mit URL relativ zum `V5t11VaadinServlet`-Präfix (`this.urlPrefix + "ui/<route>"`). Aktuell nur:
  - `"System-Status"` → `this.urlPrefix + "ui/system-status"`, order knapp unter dem zugehörigen Alt-Eintrag (z. B. 805), damit er direkt davor einsortiert wird.
- Bei jedem weiteren Cutover (siehe Phase 1 unten) wird analog verfahren: sobald eine neue Vaadin-View für einen bisherigen JSF-Eintrag existiert, bekommt der alte Eintrag `" (alt)"` und ein neuer Eintrag wird ergänzt (bis zum eigentlichen Löschen des Alt-Eintrags nach der Soak-Periode, siehe Cutover-Schritt 4/5).
- **Später analog** für `v5t11-fahrzeuge` und `v5t11-stellwerk`, sobald dort Vaadin-Views entstehen (Phase 2/3) – jeweils in deren eigenem `NavigationProducer`.

### Phase 1 — `v5t11-status` (14 Views, 3 Presenter)
- `systemStatus` (Pilot, Phase 0 abgeschlossen) → Cutover finalisieren.
- `systemControl` (`SystemControlPresenter`, 350 Zeilen) → `SystemControlView`; aktuiert echte Hardware, erhöhter Testaufwand.
- `bausteinProgrammierung*`-Familie (11 Geräte-Subviews + 2 Prog-Mode-Helper, gemeinsamer `BausteinProgrammierungPresenter`, 227 Zeilen) → als **ein Block** migrieren (gemeinsamer Presenter/Dispatch-Template); einmal ein wiederverwendbares "Geräte-Formular"-Pattern bauen, 11× replizieren.
- Nach Abschluss: prüfen, ob `v5t11-status` `v5t11-jsfcommon` noch benötigt (voraussichtlich nein).

### Phase 2 — `v5t11-fahrzeuge` (12 Views, 11 Presenter)
Empfohlene Reihenfolge innerhalb des Service:
1. `gleisParameter` (trivial, 36 Zeilen) als Aufwärmübung.
2. CRUD-Familie: `fahrzeugList` (Grid), `fahrzeugCreate`, `fahrzeugBasics`, `fahrzeugRemoveConfirm` (→ Vaadin `ConfirmDialog` statt eigener Route).
3. Mittlere Komplexität: `fahrzeugFunction`, `fahrzeugTraktion`, `fahrzeugPosition`.
4. Push-lastig (erst nach stabilem `VaadinPushBroadcaster`-Pattern): `fahrzeugControl`+`LokControllerPresenter` (Loktachometer/-steuerung), `fahrzeugMessung`/`gleisMessung`.
5. `fahrzeugProgram` (CV-Programmierung) zuletzt – höchstes Aktuations-Risiko, besonders sorgfältig testen.

Bestätigt die In-Process-Aufruf-Strategie im großen Maßstab: keine neuen REST-Endpunkte nötig, bestehende Gateways unverändert.

### Phase 3 — `v5t11-stellwerk` (1 View + Template, höchstes Risiko, in Teilschritten)
1. **3a Layout/Menü:** "leitstand"-Template-Konzept ins gemeinsame `MainLayout` überführen (klären: eigene Layout-Variante mit minimaler Chrome für maximale Canvas-Fläche vs. Standard-Layout – Entscheidung vor 3b nötig).
2. **3b Struktur:** Canvas-Grid (`ui:repeat` über Zeilen/Elemente) als Vaadin-Komponente nachbilden (ein `<canvas>` pro Zelle).
3. **3c Zeichnen/Push:** `stellwerk.js`-Zeichenfunktionen weitgehend unverändert übernehmen, nur Transport auf Vaadin-Push umstellen (bereichsgefiltert über `VaadinPushBroadcaster`).
4. **3d Interaktion:** `StellwerkPresenter` (387 Zeilen: Klick-Handling, Signal-/Weichen-Auswahl, Fahrstraßen-Reservierung) auf Vaadin-Formular-Komponenten portieren.
5. **3e** `PushService.java` (529 Zeilen) nach vollständiger Ablösung löschen.

## Phase 4 — Abschluss-Cleanup (erst nach vollständigem Soak aller 3 Services)

1. Modul `v5t11-jsfcommon` komplett löschen (inkl. `AbstractPushService`, `NavigationPresenter`, Templates, CSS/Logo – Logo vorher nach `v5t11-vaadincommon` kopieren).
2. PrimeFaces/MyFaces/afterwork/primeflex-Einträge aus Root-`pom.xml`-`dependencyManagement` entfernen.
3. `META-INF/web.xml` (Faces-Servlet-Konfiguration) in allen drei Services entfernen.
4. `quarkus.vaadin.url-mapping` ggf. von `/ui/*` auf `/*` umstellen; Root-Redirect anpassen.
5. `docs/10 Implementierung/webui.adoc` aktualisieren (PrimeFaces-Komponententabelle → Vaadin-Mapping; veralteten BootsFaces-Hinweis entfernen).
6. Vollständiger Reactor-Build + manueller Smoke-Test über alle drei Services.

## Offene Entscheidungen (vor bzw. während der Umsetzung zu klären)

1. ~~Exakte Vaadin-Version (24.x-Patch) und Kompatibilität mit Quarkus 3.39.3/Java 25 prüfen.~~ **Erledigt:** `25.2.7` gewählt (statt ursprünglich 24.6.7 LTS) – in 24.6.7 zog `vaadin-core` transitiv `vaadin-dev` → `vaadin-dev-server` → `license-checker` → `oshi-core:6.6.5` → `jna` nach, wobei die dortige BOM `jna:5.8.0` managt, oshi-core 6.6.5 aber `jna:5.15.0` erwartet (`ERROR ... Did not JNA classes` beim Start). In 25.2.7 ist diese ganze Kette (vaadin-dev/license-checker/oshi/jna) aus `vaadin-core` verschwunden – Fehler behoben, ohne Exclusion/Downgrade. Vom Nutzer verifiziert.
2. Lumo-Theme: eigenes Branding-Theme vs. minimale Anpassung (Farbe/Logo) des Default-Lumo.
3. Stellwerk-Layout: eigene chrome-arme Layout-Variante oder Standard-`MainLayout`.
4. Canvas-Ansatz Detail: `stellwerk.js` minimal-invasiv über `Element.executeJs` einbinden (empfohlen, geringeres Risiko) vs. vollständige Neuimplementierung als Lit-Komponente (optional, später).
5. `/ui/*`-Präfix dauerhaft beibehalten oder erst in Phase 4 global auf `/*` kollabieren (Mechanismus jetzt klar: eigener `V5t11VaadinServlet`; in Phase 4 könnte dessen `urlPatterns` auf `/*` geändert werden, sobald keine Faces Servlet mehr existiert).
11. ~~Live-Verifikation der Servlet-Koexistenz steht noch aus.~~ **Erledigt:** Vom Nutzer in seiner eigenen Dev-Umgebung bestätigt – mit `V5t11VaadinServlet` (`/ui/*`) sind sowohl die Faces-Views (`*.xhtml`, unverändertes Mapping) als auch die Vaadin-Pilot-Route (`/ui/system-status`) gleichzeitig erreichbar. Damit ist der Phase-0-Koexistenz-Mechanismus verifiziert und als Muster für Phase 2/3 (fahrzeuge/stellwerk) übernehmbar.
6. Vaadin-Lizenzbedarf für evtl. kommerzielle Komponenten (z. B. Charts für Messwert-Views) klären.
7. Push-Transport-Latenz (WebSocket vs. Long-Polling) muss vor Phase 3 den bisherigen Wert erreichen/übertreffen.
8. Rollback-Mechanismus (Soak-Periode + URL-Flip in `NavigationProducer`, keine formale Feature-Flag-Infrastruktur) – Akzeptanz bestätigen.
9. Manuelle Hardware-Smoke-Tests je Cutover sind einzuplanen (kein automatisierter Hardware-Testharness vorhanden) – Aufwand pro Phase realistisch einplanen.
10. Neue Heimat für `NavigationItem` (Vorschlag: `v5t11-util`) final festlegen.

## Kritische Dateien

- `/opt/prj/gedoplan/v5t11/pom.xml`
- `/opt/prj/gedoplan/v5t11/v5t11-status/src/main/resources/META-INF/web.xml` (Faces-Servlet-Mapping – auf `*.xhtml` zurücksetzen)
- `/opt/prj/gedoplan/v5t11/v5t11-vaadincommon/src/main/java/de/gedoplan/v5t11/vaadincommon/ui/V5t11VaadinServlet.java` (neu anzulegen)
- `/opt/prj/gedoplan/v5t11/v5t11-status/src/main/java/de/gedoplan/v5t11/status/vaadinui/SystemStatusView.java` (`@Route`-Wert anpassen)
- `/opt/prj/gedoplan/v5t11/v5t11-jsfcommon/src/main/java/de/gedoplan/v5t11/util/jsf/NavigationPresenter.java`
- `/opt/prj/gedoplan/v5t11/v5t11-jsfcommon/src/main/java/de/gedoplan/v5t11/util/jsf/AbstractPushService.java`
- `/opt/prj/gedoplan/v5t11/v5t11-stellwerk/src/main/resources/META-INF/resources/view/stellwerk.xhtml`
- `/opt/prj/gedoplan/v5t11/v5t11-stellwerk/src/main/resources/META-INF/resources/stellwerk.js`
- `/opt/prj/gedoplan/v5t11/v5t11-stellwerk/src/main/java/de/gedoplan/v5t11/leitstand/webui/PushService.java`
- `/opt/prj/gedoplan/v5t11/v5t11-status/src/main/java/de/gedoplan/v5t11/status/webui/NavigationProducer.java`
- `/opt/prj/gedoplan/v5t11/v5t11-status/src/main/java/de/gedoplan/v5t11/status/service/EventDispatcher.java`

## Verifikation

Da es sich um einen mehrmonatigen Migrationsplan (kein sofortiger Code-Change) handelt, erfolgt Verifikation pro Phase:
- Phase 0: Build + gleichzeitiger Betrieb alt/neu + Smoke-Test wie oben beschrieben. **Dev-Server werden nur nach expliziter Freigabe durch den Nutzer gestartet**, nicht automatisch – die eigentliche Live-Verifikation (echte Postgres/Kafka-Infrastruktur) erfolgt in der Dev-Umgebung des Nutzers.
- Phasen 1–3: je View manueller Smoke-Test alt vs. neu vor Cutover; nach Cutover Soak-Periode ohne Rollback-Bedarf vor Löschung der Alt-Artefakte.
- Phase 4: vollständiger Reactor-Build (`mvn clean verify`) + manueller Smoke-Test aller migrierten Views ohne JSF-Abhängigkeiten.
