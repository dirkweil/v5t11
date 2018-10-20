package de.gedoplan.v5t11.leitstand.entity.lok;

import de.gedoplan.baselibs.persistence.entity.SingleIdEntity;
import de.gedoplan.v5t11.leitstand.entity.fahrweg.StatusUpdateable;
import de.gedoplan.v5t11.util.jsonb.JsonbInclude;

import lombok.Getter;
import lombok.Setter;

public class Lok extends SingleIdEntity<String> implements Comparable<Lok>, StatusUpdateable<Lok> {

  @Getter(onMethod_ = @JsonbInclude)
  @Setter(onMethod_ = @JsonbInclude)
  private String id;

  @Override
  public int compareTo(Lok o) {
    return this.id.compareTo(o.id);
  }

  public synchronized void copyStatus(Lok other) {
    // nix zu tun
  }

}
