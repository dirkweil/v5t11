package de.gedoplan.v5t11.leitstand.entity.baustein;

import de.gedoplan.baselibs.persistence.entity.SingleIdEntity;
import de.gedoplan.v5t11.util.jsonb.JsonbInclude;

import javax.json.bind.annotation.JsonbNillable;

import lombok.Getter;
import lombok.Setter;

@JsonbNillable
public class LokController extends SingleIdEntity<String> implements Comparable<LokController> {

  @Getter(onMethod_ = @JsonbInclude)
  @Setter(onMethod_ = @JsonbInclude)
  private String id;

  @Getter(onMethod_ = @JsonbInclude)
  @Setter(onMethod_ = @JsonbInclude)
  private String lokId;

  @Override
  public int compareTo(LokController o) {
    return this.id.compareTo(o.id);
  }

  public synchronized void copyStatus(LokController other) {
    this.lokId = other.lokId;
  }

}
