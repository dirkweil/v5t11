package de.gedoplan.v5t11.betriebssteuerung.service;

import de.gedoplan.baselibs.utils.exception.BugException;
import de.gedoplan.v5t11.betriebssteuerung.entity.BausteinConfiguration;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

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

  protected Map<String, String> istProperties;
  protected Map<String, String> sollProperties;

  /**
   * @param istConfiguration
   * @param sollConfiguration
   */
  public ConfigurationAdapter(BausteinConfiguration istConfiguration, BausteinConfiguration sollConfiguration) {
    this.istConfiguration = istConfiguration;
    this.sollConfiguration = sollConfiguration;

    this.istProperties = this.istConfiguration.getProperties();
    this.sollProperties = null;
    if (this.sollConfiguration != null) {
      this.sollProperties = this.sollConfiguration.getProperties();
    }
    if (this.sollProperties == null) {
      this.sollProperties = Collections.emptyMap();
    }

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

  public void setAdresseIst(int adresse, boolean dirty) {
    this.istConfiguration.setAdresse(adresse);
    this.adresseDirty = dirty;
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

    private boolean dirty;

    private Class<T> clazz;
    private Function<String, T> valueOf;
    private Supplier<T[]> values;

    /**
     * Property-Adapter erstellen.
     *
     * @param istProperties
     *          Map, in dem der Ist-Wert der Property abgelegt wird.
     * @param key
     *          Key für die Ablage im Ist- und Soll-Map.
     * @param defaultValue
     *          Default-Wert.
     * @param sollProperties
     *          Map, in dem der Soll-Wert der Property abgelegt wird.
     * @param clazz
     *          Property-Typ.
     * @param valueOf
     *          Funktion, die einen String auf einen Property-Wert abbildet.
     * @param values
     *          Funktion, die die verfügbaren Property-WErte liefert oder
     *          <code>null</code>, falls unlimitiert.
     */
    public ConfigurationPropertyAdapter(Map<String, String> istProperties, String key, T defaultValue, Map<String, String> sollProperties, Class<T> clazz, Function<String, T> valueOf,
        Supplier<T[]> values) {
      this.istProperties = istProperties;
      this.key = key;
      this.defaultValue = defaultValue;
      this.sollProperties = sollProperties;
      this.clazz = clazz;
      this.valueOf = valueOf;
      this.values = values;
    }

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
      this(istProperties, key, defaultValue, sollProperties, clazz, getValueOfFunction(clazz), getValuesFunction(clazz));
    }

    private static <T> Function<String, T> getValueOfFunction(Class<T> clazz) {
      try {
        Method valueOfMethod = clazz.getMethod("valueOf", String.class);
        return new Function<String, T>() {

          @SuppressWarnings("unchecked")
          @Override
          public T apply(String valueString) {
            try {
              return (T) valueOfMethod.invoke(null, valueString);
            } catch (Exception e) {
              throw new BugException("Cannot get property value", e);
            }
          }

        };
      } catch (NoSuchMethodException e) {
        throw new IllegalArgumentException("Parameter class " + clazz.getName() + " offers no valueOf(String) method", e);
      }
    }

    private static <T> Supplier<T[]> getValuesFunction(Class<T> clazz) {
      try {
        Method valuesMethod = clazz.getMethod("values", (Class<?>[]) null);
        return new Supplier<T[]>() {

          @SuppressWarnings("unchecked")
          @Override
          public T[] get() {
            try {
              return (T[]) valuesMethod.invoke(null, (Object[]) null);
            } catch (Exception e) {
              throw new BugException("Cannot get available property values", e);
            }
          }

        };
      } catch (NoSuchMethodException e) {
        return null;
      }
    }

    public T getSoll() {
      return getValueFrom(this.sollProperties);
    }

    public void setSoll(T value) {
      String valueAsString = value.toString();
      this.sollProperties.put(this.key, valueAsString);
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

    public void setIst(T value, boolean dirty) {
      String valueAsString = value.toString();
      this.istProperties.put(this.key, valueAsString);
      this.dirty = dirty;
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
    public T[] getValues() {
      if (this.values == null) {
        throw new IllegalStateException("Parameter class " + this.clazz.getName() + " offers no values method");
      }

      return this.values.get();
    }

    private T getValueFrom(Map<String, String> properties) {
      String valueString = properties.get(this.key);
      if (valueString != null) {
        return this.valueOf.apply(valueString);
      }

      return this.defaultValue;
    }
  }
}
