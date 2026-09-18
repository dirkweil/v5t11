package de.gedoplan.v5t11.fahrzeuge.vaadinui;

import de.gedoplan.baselibs.utils.util.ResourceUtil;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrweg.Gleis;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.Fahrzeug;
import de.gedoplan.v5t11.fahrzeuge.persistence.FahrzeugRepository;
import de.gedoplan.v5t11.fahrzeuge.service.GleisMessService;
import de.gedoplan.v5t11.fahrzeuge.webui.FahrzeugListPresenter;
import de.gedoplan.v5t11.vaadincommon.ui.MainLayout;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;

import java.util.Locale;

/**
 * Vaadin-Pendant zu {@code view/gleisMessung.xhtml} + {@code GleisMessungPresenter}. Teil der Phase-2-Migration von
 * v5t11-fahrzeuge (siehe /home/dw/.claude/plans/functional-singing-canyon.md).
 * <p>
 * {@link GleisMessService} benachrichtigt Interessenten nicht über {@link
 * de.gedoplan.v5t11.fahrzeuge.webui.VaadinChangePushBroadcaster}, sondern über einen eigenen Single-Slot-Observer
 * ({@code attachObserver(Runnable)}/{@code detachObserver()}), an den sich diese View analog zu {@code
 * FahrzeugControlView}s {@code onAttach}/{@code onDetach}-Pattern direkt anmeldet. Der Observer liefert kein
 * Änderungsobjekt, daher liest {@link #refreshAll()} alle Anzeigewerte frisch aus den Service-Gettern.
 * {@code GleisMessService} selbst bleibt unverändert (Rollback-Pfad ist die alte {@code gleisMessung.xhtml}).
 */
@Route(value = "gleis-messung", layout = MainLayout.class)
@PageTitle("Gleislängen - v5t11")
public class GleisMessungView extends VerticalLayout {

  @Inject
  FahrzeugRepository fahrzeugRepository;

  @Inject
  FahrzeugListPresenter fahrzeugListPresenter;

  @Inject
  GleisMessService gleisMessService;

  private Fahrzeug fahrzeug;

  private Button messenButton;
  private Button beendenButton;
  private Span statusField;
  private Grid<Gleis> grid;

  private Runnable selectedAktion;
  private String selectedAktionsBeschreibung;

  @PostConstruct
  void init() {
    setSizeFull();
    getStyle().set("overflow", "auto");

    this.fahrzeug = getRefreshedFahrzeug();

    add(buildHeader(), buildActionsBar(), buildGrid());
  }

  private Fahrzeug getRefreshedFahrzeug() {
    Fahrzeug current = this.fahrzeugListPresenter.getCurrentFahrzeug();
    if (!this.fahrzeugRepository.isAttached(current)) {
      current = this.fahrzeugRepository.findById(current.getId()).get();
      this.fahrzeugListPresenter.setCurrentFahrzeug(current);
    }
    return current;
  }

  @Override
  protected void onAttach(AttachEvent attachEvent) {
    super.onAttach(attachEvent);
    this.gleisMessService.attachObserver(this::onServiceChanged);
    refreshAll();
  }

  @Override
  protected void onDetach(DetachEvent detachEvent) {
    this.gleisMessService.detachObserver();
    super.onDetach(detachEvent);
  }

  private void onServiceChanged() {
    getUI().ifPresent(ui -> ui.access(this::refreshAll));
  }

  // ---------- Kopfbereich ----------

