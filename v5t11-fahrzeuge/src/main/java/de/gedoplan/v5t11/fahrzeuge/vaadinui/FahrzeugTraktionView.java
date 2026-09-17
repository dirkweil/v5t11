package de.gedoplan.v5t11.fahrzeuge.vaadinui;

import de.gedoplan.baselibs.utils.util.ResourceUtil;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.Fahrzeug;
import de.gedoplan.v5t11.fahrzeuge.persistence.FahrzeugRepository;
import de.gedoplan.v5t11.fahrzeuge.webui.FahrzeugListPresenter;
import de.gedoplan.v5t11.vaadincommon.ui.MainLayout;

import com.flowingcode.vaadin.addons.twincolgrid.TwinColGrid;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.GridMultiSelectionModel;
import com.vaadin.flow.component.grid.GridMultiSelectionModel.SelectAllCheckboxVisibility;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Vaadin-Pendant zu {@code view/fahrzeugTraktion.xhtml} + {@code FahrzeugTraktionPresenter}. Teil der
 * Phase-2-Migration von v5t11-fahrzeuge (siehe /home/dw/.claude/plans/functional-singing-canyon.md).
 * <p>
 * Das JSF-Original nutzt PrimeFaces' {@code p:pickList} (Dual-Listen-Transfer). Vaadin hat kein eingebautes
 * Äquivalent; stattdessen kommt das Add-on {@code com.flowingcode.vaadin.addons:twincolgrid} zum Einsatz, das
 * dieselbe Funktionalität abbildet (verfügbare/angehängte Fahrzeuge, Transfer per Button/Drag&amp;Drop). Anders als
 * im JSF-Original (dort waren die PickList-eigenen Sortierpfeile bewusst ausgeblendet) ist manuelles Umsortieren
 * im rechten Grid hier aktiviert ({@code setSelectionGridReorderingAllowed}), da die Zugreihenfolge über
 * {@code Fahrzeug.gezogeneFahrzeuge} (JPA {@code @OrderColumn}) ohnehin persistiert wird.
 */
@Route(value = "fahrzeug-traktion", layout = MainLayout.class)
@PageTitle("Fahrzeug-Traktion - v5t11")
public class FahrzeugTraktionView extends VerticalLayout {

  @Inject
  FahrzeugRepository fahrzeugRepository;

  @Inject
  Validator validator;

  @Inject
  FahrzeugListPresenter fahrzeugListPresenter;

  private Fahrzeug fahrzeug;
  private TwinColGrid<Fahrzeug> twinColGrid;

  @PostConstruct
  void init() {
    setSizeFull();
    getStyle().set("overflow", "auto");

    this.fahrzeug = getRefreshedFahrzeug();

    add(buildHeader(), buildTwinColGrid());
    setFlexGrow(1, this.twinColGrid);
  }

  private Fahrzeug getRefreshedFahrzeug() {
    Fahrzeug current = this.fahrzeugListPresenter.getCurrentFahrzeug();
    if (!this.fahrzeugRepository.isAttached(current)) {
      current = this.fahrzeugRepository.findById(current.getId()).get();
      this.fahrzeugListPresenter.setCurrentFahrzeug(current);
    }
    return current;
  }

  // ---------- Kopfbereich ----------

  private HorizontalLayout buildHeader() {
    Span betriebsnummer = new Span(this.fahrzeug.getBetriebsnummer());
    betriebsnummer.getStyle().set("font-size", "1.75rem").set("font-weight", "bold");

    Image image = new Image("/" + getImage(this.fahrzeug), this.fahrzeug.getBetriebsnummer());
    image.getStyle().set("max-height", "50px").set("margin", "0 1rem");

    Span decoderAdrText = new Span(String.valueOf(this.fahrzeug.getFahrzeugdecoder().getDecoderAdr()));

    HorizontalLayout left = new HorizontalLayout(betriebsnummer, image, decoderAdrText);
    left.setAlignItems(FlexComponent.Alignment.CENTER);

    Button saveButton = new Button("speichern", event -> saveFahrzeug());
    saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    Button backButton = new Button("zurück", event -> navigateToControl());

    HorizontalLayout right = new HorizontalLayout(saveButton, backButton);
    right.setAlignItems(FlexComponent.Alignment.CENTER);

    HorizontalLayout header = new HorizontalLayout(left, right);
    header.setWidthFull();
    header.setAlignItems(FlexComponent.Alignment.CENTER);
    header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

    return header;
  }

  private void navigateToControl() {
    getUI().ifPresent(ui -> ui.getPage().setLocation("/ui/fahrzeug-control"));
  }

  // ---------- Zugbildung ----------

  private TwinColGrid<Fahrzeug> buildTwinColGrid() {
    List<Fahrzeug> candidates = new ArrayList<>(this.fahrzeugRepository.findOhneZugFahrzeugSortedByBetriebsnummer());
    candidates.removeIf(f -> f.getId().equals(this.fahrzeug.getId()));
    candidates.addAll(this.fahrzeug.getGezogeneFahrzeuge());
    candidates.sort(Comparator.comparing(Fahrzeug::getBetriebsnummer));

    this.twinColGrid = new TwinColGrid<>(candidates);
    this.twinColGrid.withAvailableGridCaption("Verfügbare Fahrzeuge");
    this.twinColGrid.withSelectionGridCaption("Angehängte Fahrzeuge");
    this.twinColGrid.addColumn(Fahrzeug::getBetriebsnummer);
    this.twinColGrid.setValue(new LinkedHashSet<>(this.fahrzeug.getGezogeneFahrzeuge()));
    this.twinColGrid.setSelectionGridReorderingAllowed(true);
    this.twinColGrid.withDragAndDropSupport();
    this.twinColGrid.setSizeFull();
    this.twinColGrid.forEachGrid(grid -> {
      grid.setSizeFull();
      if (grid.getSelectionModel() instanceof GridMultiSelectionModel<Fahrzeug> multiSelectionModel) {
        multiSelectionModel.setSelectAllCheckboxVisibility(SelectAllCheckboxVisibility.HIDDEN);
      }
    });

    return this.twinColGrid;
  }

  // ---------- Speichern ----------

  private void saveFahrzeug() {
    this.fahrzeug.setGezogeneFahrzeuge(new ArrayList<>(this.twinColGrid.getValue()));

    Set<ConstraintViolation<Fahrzeug>> violations = this.validator.validate(this.fahrzeug);
    if (!violations.isEmpty()) {
      String message = violations.stream().map(cv -> cv.getPropertyPath() + " " + cv.getMessage()).reduce((a, b) -> a + "; " + b).orElse("");
      Notification.show(message).addThemeVariants(NotificationVariant.LUMO_ERROR);
      return;
    }

    this.fahrzeug = this.fahrzeugRepository.merge(this.fahrzeug);
    this.fahrzeugListPresenter.setCurrentFahrzeug(this.fahrzeug);

    navigateToControl();
  }

  private String getImage(Fahrzeug fahrzeug) {
    if (fahrzeug.getBetriebsnummer() != null) {
      String name = fahrzeug.getBetriebsnummer().replaceAll("\\s+", "_");
      while (!name.isEmpty()) {
        String resourceName = "images/" + name + ".png";
        if (ResourceUtil.getResource("META-INF/resources/" + resourceName) != null) {
          return resourceName;
        }

        name = name.substring(0, name.length() - 1);
      }
    }

    return "images/" + fahrzeug.getFahrzeugTyp().name() + ".png";
  }

}
