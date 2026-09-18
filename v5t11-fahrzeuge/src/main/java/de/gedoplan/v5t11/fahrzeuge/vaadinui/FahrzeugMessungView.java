package de.gedoplan.v5t11.fahrzeuge.vaadinui;

import de.gedoplan.baselibs.utils.util.ResourceUtil;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrweg.Gleis;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.Fahrzeug;
import de.gedoplan.v5t11.fahrzeuge.persistence.FahrzeugRepository;
import de.gedoplan.v5t11.fahrzeuge.service.GeschwindigkeitsMessService;
import de.gedoplan.v5t11.fahrzeuge.webui.FahrzeugListPresenter;
import de.gedoplan.v5t11.vaadincommon.ui.MainLayout;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.formlayout.FormLayout;
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
import java.util.List;

/**
 * Vaadin-Pendant zu {@code view/fahrzeugMessung.xhtml} + {@code FahrzeugMessungPresenter}. Teil der
 * Phase-2-Migration von v5t11-fahrzeuge (siehe /home/dw/.claude/plans/functional-singing-canyon.md).
 * <p>
 * {@link GeschwindigkeitsMessService} benachrichtigt Interessenten nicht über {@link
 * de.gedoplan.v5t11.fahrzeuge.webui.VaadinChangePushBroadcaster}, sondern über einen eigenen
 * Single-Slot-Observer ({@code attachObserver(Runnable)}/{@code detachObserver()}), an den sich diese View analog
 * zu {@code FahrzeugControlView}s {@code onAttach}/{@code onDetach}-Pattern direkt anmeldet (siehe {@link
 * GleisMessungView}, gleiches Muster). Der Observer liefert kein Änderungsobjekt, daher liest {@link #refreshAll()}
 * alle Anzeigewerte frisch aus den Service-Gettern. {@code GeschwindigkeitsMessService} selbst bleibt unverändert
 * inkl. seiner {@code Thread.sleep(5000)}-Wartezeit im Event-Handler-Thread (Rollback-Pfad ist die alte
 * {@code fahrzeugMessung.xhtml}).
 */
@Route(value = "fahrzeug-messung", layout = MainLayout.class)
@PageTitle("Geschwindigkeiten - v5t11")
public class FahrzeugMessungView extends VerticalLayout {

  @Inject
  FahrzeugRepository fahrzeugRepository;

  @Inject
  FahrzeugListPresenter fahrzeugListPresenter;

  @Inject
  GeschwindigkeitsMessService geschwindigkeitsMessService;

  private Fahrzeug fahrzeug;

  private Button hoechstgeschwindigkeitButton;
  private Button profilButton;
  private Button abbrechenButton;
  private Span statusField;
  private Grid<GeschwindigkeitsEntry> grid;

  private Runnable selectedAktion;
  private String selectedAktionsBeschreibung;

  private record GeschwindigkeitsEntry(int fahrstufe, Long vorwaerts, Long rueckwaerts) {
  }

  @PostConstruct
  void init() {
    setSizeFull();
    getStyle().set("overflow", "auto");

    this.fahrzeug = getRefreshedFahrzeug();

    add(buildHeader(), buildInfoSection(), buildActionsBar(), buildGrid());
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
    this.geschwindigkeitsMessService.attachObserver(this::onServiceChanged);
    refreshAll();
  }

  @Override
  protected void onDetach(DetachEvent detachEvent) {
    this.geschwindigkeitsMessService.detachObserver();
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
    this.geschwindigkeitsMessService.save();
    navigateToControl();
  }

  private void navigateToControl() {
    getUI().ifPresent(ui -> ui.getPage().setLocation("/ui/fahrzeug-control"));
  }

  // ---------- Infobereich ----------

