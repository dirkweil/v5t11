package de.gedoplan.v5t11.status.service;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.enterprise.util.AnnotationLiteral;
import jakarta.inject.Qualifier;

@Qualifier
@Target({ TYPE, METHOD, PARAMETER, FIELD })
@Retention(RUNTIME)
@Documented
public @interface Programmierfamilie {
  Class<?> value();

  public static class Literal extends AnnotationLiteral<Programmierfamilie> implements Programmierfamilie {

    private Class<?> value;

    public Literal(Class<?> value) {
      this.value = value;
    }

    @Override
    public Class<?> value() {
      return this.value;
    }
  }

}
