---
name: faces-vaadin-servlet
description: Use when migrating a Quarkus app from Jakarta Faces to Vaadin, or adding Vaadin (quarkus vaadin-quarkus-extension) to a service that still has a FacesServlet. Ensures a custom VaadinServlet under a URL prefix is created right away, so Vaadin does not shadow the Faces *.xhtml mapping.
---

# Faces und Vaadin parallel betreiben: eigenes VaadinServlet

## Problem
Die `vaadin-quarkus-extension` registriert `QuarkusVaadinServlet` fest auf `/*`.
Ein Pfadpräfix-Mapping schlägt nach Servlet-Spec das Extension-Mapping `*.xhtml` des
`FacesServlet` (`META-INF/web.xml`). Folge: Sobald Vaadin im Classpath ist, liefern alle
Faces-Seiten 500/404 oder landen bei Vaadin.

## Regel
Beim **ersten** Einbinden von Vaadin in einen Service, der noch Faces nutzt, sofort ein eigenes
Servlet anlegen - nicht erst nach dem Fehler. Die Extension (`VaadinQuarkusProcessor.registerUserServlets`)
registriert ihr Default-Mapping `/*` nur, wenn keine eigene `@WebServlet`-Klasse existiert, die
`VaadinServlet` erweitert.

```java
@WebServlet(urlPatterns = "/ui/*", asyncSupported = true, loadOnStartup = 1)
public class <Projekt>VaadinServlet extends QuarkusVaadinServlet {
}
```

- Bei einem Multimodul-Projekt kann das Servlet in einem gemeinsamen Vaadin-Modul (z.B.: `<project>-vaadincommon`) liegen.
- Die Klasse braucht `@WebServlet`, sonst wird sie von der Extension ignoriert (Warnung im Log).

## Folgen für den Rest der Migration
1. **URLs** neuer Views: `<web-url>/ui/<route>` (nicht `<web-url>/<route>`).
3. Das Vaadin-Modul muss Servlet-API (`jakarta.servlet`) im Classpath haben (kommt über die Extension).
4. Nach dem Hinzufügen der POM-Abhängigkeit/der Servlet-Klasse den Dev Mode des Services **neu starten**
   (Live-Reload findet ggf. neue Jars aus anderen Modulen nicht: `ClassNotFoundException`).
5. Nicht Faces auf ein Präfix-Mapping umstellen - das würde alle bestehenden URLs ändern.

## Verifikation
```
curl -s -o /dev/null -w "%{http_code}\n" http://localhost:<port>/view/<seite>.xhtml   # Faces: 200
curl -s -o /dev/null -w "%{http_code}\n" http://localhost:<port>/ui/<route>            # Vaadin: 200
```
Beide müssen 200 liefern. Bei 500: Dev-Mode-Fehlerseite lesen (`Caused by`).
