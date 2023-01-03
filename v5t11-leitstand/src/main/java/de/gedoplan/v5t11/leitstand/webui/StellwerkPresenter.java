package de.gedoplan.v5t11.leitstand.webui;

import de.gedoplan.v5t11.leitstand.entity.stellwerk.Stellwerk;
import org.jboss.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

@Named
@ViewScoped
public class StellwerkPresenter implements Serializable {

  @Inject
  StellwerkSessionHolder stellwerkSessionHolder;

  @Inject
  PushService2 pushService2;

  @Inject
  Logger logger;

  @PostConstruct
  void postConstruct() {
    String bereich = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("bereich");
    this.logger.debugf("bereich: %s", bereich);

    this.stellwerkSessionHolder.changeBereich(bereich);
  }

  public Stellwerk getStellwerk() {
    return this.stellwerkSessionHolder.getStellwerk();
  }

  public void elementClicked() {
    String uiId = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("uiId");
    this.logger.debugf("stellwerkElementClicked on %s", uiId);
    this.logger.debugf("pushService2: " + this.pushService2);
  }
}
