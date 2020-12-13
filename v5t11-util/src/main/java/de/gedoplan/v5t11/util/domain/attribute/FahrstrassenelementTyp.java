package de.gedoplan.v5t11.util.domain.attribute;

import java.util.HashMap;
import java.util.Map;

import javax.json.bind.adapter.JsonbAdapter;
import javax.json.bind.annotation.JsonbTypeAdapter;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import lombok.Getter;

@JsonbTypeAdapter(FahrstrassenelementTyp.Adapter4Json.class)
public enum FahrstrassenelementTyp {
  GLEISABSCHNITT("G"),
  SIGNAL("S"),
  WEICHE("W");

  @Getter
  private String code;

  private static Map<String, FahrstrassenelementTyp> lookup = new HashMap<>();
  static {
    for (FahrstrassenelementTyp s : FahrstrassenelementTyp.values()) {
      lookup.put(s.code, s);
    }
  }

  private FahrstrassenelementTyp(String code) {
    this.code = code;
  }

  public static FahrstrassenelementTyp ofCode(String code) {
    return ofCode(code, false);
  }

  public static FahrstrassenelementTyp ofCode(String code, boolean strict) {
    var stellung = lookup.get(code);
    if (stellung == null) {
      stellung = valueOf(code);
    }
    if (stellung == null && strict) {
      throw new IllegalArgumentException("Unbekannter FahrstrassenReservierungsTyp: " + code);
    }
    return stellung;
  }

  public static class Adapter4Json implements JsonbAdapter<FahrstrassenelementTyp, String> {

    @Override
    public String adaptToJson(FahrstrassenelementTyp obj) throws Exception {
      return obj == null ? null : obj.getCode();
    }

    @Override
    public FahrstrassenelementTyp adaptFromJson(String s) throws Exception {
      return s == null ? null : ofCode(s, true);
    }
  }

  // TODO autoApply wirkt nicht; warum?
  @Converter(autoApply = true)
  public static class Adapter4Jpa implements AttributeConverter<FahrstrassenelementTyp, String> {

    @Override
    public String convertToDatabaseColumn(FahrstrassenelementTyp attribute) {
      return attribute == null ? null : attribute.getCode();
    }

    @Override
    public FahrstrassenelementTyp convertToEntityAttribute(String dbData) {
      return dbData == null ? null : ofCode(dbData);
    }

  }

}
