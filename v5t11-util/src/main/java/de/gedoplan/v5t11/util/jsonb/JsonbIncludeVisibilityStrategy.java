package de.gedoplan.v5t11.util.jsonb;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

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
    if ((method.getModifiers() & Modifier.ABSTRACT) != 0) {
      return false;
    }

    JsonbInclude jsonBInclude = method.getAnnotation(JsonbInclude.class);

    // Suche nach dem unerklärlichen Phänomen, dass getId manchmal keine Annotationen hat (scheinbar nur wenn Yasson genutzt wird ...
    // if ("getId".equals(method.getName())) {
    // System.out.println("### method: " + method);
    // System.out.println(" ### jsonBInclude: " + jsonBInclude);
    // System.out.println(" ### dito, declared: " + method.getDeclaredAnnotation(JsonbInclude.class));
    // System.out.println(" ### declaringClass: " + method.getDeclaringClass());
    // System.out.println(" ### annotations: " + Stream.of(method.getAnnotations()).map(a -> a.toString()).collect(Collectors.joining(",")));
    //
    // if (jsonBInclude == null) {
    // return false;
    // }
    // }

    return jsonBInclude != null && (this.full || !jsonBInclude.full());
  }

  public static final JsonbIncludeVisibilityStrategy SHORT = new JsonbIncludeVisibilityStrategy(false);
  public static final JsonbIncludeVisibilityStrategy FULL = new JsonbIncludeVisibilityStrategy(true);
}
