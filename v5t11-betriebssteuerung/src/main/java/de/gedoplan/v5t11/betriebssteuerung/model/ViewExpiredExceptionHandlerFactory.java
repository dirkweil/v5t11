package de.gedoplan.v5t11.betriebssteuerung.model;

import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerFactory;

public class ViewExpiredExceptionHandlerFactory extends ExceptionHandlerFactory {
  private ExceptionHandlerFactory parent;

  public ViewExpiredExceptionHandlerFactory(ExceptionHandlerFactory parent) {
    this.parent = parent;
  }

  @Override
  public ExceptionHandler getExceptionHandler() {
    return new ViewExpiredExceptionHandler(this.parent.getExceptionHandler());
  }
}
