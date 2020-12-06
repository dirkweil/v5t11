package de.gedoplan.v5t11.util.domain.attribute;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.json.bind.adapter.JsonbAdapter;
import javax.json.bind.annotation.JsonbTypeAdapter;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import lombok.Getter;

/**
 * Signaltyp.
 *
 * @author dw
 */
@JsonbTypeAdapter(SignalTyp.Adapter4Json.class)
public enum SignalTyp {
  /**
   * Hauptsignal 3-begriffig.
   */
  HAUPTSIGNAL_RT_GE_GN("HRtGeGn", SignalStellung.HALT, SignalStellung.FAHRT, SignalStellung.LANGSAMFAHRT),

  /**
   * Hauptsignal 2-begriffig.
   */
  HAUPTSIGNAL_RT_GN("HRtGn", SignalStellung.HALT, SignalStellung.FAHRT),

  /**
   * Hauptsignal 2-begriffig (nur Langsamfahrt).
   */
  HAUPTSIGNAL_RT_GE("HRtGe", SignalStellung.HALT, SignalStellung.LANGSAMFAHRT),

  /**
   * Hauptsperrsignal.
   */
  HAUPTSPERRSIGNAL("HS", SignalStellung.HALT, SignalStellung.FAHRT, SignalStellung.LANGSAMFAHRT, SignalStellung.RANGIERFAHRT),

  /**
   * Sperrsignal.
   */
  SPERRSIGNAL("S", SignalStellung.HALT, SignalStellung.RANGIERFAHRT),

  /**
   * Vorsignal.
   */
  VORSIGNAL("V", SignalStellung.HALT, SignalStellung.FAHRT, SignalStellung.LANGSAMFAHRT, SignalStellung.DUNKEL),

  /**
   * Bahnübergang.
   */
  BAHNUEBERGANG("Ü", SignalStellung.HALT, SignalStellung.FAHRT);

  @Getter
  private String code;

  @Getter
  private SortedSet<SignalStellung> erlaubteStellungen;

  private static Map<String, SignalTyp> lookup = new HashMap<>();
  static {
    for (SignalTyp s : SignalTyp.values()) {
      lookup.put(s.code, s);
    }
  }

  private SignalTyp(String code, SignalStellung... erlaubteStellungen) {
    this.code = code;
    this.erlaubteStellungen = new TreeSet<>(Arrays.asList(erlaubteStellungen));
  }

  public static SignalTyp ofCode(String code) {
    return ofCode(code, false);
  }

  public static SignalTyp ofCode(String code, boolean strict) {
    var stellung = lookup.get(code);
    if (stellung == null) {
      stellung = valueOf(code);
    }
    if (stellung == null && strict) {
      throw new IllegalArgumentException("Unbekannter SignalTyp: " + code);
    }
    return stellung;
  }

  public static class Adapter4Json implements JsonbAdapter<SignalTyp, String> {

    @Override
    public String adaptToJson(SignalTyp obj) throws Exception {
      return obj == null ? null : obj.getCode();
    }

    @Override
    public SignalTyp adaptFromJson(String s) throws Exception {
      return s == null ? null : ofCode(s, true);
    }
  }

  // TODO autoApply wirkt nicht; warum?
  @Converter(autoApply = true)
  public static class Adapter4Jpa implements AttributeConverter<SignalTyp, String> {

    @Override
    public String convertToDatabaseColumn(SignalTyp attribute) {
      return attribute == null ? null : attribute.getCode();
    }

    @Override
    public SignalTyp convertToEntityAttribute(String dbData) {
      return dbData == null ? null : ofCode(dbData);
    }

  }

};
