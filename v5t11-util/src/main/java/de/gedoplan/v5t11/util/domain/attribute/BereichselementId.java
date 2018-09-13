package de.gedoplan.v5t11.util.domain.attribute;

import de.gedoplan.v5t11.util.misc.NameComparator;

import javax.persistence.Embeddable;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class BereichselementId implements Comparable<BereichselementId> {
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

}
