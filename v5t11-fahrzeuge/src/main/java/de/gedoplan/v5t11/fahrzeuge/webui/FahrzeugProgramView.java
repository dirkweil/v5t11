package de.gedoplan.v5t11.fahrzeuge.webui;

import de.gedoplan.baselibs.utils.util.ResourceUtil;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.Fahrzeug;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.FahrzeugKonfiguration;
import de.gedoplan.v5t11.fahrzeuge.gateway.StatusGateway;
import de.gedoplan.v5t11.fahrzeuge.persistence.FahrzeugRepository;
import de.gedoplan.v5t11.util.domain.attribute.DecoderAdr;
import de.gedoplan.v5t11.util.domain.attribute.SystemTyp;
import de.gedoplan.v5t11.vaadincommon.ui.MainLayout;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

/**
 * Vaadin-Pendant zu {@code view/fahrzeugProgram.xhtml} + {@code FahrzeugProgramPresenter}, letzte migrierte View der
 * Phase-2-Migration von v5t11-fahrzeuge (siehe /home/dw/.claude/plans/functional-singing-canyon.md) – programmiert
 * CV-/Parameterwerte direkt auf die Fahrzeugdecoder-Hardware, daher höchstes Aktuations-Risiko dieses Moduls.
 * <p>
 * Lesen/Schreiben bleibt bewusst synchron/blockierend wie im JSF-Original (bis zu 120s laut konfiguriertem
 * REST-Timeout, serverseitig ohnehin über {@code synchronized (Zentrale.class)} stationsweit serialisiert) – der
 * Bestätigungsdialog ({@link #openActionDialog}/{@link #execPendingAction}) zeigt während des blockierenden Calls
 * lediglich eine Ladeanzeige und deaktiviert seine Buttons, ohne die Aktion selbst zu asynchronisieren.
 * <p>
 * {@code FahrzeugKonfiguration.equals()}/{@code hashCode()} basiert nur auf {@code nr}; zwei frisch angelegte, noch
 * unnummerierte Zeilen ({@code nr == null}) wären damit Grid-Item-identisch. Ist-/Soll-Anzeige werden daher nicht
 * über einen Grid-Refresh, sondern über die {@link IdentityHashMap}-Felder {@link #istFields}/{@link #sollFields}
 * direkt aktualisiert.
 */
@Route(value = "fahrzeug-program", layout = MainLayout.class)
@PageTitle("Programmierung - v5t11")
public class FahrzeugProgramView extends VerticalLayout {

  @Inject
  FahrzeugRepository fahrzeugRepository;

  @Inject
  @RestClient
  StatusGateway statusGateway;

  @Inject
  Validator validator;

  @Inject
  CurrentFahrzeugHolder currentFahrzeugHolder;

  @Inject
  Logger log;

  private Fahrzeug fahrzeug;
  private Grid<FahrzeugKonfiguration> grid;
  private Column<FahrzeugKonfiguration> nrColumn;
  private Span adrInfoField;
  private final IdentityHashMap<FahrzeugKonfiguration, IntegerField> istFields = new IdentityHashMap<>();
  private final IdentityHashMap<FahrzeugKonfiguration, IntegerField> sollFields = new IdentityHashMap<>();

  private List<FahrzeugKonfiguration> pendingRows;
  private Consumer<List<FahrzeugKonfiguration>> pendingAction;
  private String pendingDescription;

  @PostConstruct
  void init() {
    setSizeFull();
    getStyle().set("overflow", "auto");

    this.fahrzeug = getRefreshedFahrzeug();

    Grid<FahrzeugKonfiguration> gridComponent = buildGrid();
    HorizontalLayout headerComponent = buildHeader();
    refreshHeaderDerived();

    add(headerComponent, gridComponent);
  }

  private Fahrzeug getRefreshedFahrzeug() {
    Fahrzeug current = this.currentFahrzeugHolder.getCurrentFahrzeug();
    if (!this.fahrzeugRepository.isAttached(current)) {
      current = this.fahrzeugRepository.findById(current.getId()).get();
      this.currentFahrzeugHolder.setCurrentFahrzeug(current);
    }
    return current;
  }

  private DecoderAdr decoderAdr() {
    return this.fahrzeug.getFahrzeugdecoder().getDecoderAdr();
  }

  private String konfigWertBezeichnung() {
    return decoderAdr().getSystemTyp().getKonfigWertBezeichnung();
  }

  // ---------- Kopfbereich ----------

