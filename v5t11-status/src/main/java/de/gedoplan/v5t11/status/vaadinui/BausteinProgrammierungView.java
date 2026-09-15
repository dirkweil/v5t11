package de.gedoplan.v5t11.status.vaadinui;

import de.gedoplan.baselibs.utils.util.ClassUtil;
import de.gedoplan.v5t11.status.entity.Kanal;
import de.gedoplan.v5t11.status.entity.Steuerung;
import de.gedoplan.v5t11.status.entity.baustein.Baustein;
import de.gedoplan.v5t11.status.entity.baustein.Konfigurierbar;
import de.gedoplan.v5t11.status.service.BausteinConfigurationService;
import de.gedoplan.v5t11.status.service.ConfigurationRuntimeService;
import de.gedoplan.v5t11.status.service.CurrentBausteinHolder;
import de.gedoplan.v5t11.status.service.Programmierfamilie;
import de.gedoplan.v5t11.status.service.besetztmelder.bmmiba3.BMMiba3ConfigurationAdapter;
import de.gedoplan.v5t11.status.service.besetztmelder.hebm8.HEBM8ConfigurationAdapter;
import de.gedoplan.v5t11.status.service.besetztmelder.muet8i.Muet8iConfigurationAdapter;
import de.gedoplan.v5t11.status.service.besetztmelder.muet8k.Muet8kConfigurationAdapter;
import de.gedoplan.v5t11.status.service.besetztmelder.sxbm1.SXBM1ConfigurationAdapter;
import de.gedoplan.v5t11.status.service.besetztmelder.vm5262.VM5262ConfigurationAdapter;
import de.gedoplan.v5t11.status.service.funktionsdecoder.sd8.SD8ConfigurationAdapter;
import de.gedoplan.v5t11.status.service.funktionsdecoder.sd8.SD8ConfigurationAdapter.ServoConfiguration;
import de.gedoplan.v5t11.status.service.funktionsdecoder.sd8.SD8RuntimeService;
import de.gedoplan.v5t11.status.service.funktionsdecoder.strfd1.STRFD1ConfigurationAdapter;
import de.gedoplan.v5t11.status.service.funktionsdecoder.sxsd1.SXSD1ConfigurationAdapter;
import de.gedoplan.v5t11.status.service.funktionsdecoder.wdmiba.WDMibaConfigurationAdapter;
import de.gedoplan.v5t11.status.service.funktionsdecoder.wdmiba3.WDMiba3ConfigurationAdapter;
import de.gedoplan.v5t11.vaadincommon.ui.MainLayout;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.FieldSet;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.inject.Inject;

/**
 * Vaadin-Pendant zur bausteinProgrammierung*-Familie (Landing-Seite + 2 Prog-Mode-Zwischenschritte + 11
 * geräte-spezifische Formulare), siehe {@code BausteinProgrammierungPresenter}. Bildet den gesamten Ablauf in EINER
 * View mit view-lokalem State ab (wie {@link SystemControlView}) statt über mehrere xhtml-Seiten/Routen; ein interner
 * Content-Bereich wird je Zustand (Landing / openProgMode / Geräteformular / closeProgMode) neu aufgebaut.
 * <p>
 * Alle Fachklassen ({@link Steuerung}, {@link BausteinConfigurationService},
 * {@link ConfigurationRuntimeService}+Subklassen, {@link de.gedoplan.v5t11.status.service.ConfigurationAdapter}
 * +Subklassen) werden unverändert 1:1 weiterverwendet. Die Auswahl des aktuellen Bausteins läuft über
 * {@link CurrentBausteinHolder}, da dieselbe CDI-Produktionsstelle für {@code @Current Baustein} auch vom (weiterhin
 * vorhandenen) JSF-Presenter genutzt wird.
 * <p>
 * Zwei bewusste Abweichungen vom JSF-Original: (1) "abbrechen" im Geräteformular führt hier für alle Geräte
 * einheitlich über den closeProgMode-Bestätigungsschritt (im Original wich {@code bausteinProgrammierung_HEBM8.xhtml}
 * davon ab und sprang direkt zur Landing-Seite, ohne {@code abort()} aufzurufen – wirkt wie ein Versehen). (2) Die
 * "Test"-Buttons bei SD8 ({@code testStart}/{@code testEnde}) sind hier funktionsfähig, da Vaadin (anders als das
 * {@code c:forEach} der JSF-Seite) pro Schleifendurchlauf korrektes Variablen-Capture hat.
 */
