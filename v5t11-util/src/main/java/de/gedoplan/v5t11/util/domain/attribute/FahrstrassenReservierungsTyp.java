package de.gedoplan.v5t11.util.domain.attribute;

import java.util.HashMap;
import java.util.Map;

import javax.json.bind.adapter.JsonbAdapter;
import javax.json.bind.annotation.JsonbTypeAdapter;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@JsonbTypeAdapter(FahrstrassenReservierungsTyp.Adapter4Json.class)
public enum FahrstrassenReservierungsTyp {
  UNRESERVIERT("U"),
  ZUGFAHRT("Z"),
  RANGIERFAHRT("R");

  private String string;

  private static Map<String, FahrstrassenReservierungsTyp> lookup = new HashMap<>();
  static {
    for (FahrstrassenReservierungsTyp s : FahrstrassenReservierungsTyp.values()) {
      lookup.put(s.string, s);
    }
  }

  private FahrstrassenReservierungsTyp(String string) {
    this.string = string;
  }

  @Override
  public String toString() {
    return this.string;
  }

  public static FahrstrassenReservierungsTyp fromString(String string) {
    return fromString(string, false);
  }

  public static FahrstrassenReservierungsTyp fromString(String string, boolean strict) {
    var stellung = lookup.get(string);
    if (stellung == null) {
      stellung = valueOf(string);
    }
    if (stellung == null && strict) {
      throw new IllegalArgumentException("Unbekannter FahrstrassenReservierungsTyp: " + string);
    }
    return stellung;
  }

  public static class Adapter4Json implements JsonbAdapter<FahrstrassenReservierungsTyp, String> {

    @Override
    public String adaptToJson(FahrstrassenReservierungsTyp obj) throws Exception {
      return obj == null ? null : obj.toString();
    }

    @Override
    public FahrstrassenReservierungsTyp adaptFromJson(String s) throws Exception {
      return s == null ? null : fromString(s, true);
    }
  }

  // TODO autoApply wirkt nicht; warum?
  @Converter(autoApply = true)
  public static class Adapter4Jpa implements AttributeConverter<FahrstrassenReservierungsTyp, String> {

    @Override
    public String convertToDatabaseColumn(FahrstrassenReservierungsTyp attribute) {
      return attribute == null ? null : attribute.toString();
    }

    @Override
    public FahrstrassenReservierungsTyp convertToEntityAttribute(String dbData) {
      return dbData == null ? null : fromString(dbData);
    }

  }

}
