package de.gedoplan.v5t11.util.domain.attribute;

import java.util.HashMap;
import java.util.Map;

import javax.json.bind.adapter.JsonbAdapter;
import javax.json.bind.annotation.JsonbTypeAdapter;

import lombok.Getter;

@JsonbTypeAdapter(FahrstrassenReservierungsTyp.Adapter4Json.class)
public enum FahrstrassenReservierungsTyp {
  UNRESERVIERT("U"),
  ZUGFAHRT("Z"),
  RANGIERFAHRT("R");

  @Getter
  private String code;

  private static Map<String, FahrstrassenReservierungsTyp> lookup = new HashMap<>();
  static {
    for (FahrstrassenReservierungsTyp s : FahrstrassenReservierungsTyp.values()) {
      lookup.put(s.code, s);
    }
  }

  private FahrstrassenReservierungsTyp(String code) {
    this.code = code;
  }

  public static FahrstrassenReservierungsTyp ofCode(String code) {
    return ofCode(code, false);
  }

  public static FahrstrassenReservierungsTyp ofCode(String code, boolean strict) {
    var stellung = lookup.get(code);
    if (stellung == null) {
      stellung = valueOf(code);
    }
    if (stellung == null && strict) {
      throw new IllegalArgumentException("Unbekannter FahrstrassenReservierungsTyp: " + code);
    }
    return stellung;
  }

  public static class Adapter4Json implements JsonbAdapter<FahrstrassenReservierungsTyp, String> {

    @Override
    public String adaptToJson(FahrstrassenReservierungsTyp obj) throws Exception {
      return obj == null ? null : obj.getCode();
    }

    @Override
    public FahrstrassenReservierungsTyp adaptFromJson(String s) throws Exception {
      return s == null ? null : ofCode(s, true);
    }
  }
}
