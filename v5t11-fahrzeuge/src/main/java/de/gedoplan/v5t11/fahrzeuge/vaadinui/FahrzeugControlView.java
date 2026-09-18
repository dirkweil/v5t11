package de.gedoplan.v5t11.fahrzeuge.vaadinui;

import de.gedoplan.baselibs.utils.util.ResourceUtil;
import de.gedoplan.baselibs.utils.xml.XmlConverter;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.Fahrzeug;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.FahrzeugFunktion;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrweg.Gleis;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.Fahrzeugdecoder;
import de.gedoplan.v5t11.fahrzeuge.gateway.StatusGateway;
import de.gedoplan.v5t11.fahrzeuge.persistence.FahrzeugRepository;
import de.gedoplan.v5t11.fahrzeuge.service.ParcoursService;
import de.gedoplan.v5t11.fahrzeuge.webui.FahrzeugListPresenter;
import de.gedoplan.v5t11.fahrzeuge.webui.LokControllerPresenter;
import de.gedoplan.v5t11.fahrzeuge.webui.VaadinChangePushBroadcaster;
import de.gedoplan.v5t11.util.domain.attribute.BereichselementId;
import de.gedoplan.v5t11.util.domain.attribute.DecoderAdr;
import de.gedoplan.v5t11.vaadincommon.ui.MainLayout;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.slider.IntegerSlider;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.text.Collator;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.microprofile.rest.client.inject.RestClient;

/**
 * Vaadin-Pendant zu {@code view/fahrzeugControl.xhtml} + {@code FahrzeugControlPresenter}, inkl. der einfachen,
 * zuvor zurückgestellten Menüpunkte "Basisdaten" ({@code fahrzeugBasics.xhtml}), "Position"
 * ({@code fahrzeugPosition.xhtml}) und "Löschen" ({@code fahrzeugRemoveConfirm.xhtml}) sowie der Menüpunkte
 * "Funktionen" (eigene Route {@link FahrzeugFunctionView}, Pendant zu {@code fahrzeugFunction.xhtml}) und
 * "Zugbildung" (eigene Route {@link FahrzeugTraktionView}, Pendant zu {@code fahrzeugTraktion.xhtml}). Teil der
 * Phase-2-Migration von v5t11-fahrzeuge (siehe /home/dw/.claude/plans/functional-singing-canyon.md).
 * "Gleislängen" (eigene Route {@link GleisMessungView}, Pendant zu {@code gleisMessung.xhtml}) und
 * "Geschwindigkeiten" (eigene Route {@link FahrzeugMessungView}, Pendant zu {@code fahrzeugMessung.xhtml}).
 * <p>
 * Nur der Menüpunkt "Programmierung" bleibt bewusst ein Cross-Link auf die weiterhin-JSF-View – deren "zurück"
 * führt auf das unveränderte alte fahrzeugControl.xhtml zurück, ein akzeptierter Bruch für die Übergangszeit.
 * <p>
 * Steuerungsaktionen (Aktiv/Fahrstufe/Rückwärts/Funktionen) mutieren nie lokal, sondern rufen ausschließlich
 * {@link StatusGateway#changeFahrzeugdecoder} auf; die tatsächliche Aktualisierung kommt asynchron über Kafka
 * zurück ({@code StatusUpdater}) und wird per {@link VaadinChangePushBroadcaster} (Pendant zu
 * {@code v5t11-status/.../VaadinChangePushBroadcaster} aus Phase 1a) verteilt. Anders als dort ist {@link Fahrzeug}
 * ein JPA-Entity, das bei jeder Änderung in einer fremden Transaktion neu geladen wird – Abgleich daher über
 * {@code getId().equals(...)} statt Objektidentität.
 */
@Route(value = "fahrzeug-control", layout = MainLayout.class)
public class FahrzeugControlView extends VerticalLayout implements HasDynamicTitle {

  @Inject
  FahrzeugRepository fahrzeugRepository;

  @Inject
  @RestClient
  StatusGateway statusGateway;

  @Inject
  Validator validator;

  @Inject
  FahrzeugListPresenter fahrzeugListPresenter;

  @Inject
  LokControllerPresenter lokControllerPresenter;

  @Inject
  ParcoursService parcoursService;

  @Inject
  VaadinChangePushBroadcaster pushBroadcaster;

  private final Consumer<Object> changeListener = this::onChanged;

  private Fahrzeug fahrzeug;

  private Checkbox aktivField;
  private IntegerSlider fahrstufeField;
  private Span fahrstufeValueField;
  private Checkbox rueckwaertsField;
  private Span positionField;
  private Checkbox lichtCheckbox;
  private final Map<FahrzeugFunktion, Checkbox> funktionCheckboxes = new LinkedHashMap<>();

