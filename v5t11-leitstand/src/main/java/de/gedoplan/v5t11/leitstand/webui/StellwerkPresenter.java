package de.gedoplan.v5t11.leitstand.webui;

import de.gedoplan.v5t11.leitstand.entity.stellwerk.Stellwerk;

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

  @PostConstruct
  void postConstruct() {
    this.stellwerkSessionHolder.changeBereich(FacesContext.getCurrentInstance()
      .getExternalContext().getRequestParameterMap().get("bereich"));
  }

  public Stellwerk getStellwerk() {
    return this.stellwerkSessionHolder.getStellwerk();
  }
}
