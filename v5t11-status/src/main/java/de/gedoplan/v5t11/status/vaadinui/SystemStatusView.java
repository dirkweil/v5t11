package de.gedoplan.v5t11.status.vaadinui;

import de.gedoplan.v5t11.status.entity.fahrweg.Gleis;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Signal;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Weiche;
import de.gedoplan.v5t11.status.entity.fahrzeug.Fahrzeugdecoder;
import de.gedoplan.v5t11.status.webui.SystemStatusPresenter;
import de.gedoplan.v5t11.vaadincommon.ui.MainLayout;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;

import java.util.stream.Collectors;

/**
 * Vaadin-Pendant zu {@code view/systemStatus.xhtml} / {@code SystemStatusPresenter}. Pilot-View der Phase 0 der
 * JSF->Vaadin-Migration (siehe /home/dw/.claude/plans/functional-singing-canyon.md).
 * <p>
 * Nutzt bewusst denselben {@link SystemStatusPresenter} wie die bestehende JSF-Seite, statt die (dünne)
 * Anzeigelogik zu duplizieren - der Presenter selbst kennt keine JSF-Spezifika.
 */
@Route(value = "system-status", layout = MainLayout.class)
@PageTitle("System-Status - v5t11")
public class SystemStatusView extends VerticalLayout {

  @Inject
  SystemStatusPresenter systemStatusPresenter;

  @PostConstruct
  void init() {
    setSizeFull();

    TabSheet tabSheet = new TabSheet();
    tabSheet.setSizeFull();

    tabSheet.add("Zentrale", zentraleTab());
    tabSheet.add("Loks", loksTab());
    tabSheet.add("Weichen", weichenTab());
    tabSheet.add("Signale", signaleTab());
    tabSheet.add("Gleise", gleiseTab());

    add(tabSheet);
  }

  private Span zentraleTab() {
    return new Span("Gleisspannung: " + this.systemStatusPresenter.getZentrale().isGleisspannung());
  }

  private Grid<Fahrzeugdecoder> loksTab() {
    Grid<Fahrzeugdecoder> grid = new Grid<>();
    grid.addColumn(lok -> lok.getId()).setHeader("Lok");
    grid.addColumn(lok -> lok.getFahrstufe() + " (max. " + lok.getId().getSystemTyp().getMaxFahrstufe() + ")").setHeader("Fahrstufe");
    grid.addColumn(lok -> lok.isRueckwaerts()).setHeader("rückwärts");
    grid.addColumn(lok -> lok.isLicht()).setHeader("Licht");
    grid.setItems(this.systemStatusPresenter.getLoks());
    return grid;
  }

  private Grid<Weiche> weichenTab() {
    Grid<Weiche> grid = new Grid<>();
    grid.addColumn(weiche -> weiche.getBereich() + "/" + weiche.getName()).setHeader("Weiche");
    grid.addColumn(weiche -> weiche.getFunktionsdecoderZuordnung()).setHeader("Adresse");
    grid.addColumn(weiche -> weiche.getStellung().toString()).setHeader("Stellung");
    grid.setItems(this.systemStatusPresenter.getBereiche().stream()
      .flatMap(bereich -> this.systemStatusPresenter.getWeichen(bereich).stream())
      .collect(Collectors.toList()));
    return grid;
  }

  private Grid<Signal> signaleTab() {
    Grid<Signal> grid = new Grid<>();
    grid.addColumn(signal -> signal.getBereich() + "/" + signal.getName()).setHeader("Signal");
    grid.addColumn(signal -> signal.getFunktionsdecoderZuordnung()).setHeader("Adresse");
    grid.addColumn(signal -> signal.getStellung().toString()).setHeader("Stellung");
    grid.setItems(this.systemStatusPresenter.getBereiche().stream()
      .flatMap(bereich -> this.systemStatusPresenter.getSignale(bereich).stream())
      .collect(Collectors.toList()));
    return grid;
  }

  private Grid<Gleis> gleiseTab() {
    Grid<Gleis> grid = new Grid<>();
    grid.addColumn(gleis -> gleis.getBereich() + "/" + gleis.getName()).setHeader("Gleis");
    grid.addColumn(gleis -> gleis.getBesetztmelder().getAdresse() + "/" + gleis.getAnschluss()).setHeader("Adresse");
    grid.addColumn(gleis -> gleis.isBesetzt()).setHeader("besetzt");
    grid.setItems(this.systemStatusPresenter.getBereiche().stream()
      .flatMap(bereich -> this.systemStatusPresenter.getGleise(bereich).stream())
      .collect(Collectors.toList()));
    return grid;
  }
}