@Route(value = "baustein-programmierung", layout = MainLayout.class)
@PageTitle("Baustein-Programmierung - v5t11")
public class BausteinProgrammierungView extends VerticalLayout {

  @Inject
  Steuerung steuerung;

  @Inject
  BausteinConfigurationService bausteinConfigurationService;

  @Inject
  CurrentBausteinHolder currentBausteinHolder;

  @Inject
  Instance<Baustein> bausteinInstanzen;

  private List<Baustein> konfigurierteBausteine;
  private List<Baustein> neueBausteine;
  private List<Integer> busNummern;

  private Baustein currentBaustein;
  private ConfigurationRuntimeService configurationRuntimeService;
  private int busNr;
  private boolean busNrFixed;

  private final VerticalLayout content = new VerticalLayout();

  @PostConstruct
  void init() {
    setSizeFull();
    this.content.setSizeFull();

    this.konfigurierteBausteine = Stream.concat(this.steuerung.getBesetztmelder().stream(), this.steuerung.getFunktionsdecoder().stream())
      .filter(baustein -> baustein.getClass().getAnnotation(Konfigurierbar.class) != null)
      .collect(Collectors.toList());

    this.neueBausteine = this.bausteinInstanzen.stream()
      .map(Baustein::getClass)
      .map(ClassUtil::getProxiedClass)
      .map(BausteinProgrammierungView::createBaustein)
      .collect(Collectors.toCollection(ArrayList::new));

    this.busNummern = new ArrayList<>();
    for (int i = 0; i < this.steuerung.getZentrale().getBusAnzahl(); ++i) {
      this.busNummern.add(i);
    }

    add(this.content);
    showLanding();
  }

  private static Baustein createBaustein(Class<?> bausteinClass) {
    try {
      return (Baustein) bausteinClass.getDeclaredConstructor().newInstance();
    } catch (Exception e) {
      throw new IllegalArgumentException("Ungueltige Bausteinklasse", e);
    }
  }

  // ---------- Landing ----------

  private void showLanding() {
    this.currentBaustein = null;
    this.configurationRuntimeService = null;

    this.content.removeAll();

    TabSheet tabSheet = new TabSheet();
    tabSheet.setSizeFull();
    tabSheet.add("Vorkonfigurierte Bausteine", buildBausteinGrid(this.konfigurierteBausteine, true));
    tabSheet.add("Neue Bausteine", buildBausteinGrid(this.neueBausteine, false));

    this.content.add(tabSheet);
  }

  private Grid<Baustein> buildBausteinGrid(List<Baustein> bausteine, boolean mitAdresseUndOrt) {
    Grid<Baustein> grid = new Grid<>();
    grid.addColumn(Baustein::getLabel).setHeader(mitAdresseUndOrt ? "Id" : "Typ");
    if (mitAdresseUndOrt) {
      grid.addColumn(Baustein::getSimpleClassName).setHeader("Typ");
      grid.addColumn(Baustein::getAdresse).setHeader("Adresse");
      grid.addColumn(Baustein::getEinbauOrt).setHeader("Einbauort");
    }
    grid.setItems(bausteine);
    grid.addItemClickListener(event -> selectBaustein(event.getItem()));
    grid.setSizeFull();
    return grid;
  }