  private HorizontalLayout buildHeader() {
    Span betriebsnummer = new Span(this.fahrzeug.getBetriebsnummer());
    betriebsnummer.getStyle().set("font-size", "1.75rem").set("font-weight", "bold");

    Image image = new Image("/" + getImage(this.fahrzeug), this.fahrzeug.getBetriebsnummer());
    image.getStyle().set("max-height", "50px").set("margin", "0 1rem");

    TextField decoderNameField = new TextField("Decoder");
    String decoderName = this.fahrzeug.getFahrzeugdecoder().getDecoderName();
    decoderNameField.setValue(decoderName == null ? "" : decoderName);
    decoderNameField.addValueChangeListener(event -> {
      if (event.isFromClient()) {
        this.fahrzeug.getFahrzeugdecoder().setDecoderName(event.getValue());
      }
    });

    Select<SystemTyp> systemTypField = new Select<>();
    systemTypField.setLabel("Systemtyp");
    systemTypField.setItems(SystemTyp.values());
    systemTypField.setValue(decoderAdr().getSystemTyp());
    systemTypField.addValueChangeListener(event -> {
      if (event.isFromClient() && event.getValue() != null) {
        decoderAdr().setSystemTyp(event.getValue());
        refreshHeaderDerived();
      }
    });

    IntegerField adresseField = new IntegerField("Adresse");
    adresseField.setRequiredIndicatorVisible(true);
    adresseField.setValue(decoderAdr().getAdresse());
    adresseField.addValueChangeListener(event -> {
      if (event.isFromClient()) {
        decoderAdr().setAdresse(event.getValue() == null ? 0 : event.getValue());
        refreshHeaderDerived();
      }
    });

    this.adrInfoField = new Span();

    HorizontalLayout left = new HorizontalLayout(betriebsnummer, image, decoderNameField, systemTypField, adresseField, this.adrInfoField);
    left.setAlignItems(FlexComponent.Alignment.CENTER);

    Button saveButton = new Button("Speichern", event -> save());
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

  private void refreshHeaderDerived() {
    String info = decoderAdr().getAdrInfo();
    this.adrInfoField.setText(info == null ? "" : info);
    this.nrColumn.setHeader(konfigWertBezeichnung());
  }

  private void navigateToControl() {
    getUI().ifPresent(ui -> ui.getPage().setLocation("/ui/fahrzeug-control"));
  }

  // ---------- Tabelle ----------

  private Grid<FahrzeugKonfiguration> buildGrid() {
    this.grid = new Grid<>();
    this.grid.addThemeVariants(GridVariant.LUMO_COMPACT);
    this.grid.setWidthFull();
    this.grid.setAllRowsVisible(true);
    this.grid.setItems(this.fahrzeug.getFahrzeugdecoder().getKonfigurationen());

    Column<FahrzeugKonfiguration> removeColumn = this.grid.addComponentColumn(this::buildRemoveButton).setFlexGrow(0).setWidth("60px");
    Button addButton = new Button(VaadinIcon.PLUS.create(), event -> addKonfiguration());
    addButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
    removeColumn.setHeader(addButton);

    this.nrColumn = this.grid.addComponentColumn(this::buildNrField).setFlexGrow(0).setWidth("90px");

    this.grid.addComponentColumn(this::buildBeschreibungField).setFlexGrow(1);

    Column<FahrzeugKonfiguration> writeColumn = this.grid.addComponentColumn(this::buildWriteButton).setFlexGrow(0).setWidth("60px");
    Button writeAllButton = new Button(VaadinIcon.UPLOAD.create(), event -> writeAllKonfigurationen());
    writeAllButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
    writeColumn.setHeader(writeAllButton);

    this.grid.addComponentColumn(this::buildSollField).setFlexGrow(0).setWidth("150px");

    Column<FahrzeugKonfiguration> copyColumn = this.grid.addComponentColumn(this::buildCopyButton).setFlexGrow(0).setWidth("60px");
    Button copyAllButton = new Button(VaadinIcon.ANGLE_LEFT.create(), event -> copyIst2SollAll());
    copyAllButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
    copyColumn.setHeader(copyAllButton);

    this.grid.addComponentColumn(this::buildIstField).setFlexGrow(0).setWidth("150px");

    Column<FahrzeugKonfiguration> readColumn = this.grid.addComponentColumn(this::buildReadButton).setFlexGrow(0).setWidth("60px");
    Button readAllButton = new Button(VaadinIcon.DOWNLOAD.create(), event -> readAllKonfigurationen());
    readAllButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
    readColumn.setHeader(readAllButton);

    return this.grid;
  }

  private Button buildRemoveButton(FahrzeugKonfiguration konfiguration) {
    return new Button(VaadinIcon.TRASH.create(), event -> removeKonfiguration(konfiguration));
  }

  private IntegerField buildNrField(FahrzeugKonfiguration konfiguration) {
    IntegerField field = new IntegerField();
    field.setWidthFull();
    field.setMin(1);
    field.setValue(konfiguration.getNr());
    field.setReadOnly(konfiguration.getNr() != null);
    field.addValueChangeListener(event -> {
      if (event.isFromClient()) {
        konfiguration.setNr(event.getValue());
        if (event.getValue() != null) {
          field.setReadOnly(true);
        }
      }
    });
    return field;
  }

  private TextArea buildBeschreibungField(FahrzeugKonfiguration konfiguration) {
    TextArea field = new TextArea();
    field.setWidthFull();
    field.setValue(konfiguration.getBeschreibung() == null ? "" : konfiguration.getBeschreibung());
    field.addValueChangeListener(event -> {
      if (event.isFromClient()) {
        konfiguration.setBeschreibung(event.getValue());
      }
    });
    return field;
  }

  private Button buildWriteButton(FahrzeugKonfiguration konfiguration) {
    return new Button(VaadinIcon.UPLOAD.create(), event -> writeKonfiguration(konfiguration));
  }

  private IntegerField buildSollField(FahrzeugKonfiguration konfiguration) {
    IntegerField field = new IntegerField();
    field.setWidthFull();
    field.setValue(konfiguration.getSoll());
    field.addValueChangeListener(event -> {
      if (event.isFromClient()) {
        konfiguration.setSoll(event.getValue());
      }
    });
    this.sollFields.put(konfiguration, field);
    return field;
  }

  private Button buildCopyButton(FahrzeugKonfiguration konfiguration) {
    return new Button(VaadinIcon.ANGLE_LEFT.create(), event -> copyIst2Soll(konfiguration));
  }

  private IntegerField buildIstField(FahrzeugKonfiguration konfiguration) {
    IntegerField field = new IntegerField();
    field.setWidthFull();
    field.setReadOnly(true);
    field.setValue(konfiguration.getIst());
    this.istFields.put(konfiguration, field);
    return field;
  }

  private Button buildReadButton(FahrzeugKonfiguration konfiguration) {
    return new Button(VaadinIcon.DOWNLOAD.create(), event -> readKonfiguration(konfiguration));
  }

  private void addKonfiguration() {
    this.fahrzeug.getFahrzeugdecoder().getKonfigurationen().add(0, new FahrzeugKonfiguration(null, null, null));
    refreshGridItems();
  }

  private void removeKonfiguration(FahrzeugKonfiguration konfiguration) {
    Iterator<FahrzeugKonfiguration> iterator = this.fahrzeug.getFahrzeugdecoder().getKonfigurationen().iterator();
    while (iterator.hasNext()) {
      if (iterator.next() == konfiguration) {
        iterator.remove();
        break;
      }
    }

    this.istFields.remove(konfiguration);
    this.sollFields.remove(konfiguration);
    refreshGridItems();
  }

  private void refreshGridItems() {
    this.grid.setItems(this.fahrzeug.getFahrzeugdecoder().getKonfigurationen());
  }

  // ---------- Lesen/Schreiben/Kopieren ----------

  private void readAllKonfigurationen() {
    openActionDialog(this.fahrzeug.getFahrzeugdecoder().getKonfigurationen(), " aus dem Fahrzeug lesen", this::readKonfigurationen);
  }

  private void readKonfiguration(FahrzeugKonfiguration konfiguration) {
    openActionDialog(List.of(konfiguration), " aus dem Fahrzeug lesen", this::readKonfigurationen);
  }

  private void writeAllKonfigurationen() {
    openActionDialog(this.fahrzeug.getFahrzeugdecoder().getKonfigurationen(), " in das Fahrzeug schreiben", this::writeKonfigurationen);
  }

  private void writeKonfiguration(FahrzeugKonfiguration konfiguration) {
    openActionDialog(List.of(konfiguration), " in das Fahrzeug schreiben", this::writeKonfigurationen);
  }

  private void readKonfigurationen(List<FahrzeugKonfiguration> rows) {
    List<Integer> keys = rows.stream().map(FahrzeugKonfiguration::getNr).toList();
    Map<Integer, Integer> result = this.statusGateway.getFahrzeugdecoderConfig(decoderAdr().getSystemTyp(), keys);
    rows.forEach(konfiguration -> {
      Integer ist = result.get(konfiguration.getNr());
      if (ist != null && ist < 0) {
        ist = null;
        Notification.show(konfigWertBezeichnung() + " " + konfiguration.getNr() + " kann nicht gelesen werden")
          .addThemeVariants(NotificationVariant.LUMO_WARNING);
      }
      konfiguration.setIst(ist);
      this.istFields.get(konfiguration).setValue(ist);
    });
  }

  private void writeKonfigurationen(List<FahrzeugKonfiguration> rows) {
    Map<Integer, Integer> nrToSoll = rows.stream().collect(Collectors.toMap(FahrzeugKonfiguration::getNr, FahrzeugKonfiguration::getSoll));
    this.statusGateway.setFahrzeugdecoderConfig(decoderAdr().getSystemTyp(), nrToSoll);
    rows.forEach(konfiguration -> {
      konfiguration.setIst(konfiguration.getSoll());
      this.istFields.get(konfiguration).setValue(konfiguration.getIst());
    });
  }

  private void copyIst2Soll(FahrzeugKonfiguration konfiguration) {
    if (konfiguration.getIst() != null) {
      konfiguration.setSoll(konfiguration.getIst());
      this.sollFields.get(konfiguration).setValue(konfiguration.getSoll());
    }
  }

  private void copyIst2SollAll() {
    this.fahrzeug.getFahrzeugdecoder().getKonfigurationen().forEach(this::copyIst2Soll);
  }

  // ---------- Bestätigungsdialog ----------

  private void openActionDialog(List<FahrzeugKonfiguration> rows, String verb, Consumer<List<FahrzeugKonfiguration>> action) {
    this.pendingRows = rows;
    this.pendingAction = action;
    this.pendingDescription = rows.stream()
      .map(FahrzeugKonfiguration::getNr)
      .map(String::valueOf)
      .collect(Collectors.joining(",", konfigWertBezeichnung() + " ", verb));

    Dialog dialog = new Dialog();
    dialog.setHeaderTitle(this.pendingDescription);

    Span warning = new Span("Das Fahrzeug muss sich alleine auf dem Programmiergleis befinden!");
    ProgressBar progressBar = new ProgressBar();
    progressBar.setIndeterminate(true);
    progressBar.setVisible(false);

    VerticalLayout body = new VerticalLayout(warning, progressBar);
    body.setPadding(false);
    dialog.add(body);

    Button cancelButton = new Button("abbrechen", event -> dialog.close());
    Button okButton = new Button("ok");
    okButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    dialog.getFooter().add(cancelButton, okButton);

    okButton.addClickListener(event -> execPendingAction(dialog, okButton, cancelButton, progressBar));

    dialog.open();
  }

  private void execPendingAction(Dialog dialog, Button okButton, Button cancelButton, ProgressBar progressBar) {
    okButton.setEnabled(false);
    cancelButton.setEnabled(false);
    progressBar.setVisible(true);
    getUI().ifPresent(UI::push);

    try {
      this.pendingAction.accept(this.pendingRows);
      dialog.close();
    } catch (Exception e) {
      String msg = String.format("Kann Aktion \"%s\" nicht durchführen: %s", this.pendingDescription, e);
      this.log.error(msg, e);
      Notification.show(msg).addThemeVariants(NotificationVariant.LUMO_ERROR);
      okButton.setEnabled(true);
      cancelButton.setEnabled(true);
      progressBar.setVisible(false);
    }
  }

  // ---------- Speichern ----------

  private void save() {
    Set<ConstraintViolation<Fahrzeug>> violations = this.validator.validate(this.fahrzeug);
    if (!violations.isEmpty()) {
      String message = violations.stream().map(cv -> cv.getPropertyPath() + " " + cv.getMessage()).reduce((a, b) -> a + "; " + b).orElse("");
      Notification.show(message).addThemeVariants(NotificationVariant.LUMO_ERROR);
      return;
    }

    List<FahrzeugKonfiguration> list = this.fahrzeug.getFahrzeugdecoder().getKonfigurationen();
    if (Set.copyOf(list).size() != list.size()) {
      Notification.show(konfigWertBezeichnung() + " doppelt").addThemeVariants(NotificationVariant.LUMO_ERROR);
      return;
    }

    list.sort(Comparator.comparing(FahrzeugKonfiguration::getNr));

    this.fahrzeug = this.fahrzeugRepository.merge(this.fahrzeug);
    this.currentFahrzeugHolder.setCurrentFahrzeug(this.fahrzeug);
    refreshGridItems();
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