  @PostConstruct
  void init() {
    setSizeFull();
    getStyle().set("overflow", "auto");

    this.fahrzeug = getRefreshedFahrzeug();

    add(buildHeader());
    add(buildSteuerbereich());
  }

  @Override
  public String getPageTitle() {
    return this.fahrzeug.getBetriebsnummer() + " - v5t11";
  }

  @Override
  protected void onAttach(AttachEvent attachEvent) {
    super.onAttach(attachEvent);
    this.pushBroadcaster.addListener(this.changeListener);
  }

  @Override
  protected void onDetach(DetachEvent detachEvent) {
    this.pushBroadcaster.removeListener(this.changeListener);
    super.onDetach(detachEvent);
  }

  private void onChanged(Object changed) {
    if (changed instanceof Fahrzeug f && f.getId().equals(this.fahrzeug.getId())) {
      getUI().ifPresent(ui -> ui.access(this::refreshAll));
    }
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

  private Component buildHeader() {
    Span betriebsnummer = new Span(this.fahrzeug.getBetriebsnummer());
    betriebsnummer.getStyle().set("font-size", "1.75rem").set("font-weight", "bold");

    Image image = new Image("/" + getImage(this.fahrzeug), this.fahrzeug.getBetriebsnummer());
    image.getStyle().set("max-height", "50px").set("margin", "0 1rem");

    Span decoderAdrText = new Span(String.valueOf(decoderAdr()));

    HorizontalLayout left = new HorizontalLayout(betriebsnummer, image, decoderAdrText);
    left.setAlignItems(FlexComponent.Alignment.CENTER);

    Checkbox lokControl1 = buildLokControlCheckbox(0, "1");
    Checkbox lokControl2 = buildLokControlCheckbox(1, "2");

    Button backButton = new Button("zurück", event -> navigateToList());

    HorizontalLayout right = new HorizontalLayout(lokControl1, lokControl2, buildEditMenu(), backButton);
    right.setAlignItems(FlexComponent.Alignment.CENTER);

    HorizontalLayout header = new HorizontalLayout(left, right);
    header.setWidthFull();
    header.setAlignItems(FlexComponent.Alignment.CENTER);
    header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

    return header;
  }

  private Checkbox buildLokControlCheckbox(int lokcontrollerNr, String label) {
    LokControllerPresenter.LokcontrollerAdapter adapter = this.lokControllerPresenter.getLokcontrollerAdapter(this.fahrzeug, lokcontrollerNr);

    Checkbox checkbox = new Checkbox(label);
    checkbox.addClassName("toggle-buttons");
    checkbox.setValue(adapter.isAssigned());
    checkbox.addValueChangeListener(event -> {
      if (event.isFromClient()) {
        adapter.setAssigned(event.getValue());
      }
    });
    return checkbox;
  }

  private MenuBar buildEditMenu() {
    MenuBar menuBar = new MenuBar();
    MenuItem bearbeiten = menuBar.addItem("bearbeiten");
    SubMenu subMenu = bearbeiten.getSubMenu();
    subMenu.addItem("Basisdaten", event -> openBasicsDialog());
    subMenu.addItem("Position", event -> openPositionDialog());
    subMenu.addItem("Zugbildung", event -> navigateToTraktion());
    subMenu.addItem("Funktionen", event -> navigateToFunction());
    subMenu.addItem("Programmierung", event -> navigateToJsf("/view/fahrzeugProgram.xhtml"));
    subMenu.addItem("Geschwindigkeiten", event -> navigateToFahrzeugMessung());
    subMenu.addItem("Gleislängen", event -> navigateToGleisMessung());
    subMenu.addSeparator();
    subMenu.addComponent(buildExportAnchor());
    subMenu.addSeparator();
    subMenu.addItem("Löschen", event -> openRemoveConfirmDialog());
    return menuBar;
  }

  private Anchor buildExportAnchor() {
    StreamResource resource = new StreamResource(exportFilename(this.fahrzeug), () -> {
      try {
        String xml = XmlConverter.toXml(this.fahrzeug);
        return new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
      } catch (Exception e) {
        return new ByteArrayInputStream(new byte[0]);
      }
    });
    return new Anchor(resource, "Export");
  }

  private String exportFilename(Fahrzeug fahrzeug) {
    return fahrzeug.getBetriebsnummer().replaceAll("[^a-zA-Z0-9-]", "_") + ".xml";
  }

  private void navigateToJsf(String path) {
    this.fahrzeugListPresenter.setCurrentFahrzeug(this.fahrzeug);
    getUI().ifPresent(ui -> ui.getPage().setLocation(path));
  }

  private void navigateToFunction() {
    this.fahrzeugListPresenter.setCurrentFahrzeug(this.fahrzeug);
    getUI().ifPresent(ui -> ui.getPage().setLocation("/ui/fahrzeug-function"));
  }

  private void navigateToTraktion() {
    this.fahrzeugListPresenter.setCurrentFahrzeug(this.fahrzeug);
    getUI().ifPresent(ui -> ui.getPage().setLocation("/ui/fahrzeug-traktion"));
  }

  private void navigateToGleisMessung() {
    this.fahrzeugListPresenter.setCurrentFahrzeug(this.fahrzeug);
    getUI().ifPresent(ui -> ui.getPage().setLocation("/ui/gleis-messung"));
  }

  private void navigateToFahrzeugMessung() {
    this.fahrzeugListPresenter.setCurrentFahrzeug(this.fahrzeug);
    getUI().ifPresent(ui -> ui.getPage().setLocation("/ui/fahrzeug-messung"));
  }

  private void navigateToList() {
    getUI().ifPresent(ui -> ui.getPage().setLocation("/ui/fahrzeug-list"));
  }

  // ---------- Basisdaten-Dialog ----------

  private void openBasicsDialog() {
    Dialog dialog = new Dialog();
    dialog.setHeaderTitle("Basisdaten bearbeiten");

    TextField beschreibungField = new TextField("Beschreibung");
    beschreibungField.setValue(this.fahrzeug.getBeschreibung() == null ? "" : this.fahrzeug.getBeschreibung());
    beschreibungField.setWidth("30em");

    IntegerField laengeField = new IntegerField("Länge [mm]");
    laengeField.setValue(this.fahrzeug.getLaenge());

    VerticalLayout formLayout = new VerticalLayout(beschreibungField, laengeField);
    formLayout.setPadding(false);
    dialog.add(formLayout);

    Button saveButton = new Button("speichern", event -> {
      this.fahrzeug.setBeschreibung(beschreibungField.getValue());
      this.fahrzeug.setLaenge(laengeField.getValue() == null ? 0 : laengeField.getValue());
      if (saveFahrzeug()) {
        dialog.close();
      }
    });
    saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    Button cancelButton = new Button("abbrechen", event -> dialog.close());
    dialog.getFooter().add(cancelButton, saveButton);

    dialog.open();
  }

  // ---------- Position-Dialog ----------

  private void openPositionDialog() {
    Dialog dialog = new Dialog();
    dialog.setHeaderTitle("Position bearbeiten");

    ComboBox<Gleis> gleisField = new ComboBox<>("Gleis");
    gleisField.setItems(this.parcoursService.getGleise());
    gleisField.setItemLabelGenerator(gleis -> gleis.getId().toString());
    gleisField.setClearButtonVisible(true);
    if (this.fahrzeug.getGleisId() != null) {
      gleisField.setValue(this.parcoursService.findGleisById(this.fahrzeug.getGleisId()).orElse(null));
    }

    IntegerField positionField = new IntegerField("Position [mm]");
    positionField.setValue(this.fahrzeug.getGleisPosition());

    Checkbox richtungField = new Checkbox();
    richtungField.addClassName("toggle-buttons");
    richtungField.setValue(this.fahrzeug.isGleisZaehlrichtung());
    richtungField.setLabel(richtungField.getValue() ? "in Zählrichtung vorwärts" : "in Zählrichtung rückwärts");
    richtungField.addValueChangeListener(
      event -> richtungField.setLabel(event.getValue() ? "in Zählrichtung vorwärts" : "in Zählrichtung rückwärts"));

    VerticalLayout formLayout = new VerticalLayout(gleisField, positionField, richtungField);
    formLayout.setPadding(false);
    dialog.add(formLayout);

    Button saveButton = new Button("speichern", event -> {
      Gleis gleis = gleisField.getValue();
      this.fahrzeug.setGleisId(gleis == null ? null : gleis.getId());
      this.fahrzeug.setGleisPosition(positionField.getValue() == null ? 0 : positionField.getValue());
      this.fahrzeug.setGleisZaehlrichtung(richtungField.getValue());
      if (saveFahrzeug()) {
        dialog.close();
      }
    });
    saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    Button cancelButton = new Button("abbrechen", event -> dialog.close());
    dialog.getFooter().add(cancelButton, saveButton);

    dialog.open();
  }

  private boolean saveFahrzeug() {
    Set<ConstraintViolation<Fahrzeug>> violations = this.validator.validate(this.fahrzeug);
    if (!violations.isEmpty()) {
      String message = violations.stream().map(cv -> cv.getPropertyPath() + " " + cv.getMessage()).reduce((a, b) -> a + "; " + b).orElse("");
      Notification.show(message).addThemeVariants(NotificationVariant.LUMO_ERROR);
      return false;
    }

    Iterator<FahrzeugFunktion> iterator = this.fahrzeug.getFahrzeugdecoder().getFunktionen().iterator();
    while (iterator.hasNext()) {
      FahrzeugFunktion funktion = iterator.next();
      if (funktion.getMaske() == 0 || funktion.getBeschreibung() == null || funktion.getBeschreibung().strip().isEmpty()) {
        iterator.remove();
      }
    }

    this.fahrzeug = this.fahrzeugRepository.merge(this.fahrzeug);
    this.fahrzeugListPresenter.setCurrentFahrzeug(this.fahrzeug);
    refreshAll();
    return true;
  }

  // ---------- Löschen ----------

  private void openRemoveConfirmDialog() {
    ConfirmDialog dialog = new ConfirmDialog();
    dialog.setHeader("Fahrzeug löschen");
    dialog.setText("Fahrzeug " + this.fahrzeug.getBetriebsnummer() + " wirklich löschen?");
    dialog.setCancelable(true);
    dialog.setConfirmText("löschen");
    dialog.setConfirmButtonTheme("error primary");
    dialog.addConfirmListener(event -> {
      this.fahrzeugRepository.removeById(this.fahrzeug.getId());
      navigateToList();
    });
    dialog.open();
  }

  // ---------- Steuerbereich ----------

  private Component buildSteuerbereich() {
    FormLayout form = new FormLayout();
    form.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));
    form.setWidthFull();

