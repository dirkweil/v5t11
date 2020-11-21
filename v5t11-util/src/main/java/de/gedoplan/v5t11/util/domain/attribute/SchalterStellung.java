package de.gedoplan.v5t11.util.domain.attribute;

import java.util.HashMap;
import java.util.Map;

import javax.json.bind.adapter.JsonbAdapter;
import javax.json.bind.annotation.JsonbTypeAdapter;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import lombok.Getter;

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

  @Getter
  private String code;

  private static Map<String, SchalterStellung> lookup = new HashMap<>();
  static {
    for (SchalterStellung s : SchalterStellung.values()) {
      lookup.put(s.code, s);
    }
  }

  private SchalterStellung(String code) {
    this.code = code;
  }

  public static SchalterStellung ofCode(String code) {
    return ofCode(code, false);
  }

  public static SchalterStellung ofCode(String code, boolean strict) {
    var stellung = lookup.get(code);
    if (stellung == null) {
      stellung = valueOf(code);
    }
    if (stellung == null && strict) {
      throw new IllegalArgumentException("Unbekannte SchalterStellung: " + code);
    }
    return stellung;
  }

  public static class Adapter4Json implements JsonbAdapter<SchalterStellung, String> {

    @Override
    public String adaptToJson(SchalterStellung obj) throws Exception {
      return obj == null ? null : obj.getCode();
    }

    @Override
    public SchalterStellung adaptFromJson(String s) throws Exception {
      return s == null ? null : ofCode(s, true);
    }
  }

  // TODO autoApply wirkt nicht; warum?
  @Converter(autoApply = true)
  public static class Adapter4Jpa implements AttributeConverter<SchalterStellung, String> {

    @Override
    public String convertToDatabaseColumn(SchalterStellung attribute) {
      return attribute == null ? null : attribute.getCode();
    }

    @Override
    public SchalterStellung convertToEntityAttribute(String dbData) {
      return dbData == null ? null : ofCode(dbData);
    }

  }
}