  private void selectBaustein(Baustein baustein) {
    this.steuerung.getZentrale().setGleisspannung(false);

    this.currentBaustein = baustein;
    this.currentBausteinHolder.setCurrentBaustein(baustein);

    int adr = baustein.getAdresse();
    this.busNr = Kanal.toBusNr(adr);
    this.busNrFixed = adr != 0;

    Class<?> bausteinClass = ClassUtil.getProxiedClass(baustein.getClass());
    try {
      Class<?> programmierfamilie = bausteinClass.getAnnotation(Konfigurierbar.class).programmierFamilie();
      if (programmierfamilie == Void.class) {
        programmierfamilie = bausteinClass;
      }
      this.configurationRuntimeService = CDI.current().select(ConfigurationRuntimeService.class, new Programmierfamilie.Literal(programmierfamilie)).get();
      this.configurationRuntimeService.saveProgKanalWerte();

      showOpenProgMode();
    } catch (Exception e) {
      String message = e.getMessage();
      if (message == null || message.isBlank()) {
        message = e.toString();
      }

      Notification notification = Notification.show(message, 5000, Notification.Position.BOTTOM_START);
      notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }
  }

  // ---------- Prog-Mode-Zwischenschritte ----------

  private void showOpenProgMode() {
    this.content.removeAll();

    this.content.add(new H3(this.configurationRuntimeService.getOpenProgModeMessage()));
    Button okButton = new Button("ok", event -> edit());
    Button abortButton = new Button("abbrechen", event -> abort());
    this.content.add(new HorizontalLayout(okButton, abortButton));
  }

  private void edit() {
    this.configurationRuntimeService.setBusNr(this.busNr);
    this.configurationRuntimeService.getRuntimeValues();

    showDeviceForm();
  }

  private void showCloseProgMode() {
    this.content.removeAll();

    this.content.add(new H3(this.configurationRuntimeService.getCloseProgModeMessage()));
    this.content.add(new Button("ok", event -> abort()));
  }

  private void abort() {
    this.configurationRuntimeService = null;
    showLanding();
  }

  // ---------- Geräteformular ----------

  private void showDeviceForm() {
    this.content.removeAll();
    this.content.add(buildBausteinFieldset());
    this.content.add(buildParameterFieldset());
  }

  private FieldSet buildBausteinFieldset() {
    FieldSet fieldset = new FieldSet();
    fieldset.setLegendText("Baustein");

    TextField typField = new TextField();
    typField.setValue(this.currentBaustein.getSimpleClassName());
    typField.setReadOnly(true);

    TextField idField = new TextField();
    idField.setValue(Objects.toString(this.currentBaustein.getId(), ""));
    idField.setReadOnly(true);

    TextField ortField = new TextField();
    ortField.setValue(Objects.toString(this.currentBaustein.getEinbauOrt(), ""));
    ortField.setReadOnly(true);

    ComboBox<Integer> busNrField = new ComboBox<>();
    busNrField.setItems(this.busNummern);
    busNrField.setValue(this.busNr);
    busNrField.setEnabled(!this.busNrFixed);
    busNrField.addValueChangeListener(event -> {
      if (event.isFromClient() && event.getValue() != null) {
        this.busNr = event.getValue();
      }
    });

    FormLayout form = newForm();
    form.addFormItem(typField, "Typ:");
    form.addFormItem(idField, "Id:");
    form.addFormItem(ortField, "Einbauort:");
    form.addFormItem(busNrField, "SX-Bus:");
    fieldset.add(form);

    return fieldset;
  }

  private FieldSet buildParameterFieldset() {
    FieldSet fieldset = new FieldSet();
    fieldset.setLegendText("Parameter");

    fieldset.add(buildDeviceForm());

    Button programButton = new Button("programmieren", event -> {
      this.configurationRuntimeService.program();
      showCloseProgMode();
    });
    Button abortButton = new Button("abbrechen", event -> showCloseProgMode());
    fieldset.add(new HorizontalLayout(programButton, abortButton));

    return fieldset;
  }