  private Component buildInfoSection() {
    FormLayout form = new FormLayout();
    form.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 2));

    Gleis messGleis = this.geschwindigkeitsMessService.getMessGleis();
    form.addFormItem(new Span(gleisLabel(messGleis)), "Messgleis:");
    form.addFormItem(new Span(messGleis == null ? "" : formatLaenge(messGleis.getLaenge())), "Länge:");
    form.addFormItem(new Span(gleisLabel(this.geschwindigkeitsMessService.getLinkesAnschlussGleis())), "Gleis links:");
    form.addFormItem(new Span(gleisLabel(this.geschwindigkeitsMessService.getRechtesAnschlussGleis())), "Gleis rechts:");

    return form;
  }

  private String gleisLabel(Gleis gleis) {
    return gleis == null ? "" : String.valueOf(gleis.getId());
  }

  private String formatLaenge(Integer laenge) {
    return laenge == null ? "" : String.format(Locale.GERMAN, "%,d mm", laenge);
  }

  // ---------- Aktionsleiste ----------

  private Component buildActionsBar() {
    this.hoechstgeschwindigkeitButton = new Button("Höchstgeschwindigkeit messen", event -> selectHoechstgeschwindigkeitMessen());
    this.profilButton = new Button("Geschwindigkeitsprofil erstellen", event -> selectGeschwindigkeitsprofilErstellen());
    this.abbrechenButton = new Button("Messung abbrechen", event -> messungAbbrechen());
    this.statusField = new Span();
    this.statusField.getStyle().set("font-weight", "bold");

    HorizontalLayout bar = new HorizontalLayout(this.hoechstgeschwindigkeitButton, this.profilButton, this.abbrechenButton, this.statusField);
    bar.setAlignItems(FlexComponent.Alignment.CENTER);
    return bar;
  }

  private void selectHoechstgeschwindigkeitMessen() {
    setSelectedAktion(
      "Höchstgeschwindigkeit messen",
      "Messgleis und angrenzende Gleise räumen. Fahrzeug auf einem der Umkehrgleise so aufstellen, dass es in Richtung Messgleis fahren wird.",
      () -> this.geschwindigkeitsMessService.startHoechstgeschwindigkeitsMessung(this.fahrzeug));
  }

  private void selectGeschwindigkeitsprofilErstellen() {
    setSelectedAktion(
      "Geschwindigkeitsprofil erstellen",
      "Messgleis und angrenzende Gleise räumen. Fahrzeug auf einem der Umkehrgleise so aufstellen, dass es in Richtung Messgleis fahren wird.",
      () -> this.geschwindigkeitsMessService.startProfilMessung(this.fahrzeug));
  }

  private void messungAbbrechen() {
    this.geschwindigkeitsMessService.abbrechen();
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

    this.grid.addColumn(GeschwindigkeitsEntry::fahrstufe).setHeader("Fahrstufe")
      .setTextAlign(ColumnTextAlign.CENTER).setWidth("150px").setFlexGrow(0);
    this.grid.addColumn(entry -> formatGeschwindigkeit(entry.vorwaerts())).setHeader("vorwärts")
      .setTextAlign(ColumnTextAlign.CENTER).setWidth("250px").setFlexGrow(0);
    this.grid.addComponentColumn(entry -> buildRemoveButton(entry.fahrstufe())).setWidth("60px").setFlexGrow(0);
    this.grid.addColumn(entry -> formatGeschwindigkeit(entry.rueckwaerts())).setHeader("rückwärts")
      .setTextAlign(ColumnTextAlign.CENTER).setWidth("250px").setFlexGrow(0);
    this.grid.addComponentColumn(entry -> buildRemoveButton(-entry.fahrstufe())).setWidth("60px").setFlexGrow(0);

    return this.grid;
  }

  private Button buildRemoveButton(int fahrstufe) {
    Button button = new Button(VaadinIcon.TRASH.create(), event -> removeMessung(fahrstufe));
    button.getElement().setAttribute("title", "Messung löschen");
    return button;
  }

  private void removeMessung(int fahrstufe) {
    this.geschwindigkeitsMessService.removeMessung(fahrstufe);
    refreshGrid();
  }

  private String formatGeschwindigkeit(Long modellMicromProS) {
    if (modellMicromProS == null) {
      return "";
    }

    long real = this.geschwindigkeitsMessService.convertModellZuRealGeschwindigkeit(modellMicromProS);
    return String.format(Locale.GERMAN, "%,d µm/s (%,d km/h)", modellMicromProS, real);
  }

  // ---------- Refresh ----------

  private void refreshAll() {
    boolean aktiv = this.geschwindigkeitsMessService.isAktiv();
    this.hoechstgeschwindigkeitButton.setEnabled(!aktiv);
    this.profilButton.setEnabled(!aktiv);
    this.abbrechenButton.setEnabled(aktiv);
    this.statusField.setText("Status: " + this.geschwindigkeitsMessService.getStatusDescription());
    refreshGrid();
  }

  private void refreshGrid() {
    List<GeschwindigkeitsEntry> entries = this.geschwindigkeitsMessService
      .getGeschwindigkeit()
      .keySet()
      .stream()
      .mapToInt(Math::abs)
      .sorted()
      .distinct()
      .mapToObj(fahrstufe -> new GeschwindigkeitsEntry(
        fahrstufe,
        this.geschwindigkeitsMessService.getGeschwindigkeit().get(fahrstufe),
        this.geschwindigkeitsMessService.getGeschwindigkeit().get(-fahrstufe)))
      .toList();
    this.grid.setItems(entries);
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
