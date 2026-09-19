package de.gedoplan.v5t11.fahrzeuge.vaadinui;

import de.gedoplan.baselibs.utils.util.ResourceUtil;
import de.gedoplan.baselibs.utils.xml.XmlConverter;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.Fahrzeug;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.FahrzeugTyp;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.Fahrzeugdecoder;
import de.gedoplan.v5t11.fahrzeuge.persistence.FahrzeugRepository;
import de.gedoplan.v5t11.fahrzeuge.webui.FahrzeugListPresenter;
import de.gedoplan.v5t11.util.domain.attribute.DecoderAdr;
import de.gedoplan.v5t11.util.domain.attribute.SystemTyp;
import de.gedoplan.v5t11.vaadincommon.ui.MainLayout;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.streams.UploadHandler;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Vaadin-Pendant zu {@code view/fahrzeugList.xhtml} + {@code view/fahrzeugCreate.xhtml}. Teil der Phase-2-Migration
 * von v5t11-fahrzeuge (siehe /home/dw/.claude/plans/functional-singing-canyon.md).
 * <p>
 * Bearbeiten ("Basisdaten") und Löschen sind bewusst nicht Teil dieser View: sie hängen im JSF-Original nicht an
 * fahrzeugList, sondern am "bearbeiten"-Menü von fahrzeugControl.xhtml, das erst in einem späteren Schritt migriert
 * wird. Der XML-Import beim Anlegen (Pendant zu {@code FahrzeugCreatePresenter#handleFileUpload}) ist über
 * {@link Upload}/{@link UploadHandler#inMemory} nachgebaut und nutzt wie der bestehende Export in
 * {@link FahrzeugControlView} {@link XmlConverter}.
 */
@Route(value = "fahrzeug-list", layout = MainLayout.class)
@PageTitle("Fahrzeug-Management - v5t11")
public class FahrzeugListView extends VerticalLayout {

  @Inject
  FahrzeugRepository fahrzeugRepository;

  @Inject
  Validator validator;

  @Inject
  FahrzeugListPresenter fahrzeugListPresenter;

  private Div tileContainer;
  private VerticalLayout extraContent;
  private MainLayout mainLayout;
  private List<Fahrzeug> fahrzeuge;
  private Set<FahrzeugTyp> currentFilter = Set.of(FahrzeugTyp.LOK);

  @PostConstruct
  void init() {
    setSizeFull();

    this.fahrzeuge = this.fahrzeugRepository.findAllSortedByBetriebsnummer();

    CheckboxGroup<FahrzeugTyp> filter = new CheckboxGroup<>();
    filter.setLabel("Filter");
    filter.setItems(FahrzeugTyp.values());
    filter.setItemLabelGenerator(FahrzeugTyp::getBezeichnung);
    filter.setValue(this.currentFilter);
    filter.addValueChangeListener(event -> {
      this.currentFilter = event.getValue();
      refreshTiles();
    });

    Button createButton = new Button("Neues Fahrzeug", event -> openCreateDialog());
    createButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

    this.extraContent = new VerticalLayout(filter, createButton);

    this.tileContainer = new Div();
    this.tileContainer.getStyle()
      .set("display", "grid")
      .set("grid-template-columns", "repeat(auto-fill, minmax(220px, 1fr))")
      .set("gap", "1rem")
      .set("width", "100%");

    add(this.tileContainer);

    refreshTiles();
  }

  @Override
  protected void onAttach(AttachEvent attachEvent) {
    this.mainLayout = (MainLayout) getParent().orElseThrow();
    this.mainLayout.setExtraContent(this.extraContent);
  }

  @Override
  protected void onDetach(DetachEvent detachEvent) {
    this.mainLayout.setExtraContent();
  }

  private void refreshTiles() {
    this.tileContainer.removeAll();
    this.fahrzeuge.stream()
      .filter(f -> this.currentFilter.contains(f.getFahrzeugTyp()))
      .forEach(fahrzeug -> this.tileContainer.add(buildTile(fahrzeug)));
  }

  private Div buildTile(Fahrzeug fahrzeug) {
    Span title = new Span(fahrzeug.getBetriebsnummer());
    title.getStyle().set("font-weight", "bold").set("text-decoration", "underline");

    Div header = new Div(title);
    header.getStyle()
      .set("background", "var(--lumo-contrast-5pct)")
      .set("padding", "0.5rem 0.75rem")
      .set("border-bottom", "1px solid var(--lumo-contrast-20pct)");

    Image image = new Image("/" + getImage(fahrzeug), fahrzeug.getBetriebsnummer());
    image.getStyle().set("max-height", "50px").set("max-width", "100%");

    Div imageArea = new Div(image);
    imageArea.getStyle()
      .set("background", "var(--lumo-base-color)")
      .set("padding", "0.75rem")
      .set("display", "flex")
      .set("justify-content", "center");

    Div tile = new Div(header, imageArea);
    tile.getStyle()
      .set("border", "1px solid var(--lumo-contrast-20pct)")
      .set("border-radius", "var(--lumo-border-radius-m)")
      .set("overflow", "hidden")
      .set("cursor", "pointer");
    tile.addClickListener(event -> navigateToControl(fahrzeug));

    return tile;
  }

  private void openCreateDialog() {
    Dialog dialog = new Dialog();
    dialog.setHeaderTitle("Neues Fahrzeug");

    TextField betriebsnummerField = new TextField("Betriebsnummer");
    Select<FahrzeugTyp> fahrzeugTypField = new Select<>();
    fahrzeugTypField.setLabel("Fahrzeugtyp");
    fahrzeugTypField.setItems(FahrzeugTyp.values());
    fahrzeugTypField.setItemLabelGenerator(FahrzeugTyp::getBezeichnung);
    fahrzeugTypField.setValue(FahrzeugTyp.LOK);

    Select<SystemTyp> systemTypField = new Select<>();
    systemTypField.setLabel("Systemtyp");
    systemTypField.setItems(SystemTyp.values());
    systemTypField.setValue(SystemTyp.DCC);

    IntegerField adresseField = new IntegerField("Adresse");
    adresseField.setValue(3);

    Fahrzeug[] importedHolder = new Fahrzeug[1];

    Span importHint = new Span("Fahrzeug aus XML-Datei importieren (optional):");

    Upload upload = new Upload();
    upload.setAcceptedFileTypes(".xml", "text/xml", "application/xml");
    upload.setMaxFiles(1);
    upload.setMaxFileSize(100_000);
    upload.setUploadButton(new Button("XML importieren"));
    upload.setUploadHandler(UploadHandler.inMemory((metadata, data) -> {
      try (Reader reader = new InputStreamReader(new ByteArrayInputStream(data), StandardCharsets.UTF_8)) {
        Fahrzeug imported = XmlConverter.fromXml(Fahrzeug.class, reader);
        importedHolder[0] = imported;
        betriebsnummerField.setValue(imported.getBetriebsnummer() == null ? "" : imported.getBetriebsnummer());
        fahrzeugTypField.setValue(imported.getFahrzeugTyp());
        systemTypField.setValue(imported.getFahrzeugdecoder().getDecoderAdr().getSystemTyp());
        adresseField.setValue(imported.getFahrzeugdecoder().getDecoderAdr().getAdresse());
      } catch (Exception e) {
        Notification.show("Upload fehlgeschlagen").addThemeVariants(NotificationVariant.LUMO_ERROR);
      }
      upload.clearFileList();
    }));
    upload.addFileRejectedListener(event -> Notification.show(event.getErrorMessage()).addThemeVariants(NotificationVariant.LUMO_ERROR));

    VerticalLayout formLayout =
      new VerticalLayout(importHint, upload, betriebsnummerField, fahrzeugTypField, systemTypField, adresseField);
    formLayout.setPadding(false);
    dialog.add(formLayout);

    Button saveButton = new Button("speichern", event -> {
      if (createFahrzeug(importedHolder[0], betriebsnummerField.getValue(), fahrzeugTypField.getValue(), systemTypField.getValue(), adresseField.getValue())) {
        dialog.close();
      }
    });
    saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    Button cancelButton = new Button("abbrechen", event -> dialog.close());
    dialog.getFooter().add(cancelButton, saveButton);

    dialog.open();
  }

  private boolean createFahrzeug(Fahrzeug imported, String betriebsnummer, FahrzeugTyp fahrzeugTyp, SystemTyp systemTyp, Integer adresse) {
    if (this.fahrzeuge.stream().anyMatch(f -> f.getBetriebsnummer().equals(betriebsnummer))) {
      Notification.show("Betriebsnummer bereits vergeben").addThemeVariants(NotificationVariant.LUMO_ERROR);
      return false;
    }

    Fahrzeug fahrzeug;
    if (imported == null) {
      fahrzeug = Fahrzeug.builder()
        .betriebsnummer(betriebsnummer)
        .fahrzeugTyp(fahrzeugTyp)
        .systemTyp(systemTyp)
        .adresse(adresse == null ? 0 : adresse)
        .build();
    } else {
      fahrzeug = imported;
      fahrzeug.setBetriebsnummer(betriebsnummer);
      fahrzeug.setFahrzeugTyp(fahrzeugTyp);
      if (fahrzeug.getFahrzeugdecoder() == null) {
        fahrzeug.setFahrzeugdecoder(new Fahrzeugdecoder(null, new DecoderAdr(systemTyp, adresse == null ? 0 : adresse), new ArrayList<>(), new ArrayList<>()));
      } else {
        fahrzeug.getFahrzeugdecoder().getDecoderAdr().setSystemTyp(systemTyp);
        fahrzeug.getFahrzeugdecoder().getDecoderAdr().setAdresse(adresse == null ? 0 : adresse);
      }
    }

    Set<ConstraintViolation<Fahrzeug>> violations = this.validator.validate(fahrzeug);
    if (!violations.isEmpty()) {
      String message = violations.stream().map(cv -> cv.getPropertyPath() + " " + cv.getMessage()).reduce((a, b) -> a + "; " + b).orElse("");
      Notification.show(message).addThemeVariants(NotificationVariant.LUMO_ERROR);
      return false;
    }

    this.fahrzeugRepository.merge(fahrzeug);
    this.fahrzeuge = this.fahrzeugRepository.findAllSortedByBetriebsnummer();
    refreshTiles();

    navigateToControl(fahrzeug);
    return true;
  }

  private void navigateToControl(Fahrzeug fahrzeug) {
    this.fahrzeugListPresenter.setCurrentFahrzeug(fahrzeug);
    getUI().ifPresent(ui -> ui.getPage().setLocation("/ui/fahrzeug-control"));
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
