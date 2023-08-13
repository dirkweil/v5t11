package de.gedoplan.v5t11.util.jsonb;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;

public final class JsonbWithIncludeVisibility {
  public static final Jsonb SHORT = JsonbBuilder.create(new JsonbConfig().withPropertyVisibilityStrategy(JsonbIncludeVisibilityStrategy.SHORT));
  public static final Jsonb FULL = JsonbBuilder.create(new JsonbConfig().withPropertyVisibilityStrategy(JsonbIncludeVisibilityStrategy.FULL));
}
