package de.gedoplan.v5t11.status.jsonb;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;

public final class JsonbWithIncludeVisibility {
  public static final Jsonb SHORT = JsonbBuilder.create(new JsonbConfig().withPropertyVisibilityStrategy(JsonbIncludeVisibilityStrategy.SHORT));
  public static final Jsonb FULL = JsonbBuilder.create(new JsonbConfig().withPropertyVisibilityStrategy(JsonbIncludeVisibilityStrategy.FULL));
}