  private Component buildDeviceForm() {
    return switch (this.configurationRuntimeService.getConfiguration()) {
      case HEBM8ConfigurationAdapter c -> buildHEBM8Form(c);
      case BMMiba3ConfigurationAdapter c -> buildBMMiba3Form(c);
      case SXBM1ConfigurationAdapter c -> buildSXBM1Form(c);
      case Muet8kConfigurationAdapter c -> buildMuet8kForm(c);
      case Muet8iConfigurationAdapter c -> buildMuet8iForm(c);
      case VM5262ConfigurationAdapter c -> buildVM5262Form(c);
      case SD8ConfigurationAdapter c -> buildSD8Form(c);
      case STRFD1ConfigurationAdapter c -> buildSTRFD1Form(c);
      case SXSD1ConfigurationAdapter c -> buildSXSD1Form(c);
      case WDMiba3ConfigurationAdapter c -> buildWDMiba3Form(c);
      case WDMibaConfigurationAdapter c -> buildWDMibaForm(c);
      default -> throw new IllegalStateException("Kein Formular für " + this.configurationRuntimeService.getConfiguration().getClass());
    };
  }

  private FormLayout newForm() {
    FormLayout form = new FormLayout();
    form.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));
    return form;
  }

  // ---------- Geräte-Formulare (je eines pro ehemaligem xhtml-Fragment) ----------

  private FormLayout buildHEBM8Form(HEBM8ConfigurationAdapter c) {
    FormLayout form = newForm();
    ConfigurationFormFields.addAddressRow(form, c);
    ConfigurationFormFields.addIntRow(form, "Abfallverzögerung (ms, Vielfaches von 100):", c.getAbfallVerzoegerung());
    return form;
  }

  private FormLayout buildBMMiba3Form(BMMiba3ConfigurationAdapter c) {
    FormLayout form = newForm();
    ConfigurationFormFields.addAddressRow(form, c);
    ConfigurationFormFields.addIntRow(form, "Ansprechverzögerung:", c.getAnsprechVerzoegerung());
    ConfigurationFormFields.addIntRow(form, "Abfallverzögerung:", c.getAbfallVerzoegerung());
    ConfigurationFormFields.addEnumRow(form, "Zeittakt für Verzögerungen:", c.getZeittakt());
    ConfigurationFormFields.addEnumRow(form, "Belegtmeldung bei ZE Stopp:", c.getMeldungBeiZeStopp());
    ConfigurationFormFields.addEnumRow(form, "Belegtmeldung bei fehlendem Fahrstrom:", c.getMeldungBeiFehlendemFahrstrom());
    ConfigurationFormFields.addBooleanRow(form, "Meldungs-Negation:", c.getMeldungsNegation());
    ConfigurationFormFields.addEnumRow(form, "Meldungs-Speicherung:", c.getMeldungsSpeicherung());
    return form;
  }

  private FormLayout buildSXBM1Form(SXBM1ConfigurationAdapter c) {
    FormLayout form = newForm();
    ConfigurationFormFields.addAddressRow(form, c);
    ConfigurationFormFields.addIntRow(form, "Abfallverzögerung (ms, Vielfaches von 80):", c.getAbfallVerzoegerung());
    return form;
  }

  private FormLayout buildMuet8kForm(Muet8kConfigurationAdapter c) {
    FormLayout form = newForm();
    ConfigurationFormFields.addAddressRow(form, c);
    ConfigurationFormFields.addIntRow(form, "Abfallverzögerung (ms, Vielfaches von 80):", c.getAbfallVerzoegerung());
    return form;
  }

  private FormLayout buildMuet8iForm(Muet8iConfigurationAdapter c) {
    FormLayout form = newForm();
    ConfigurationFormFields.addAddressRow(form, c);
    ConfigurationFormFields.addIntRow(form, "Abfallverzögerung (ms, Vielfaches von 350):", c.getAbfallVerzoegerung());
    ConfigurationFormFields.addEnumRow(form, "Belegtmeldung bei ZE Stopp:", c.getMeldungBeiZeStopp());
    ConfigurationFormFields.addBooleanRow(form, "Meldungs-Negation:", c.getMeldungsNegation());
    return form;
  }

  private FormLayout buildVM5262Form(VM5262ConfigurationAdapter c) {
    FormLayout form = newForm();
    ConfigurationFormFields.addAddressRow(form, c);
    ConfigurationFormFields.addIntRow(form, "Ansprechverzögerung (ms, Vielfaches von 10):", c.getAnsprechVerzoegerung());
    ConfigurationFormFields.addIntRow(form, "Abfallverzögerung (ms, Vielfaches von 10):", c.getAbfallVerzoegerung());
    return form;
  }

  private FormLayout buildSTRFD1Form(STRFD1ConfigurationAdapter c) {
    FormLayout form = newForm();
    ConfigurationFormFields.addAddressRow(form, c);
    ConfigurationFormFields.addBooleanArrayRow(form, "Dauerausgang (0..7):", c.getDauer(), c::resetDauerToSoll);
    ConfigurationFormFields.addIntRow(form, "Impulsdauer (ms):", c.getImpulsDauer());
    return form;
  }

  private FormLayout buildSXSD1Form(SXSD1ConfigurationAdapter c) {
    FormLayout form = newForm();
    ConfigurationFormFields.addAddressRow(form, c);
    return form;
  }

  private FormLayout buildWDMiba3Form(WDMiba3ConfigurationAdapter c) {
    FormLayout form = newForm();
    ConfigurationFormFields.addAddressRow(form, c);
    ConfigurationFormFields.addBooleanArrayRow(form, "Dauerausgang (0..7):", c.getDauer(), c::resetDauerToSoll);
    ConfigurationFormFields.addIntRow(form, "Impulsdauer (ms):", c.getImpulsDauer());
    return form;
  }

  private FormLayout buildWDMibaForm(WDMibaConfigurationAdapter c) {
    FormLayout form = newForm();
    ConfigurationFormFields.addAddressRow(form, c);
    ConfigurationFormFields.addBooleanArrayRow(form, "Dauerausgang (0..7):", c.getDauer(), c::resetDauerToSoll);
    return form;
  }

  private Component buildSD8Form(SD8ConfigurationAdapter c) {
    SD8RuntimeService sd8Service = (SD8RuntimeService) this.configurationRuntimeService;

    VerticalLayout layout = new VerticalLayout();
    layout.setPadding(false);

    FormLayout headerForm = newForm();
    ConfigurationFormFields.addAddressRow(headerForm, c);
    ConfigurationFormFields.addIntRow(headerForm, "Abschaltzeit (in 100 ms, 0=nie):", c.getAbschaltZeit());
    layout.add(headerForm);

    TabSheet tabSheet = new TabSheet();
    for (ServoConfiguration servo : c.getServoConfiguration()) {
      tabSheet.add("Servo " + servo.getServoNummer(), buildServoForm(servo, sd8Service));
    }
    layout.add(tabSheet);

    return layout;
  }

  private FormLayout buildServoForm(ServoConfiguration servo, SD8RuntimeService sd8Service) {
    FormLayout form = newForm();
    ConfigurationFormFields.addIntRowWithButton(form, "Start:", servo.getStart(), "Test", () -> sd8Service.testStart(servo));
    ConfigurationFormFields.addIntRowWithButton(form, "Ende:", servo.getEnde(), "Test", () -> sd8Service.testEnde(servo));
    ConfigurationFormFields.addIntRow(form, "Geschwindigkeit:", servo.getGeschwindigkeit());
    ConfigurationFormFields.addIntRow(form, "Nachwippen am Start:", servo.getStartNachwippen());
    ConfigurationFormFields.addIntRow(form, "Nachwippen am Ende:", servo.getEndeNachwippen());
    return form;
  }
}
