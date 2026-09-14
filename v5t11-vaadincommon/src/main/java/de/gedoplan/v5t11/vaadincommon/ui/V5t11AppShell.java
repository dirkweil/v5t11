package de.gedoplan.v5t11.vaadincommon.ui;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;

/**
 * Aktiviert Vaadin-Server-Push für alle v5t11-Services mit Vaadin-GUI. Ersetzt {@code AbstractPushService} aus
 * v5t11-jsfcommon für neue (Vaadin-)Views, z. B. für den Live-Refresh des Navigationsmenüs
 * ({@link VaadinNavigationMenu}).
 */
@Push
public class V5t11AppShell implements AppShellConfigurator {
}
