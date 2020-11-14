package de.gedoplan.v5t11.status.entity.fahrzeug;

import de.gedoplan.v5t11.util.domain.attribute.SystemTyp;

import javax.json.bind.JsonbException;
import javax.json.bind.adapter.JsonbAdapter;

public class FahrzeugIdTypeAdapter implements JsonbAdapter<FahrzeugId, String> {

  @Override
  public String adaptToJson(FahrzeugId fahrzeugId) throws Exception {
    if (fahrzeugId == null) {
      return null;
    }
    return fahrzeugId.getAdresse() + "@" + fahrzeugId.getSystemTyp().name();
  }

  @Override
  public FahrzeugId adaptFromJson(String s) throws Exception {
    if (s == null) {
      return null;
    }

    String[] parts = s.split("@");
    if (parts.length != 2) {
      throw new JsonbException("Ungültiges Format der FahrzeugId: " + s);
    }

    SystemTyp systemTyp = SystemTyp.valueOf(parts[1]);
    if (systemTyp == null) {
      throw new JsonbException("Ungültiger SystemTyp in der FahrzeugId: " + s);
    }

    try {
      int adresse = Integer.parseInt(parts[0]);
      return new FahrzeugId(systemTyp, adresse);
    } catch (Exception e) {
      throw new JsonbException("Ungültige FahrzeugId: " + s, e);
    }
  }

}
