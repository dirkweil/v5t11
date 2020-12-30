package de.gedoplan.v5t11.util.jsonb;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.logging.Logger;

import javax.json.bind.config.PropertyVisibilityStrategy;

public enum JsonbIncludeVisibilityStrategy implements PropertyVisibilityStrategy {
  SHORT(false),
  FULL(true);

  private boolean full;

  private JsonbIncludeVisibilityStrategy(boolean full) {
    this.full = full;
  }

  private static final Logger LOGGER = Logger.getLogger(JsonbIncludeVisibilityStrategy.class.getName());

  @Override
  public boolean isVisible(Field field) {
    return isVisible(field, field.getAnnotation(JsonbInclude.class));
  }

  @Override
  public boolean isVisible(Method method) {
    if ((method.getModifiers() & Modifier.ABSTRACT) != 0) {
      LOGGER.finer(() -> method + " not visible (abstract)");
      return false;
    }

    return isVisible(method, method.getAnnotation(JsonbInclude.class));
  }

  private boolean isVisible(Member member, JsonbInclude jsonBInclude) {
    if (jsonBInclude == null) {
      LOGGER.finer(() -> member + " not visible (not annotated)");
      return false;
    }

    if (this.full) {
      LOGGER.finer(() -> member + " visible (full view)");
      return true;
    }

    if (jsonBInclude.full()) {
      LOGGER.finer(() -> member + " not visible (full item in short view)");
      return false;
    }

    LOGGER.finer(() -> member + " visible (short item in short view)");
    return true;

  }
}
