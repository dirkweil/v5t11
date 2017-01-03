package de.gedoplan.v5t11.betriebssteuerung.service;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Qualifier;

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
