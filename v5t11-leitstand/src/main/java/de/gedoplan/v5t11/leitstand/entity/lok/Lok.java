package de.gedoplan.v5t11.leitstand.entity.lok;

import de.gedoplan.baselibs.persistence.entity.SingleIdEntity;
import de.gedoplan.v5t11.util.jsonb.JsonbInclude;

import lombok.Getter;
import lombok.Setter;

public class Lok extends SingleIdEntity<String> implements Comparable<Lok> {

  @Getter(onMethod_ = @JsonbInclude)
  @Setter(onMethod_ = @JsonbInclude)
  private String id;

  public String getBildFileName() {
    return this.id.replaceAll(" ", "_") + ".png";
  }

  @Override
  public int compareTo(Lok o) {
    return this.id.compareTo(o.id);
  }

}
