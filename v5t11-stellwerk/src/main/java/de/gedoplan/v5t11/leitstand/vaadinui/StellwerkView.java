package de.gedoplan.v5t11.leitstand.vaadinui;

import de.gedoplan.v5t11.leitstand.entity.Leitstand;
import de.gedoplan.v5t11.leitstand.entity.stellwerk.Stellwerk;
import de.gedoplan.v5t11.leitstand.entity.stellwerk.StellwerkElement;
import de.gedoplan.v5t11.vaadincommon.ui.MainLayout;

import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.NotFoundException;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jakarta.inject.Inject;

import java.util.HashMap;
import java.util.Map;

import org.jboss.logging.Logger;

/**
 * Vaadin-Pendant zum bisherigen {@code view/stellwerk.xhtml}/{@code StellwerkPresenter} (Phase 3 der
 * JSF->Vaadin-Migration, siehe {@code docs/11-Agentische-migration-faces-vaadin/Phase-3/plan.md}).
 * <p>
 * Anders als {@code StellwerkSessionHolder} (JSF, {@code @SessionScoped}, ein mutable {@code stellwerk}-Feld pro
 * HTTP-Session) hält diese View den aufgelösten {@link Stellwerk} als eigenes, view-lokales Feld – Vaadin-Routen
 * werden pro Navigation neu instanziiert, sodass mehrere gleichzeitig offene Bereiche (auch innerhalb derselben
 * Session) einander nicht überschreiben können (gleiches Prinzip wie schon bei {@code SystemControlView}, Phase 1a).
 * <p>
 * Teilschritt 3b: Canvas-Grid (unverändertes {@code stellwerk.css}-Layout, eine {@link StellwerkCanvas} pro
 * {@link StellwerkElement}) und Klick-Dispatch. Zeichnen/Push (3c) und die eigentliche Interaktionslogik
 * (Fahrstraßen-Reservierung, Weichen-/Signalstellung, 3d) folgen in den nächsten Teilschritten.
 */
@Route(value = "stellwerk", layout = MainLayout.class)
@PageTitle("Stellwerk - v5t11")
@StyleSheet("stellwerk.css")
public class StellwerkView extends VerticalLayout implements HasUrlParameter<String> {

  @Inject
  Leitstand leitstand;

  @Inject
  Logger logger;

  private Stellwerk stellwerk;

  private final Map<String, StellwerkCanvas> canvasByUiId = new HashMap<>();

  @Override
  public void setParameter(BeforeEvent event, String bereich) {
    this.stellwerk = this.leitstand.getStellwerk(bereich);
    if (this.stellwerk == null) {
      event.rerouteToError(NotFoundException.class, "Unbekannter Stellwerksbereich: " + bereich);
      return;
    }

    setSizeFull();
    getStyle().set("overflow", "auto");

    removeAll();
    add(buildGrid());
  }

  private Div buildGrid() {
    Div grid = new Div();
    grid.addClassName("stellwerk");

    this.canvasByUiId.clear();
    this.stellwerk
      .getZeilen()
      .stream()
      .flatMap(zeile -> zeile.getElemente().stream())
      .forEach(element -> {
        StellwerkCanvas canvas = new StellwerkCanvas(element);
        canvas.addClickListener(event -> onElementClicked(element));
        this.canvasByUiId.put(element.getUiId(), canvas);
        grid.add(canvas);
      });

    return grid;
  }

  private void onElementClicked(StellwerkElement element) {
    // Fahrstraßen-Reservierung, Weichen-/Signalstellung: siehe Teilschritt 3d.
    this.logger.debugf("Element geklickt: %s", element.getUiId());
  }
}
