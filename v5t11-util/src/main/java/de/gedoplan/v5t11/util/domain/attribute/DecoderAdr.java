package de.gedoplan.v5t11.util.domain.attribute;

import de.gedoplan.v5t11.util.jsonb.JsonbShort;

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
@Getter(onMethod_ = @JsonbShort)
@Setter(onMethod_ = @JsonbShort)
@EqualsAndHashCode
@JsonbTypeAdapter(DecoderAdr.JsonTypeAdapter.class)
@XmlJavaTypeAdapter(DecoderAdr.XmlTypeAdapter.class)
public class DecoderAdr implements Serializable, Comparable<DecoderAdr> {

  @NotNull
  @Enumerated(EnumType.STRING)
  private SystemTyp systemTyp;

  private int adresse;

  @AssertTrue(message = "Ungültige Adresse")
  public boolean isAdresseValid() {
    if (this.systemTyp == null) {
      return true;
    }

    if (this.systemTyp == SystemTyp.SX1) {
      return this.adresse >= 1 && this.adresse <= 103;
    }

    return this.adresse >= 1 && this.adresse <= 9999;
  }

  @Override
  public int compareTo(DecoderAdr o) {
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
   * @param Text der Form Adresse + '@' + Systemtypname
   * @return Decodierte Id
   */
  public static DecoderAdr fromString(String s) {
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
      return new DecoderAdr(systemTyp, adresse);
    } catch (Exception e) {
      throw new IllegalArgumentException("Ungültige FahrzeugId: " + s, e);
    }
  }

  public String getAdrInfo() {
    if (this.systemTyp == SystemTyp.DCC) {
      if (this.adresse >= 128) {
        return String.format(" (CV17=%d, CV18=%d)", this.adresse / 256 + 192, this.adresse % 256);
      }
    }

    return null;
  }

  public static class JsonTypeAdapter implements JsonbAdapter<DecoderAdr, String> {

    @Override
    public String adaptToJson(DecoderAdr fahrzeugId) throws Exception {
      return fahrzeugId == null ? null : fahrzeugId.toString();
    }

    @Override
    public DecoderAdr adaptFromJson(String s) throws Exception {
      return s == null ? null : DecoderAdr.fromString(s);
    }

  }

  public static class XmlTypeAdapter extends XmlAdapter<String, DecoderAdr> {

    @Override
    public DecoderAdr unmarshal(String s) throws Exception {
      return s == null ? null : DecoderAdr.fromString(s);
    }

    @Override
    public String marshal(DecoderAdr fahrzeugId) throws Exception {
      return fahrzeugId == null ? null : fahrzeugId.toString();
    }
  }
}
