package de.gedoplan.v5t11.util.domain.attribute;

import de.gedoplan.v5t11.util.jsonb.JsonbInclude;

import java.io.Serializable;

import jakarta.json.bind.JsonbException;
import jakarta.json.bind.adapter.JsonbAdapter;
import jakarta.json.bind.annotation.JsonbTypeAdapter;
import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Access(AccessType.FIELD)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter(onMethod_ = @JsonbInclude)
@Setter(onMethod_ = @JsonbInclude)
@EqualsAndHashCode
@JsonbTypeAdapter(FahrzeugId.JsonTypeAdapter.class)
@XmlJavaTypeAdapter(FahrzeugId.XmlTypeAdapter.class)
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

  /**
   * Code für Id aus Adresse und Systemtypnamen kombinieren.
   *
   * @return Adresse + '@' + Systemtypname
   */
  @Override
  public String toString() {
    return this.adresse + "@" + this.systemTyp.name();
  }

  /**
   * Code in Adresse und Systemtyp aufteilen.
   *
   * @param Adresse + '@' + Systemtypname
   * @return Decodierte Id
   */
  public static FahrzeugId fromString(String s) {
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
      throw new IllegalArgumentException("Ungültige FahrzeugId: " + s, e);
    }
  }

  public static class JsonTypeAdapter implements JsonbAdapter<FahrzeugId, String> {

    @Override
    public String adaptToJson(FahrzeugId fahrzeugId) throws Exception {
      return fahrzeugId == null ? null : fahrzeugId.toString();
    }

    @Override
    public FahrzeugId adaptFromJson(String s) throws Exception {
      return s == null ? null : FahrzeugId.fromString(s);
    }

  }

  public static class XmlTypeAdapter extends XmlAdapter<String, FahrzeugId> {

    @Override
    public FahrzeugId unmarshal(String s) throws Exception {
      return s == null ? null : FahrzeugId.fromString(s);
    }

    @Override
    public String marshal(FahrzeugId fahrzeugId) throws Exception {
      return fahrzeugId == null ? null : fahrzeugId.toString();
    }
  }
}
