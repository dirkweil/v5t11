package de.gedoplan.v5t11.status.vaadinui;

import de.gedoplan.v5t11.status.entity.Steuerung;
import de.gedoplan.v5t11.status.entity.fahrweg.Gleis;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Signal;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Weiche;
import de.gedoplan.v5t11.status.entity.fahrzeug.Fahrzeugdecoder;
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
 * Vaadin-Pendant zum inzwischen abgelösten {@code view/systemStatus.xhtml}. Pilot-View der Phase 0 der
 * JSF->Vaadin-Migration (siehe /home/dw/.claude/plans/functional-singing-canyon.md).
 * <p>
 * Ruft wie {@code SystemControlView} die Fachklasse {@link Steuerung} direkt auf. Der frühere
 * {@code SystemStatusPresenter} war nur ein reiner Delegations-Layer ohne eigene Logik und wurde entfernt.
 */
@Route(value = "system-status", layout = MainLayout.class)
@PageTitle("System-Status - v5t11")
public class SystemStatusView extends VerticalLayout {

  @Inject
  Steuerung steuerung;

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
    return new Span("Gleisspannung: " + this.steuerung.getZentrale().isGleisspannung());
  }

  private Grid<Fahrzeugdecoder> loksTab() {
    Grid<Fahrzeugdecoder> grid = new Grid<>();
    grid.addColumn(lok -> lok.getId()).setHeader("Lok");
    grid.addColumn(lok -> lok.getFahrstufe() + " (max. " + lok.getId().getSystemTyp().getMaxFahrstufe() + ")").setHeader("Fahrstufe");
    grid.addColumn(lok -> lok.isRueckwaerts()).setHeader("rückwärts");
    grid.addColumn(lok -> lok.isLicht()).setHeader("Licht");
    grid.setItems(this.steuerung.getFahrzeugdecoder());
    grid.setSizeFull();
    return grid;
  }

  private Grid<Weiche> weichenTab() {
    Grid<Weiche> grid = new Grid<>();
    grid.addColumn(weiche -> weiche.getBereich() + "/" + weiche.getName()).setHeader("Weiche");
    grid.addColumn(weiche -> weiche.getFunktionsdecoderZuordnung()).setHeader("Adresse");
    grid.addColumn(weiche -> weiche.getStellung().toString()).setHeader("Stellung");
    grid.setItems(this.steuerung.getBereiche().stream()
      .flatMap(bereich -> this.steuerung.getWeichen(bereich).stream())
      .collect(Collectors.toList()));
    grid.setSizeFull();
    return grid;
  }

  private Grid<Signal> signaleTab() {
    Grid<Signal> grid = new Grid<>();
    grid.addColumn(signal -> signal.getBereich() + "/" + signal.getName()).setHeader("Signal");
    grid.addColumn(signal -> signal.getFunktionsdecoderZuordnung()).setHeader("Adresse");
    grid.addColumn(signal -> signal.getStellung().toString()).setHeader("Stellung");
    grid.setItems(this.steuerung.getBereiche().stream()
      .flatMap(bereich -> this.steuerung.getSignale(bereich).stream())
      .collect(Collectors.toList()));
    grid.setSizeFull();
    return grid;
  }

  private Grid<Gleis> gleiseTab() {
    Grid<Gleis> grid = new Grid<>();
    grid.addColumn(gleis -> gleis.getBereich() + "/" + gleis.getName()).setHeader("Gleis");
    grid.addColumn(gleis -> gleis.getBesetztmelder().getAdresse() + "/" + gleis.getAnschluss()).setHeader("Adresse");
    grid.addColumn(gleis -> gleis.isBesetzt()).setHeader("besetzt");
    grid.setItems(this.steuerung.getBereiche().stream()
      .flatMap(bereich -> this.steuerung.getGleise(bereich).stream())
      .collect(Collectors.toList()));
    grid.setSizeFull();
    return grid;
  }
}
