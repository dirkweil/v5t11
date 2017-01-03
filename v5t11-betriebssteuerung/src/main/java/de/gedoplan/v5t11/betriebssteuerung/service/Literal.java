package de.gedoplan.v5t11.betriebssteuerung.service;

import javax.enterprise.util.AnnotationLiteral;

public class Literal extends AnnotationLiteral<Programmierfamilie> implements Programmierfamilie {

  private String value;

  public Literal(String value) {
    this.value = value;
  }

  @Override
  public String value() {
    return this.value;
  }
}
