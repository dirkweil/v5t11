package de.gedoplan.v5t11.status.entity.baustein;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Stereotype;

@Stereotype
@Dependent
@Target({ TYPE })
@Retention(RUNTIME)
@Documented
public @interface Konfigurierbar {
  Class<?> programmierFamilie() default Void.class;
}
