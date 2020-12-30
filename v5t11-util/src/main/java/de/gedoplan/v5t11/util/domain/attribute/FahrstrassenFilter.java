package de.gedoplan.v5t11.util.domain.attribute;

import java.util.HashMap;
import java.util.Map;

import javax.json.bind.adapter.JsonbAdapter;
import javax.json.bind.annotation.JsonbTypeAdapter;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@JsonbTypeAdapter(FahrstrassenFilter.Adapter4Json.class)
public enum FahrstrassenFilter {
  FREI("F"),
  RESERVIERT("R"),
  NON_COMBI("N");

  private String string;

  private static Map<String, FahrstrassenFilter> lookup = new HashMap<>();
  static {
    for (FahrstrassenFilter s : FahrstrassenFilter.values()) {
      lookup.put(s.string, s);
    }
  }

  private FahrstrassenFilter(String string) {
    this.string = string;
  }

  @Override
  public String toString() {
    return this.string;
  }

  public static FahrstrassenFilter fromString(String string) {
    return fromString(string, false);
  }

  public static FahrstrassenFilter fromString(String string, boolean strict) {
    var stellung = lookup.get(string);
    if (stellung == null) {
      stellung = valueOf(string);
    }
    if (stellung == null && strict) {
      throw new IllegalArgumentException("Unbekannter FahrstrassenFilter: " + string);
    }
    return stellung;
  }

  public static class Adapter4Json implements JsonbAdapter<FahrstrassenFilter, String> {

    @Override
    public String adaptToJson(FahrstrassenFilter obj) throws Exception {
      return obj == null ? null : obj.toString();
    }

    @Override
    public FahrstrassenFilter adaptFromJson(String s) throws Exception {
      return s == null ? null : fromString(s, true);
    }
  }

  // TODO autoApply wirkt nicht; warum?
  @Converter(autoApply = true)
  public static class Adapter4Jpa implements AttributeConverter<FahrstrassenFilter, String> {

    @Override
    public String convertToDatabaseColumn(FahrstrassenFilter attribute) {
      return attribute == null ? null : attribute.toString();
    }

    @Override
    public FahrstrassenFilter convertToEntityAttribute(String dbData) {
      return dbData == null ? null : fromString(dbData);
    }

  }

}
