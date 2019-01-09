package de.gedoplan.v5t11.status.entity.baustein;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Stereotype;

@Stereotype
@Dependent
@Target({ TYPE })
@Retention(RUNTIME)
@Documented
public @interface Konfigurierbar {
  String programmierFamilie() default "";
}
