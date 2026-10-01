package de.gedoplan.v5t11.vaadincommon.ui;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * Willkommens-/Home-View, erreichbar unter {@code /ui/home} (und per {@link RootRedirectServlet} auch
 * unter dem Context-Root "/"). Zeigt bewusst nur die Navigation (via {@link MainLayout}), keinen eigenen
 * Inhalt - Pendant zur ehemaligen leeren JSF-Welcome-Page {@code index.xhtml}.
 * <p>
 * Bewusst <strong>kein</strong> leerer Routenpfad ({@code @Route("")}): Vaadins Bootstrap berechnet die
 * relativen Hrefs für Theme-Stylesheets so, als läge jede View mindestens eine Ebene unterhalb des
 * Servlet-Mappings - bei einer View exakt auf Höhe des Mappings selbst ({@code /ui}) führt das zu einem
 * "../" zu viel, wodurch das Theme nicht geladen wird (beobachtet identisch in allen drei Services).
 */
@Route(value = "home", layout = MainLayout.class)
@PageTitle("v5t11")
public class WelcomeView extends VerticalLayout {
}
