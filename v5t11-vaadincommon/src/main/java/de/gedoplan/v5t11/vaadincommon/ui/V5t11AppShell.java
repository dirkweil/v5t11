package de.gedoplan.v5t11.vaadincommon.ui;

import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.theme.lumo.Lumo;

/**
 * Aktiviert Vaadin-Server-Push für alle v5t11-Services mit Vaadin-GUI. Ersetzt {@code AbstractPushService} aus
 * v5t11-jsfcommon für neue (Vaadin-)Views, z. B. für den Live-Refresh des Navigationsmenüs
 * ({@link VaadinNavigationMenu}).
 * <p>
 * Seit Vaadin 25 wird KEIN Theme mehr automatisch angewendet, wenn {@code @Theme} fehlt (Javadoc von
 * {@code @NoTheme}: "Omitting Theme has the same effect as using this annotation") — Lumo muss daher explizit per
 * {@code @StyleSheet(Lumo.STYLESHEET)} angefordert werden. Das gemeinsame v5t11-Stylesheet (siehe
 * {@code META-INF/resources/themes/v5t11/styles.css}) wird danach geladen, damit es Lumo-Defaults gezielt
 * überschreiben kann.
 */
@Push
@StyleSheet(Lumo.STYLESHEET)
@StyleSheet("themes/v5t11/styles.css")
public class V5t11AppShell implements AppShellConfigurator {
}
