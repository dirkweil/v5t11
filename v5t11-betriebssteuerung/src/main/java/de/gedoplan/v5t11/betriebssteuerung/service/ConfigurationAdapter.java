package de.gedoplan.v5t11.betriebssteuerung.service;

import de.gedoplan.v5t11.betriebssteuerung.entity.BausteinConfiguration;

import java.lang.reflect.Method;
import java.util.Map;

import lombok.Getter;

/**
 * Basisklasse für Konfigurations-Adapter.
 *
 * Objekte dieses Typs enthalten die Soll- und Ist-Konfiguration eines
 * Bausteins. Die Ist-Konfiguration ist veränderlich.
 * Diese Basisklasse bietet Methoden zum Zugriff auf die Adresse an. Ein
 * zugeordnetes Dirty-Flag zeigt an, ob die Ist-Adresse verändert wurde.
 *
 * Konkrete Bausteinkonfigurationen enthalten weitere Konfigurationswerte als
 * Einträge in einem Map-Attribut. Die Helferklasse
 * {@link ConfigurationPropertyAdapter}
 * ermöglicht einfache Implementierung von Zugriffsmethoden für die spezifischen
 * Einstellwerte.
 *
 * @author dw
 *
 */
@Getter
public abstract class ConfigurationAdapter {
  protected BausteinConfiguration sollConfiguration;
  protected BausteinConfiguration istConfiguration;
  protected boolean adresseDirty;

  /**
   * @param istConfiguration
   * @param sollConfiguration
   */
  public ConfigurationAdapter(BausteinConfiguration istConfiguration, BausteinConfiguration sollConfiguration) {
    this.istConfiguration = istConfiguration;
    this.sollConfiguration = sollConfiguration;
  }

  public int getAdresseSoll() {
    return this.sollConfiguration.getAdresse();
  }

  public int getAdresseIst() {
    return this.istConfiguration.getAdresse();
  }

  public void setAdresseIst(int adresse) {
    if (adresse != this.istConfiguration.getAdresse()) {
      this.istConfiguration.setAdresse(adresse);
      this.adresseDirty = true;
    }
  }

  public void adresseResetToSoll() {
    setAdresseIst(getAdresseSoll());
  }

  /**
   * Wert löschen: {@link #adresseDirty}.
   */
  public void clearAdresseDirty() {
    this.adresseDirty = false;
  }

  /**
   * Helferklasse zur Implementierung der Zugriffe auf einen spezifischen
   * Konfigurationswert in von {@link ConfigurationAdapter} abgeleiteten
   * Klassen.
   *
   * @author dw
   *
   * @param <T>
   *          Typ des KJonfigurationswertes
   */
  public static class ConfigurationPropertyAdapter<T> {
    private Map<String, String> istProperties;
    private String key;
    private T defaultValue;
    private Map<String, String> sollProperties;
    private Class<T> clazz;
    private Method valueOfMethod;
    private Method valuesMethod;
    private boolean dirty;

    /**
     * Property-Adapter stellen.
     *
     * @param istProperties
     *          Map, in dem der Ist-Wert der Property abgelegt wird.
     * @param key
     *          Key für die Ablage im Ist- und Soll-Map.
     * @param defaultValue
     *          Default-Wert.
     * @param sollProperties
     *          Map, in dem der Soll-Wert der Property abgelegt wird.
     */
    public ConfigurationPropertyAdapter(Map<String, String> istProperties, String key, T defaultValue, Map<String, String> sollProperties, Class<T> clazz) {
      this.istProperties = istProperties;
      this.key = key;
      this.defaultValue = defaultValue;
      this.sollProperties = sollProperties;
      this.clazz = clazz;

      try {
        this.valueOfMethod = clazz.getMethod("valueOf", String.class);
      } catch (NoSuchMethodException | SecurityException e) {
        throw new IllegalArgumentException("Parameter class " + clazz.getName() + " offers no valueOf(String) method", e);
      }

      try {
        this.valuesMethod = clazz.getMethod("values", (Class<?>[]) null);
      } catch (NoSuchMethodException | SecurityException e) {
        // ignore
      }
    }

    public T getSoll() {
      return getValueFrom(this.sollProperties);
    }

    public T getIst() {
      return getValueFrom(this.istProperties);
    }

    public void setIst(T value) {
      String valueAsString = value.toString();
      String old = this.istProperties.put(this.key, valueAsString);
      if (!value.equals(old)) {
        this.dirty = true;
      }
    }

    public void resetToSoll() {
      setIst(getSoll());
    }

    public boolean isDirty() {
      return this.dirty;
    }

    public void clearDirty() {
      this.dirty = false;
    }

    /**
     * Verfügbare Werte für die Property liefern.
     *
     * Ist nur bei enums verfügbar.
     *
     * @return verfügbare Werte
     */
    @SuppressWarnings("unchecked")
    public T[] getValues() {
      try {
        return (T[]) this.valuesMethod.invoke(null, (Object[]) null);
      } catch (Exception e) {
        throw new IllegalStateException("Parameter class " + this.clazz.getName() + " offers no values method", e);
      }
    }

    @SuppressWarnings("unchecked")
    private T getValueFrom(Map<String, String> properties) {
      String valueString = properties.get(this.key);
      if (valueString != null) {
        try {
          return (T) this.valueOfMethod.invoke(null, valueString);
        } catch (Exception e) {
          // ignore
        }
      }

      return this.defaultValue;
    }
  }
}
