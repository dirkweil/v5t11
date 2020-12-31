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

  private String string;

  @Getter
  private SortedSet<SignalStellung> erlaubteStellungen;

  private static Map<String, SignalTyp> lookup = new HashMap<>();
  static {
    for (SignalTyp s : SignalTyp.values()) {
      lookup.put(s.string, s);
    }
  }

  private SignalTyp(String string, SignalStellung... erlaubteStellungen) {
    this.string = string;
    this.erlaubteStellungen = new TreeSet<>(Arrays.asList(erlaubteStellungen));
  }

  @Override
  public String toString() {
    return this.string;
  }

  public static SignalTyp fromString(String string) {
    return fromString(string, false);
  }

  public static SignalTyp fromString(String string, boolean strict) {
    var stellung = lookup.get(string);
    if (stellung == null) {
      stellung = valueOf(string);
    }
    if (stellung == null && strict) {
      throw new IllegalArgumentException("Unbekannter SignalTyp: " + string);
    }
    return stellung;
  }

  public static class Adapter4Json implements JsonbAdapter<SignalTyp, String> {

    @Override
    public String adaptToJson(SignalTyp obj) throws Exception {
      return obj == null ? null : obj.toString();
    }

    @Override
    public SignalTyp adaptFromJson(String s) throws Exception {
      return s == null ? null : fromString(s, true);
    }
  }

  // TODO autoApply wirkt nicht; warum?
  @Converter(autoApply = true)
  public static class Adapter4Jpa implements AttributeConverter<SignalTyp, String> {

    @Override
    public String convertToDatabaseColumn(SignalTyp attribute) {
      return attribute == null ? null : attribute.toString();
    }

    @Override
    public SignalTyp convertToEntityAttribute(String dbData) {
      return dbData == null ? null : fromString(dbData);
    }

  }

};