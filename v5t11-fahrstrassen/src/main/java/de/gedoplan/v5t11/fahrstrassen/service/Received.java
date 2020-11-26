package de.gedoplan.v5t11.fahrstrassen.service;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Qualifier;

@Qualifier
@Target({ TYPE, METHOD, PARAMETER, FIELD })
@Retention(RUNTIME)
@Documented
public @interface Received {

  public static final class Literal extends AnnotationLiteral<Received> implements Received {

    public static final Literal INSTANCE = new Literal();
  }
}
