package de.gedoplan.v5t11.vaadincommon.ui;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.NpmPackage;
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
 * <p>
 * {@code primeicons} wird als npm-Paket eingebunden, damit {@link VaadinNavigationMenu} dieselben
 * PrimeIcons-Klassen ({@code NavigationItem#getIcon()}, z. B. {@code "pi pi-box"}) wie das alte JSF-Menü
 * darstellen kann. Die im PrimeFaces-Jar enthaltene {@code primeicons.css} scheidet aus, da deren Font-URLs
 * JSF-EL-Ausdrücke sind und sich nicht im Vaadin-Frontend auflösen lassen.
 */
@Push
@StyleSheet(Lumo.STYLESHEET)
@StyleSheet("themes/v5t11/styles.css")
@NpmPackage(value = "primeicons", version = "8.0.1")
@CssImport("primeicons/primeicons.css")
public class V5t11AppShell implements AppShellConfigurator {
}
