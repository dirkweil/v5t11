package de.gedoplan.v5t11.fahrzeuge.vaadinui;

import de.gedoplan.baselibs.utils.util.ResourceUtil;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.Fahrzeug;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.FahrzeugFunktion;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe;
import de.gedoplan.v5t11.fahrzeuge.persistence.FahrzeugRepository;
import de.gedoplan.v5t11.fahrzeuge.webui.FahrzeugListPresenter;
import de.gedoplan.v5t11.vaadincommon.ui.MainLayout;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import java.util.Iterator;
import java.util.Set;

/**
 * Vaadin-Pendant zu {@code view/fahrzeugFunction.xhtml} + {@code FahrzeugFunctionPresenter}. Teil der
 * Phase-2-Migration von v5t11-fahrzeuge (siehe /home/dw/.claude/plans/functional-singing-canyon.md).
 * <p>
 * Anders als "Basisdaten"/"Position" (kleine Dialoge in {@link FahrzeugControlView}) ist dies eine eigene Route:
 * die Funktionsliste ist variabel lang (Zeilen hinzufügen/löschen) und hätte einen Modal-Dialog gesprengt. Die im
 * JSF-Original per {@code p:triStateCheckbox} dargestellten 16 Bit-Spalten (F15..F0, kodiert über {@code maske}/
 * {@code wert}) haben in Vaadin kein eingebautes Äquivalent und werden stattdessen über einen Button je Bit
 * dargestellt, der bei Klick zyklisch durch die drei Zustände schaltet (identische Bit-Logik wie der alte
 * {@code FahrzeugFunctionPresenter.TriStateAdapter}).
 */
@Route(value = "fahrzeug-function", layout = MainLayout.class)
@PageTitle("Fahrzeug-Funktionen - v5t11")
public class FahrzeugFunctionView extends VerticalLayout {

  @Inject
  FahrzeugRepository fahrzeugRepository;

  @Inject
  Validator validator;

  @Inject
  FahrzeugListPresenter fahrzeugListPresenter;

  private Fahrzeug fahrzeug;
  private Grid<FahrzeugFunktion> grid;