    this.aktivField = new Checkbox("inaktiv");
    this.aktivField.addClassName("toggle-buttons");
    this.aktivField.addValueChangeListener(event -> {
      this.aktivField.setLabel(event.getValue() ? "aktiv" : "inaktiv");
      if (event.isFromClient()) {
        this.statusGateway.changeFahrzeugdecoder(decoderAdr(), event.getValue(), null, null, null, null);
      }
    });
    form.addFormItem(this.aktivField, "Aktiv:");

    this.fahrstufeValueField = new Span();
    this.fahrstufeValueField.getStyle().set("width", "3em").set("flex", "0 0 auto");
    this.fahrstufeField = new IntegerSlider();
    this.fahrstufeField.setMin(0);
    this.fahrstufeField.setMax(decoderAdr().getSystemTyp().getMaxFahrstufe());
    this.fahrstufeField.addValueChangeListener(event -> {
      this.fahrstufeValueField.setText(String.valueOf(event.getValue() != null ? event.getValue() : 0));
      if (event.isFromClient() && event.getValue() != null) {
        int fahrstufe = event.getValue();
        int max = decoderAdr().getSystemTyp().getMaxFahrstufe();
        if (fahrstufe >= 0 && fahrstufe <= max) {
          this.statusGateway.changeFahrzeugdecoder(decoderAdr(), null, fahrstufe, null, null, null);
        } else {
          Notification notification = Notification.show("ungültige Fahrstufe: " + fahrstufe, 3000, Notification.Position.BOTTOM_START);
          notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
          this.fahrstufeField.setValue(this.fahrzeug.getFahrzeugdecoder().getFahrstufe());
        }
      }
    });
    this.rueckwaertsField = new Checkbox("vorwärts");
    this.rueckwaertsField.addClassName("toggle-buttons");
    this.rueckwaertsField.addValueChangeListener(event -> {
      this.rueckwaertsField.setLabel(event.getValue() ? "rückwärts" : "vorwärts");
      if (event.isFromClient()) {
        this.statusGateway.changeFahrzeugdecoder(decoderAdr(), null, null, null, null, event.getValue());
      }
    });
    HorizontalLayout fahrstufeLayout = new HorizontalLayout(this.fahrstufeValueField, this.fahrstufeField, this.rueckwaertsField);
    fahrstufeLayout.setAlignItems(FlexComponent.Alignment.CENTER);
    fahrstufeLayout.setFlexGrow(1, this.fahrstufeField);
    form.addFormItem(fahrstufeLayout, "Fahrstufe:");

