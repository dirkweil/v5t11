package de.gedoplan.v5t11.util.domain.attribute;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.json.bind.adapter.JsonbAdapter;
import javax.json.bind.annotation.JsonbTypeAdapter;

import lombok.Getter;

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

  @Getter
  private String code;

  private static Map<String, WeichenStellung> lookup = Stream.of(WeichenStellung.values()).collect(Collectors.toMap(WeichenStellung::getCode, s -> s));

  private WeichenStellung(String code) {
    this.code = code;
  }

  public static WeichenStellung ofCode(String code) {
    return ofCode(code, false);
  }

  public static WeichenStellung ofCode(String code, boolean strict) {
    var stellung = lookup.get(code);
    if (stellung == null) {
      stellung = valueOf(code);
    }
    if (stellung == null && strict) {
      throw new IllegalArgumentException("Unbekannte Weichenstellung: " + code);
    }
    return stellung;
  }

  public static class Adapter4Json implements JsonbAdapter<WeichenStellung, String> {

    @Override
    public String adaptToJson(WeichenStellung obj) throws Exception {
      return obj == null ? null : obj.getCode();
    }

    @Override
    public WeichenStellung adaptFromJson(String s) throws Exception {
      return s == null ? null : ofCode(s, true);
    }
  }

}
