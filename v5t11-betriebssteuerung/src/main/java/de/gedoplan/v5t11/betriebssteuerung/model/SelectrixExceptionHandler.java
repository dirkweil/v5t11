package de.gedoplan.v5t11.betriebssteuerung.model;

import de.gedoplan.baselibs.utils.util.ExceptionUtil;
import de.gedoplan.v5t11.selectrix.SelectrixException;

import java.util.Iterator;

import javax.faces.FacesException;
import javax.faces.application.NavigationHandler;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.FacesContext;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;

public class SelectrixExceptionHandler extends ExceptionHandlerWrapper {
  private ExceptionHandler wrapped;

  public SelectrixExceptionHandler(ExceptionHandler wrapped) {
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

      SelectrixException selectrixException = ExceptionUtil.getException(throwable, SelectrixException.class);
      if (selectrixException != null) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        NavigationHandler navigationHandler = facesContext.getApplication().getNavigationHandler();
        navigationHandler.handleNavigation(facesContext, null, "/view/viewExpired.xhtml");

        // FacesContext facesContext = FacesContext.getCurrentInstance();
        // facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler bei Selectrix-Operation:", selectrixException.getMessage()));
        // NavigationHandler navigationHandler = facesContext.getApplication().getNavigationHandler();
        // navigationHandler.handleNavigation(facesContext, null, "/view/selectrixException.xhtml");

        iterator.remove();
      }
    }

    super.handle();
  }
}
