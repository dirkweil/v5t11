package de.gedoplan.v5t11.util.domain;

import de.gedoplan.v5t11.util.jsonb.JsonbInclude;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter(onMethod_ = @JsonbInclude)
@Setter(onMethod_ = @JsonbInclude)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JoinInfo {

  private String appName;
  private long lastUpdateMillis;
}
