package de.gedoplan.v5t11.util.domain.attribute;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.json.bind.adapter.JsonbAdapter;
import jakarta.json.bind.annotation.JsonbTypeAdapter;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

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

  private String string;

  private static Map<String, SignalStellung> lookup = new HashMap<>();

  static {
    for (SignalStellung s : SignalStellung.values()) {
      lookup.put(s.string, s);
    }
  }

  private SignalStellung(String string) {
    this.string = string;
  }

  @Override
  public String toString() {
    return this.string;
  }

  public static SignalStellung fromString(String string) {
    return fromString(string, false);
  }

  public static SignalStellung fromString(String string, boolean strict) {
    var stellung = lookup.get(string);
    if (stellung == null) {
      stellung = valueOf(string);
    }
    if (stellung == null && strict) {
      throw new IllegalArgumentException("Unbekannte SignalStellung: " + string);
    }
    return stellung;
  }

  public static class Adapter4Json implements JsonbAdapter<SignalStellung, String> {

    @Override
    public String adaptToJson(SignalStellung obj) throws Exception {
      return obj == null ? null : obj.toString();
    }

    @Override
    public SignalStellung adaptFromJson(String s) throws Exception {
      return s == null ? null : fromString(s, true);
    }
  }

  public static class Adapter4JsonSet implements JsonbAdapter<Set<SignalStellung>, String> {

    @Override
    public String adaptToJson(Set<SignalStellung> signalStellungen) throws Exception {
      return signalStellungen == null ? null : signalStellungen.stream().map(SignalStellung::toString).collect(Collectors.joining());
    }

    @Override
    public Set<SignalStellung> adaptFromJson(String s) throws Exception {
      return s == null ? null : s.codePoints().mapToObj(c -> String.valueOf(c)).map(x -> SignalStellung.fromString(x, true)).collect(Collectors.toSet());
    }

  }

  // TODO autoApply wirkt nicht; warum?
  @Converter(autoApply = true)
  public static class Adapter4Jpa implements AttributeConverter<SignalStellung, String> {

    @Override
    public String convertToDatabaseColumn(SignalStellung attribute) {
      return attribute == null ? null : attribute.toString();
    }

    @Override
    public SignalStellung convertToEntityAttribute(String dbData) {
      return dbData == null ? null : fromString(dbData);
    }

  }

};
