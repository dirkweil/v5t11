package de.gedoplan.v5t11.util.jsonb;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.json.bind.config.PropertyVisibilityStrategy;

public class JsonbIncludeVisibilityStrategy implements PropertyVisibilityStrategy {

  private boolean full;

  private JsonbIncludeVisibilityStrategy(boolean full) {
    this.full = full;
  }

  @Override
  public boolean isVisible(Field field) {
    JsonbInclude jsonBInclude = field.getAnnotation(JsonbInclude.class);
    return jsonBInclude != null && (this.full || !jsonBInclude.full());
  }

  @Override
  public boolean isVisible(Method method) {
    JsonbInclude jsonBInclude = method.getAnnotation(JsonbInclude.class);
    return jsonBInclude != null && (this.full || !jsonBInclude.full());
  }

  public static final JsonbIncludeVisibilityStrategy SHORT = new JsonbIncludeVisibilityStrategy(false);
  public static final JsonbIncludeVisibilityStrategy FULL = new JsonbIncludeVisibilityStrategy(true);
}
