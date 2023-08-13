package de.gedoplan.v5t11.status.webui;

import de.gedoplan.baselibs.utils.util.ClassUtil;
import de.gedoplan.v5t11.status.entity.Kanal;
import de.gedoplan.v5t11.status.entity.Steuerung;
import de.gedoplan.v5t11.status.entity.baustein.Baustein;
import de.gedoplan.v5t11.status.entity.baustein.Konfigurierbar;
import de.gedoplan.v5t11.status.service.BausteinConfigurationService;
import de.gedoplan.v5t11.status.service.ConfigurationAdapter;
import de.gedoplan.v5t11.status.service.ConfigurationRuntimeService;
import de.gedoplan.v5t11.status.service.Current;
import de.gedoplan.v5t11.status.service.Programmierfamilie;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.jboss.logging.Logger;

import lombok.Getter;
import lombok.Setter;

/**
 * Presentation Model für die Bausteinprogrammierung.
 *
 * @author dw
 */
@Named
@SessionScoped
public class BausteinProgrammierungPresenter implements Serializable {
  @Inject
  Steuerung steuerung;

  @Inject
  BausteinConfigurationService bausteinConfigurationService;

  @Inject
  Logger log;

  // @PostConstruct
  // void postConstruct() {
  // if (this.log.isDebugEnabled()) {
  // this.log.debug("configurationRuntimeServices: ");
  // this.configurationRuntimeServices.forEach(crs -> this.log.debug(" " + crs.getClass()));
  // }
  // }

  /**
   * Liste aller konfigurierter Bausteine.
   */
  @Getter
  private List<Baustein> konfigurierteBausteine;

  /**
   * Liste aller nicht-konfigurierter Bausteine.
   */
  @Getter
  private List<Baustein> neueBausteine;

  /**
   * Aktueller Baustein.
   */
  @Produces
  @Current
  @Getter
  Baustein currentBaustein;

  @Getter
  private ConfigurationRuntimeService configurationRuntimeService;

  @Getter
  private ConfigurationAdapter configuration;

  private String progViewOutcome;

  @Getter
  @Setter
  private int busNr;

  @Getter
  private boolean busNrFixed;

  @Getter
  private List<Integer> busNummern;

  /**
   * Aktuellen Baustein wählen und Programm-Session beginnen.
   *
   * @param baustein Wert
   */
  public String selectBaustein(Baustein baustein) {
    this.steuerung.getZentrale().setGleisspannung(false);

    this.currentBaustein = baustein;
    if (this.currentBaustein == null) {
      return null;
    }

    // Bus-Nr aus Adresse entnehmen
    int adr = this.currentBaustein.getAdresse();
    this.busNr = Kanal.toBusNr(adr);

    // Bus-Nr ist fixiert, wenn der Baustein bereits eine Adresse hat
    this.busNrFixed = adr != 0;

    Class<?> bausteinClass = ClassUtil.getProxiedClass(this.currentBaustein.getClass());
    try {
      // "Injektion" des passenden ConfigurationRuntimeService per API
      Class<?> programmierfamilie = bausteinClass.getAnnotation(Konfigurierbar.class).programmierFamilie();
      if (programmierfamilie == Void.class) {
        programmierfamilie = bausteinClass;
      }
      this.configurationRuntimeService = CDI.current().select(ConfigurationRuntimeService.class, new Programmierfamilie.Literal(programmierfamilie)).get();
      this.progViewOutcome = "/view/bausteinProgrammierung_" + programmierfamilie.getSimpleName() + "?faces-redirect=true";

      this.configurationRuntimeService.saveProgKanalWerte();

      return "/view/bausteinProgrammierung_openProgMode.xhtml";
    } catch (Exception e) {
      this.log.errorf("Kann ConfigurationRuntimeService für %s nicht erzeugen: %s", bausteinClass, e);

      String message = e.getMessage();
      if (message == null || message.trim().isEmpty()) {
        message = e.toString();
      }

      FacesMessage facesMessage = new FacesMessage(message);
      facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
      FacesContext.getCurrentInstance().addMessage(null, facesMessage);
    }

    return null;
  }

  public String getOpenProgModeMessage() {
    return this.currentBaustein == null ? null : this.configurationRuntimeService.getOpenProgModeMessage();
  }

  public String getCloseProgModeMessage() {
    return this.currentBaustein == null ? null : this.configurationRuntimeService.getCloseProgModeMessage();
  }

  /**
   * Sollkonfiguration zum aktuellen Baustein besorgen und Einstelldialog
   * beginnen.
   *
   * @return Outcome
   */
  public String edit() {
    if (this.currentBaustein != null) {

      this.busNrFixed = true;
      this.configurationRuntimeService.setBusNr(this.busNr);

      // Aktuelle Ist-Werte holen
      this.configurationRuntimeService.getRuntimeValues();

      // Config-Werte für Zugriff aus Webseite bereitstellen
      this.configuration = this.configurationRuntimeService.getConfiguration();

      // In passende View navigieren
      return this.progViewOutcome;
    }

    return null;
  }

  @PostConstruct
  void init() {
    this.konfigurierteBausteine = // Stream.concat(
      Stream.concat(
          this.steuerung.getBesetztmelder().stream(),
          this.steuerung.getFunktionsdecoder().stream())// ,
        // this.steuerung.getLokdecoder().stream())
        .filter(fd -> fd.getClass().getAnnotation(Konfigurierbar.class) != null)
        .collect(Collectors.toList());

    this.busNummern = new ArrayList<>();
    for (int i = 0; i < this.steuerung.getZentrale().getBusAnzahl(); ++i) {
      this.busNummern.add(i);
    }
  }

  @Inject
  void init(Instance<Baustein> bausteinInstanzen) {
    this.neueBausteine = bausteinInstanzen.stream()
      .map(Baustein::getClass)
      .map(ClassUtil::getProxiedClass)
      .map(BausteinProgrammierungPresenter::createBaustein)
      .collect(Collectors.toCollection(ArrayList::new));
  }

  private static Baustein createBaustein(Class<?> bausteinClass) {
    try {
      return (Baustein) bausteinClass.getDeclaredConstructor().newInstance();
    } catch (Exception e) {
      throw new IllegalArgumentException("Ungueltige Bausteinklasse", e);
    }
  }

  public void program() {
    this.configurationRuntimeService.program();
  }

  /**
   * Programmier-Session beenden.
   *
   * @return Outcome
   */
  public String abort() {
    this.configurationRuntimeService = null;

    return "bausteinProgrammierung";
  }
}
