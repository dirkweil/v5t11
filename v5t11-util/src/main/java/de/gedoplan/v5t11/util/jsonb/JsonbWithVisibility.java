package de.gedoplan.v5t11.util.jsonb;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;

public final class JsonbWithVisibility {
  public static final Jsonb SHORT = JsonbBuilder.create(new JsonbConfig().withPropertyVisibilityStrategy(JsonbShortVisibilityStrategy.SHORT));
}
