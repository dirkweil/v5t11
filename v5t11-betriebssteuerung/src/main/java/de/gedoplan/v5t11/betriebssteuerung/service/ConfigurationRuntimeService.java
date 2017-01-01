package de.gedoplan.v5t11.betriebssteuerung.service;

import de.gedoplan.v5t11.betriebssteuerung.messaging.SelectrixGateway;

import javax.inject.Inject;

/**
 * Basisklasse für Services, die die Konfiguration eines Bausteins lesen und schreiben.
 *
 * @author dw
 *
 * @param <T>
 *          Typ des Konfigurations-Adapters
 */
public abstract class ConfigurationRuntimeService<T extends ConfigurationAdapter> {
  @Inject
  protected SelectrixGateway selectrixGateway;

  public abstract void getRuntimeValues(T configuration);

  public abstract void setRuntimeValues(T configuration);
}