  @PostConstruct
  void init() {
    setSizeFull();
    getStyle().set("overflow", "auto");

    this.fahrzeug = getRefreshedFahrzeug();

    add(buildHeader(), buildGrid());
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

  // ---------- Grid ----------

  private Grid<FahrzeugFunktion> buildGrid() {
    this.grid = new Grid<>();
    this.grid.addThemeVariants(GridVariant.LUMO_COMPACT);
    this.grid.setWidthFull();
    this.grid.setAllRowsVisible(true);
    this.grid.setItems(this.fahrzeug.getFahrzeugdecoder().getFunktionen());

    this.grid.addComponentColumn(this::buildGruppeComboBox).setHeader("Funktionsgruppe").setFlexGrow(0).setWidth("160px");
    this.grid.addComponentColumn(this::buildBeschreibungField).setHeader("Funktionsbeschreibung").setFlexGrow(1);
    this.grid.addComponentColumn(this::buildEigenschaftenLayout).setHeader("Eigenschaften").setFlexGrow(0).setWidth("140px");
    this.grid.addComponentColumn(this::buildBitsLayout).setHeader("Bits").setFlexGrow(0).setWidth("530px");

    Column<FahrzeugFunktion> removeColumn = this.grid.addComponentColumn(this::buildRemoveButton).setFlexGrow(0).setWidth("60px");
    Button addButton = new Button(VaadinIcon.PLUS.create(), event -> addFunktion());
    addButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
    removeColumn.setHeader(addButton);

    return this.grid;
  }

  private ComboBox<FahrzeugFunktionsGruppe> buildGruppeComboBox(FahrzeugFunktion funktion) {
    ComboBox<FahrzeugFunktionsGruppe> comboBox = new ComboBox<>();
    comboBox.setItems(FahrzeugFunktionsGruppe.values());
    comboBox.setItemLabelGenerator(FahrzeugFunktionsGruppe::getName);
    comboBox.setValue(funktion.getGruppe());
    comboBox.addValueChangeListener(event -> funktion.setGruppe(event.getValue()));
    return comboBox;
  }

  private TextField buildBeschreibungField(FahrzeugFunktion funktion) {
    TextField field = new TextField();
    field.setWidthFull();
    field.setValue(funktion.getBeschreibung() == null ? "" : funktion.getBeschreibung());
    field.addValueChangeListener(event -> funktion.setBeschreibung(event.getValue()));
    return field;
  }

  private HorizontalLayout buildEigenschaftenLayout(FahrzeugFunktion funktion) {
    Checkbox impulsField = new Checkbox("Impuls", funktion.isImpuls());
    impulsField.addValueChangeListener(event -> funktion.setImpuls(event.getValue()));

    Checkbox hornField = new Checkbox("Horn", funktion.isHorn());
    hornField.addValueChangeListener(event -> funktion.setHorn(event.getValue()));

    Checkbox faderField = new Checkbox("Fader", funktion.isFader());
    faderField.addValueChangeListener(event -> funktion.setFader(event.getValue()));

    HorizontalLayout layout = new HorizontalLayout(impulsField, hornField, faderField);
    layout.setSpacing(false);
    layout.getStyle()
      .set("flex-wrap", "wrap")
      .set("row-gap", "0")
      .set("column-gap", "0.75rem")
      .set("align-items", "center");
    return layout;
  }

  private HorizontalLayout buildBitsLayout(FahrzeugFunktion funktion) {
    HorizontalLayout layout = new HorizontalLayout();
    layout.setSpacing(false);
    layout.getStyle().set("gap", "2px");
    layout.setAlignItems(FlexComponent.Alignment.CENTER);
    layout.add(buildBitBoundLabel("15"));
    for (int bitNr = 15; bitNr >= 0; bitNr--) {
      layout.add(buildBitToggle(funktion, bitNr));
      if (bitNr % 4 == 0 && bitNr != 0) {
        layout.add(buildNibbleGap());
      }
    }
    layout.add(buildBitBoundLabel("0"));
    return layout;
  }

  private Span buildBitBoundLabel(String text) {
    Span span = new Span(text);
    span.getStyle().set("font-size", "var(--lumo-font-size-xs)").set("color", "var(--lumo-secondary-text-color)");
    return span;
  }

  private Div buildNibbleGap() {
    Div gap = new Div();
    gap.getStyle().set("width", "8px").set("flex", "0 0 auto");
    return gap;
  }

  private Button buildBitToggle(FahrzeugFunktion funktion, int bitNr) {
    Button button = new Button();
    button.getStyle()
      .set("width", "22px")
      .set("height", "22px")
      .set("min-width", "22px")
      .set("padding", "0")
      .set("display", "flex")
      .set("align-items", "center")
      .set("justify-content", "center")
      .set("border-radius", "var(--lumo-border-radius-s)");
    updateBitToggle(button, funktion, bitNr);
    button.addClickListener(event -> {
      cycleBit(funktion, bitNr);
      updateBitToggle(button, funktion, bitNr);
    });
    return button;
  }

  private void updateBitToggle(Button button, FahrzeugFunktion funktion, int bitNr) {
    int bit = 1 << bitNr;
    if ((funktion.getMaske() & bit) == 0) {
      // undefiniert: wie eine ausgeschaltete Checkbox
      button.setIcon(null);
      button.getStyle()
        .set("background", "var(--lumo-contrast-5pct)")
        .set("border", "1px solid var(--lumo-contrast-30pct)");
    } else if ((funktion.getWert() & bit) != 0) {
      // an: wie eine eingeschaltete Checkbox (blauer Hintergrund, weißer Haken)
      button.setIcon(checkboxIcon(VaadinIcon.CHECK));
      button.getStyle()
        .set("background", "var(--lumo-primary-color)")
        .set("border", "1px solid var(--lumo-primary-color)");
    } else {
      // aus: wie "an", aber mit "x" statt Haken
      button.setIcon(checkboxIcon(VaadinIcon.CLOSE_SMALL));
      button.getStyle()
        .set("background", "var(--lumo-primary-color)")
        .set("border", "1px solid var(--lumo-primary-color)");
    }
  }

  private Icon checkboxIcon(VaadinIcon vaadinIcon) {
    Icon icon = vaadinIcon.create();
    icon.setSize("22px");
    icon.getStyle().set("margin", "0");
    icon.setColor("var(--lumo-primary-contrast-color)");
    return icon;
  }

  private void cycleBit(FahrzeugFunktion funktion, int bitNr) {
    int bit = 1 << bitNr;
    if ((funktion.getMaske() & bit) == 0) {
      funktion.setMaske(funktion.getMaske() | bit);
      funktion.setWert(funktion.getWert() | bit);
    } else if ((funktion.getWert() & bit) != 0) {
      funktion.setWert(funktion.getWert() & ~bit);
    } else {
      funktion.setMaske(funktion.getMaske() & ~bit);
    }
  }

  private Button buildRemoveButton(FahrzeugFunktion funktion) {
    Button button = new Button(VaadinIcon.TRASH.create(), event -> removeFunktion(funktion));
    return button;
  }

  private void addFunktion() {
    this.fahrzeug.getFahrzeugdecoder().getFunktionen().add(0, new FahrzeugFunktion(FahrzeugFunktionsGruppe.AF, 0, 0, false, false, false, ""));
    this.grid.setItems(this.fahrzeug.getFahrzeugdecoder().getFunktionen());
  }

  private void removeFunktion(FahrzeugFunktion funktion) {
    Iterator<FahrzeugFunktion> iterator = this.fahrzeug.getFahrzeugdecoder().getFunktionen().iterator();
    while (iterator.hasNext()) {
      if (iterator.next() == funktion) {
        iterator.remove();
        break;
      }
    }

    this.grid.setItems(this.fahrzeug.getFahrzeugdecoder().getFunktionen());
  }

  // ---------- Speichern ----------

  private void saveFahrzeug() {
    Set<ConstraintViolation<Fahrzeug>> violations = this.validator.validate(this.fahrzeug);
    if (!violations.isEmpty()) {
      String message = violations.stream().map(cv -> cv.getPropertyPath() + " " + cv.getMessage()).reduce((a, b) -> a + "; " + b).orElse("");
      Notification.show(message).addThemeVariants(NotificationVariant.LUMO_ERROR);
      return;
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
