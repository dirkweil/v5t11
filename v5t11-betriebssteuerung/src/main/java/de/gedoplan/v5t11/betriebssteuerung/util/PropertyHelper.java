package de.gedoplan.v5t11.betriebssteuerung.util;

import de.gedoplan.v5t11.betriebssteuerung.entity.BausteinConfiguration;

public final class PropertyHelper
{
  public static int getIntProperty(BausteinConfiguration bausteinConfiguration, String key, int defaultValue)
  {
    String stringValue = bausteinConfiguration.getProperties().get(key);
    if (stringValue != null)
    {
      try
      {
        return Integer.parseInt(stringValue);
      }
      catch (Exception ex)
      {
        // ignore
      }
    }
    return defaultValue;
  }

  public static void setIntProperty(BausteinConfiguration bausteinConfiguration, String key, int value)
  {
    bausteinConfiguration.getProperties().put(key, Integer.toString(value));
  }

  public static boolean getBooleanProperty(BausteinConfiguration bausteinConfiguration, String key, boolean defaultValue)
  {
    String stringValue = bausteinConfiguration.getProperties().get(key);
    if (stringValue != null)
    {
      try
      {
        return Boolean.parseBoolean(stringValue);
      }
      catch (Exception ex)
      {
        // ignore
      }
    }
    return defaultValue;
  }

  public static void setBooleanProperty(BausteinConfiguration bausteinConfiguration, String key, boolean value)
  {
    bausteinConfiguration.getProperties().put(key, Boolean.toString(value));
  }

  public static <E extends Enum<E>> E getEnumProperty(BausteinConfiguration bausteinConfiguration, String key, E defaultValue, Class<E> enumClass)
  {
    String stringValue = bausteinConfiguration.getProperties().get(key);
    if (stringValue != null)
    {
      try
      {
        return Enum.valueOf(enumClass, stringValue);
      }
      catch (Exception ex)
      {
        // ignore
      }
    }

    return defaultValue;
  }

  public static <E extends Enum<E>> void setEnumProperty(BausteinConfiguration bausteinConfiguration, String key, E value)
  {
    bausteinConfiguration.getProperties().put(key, value.toString());
  }

}
