package de.gedoplan.v5t11.util.domain.attribute;

import de.gedoplan.v5t11.util.jsonb.JsonbInclude;

import java.io.Serializable;

import javax.json.bind.JsonbException;
import javax.json.bind.adapter.JsonbAdapter;
import javax.json.bind.annotation.JsonbTypeAdapter;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Embeddable
@Access(AccessType.FIELD)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter(onMethod_ = @JsonbInclude)
@Setter(onMethod_ = @JsonbInclude)
@EqualsAndHashCode
@ToString
@JsonbTypeAdapter(FahrzeugId.JsonTypeAdapter.class)
public class FahrzeugId implements Serializable, Comparable<FahrzeugId> {

  @NotNull
  @Enumerated(EnumType.STRING)
  private SystemTyp systemTyp;
  private int adresse;

  @AssertTrue(message = "Ungültige Adresse")
  boolean isAdresseValid() {
    if (this.systemTyp == null) {
      return true;
    }

    if (this.systemTyp == SystemTyp.SX1) {
      return this.adresse >= 1 && this.adresse <= 103;
    }

    return this.adresse >= 1 && this.adresse <= 9999;
  }

  @Override
  public int compareTo(FahrzeugId o) {
    int diff = Integer.compare(this.adresse, o.adresse);
    if (diff != 0) {
      return diff;
    }

    return this.systemTyp.compareTo(o.systemTyp);
  }

  public static class JsonTypeAdapter implements JsonbAdapter<FahrzeugId, String> {

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

}