    this.positionField = new Span();
    form.addFormItem(this.positionField, "Position:");

    buildFunktionenSection(form);

    refreshAll();

    return form;
  }

  private void buildFunktionenSection(FormLayout form) {
    for (FahrzeugFunktionsGruppe gruppe : getCurrentFunktionsGruppen()) {
      FlexLayout row = new FlexLayout();
      row.getStyle().set("flex-wrap", "wrap").set("gap", "0.5rem");
      row.setAlignItems(FlexComponent.Alignment.CENTER);

      if (gruppe == FahrzeugFunktionsGruppe.FL) {
        this.lichtCheckbox = new Checkbox("Licht");
        this.lichtCheckbox.addClassName("toggle-buttons");
        this.lichtCheckbox.addValueChangeListener(event -> {
          if (event.isFromClient()) {
            this.statusGateway.changeFahrzeugdecoder(decoderAdr(), null, null, null, event.getValue(), null);
          }
        });
        row.add(this.lichtCheckbox);
      }

      for (FahrzeugFunktion funktion : funktionenOfGruppe(gruppe)) {
        Checkbox checkbox = new Checkbox(funktion.getBeschreibung());
        checkbox.addClassName("toggle-buttons");
        checkbox.addValueChangeListener(event -> {
          if (event.isFromClient()) {
            toggleFunktion(funktion, event.getValue());
          }
        });
        this.funktionCheckboxes.put(funktion, checkbox);
        row.add(checkbox);
      }

      form.addFormItem(row, gruppe.getName() + ":");
    }
  }

  private List<FahrzeugFunktionsGruppe> getCurrentFunktionsGruppen() {
    return Stream.concat(
        Stream.of(FahrzeugFunktionsGruppe.FL),
        this.fahrzeug.getFahrzeugdecoder().getFunktionen().stream().map(FahrzeugFunktion::getGruppe))
      .sorted()
      .distinct()
      .collect(Collectors.toList());
  }

  private List<FahrzeugFunktion> funktionenOfGruppe(FahrzeugFunktionsGruppe gruppe) {
    return this.fahrzeug.getFahrzeugdecoder().getFunktionen().stream()
      .filter(f -> f.getGruppe() == gruppe)
      .sorted(Comparator.comparing(FahrzeugFunktion::getBeschreibung, Collator.getInstance()))
      .collect(Collectors.toList());
  }

  private void toggleFunktion(FahrzeugFunktion funktion, boolean aktiv) {
    int fktBits = this.fahrzeug.getFahrzeugdecoder().getFktBits() & ~funktion.getMaske();
    if (aktiv) {
      fktBits |= funktion.getWert();
    }
    this.statusGateway.changeFahrzeugdecoder(decoderAdr(), null, null, fktBits, null, null);
  }

  private void refreshAll() {
    // Kein fahrzeugListPresenter.setCurrentFahrzeug(...) hier: dieser Pfad wird auch vom Push-Handler
    // (onChanged) aus ui.access(...) heraus aufgerufen, ohne aktiven @SessionScoped-Kontext. Die Bridge wird
    // stattdessen direkt vor jeder Navigation gesetzt (siehe navigateToJsf/saveFahrzeug), wo garantiert ein
    // echter Request/Klick vorliegt.
    this.fahrzeug = this.fahrzeugRepository.findById(this.fahrzeug.getId()).get();

    Fahrzeugdecoder decoder = this.fahrzeug.getFahrzeugdecoder();
    this.aktivField.setValue(decoder.isAktiv());
    this.aktivField.setLabel(decoder.isAktiv() ? "aktiv" : "inaktiv");
    this.fahrstufeField.setValue(decoder.getFahrstufe());
    this.fahrstufeValueField.setText(String.valueOf(decoder.getFahrstufe()));
    this.rueckwaertsField.setValue(decoder.isRueckwaerts());
    this.rueckwaertsField.setLabel(decoder.isRueckwaerts() ? "rückwärts" : "vorwärts");
    this.positionField.setText(getPositionsbeschreibung());
    refreshFunktionen();
  }

  private void refreshFunktionen() {
    int fktBits = this.fahrzeug.getFahrzeugdecoder().getFktBits();
    this.lichtCheckbox.setValue(this.fahrzeug.getFahrzeugdecoder().isLicht());
    this.funktionCheckboxes.forEach((funktion, checkbox) -> checkbox.setValue((fktBits & funktion.getMaske()) == funktion.getWert()));
  }

  private String getPositionsbeschreibung() {
    BereichselementId gleisId = this.fahrzeug.getGleisId();
    if (gleisId == null) {
      return "unbekannt";
    }

    return String.format("%s, %d mm, in Zählrichtung %s",
      gleisId,
      this.fahrzeug.getGleisPosition(),
      this.fahrzeug.isGleisZaehlrichtung() ? "vorwärts" : "rückwärts");
  }

  private DecoderAdr decoderAdr() {
    return this.fahrzeug.getFahrzeugdecoder().getDecoderAdr();
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
