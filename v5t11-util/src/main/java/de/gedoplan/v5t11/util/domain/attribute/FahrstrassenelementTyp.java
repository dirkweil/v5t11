package de.gedoplan.v5t11.util.domain.attribute;

import java.util.HashMap;
import java.util.Map;

import jakarta.json.bind.adapter.JsonbAdapter;
import jakarta.json.bind.annotation.JsonbTypeAdapter;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@JsonbTypeAdapter(FahrstrassenelementTyp.Adapter4Json.class)
public enum FahrstrassenelementTyp {
  GLEIS("G"),
  SIGNAL("S"),
  WEICHE("W");

  private String string;

  private static Map<String, FahrstrassenelementTyp> lookup = new HashMap<>();

  static {
    for (FahrstrassenelementTyp s : FahrstrassenelementTyp.values()) {
      lookup.put(s.string, s);
    }
  }

  private FahrstrassenelementTyp(String string) {
    this.string = string;
  }

  @Override
  public String toString() {
    return this.string;
  }

  public static FahrstrassenelementTyp fromString(String string) {
    return fromString(string, false);
  }

  public static FahrstrassenelementTyp fromString(String string, boolean strict) {
    var stellung = lookup.get(string);
    if (stellung == null) {
      stellung = valueOf(string);
    }
    if (stellung == null && strict) {
      throw new IllegalArgumentException("Unbekannter FahrstrassenReservierungsTyp: " + string);
    }
    return stellung;
  }

  public static class Adapter4Json implements JsonbAdapter<FahrstrassenelementTyp, String> {

    @Override
    public String adaptToJson(FahrstrassenelementTyp obj) throws Exception {
      return obj == null ? null : obj.toString();
    }

    @Override
    public FahrstrassenelementTyp adaptFromJson(String s) throws Exception {
      return s == null ? null : fromString(s, true);
    }
  }

  // TODO autoApply wirkt nicht; warum?
  @Converter(autoApply = true)
  public static class Adapter4Jpa implements AttributeConverter<FahrstrassenelementTyp, String> {

    @Override
    public String convertToDatabaseColumn(FahrstrassenelementTyp attribute) {
      return attribute == null ? null : attribute.toString();
    }

    @Override
    public FahrstrassenelementTyp convertToEntityAttribute(String dbData) {
      return dbData == null ? null : fromString(dbData);
    }

  }
}
