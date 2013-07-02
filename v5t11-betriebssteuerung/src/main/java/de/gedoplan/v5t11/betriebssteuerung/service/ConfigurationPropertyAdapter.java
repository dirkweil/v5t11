package de.gedoplan.v5t11.betriebssteuerung.service;

import java.lang.reflect.Method;
import java.util.Map;

public class ConfigurationPropertyAdapter<T>
{
  private Map<String, String> istProperties;
  private String              key;
  private T                   defaultValue;
  private Map<String, String> sollProperties;
  private Class<T>            clazz;
  private Method              valueOfMethod;
  private Method              valuesMethod;
  private boolean             dirty;

  /**
   * @param istProperties
   * @param key
   * @param defaultValue
   * @param sollProperties
   */
  public ConfigurationPropertyAdapter(Map<String, String> istProperties, String key, T defaultValue, Map<String, String> sollProperties, Class<T> clazz)
  {
    this.istProperties = istProperties;
    this.key = key;
    this.defaultValue = defaultValue;
    this.sollProperties = sollProperties;
    this.clazz = clazz;

    try
    {
      this.valueOfMethod = clazz.getMethod("valueOf", String.class);
    }
    catch (NoSuchMethodException | SecurityException e)
    {
      throw new IllegalArgumentException("Parameter class " + clazz.getName() + " offers no valueOf(String) method", e);
    }

    try
    {
      this.valuesMethod = clazz.getMethod("values", (Class<?>[]) null);
    }
    catch (NoSuchMethodException | SecurityException e)
    {
      // ignore
    }
  }

  public T getSoll()
  {
    return getValueFrom(this.sollProperties);
  }

  public T getIst()
  {
    return getValueFrom(this.istProperties);
  }

  public void setIst(T value)
  {
    String valueAsString = value.toString();
    String old = this.istProperties.put(this.key, valueAsString);
    if (!value.equals(old))
    {
      this.dirty = true;
    }
  }

  public void resetToSoll()
  {
    setIst(getSoll());
  }

  /**
   * Wert liefern: {@link #dirty}.
   * 
   * @return Wert
   */
  public boolean isDirty()
  {
    return this.dirty;
  }

  public void clearDirty()
  {
    this.dirty = false;
  }

  @SuppressWarnings("unchecked")
  public T[] getValues()
  {
    try
    {
      return (T[]) this.valuesMethod.invoke(null, (Object[]) null);
    }
    catch (Exception e)
    {
      throw new IllegalStateException("Parameter class " + this.clazz.getName() + " offers no values method", e);
    }
  }

  @SuppressWarnings("unchecked")
  private T getValueFrom(Map<String, String> properties)
  {
    String valueString = properties.get(this.key);
    if (valueString != null)
    {
      try
      {
        return (T) this.valueOfMethod.invoke(null, valueString);
      }
      catch (Exception e)
      {
        // ignore
      }
    }

    return this.defaultValue;
  }
}
