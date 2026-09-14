# Migration Faces -> Vaadin: Ergebnis der Phase 0

In dieser Phase wurde die bestehende Faces-Anwendung analysiert und die Anforderungen für die Migration zu Vaadin identifiziert.

## Gebaut wurde:
- Root-pom.xml: Vaadin-BOM + vaadin-quarkus-extension (Version 24.6.7, LTS) in dependencyManagement, neues Modul v5t11-vaadincommon in den Reactor aufgenommen.
- Neues Modul v5t11-vaadincommon: MainLayout (Vaadin AppLayout, Pendant zum v5t11.xhtml-Template), VaadinNavigationMenu (Vaadin SideNav) und V5t11AppShell (@Push, aktiviert Vaadin-Server-Push).
- Kleine additive Erweiterung in NavigationPresenter (jsfcommon): ein Listener-Hook, über den sich Vaadin-Komponenten für Menü-Refresh-Benachrichtigungen registrieren können — die bestehende Föderationslogik (CDI-Events/Messaging) wurde wiederverwendet, nicht dupliziert.
- In v5t11-status: Abhängigkeiten auf v5t11-vaadincommon/vaadin-quarkus-extension ergänzt, Pilot-View SystemStatusView unter der Route /ui/system-status erstellt — nutzt denselben SystemStatusPresenter wie die bestehende JSF-Seite.

## Ergebnis:
- Die Gesamtanwendung kann fehlerfrei gebaut werden.
- Beim Start von v5t11-status wird ein Fehler bzgl. OSHI/JNA angezeigt. Ggf. zu alte Vaadin-Version in der Quarkus-Extension.
- Die neue View ui/system-status wird korrekt angezeigt.
- Die Faces-Views werden scheinbar von Vaadin verdeckt, d.h. die JSF-Seiten werden nicht mehr angezeigt.
- Das REST API rs/* funktioniert weiterhin.
- Vaadin erzeugt Frontend-Code in src/main/frontend, der nicht versioniert werden sollte. Daher wurde die .gitignore entsprechend angepasst.