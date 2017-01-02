package de.gedoplan.v5t11.betriebssteuerung.model;

import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerFactory;

public class SelectrixExceptionHandlerFactory extends ExceptionHandlerFactory {
  private ExceptionHandlerFactory parent;

  public SelectrixExceptionHandlerFactory(ExceptionHandlerFactory parent) {
    this.parent = parent;
  }

  @Override
  public ExceptionHandler getExceptionHandler() {
    return new SelectrixExceptionHandler(this.parent.getExceptionHandler());
  }
}
