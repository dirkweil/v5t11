package de.gedoplan.v5t11.betriebssteuerung.model;

import de.gedoplan.baselibs.utils.exception.BugException;
import de.gedoplan.v5t11.betriebssteuerung.entity.BausteinConfiguration;
import de.gedoplan.v5t11.betriebssteuerung.service.ConfigurationAdapter;
import de.gedoplan.v5t11.betriebssteuerung.service.ConfigurationRuntimeService;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.Baustein;

import java.io.Serializable;
import java.lang.reflect.Method;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Basisklasse für Presentation Models für die Programmierung von Bausteinen.
 *
 * @author dw
 */
public abstract class BausteinProgrammierungModel_XXX<A extends ConfigurationAdapter, S extends ConfigurationRuntimeService<A>> implements Serializable {
  @Inject
  BausteinProgrammierungModel bausteinProgrammierungModel;

  private Class<S> configurationRuntimeServiceClass;
  S configurationRuntimeService;

  BausteinConfiguration currentBausteinIstConfiguration;

  private Class<A> configurationAdapterClass;
  private A configurationAdapter;

  protected Log log = LogFactory.getLog(getClass());

  public BausteinProgrammierungModel_XXX(Class<A> configurationAdapterClass, Class<S> configurationRuntimeServiceClass) {
    this.configurationAdapterClass = configurationAdapterClass;
    this.configurationRuntimeServiceClass = configurationRuntimeServiceClass;
  }

  /**
   *
   */
  protected BausteinProgrammierungModel_XXX() {
  }

  @SuppressWarnings({ "unchecked" })
  @PostConstruct
  private void init() {
    // Manuelle Injektion in this.configurationRuntimeService
    this.configurationRuntimeService = CDI.current().select(this.configurationRuntimeServiceClass).get();

    // Bausteinkonfiguration (Soll + Ist) erstellen
    this.currentBausteinIstConfiguration = new BausteinConfiguration(this.bausteinProgrammierungModel.getCurrentBaustein().getId());

    try {
      Method createInstanceMethod = this.configurationAdapterClass.getMethod("createInstance", Baustein.class, BausteinConfiguration.class, BausteinConfiguration.class);
      this.configurationAdapter = (A) createInstanceMethod.invoke(
          null, this.bausteinProgrammierungModel.getCurrentBaustein(), this.currentBausteinIstConfiguration, this.bausteinProgrammierungModel.getCurrentBausteinSollConfiguration());
    } catch (Exception e) {
      throw new BugException("Cannot create configuration adapter", e);
    }

    // Ist-Konfiguration aus Baustein einlesen
    this.configurationRuntimeService.getRuntimeValues(this.configurationAdapter);

    if (this.log.isDebugEnabled()) {
      this.log.debug("Init: configurationRuntimeService=" + this.configurationRuntimeService + ", configurationAdapter=" + this.configurationAdapter);
    }
  }

  /**
   * Wert liefern: {@link #configurationAdapter}.
   *
   * @return Wert
   */
  public A getConfiguration() {
    return this.configurationAdapter;
  }

  public void program() {
    this.configurationRuntimeService.setRuntimeValues(this.configurationAdapter);

    this.bausteinProgrammierungModel.setCurrentBausteinIstConfiguration(this.currentBausteinIstConfiguration);
  }
}
