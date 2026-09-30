package de.gedoplan.v5t11.leitstand.vaadinui;

import de.gedoplan.v5t11.leitstand.entity.Leitstand;
import de.gedoplan.v5t11.leitstand.entity.stellwerk.Stellwerk;
import de.gedoplan.v5t11.vaadincommon.ui.MainLayout;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.NotFoundException;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jakarta.inject.Inject;

/**
 * Vaadin-Pendant zum bisherigen {@code view/stellwerk.xhtml}/{@code StellwerkPresenter} (Phase 3 der
 * JSF->Vaadin-Migration, siehe {@code docs/11-Agentische-migration-faces-vaadin/Phase-3/plan.md}).
 * <p>
 * Anders als {@code StellwerkSessionHolder} (JSF, {@code @SessionScoped}, ein mutable {@code stellwerk}-Feld pro
 * HTTP-Session) hält diese View den aufgelösten {@link Stellwerk} als eigenes, view-lokales Feld – Vaadin-Routen
 * werden pro Navigation neu instanziiert, sodass mehrere gleichzeitig offene Bereiche (auch innerhalb derselben
 * Session) einander nicht überschreiben können (gleiches Prinzip wie schon bei {@code SystemControlView}, Phase 1a).
 * <p>
 * Teilschritt 3a: Route, Menü-Anbindung und Auflösung des Bereichs-Parameters. Das Canvas-Grid (3b), Zeichnen/Push
 * (3c) und die Interaktion (3d) folgen in den nächsten Teilschritten.
 */
@Route(value = "stellwerk", layout = MainLayout.class)
@PageTitle("Stellwerk - v5t11")
public class StellwerkView extends VerticalLayout implements HasUrlParameter<String> {

  @Inject
  Leitstand leitstand;

  private Stellwerk stellwerk;

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
    add(new H2("Stellwerk " + this.stellwerk.getBereich()));
  }
}
