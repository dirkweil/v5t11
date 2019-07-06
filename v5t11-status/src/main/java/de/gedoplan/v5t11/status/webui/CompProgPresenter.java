package de.gedoplan.v5t11.status.webui;

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

import javax.annotation.PostConstruct;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Model;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.CDI;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.apache.commons.logging.Log;

import lombok.Getter;

/**
 * Presentation Model für die Bausteinprogrammierung.
 *
 * @author dw
 */
@Model
@SessionScoped
public class CompProgPresenter implements Serializable {
  @Inject
  Steuerung steuerung;

  @Inject
  Instance<Baustein> bausteinInstanzen;

  @Inject
  BausteinConfigurationService bausteinConfigurationService;

  @Inject
  Conversation conversation;

  @Inject
  Log log;

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
  private Baustein currentBaustein;

  @Getter
  private ConfigurationRuntimeService configurationRuntimeService;

  @Getter
  private ConfigurationAdapter configuration;

  private String progViewOutcome;

  /**
   * Aktuellen Baustein wählen und Programm-Session beginnen.
   *
   * @param baustein
   *        Wert
   */
  public String selectBaustein(Baustein baustein) {
    this.steuerung.getZentrale().setGleisspannung(false);

    this.currentBaustein = baustein;
    if (this.currentBaustein == null) {
      return null;
    }

    if (this.conversation.isTransient()) {
      this.conversation.begin();
    }

    try {
      // "Injektion" des passenden ConfigurationRuntimeService per API
      Class<? extends Baustein> bausteinClass = this.currentBaustein.getClass();
      Class<?> programmierfamilie = bausteinClass.getAnnotation(Konfigurierbar.class).programmierFamilie();
      if (programmierfamilie == Void.class) {
        programmierfamilie = bausteinClass;
      }
      this.configurationRuntimeService = CDI.current().select(ConfigurationRuntimeService.class, new Programmierfamilie.Literal(programmierfamilie)).get();
      this.progViewOutcome = "/view/compProg_" + programmierfamilie.getSimpleName() + "?faces-redirect=true";

      this.configurationRuntimeService.saveProgKanalWerte();

      return "openProgMode";
    }
    catch (Exception e) {
      this.log.error("Kann ConfigurationRuntimeService nicht erzeugen", e);

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
    return this.currentBaustein == null ? null : this.currentBaustein.isBusBaustein() ? "Bitte Programmiertaster am Baustein drücken!" : "Bitte Fahrzeug alleine auf das Programmiergleis stellen!";
  }

  public String getCloseProgModeMessage() {
    return this.currentBaustein == null ? null
        : this.currentBaustein.isBusBaustein() ? "Bitte Programmiertaster am Baustein drücken!" : "Fahrzeug kann vom Programmiergleis entfernt werden.";
  }

  /**
   * Sollkonfiguration zum aktuellen Baustein besorgen und Einstelldialog
   * beginnen.
   *
   * @return Outcome
   */
  public String edit() {
    if (this.currentBaustein != null) {

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
  private void init() {
    this.konfigurierteBausteine = // Stream.concat(
        Stream.concat(
            this.steuerung.getBesetztmelder().stream(),
            this.steuerung.getFunktionsdecoder().stream())// ,
            // this.steuerung.getLokdecoder().stream())
            .filter(fd -> fd.getClass().getAnnotation(Konfigurierbar.class) != null)
            .collect(Collectors.toList());

    this.neueBausteine = new ArrayList<>();
    for (Baustein b : this.bausteinInstanzen) {
      this.neueBausteine.add(b);
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
    if (!this.conversation.isTransient()) {
      this.conversation.end();
    }

    return "compProg";
  }
}
