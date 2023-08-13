package de.gedoplan.v5t11.util.domain.attribute;

import java.util.HashMap;
import java.util.Map;

import jakarta.json.bind.adapter.JsonbAdapter;
import jakarta.json.bind.annotation.JsonbTypeAdapter;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Schalterstellung.
 *
 * @author dw
 */
@JsonbTypeAdapter(SchalterStellung.Adapter4Json.class)
public enum SchalterStellung {
  // Achtung: Die Reihenfolge der beiden Konstanten muss erhalten bleiben, da die Ordinalwerte als Stellungswerte genutzt werden!
  AUS("0"),
  EIN("1");

  private String string;

  private static Map<String, SchalterStellung> lookup = new HashMap<>();

  static {
    for (SchalterStellung s : SchalterStellung.values()) {
      lookup.put(s.string, s);
    }
  }

  private SchalterStellung(String string) {
    this.string = string;
  }

  @Override
  public String toString() {
    return this.string;
  }

  public static SchalterStellung fromString(String string) {
    return fromString(string, false);
  }

  public static SchalterStellung fromString(String string, boolean strict) {
    var stellung = lookup.get(string);
    if (stellung == null) {
      stellung = valueOf(string);
    }
    if (stellung == null && strict) {
      throw new IllegalArgumentException("Unbekannte SchalterStellung: " + string);
    }
    return stellung;
  }

  public static class Adapter4Json implements JsonbAdapter<SchalterStellung, String> {

    @Override
    public String adaptToJson(SchalterStellung obj) throws Exception {
      return obj == null ? null : obj.toString();
    }

    @Override
    public SchalterStellung adaptFromJson(String s) throws Exception {
      return s == null ? null : fromString(s, true);
    }
  }

  // TODO autoApply wirkt nicht; warum?
  @Converter(autoApply = true)
  public static class Adapter4Jpa implements AttributeConverter<SchalterStellung, String> {

    @Override
    public String convertToDatabaseColumn(SchalterStellung attribute) {
      return attribute == null ? null : attribute.toString();
    }

    @Override
    public SchalterStellung convertToEntityAttribute(String dbData) {
      return dbData == null ? null : fromString(dbData);
    }

  }
}