  private Component buildHeader() {
    Span betriebsnummer = new Span(this.fahrzeug.getBetriebsnummer());
    betriebsnummer.getStyle().set("font-size", "1.75rem").set("font-weight", "bold");

    Image image = new Image("/" + getImage(this.fahrzeug), this.fahrzeug.getBetriebsnummer());
    image.getStyle().set("max-height", "50px").set("margin", "0 1rem");

    Span decoderAdrText = new Span(String.valueOf(this.fahrzeug.getFahrzeugdecoder().getDecoderAdr()));

    HorizontalLayout left = new HorizontalLayout(betriebsnummer, image, decoderAdrText);
    left.setAlignItems(FlexComponent.Alignment.CENTER);

    Button saveButton = new Button("speichern", event -> saveAndReturn());
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

  private void saveAndReturn() {
    this.gleisMessService.save();
    navigateToControl();
  }

  private void navigateToControl() {
    getUI().ifPresent(ui -> ui.getPage().setLocation("/ui/fahrzeug-control"));
  }

  // ---------- Aktionsleiste ----------

  private Component buildActionsBar() {
    this.messenButton = new Button("Gleislängen messen", event -> selectGleislaengenMessen());
    this.beendenButton = new Button("Messung beenden", event -> messungAbbrechen());
    this.statusField = new Span();
    this.statusField.getStyle().set("font-weight", "bold");

    HorizontalLayout bar = new HorizontalLayout(this.messenButton, this.beendenButton, this.statusField);
    bar.setAlignItems(FlexComponent.Alignment.CENTER);
    return bar;
  }

  private void selectGleislaengenMessen() {
    setSelectedAktion(
      "Gleislängen messen",
      "Fahrzeug mit gleichbleibender mittlerer Geschwindigkeit zu messende Gleise durchfahren lassen.",
      () -> this.gleisMessService.startLaengenMessung(this.fahrzeug));
  }

  private void messungAbbrechen() {
    this.gleisMessService.abbrechen();
    refreshAll();
  }

  private void setSelectedAktion(String beschreibung, String anleitung, Runnable aktion) {
    this.selectedAktion = aktion;
    this.selectedAktionsBeschreibung = beschreibung;

    ConfirmDialog dialog = new ConfirmDialog();
    dialog.setHeader(beschreibung);
    dialog.setText(anleitung);
    dialog.setCancelable(true);
    dialog.setCancelText("abbrechen");
    dialog.setConfirmText("ok");
    dialog.addConfirmListener(event -> execSelectedAktion());
    dialog.open();
  }

  private void execSelectedAktion() {
    try {
      this.selectedAktion.run();
    } catch (Exception e) {
      String msg = String.format("Kann Aktion \"%s\" nicht durchführen: %s", this.selectedAktionsBeschreibung, e);
      Notification.show(msg).addThemeVariants(NotificationVariant.LUMO_ERROR);
    }
    refreshAll();
  }

  // ---------- Tabelle ----------

  private Component buildGrid() {
    this.grid = new Grid<>();
    this.grid.setWidthFull();

    this.grid.addColumn(Gleis::getBereich).setHeader("Bereich").setWidth("5rem").setFlexGrow(0);
    this.grid.addColumn(Gleis::getName).setHeader("Name").setWidth("5rem").setFlexGrow(0);
    this.grid.addColumn(gleis -> formatLaenge(gleis.getLaenge())).setHeader("Länge bisher")
      .setTextAlign(ColumnTextAlign.END).setWidth("7rem").setFlexGrow(0);
    this.grid.addColumn(gleis -> formatLaenge(this.gleisMessService.getMessungen().get(gleis))).setHeader("Länge neu")
      .setTextAlign(ColumnTextAlign.END).setWidth("7rem").setFlexGrow(0);

    this.grid.addComponentColumn(this::buildRemoveButton).setWidth("60px").setFlexGrow(0);

    return this.grid;
  }

  private Button buildRemoveButton(Gleis gleis) {
    Button button = new Button(VaadinIcon.TRASH.create(), event -> removeMessung(gleis));
    button.getElement().setAttribute("title", "Messung löschen");
    return button;
  }

  private void removeMessung(Gleis gleis) {
    this.gleisMessService.removeMessung(gleis);
    refreshGrid();
  }

  private String formatLaenge(Integer laenge) {
    return laenge == null ? "" : String.format(Locale.GERMAN, "%,d mm", laenge);
  }

  // ---------- Refresh ----------

  private void refreshAll() {
    boolean aktiv = this.gleisMessService.isAktiv();
    this.messenButton.setEnabled(!aktiv);
    this.beendenButton.setEnabled(aktiv);
    this.statusField.setText("Status: " + this.gleisMessService.getStatusDescription());
    refreshGrid();
  }

  private void refreshGrid() {
    this.grid.setItems(this.gleisMessService.getMessungen().keySet());
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
