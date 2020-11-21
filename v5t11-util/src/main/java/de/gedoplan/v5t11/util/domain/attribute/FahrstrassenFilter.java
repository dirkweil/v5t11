package de.gedoplan.v5t11.util.domain.attribute;

import java.util.HashMap;
import java.util.Map;

import javax.json.bind.adapter.JsonbAdapter;
import javax.json.bind.annotation.JsonbTypeAdapter;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import lombok.Getter;

@JsonbTypeAdapter(FahrstrassenFilter.Adapter4Json.class)
public enum FahrstrassenFilter {
  FREI("F"),
  RESERVIERT("R"),
  NON_COMBI("N");

  @Getter
  private String code;

  private static Map<String, FahrstrassenFilter> lookup = new HashMap<>();
  static {
    for (FahrstrassenFilter s : FahrstrassenFilter.values()) {
      lookup.put(s.code, s);
    }
  }

  private FahrstrassenFilter(String code) {
    this.code = code;
  }

  public static FahrstrassenFilter ofCode(String code) {
    return ofCode(code, false);
  }

  public static FahrstrassenFilter ofCode(String code, boolean strict) {
    var stellung = lookup.get(code);
    if (stellung == null) {
      stellung = valueOf(code);
    }
    if (stellung == null && strict) {
      throw new IllegalArgumentException("Unbekannter FahrstrassenFilter: " + code);
    }
    return stellung;
  }

  public static class Adapter4Json implements JsonbAdapter<FahrstrassenFilter, String> {

    @Override
    public String adaptToJson(FahrstrassenFilter obj) throws Exception {
      return obj == null ? null : obj.getCode();
    }

    @Override
    public FahrstrassenFilter adaptFromJson(String s) throws Exception {
      return s == null ? null : ofCode(s, true);
    }
  }

  // TODO autoApply wirkt nicht; warum?
  @Converter(autoApply = true)
  public static class Adapter4Jpa implements AttributeConverter<FahrstrassenFilter, String> {

    @Override
    public String convertToDatabaseColumn(FahrstrassenFilter attribute) {
      return attribute == null ? null : attribute.getCode();
    }

    @Override
    public FahrstrassenFilter convertToEntityAttribute(String dbData) {
      return dbData == null ? null : ofCode(dbData);
    }

  }

}
