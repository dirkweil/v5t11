package de.gedoplan.v5t11.betriebssteuerung.model;

import de.gedoplan.baselibs.utils.util.ExceptionUtil;

import java.util.Iterator;

import javax.faces.FacesException;
import javax.faces.application.NavigationHandler;
import javax.faces.application.ViewExpiredException;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.FacesContext;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;

public class ViewExpiredExceptionHandler extends ExceptionHandlerWrapper {
  private ExceptionHandler wrapped;

  public ViewExpiredExceptionHandler(ExceptionHandler wrapped) {
    this.wrapped = wrapped;
  }

  @Override
  public ExceptionHandler getWrapped() {
    return this.wrapped;
  }

  @Override
  public void handle() throws FacesException {
    Iterator<ExceptionQueuedEvent> iterator = getUnhandledExceptionQueuedEvents().iterator();
    while (iterator.hasNext()) {
      ExceptionQueuedEvent event = iterator.next();
      ExceptionQueuedEventContext eventContext = (ExceptionQueuedEventContext) event.getSource();
      Throwable throwable = eventContext.getException();

      if (ExceptionUtil.getException(throwable, ViewExpiredException.class) != null) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        NavigationHandler navigationHandler = facesContext.getApplication().getNavigationHandler();
        navigationHandler.handleNavigation(facesContext, null, "/view/viewExpired.xhtml");

        iterator.remove();
      }
    }

    super.handle();
  }
}
