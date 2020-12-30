package de.gedoplan.v5t11.util.domain.attribute;

import de.gedoplan.v5t11.util.misc.NameComparator;

import java.io.Serializable;

import javax.json.bind.adapter.JsonbAdapter;
import javax.json.bind.annotation.JsonbTypeAdapter;
import javax.persistence.Embeddable;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode
@JsonbTypeAdapter(value = BereichselementId.JsonTypeAdapter.class)
public class BereichselementId implements Comparable<BereichselementId>, Serializable {
  private String bereich;
  private String name;

  @Override
  public int compareTo(BereichselementId other) {
    int diff = this.bereich.compareTo(other.bereich);
    if (diff != 0) {
      return diff;
    }

    return NameComparator.compare(this.name, other.name);
  }

  /**
   * Code für Id aus Name und Bereich kombinieren.
   * 
   * @return Name + '@' + Bereich
   */
  @Override
  public String toString() {
    return this.name + "@" + this.bereich;
  }

  /**
   * Code in Name und Bereich aufteilen.
   * 
   * @param Name + '@' + Bereich
   * @return Decodierte Id
   */
  public static BereichselementId fromString(String s) {
    String[] parts = s.split("@");
    if (parts.length != 2) {
      throw new IllegalArgumentException("Ungültiges Format der BereichselementId: " + s);
    }

    return new BereichselementId(parts[1], parts[0]);
  }

  public static class JsonTypeAdapter implements JsonbAdapter<BereichselementId, String> {

    @Override
    public String adaptToJson(BereichselementId obj) throws Exception {
      return obj.toString();
    }

    @Override
    public BereichselementId adaptFromJson(String s) throws Exception {
      return fromString(s);
    }

  }

}
