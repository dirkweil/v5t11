package de.gedoplan.v5t11.util.domain.attribute;

import java.util.HashMap;
import java.util.Map;

import javax.json.bind.adapter.JsonbAdapter;
import javax.json.bind.annotation.JsonbTypeAdapter;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import lombok.Getter;

/**
 * Signalstellung.
 *
 * @author dw
 */
@JsonbTypeAdapter(SignalStellung.Adapter4Json.class)
public enum SignalStellung {
  /**
   * Halt (Hp0, Hp00, Sr0, Vr0).
   */
  HALT("H"),

  /**
   * Fahrt (Hp1, Vr1).
   */
  FAHRT("F"),

  /**
   * Langsamfahrt (Hp2, Vr2).
   */
  LANGSAMFAHRT("L"),

  /**
   * Rangierfahrt (Sr1).
   */
  RANGIERFAHRT("R"),

  /**
   * Dunkel (Vorsignal am Hauptsignalmast bei Hp0).
   */
  DUNKEL("D");

  @Getter
  private String code;

  private static Map<String, SignalStellung> lookup = new HashMap<>();
  static {
    for (SignalStellung s : SignalStellung.values()) {
      lookup.put(s.code, s);
    }
  }

  private SignalStellung(String code) {
    this.code = code;
  }

  public static SignalStellung ofCode(String code) {
    return ofCode(code, false);
  }

  public static SignalStellung ofCode(String code, boolean strict) {
    var stellung = lookup.get(code);
    if (stellung == null) {
      stellung = valueOf(code);
    }
    if (stellung == null && strict) {
      throw new IllegalArgumentException("Unbekannte SignalStellung: " + code);
    }
    return stellung;
  }

  public static class Adapter4Json implements JsonbAdapter<SignalStellung, String> {

    @Override
    public String adaptToJson(SignalStellung obj) throws Exception {
      return obj == null ? null : obj.getCode();
    }

    @Override
    public SignalStellung adaptFromJson(String s) throws Exception {
      return s == null ? null : ofCode(s, true);
    }
  }

  // TODO autoApply wirkt nicht; warum?
  @Converter(autoApply = true)
  public static class Adapter4Jpa implements AttributeConverter<SignalStellung, String> {

    @Override
    public String convertToDatabaseColumn(SignalStellung attribute) {
      return attribute == null ? null : attribute.getCode();
    }

    @Override
    public SignalStellung convertToEntityAttribute(String dbData) {
      return dbData == null ? null : ofCode(dbData);
    }

  }

};
