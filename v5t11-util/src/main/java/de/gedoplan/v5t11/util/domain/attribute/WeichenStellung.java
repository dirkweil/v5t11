package de.gedoplan.v5t11.util.domain.attribute;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.json.bind.adapter.JsonbAdapter;
import jakarta.json.bind.annotation.JsonbTypeAdapter;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Weichenstellung.
 *
 * @author dw
 */
@JsonbTypeAdapter(WeichenStellung.Adapter4Json.class)
public enum WeichenStellung {
  // Achtung: Die Reihenfolge der beiden Konstanten muss erhalten bleiben, da die Ordinalwerte als Stellungswerte genutzt werden!
  GERADE("G"),
  ABZWEIGEND("A");

  private String string;

  private static Map<String, WeichenStellung> lookup = Stream.of(WeichenStellung.values()).collect(Collectors.toMap(WeichenStellung::toString, s -> s));

  private WeichenStellung(String string) {
    this.string = string;
  }

  @Override
  public String toString() {
    return this.string;
  }

  public static WeichenStellung fromString(String string) {
    return fromString(string, false);
  }

  public static WeichenStellung fromString(String string, boolean strict) {
    var stellung = lookup.get(string);
    if (stellung == null) {
      stellung = valueOf(string);
    }
    if (stellung == null && strict) {
      throw new IllegalArgumentException("Unbekannte Weichenstellung: " + string);
    }
    return stellung;
  }

  public static class Adapter4Json implements JsonbAdapter<WeichenStellung, String> {

    @Override
    public String adaptToJson(WeichenStellung obj) throws Exception {
      return obj == null ? null : obj.toString();
    }

    @Override
    public WeichenStellung adaptFromJson(String s) throws Exception {
      return s == null ? null : fromString(s, true);
    }
  }

  // TODO autoApply wirkt nicht; warum?
  @Converter(autoApply = true)
  public static class Adapter4Jpa implements AttributeConverter<WeichenStellung, String> {

    @Override
    public String convertToDatabaseColumn(WeichenStellung attribute) {
      return attribute == null ? null : attribute.toString();
    }

    @Override
    public WeichenStellung convertToEntityAttribute(String dbData) {
      return dbData == null ? null : fromString(dbData);
    }

  }
}
