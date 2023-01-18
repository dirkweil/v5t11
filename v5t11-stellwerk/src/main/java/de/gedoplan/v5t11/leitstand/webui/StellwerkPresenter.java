package de.gedoplan.v5t11.leitstand.webui;

import de.gedoplan.v5t11.leitstand.entity.fahrweg.Gleis;
import de.gedoplan.v5t11.leitstand.entity.fahrweg.Signal;
import de.gedoplan.v5t11.leitstand.entity.fahrweg.Weiche;
import de.gedoplan.v5t11.leitstand.entity.stellwerk.Stellwerk;
import de.gedoplan.v5t11.leitstand.entity.stellwerk.StellwerkDkw2;
import de.gedoplan.v5t11.leitstand.entity.stellwerk.StellwerkEinfachWeiche;
import de.gedoplan.v5t11.leitstand.entity.stellwerk.StellwerkElement;
import de.gedoplan.v5t11.leitstand.entity.stellwerk.StellwerkGleis;
import lombok.Getter;
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
  Logger logger;

  @Getter
  private boolean controlPanelEnabled;

  private Gleis startGleis = null;
  private long gleisClickMillis = -1L;

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
    this.logger.tracef("stellwerkElementClicked on %s", uiId);

    StellwerkElement element = this.stellwerkSessionHolder.getElementByUiUd(uiId);
    if (element != null) {
      this.logger.tracef("element: %s %s", element.getClass().getSimpleName(), element.getId());

      this.controlPanelEnabled = false;
      this.text = new StringBuilder();

      if (element instanceof StellwerkGleis stellwerkGleis) {
        gleisClicked(stellwerkGleis.findGleis());
      } else if (element instanceof StellwerkEinfachWeiche stellwerkGleis) {
        gleisClicked(stellwerkGleis.findGleis());
        weicheClicked(stellwerkGleis.findWeiche());
      } else if (element instanceof StellwerkDkw2 stellwerkGleis) {
        gleisClicked(stellwerkGleis.findGleis());
        weicheClicked(stellwerkGleis.findWeicheA());
        weicheClicked(stellwerkGleis.findWeicheB());
      }

      Signal signal = element.findSignal();
      if (signal != null) {
        signalClicked(signal);
      }
    }
  }

  private void gleisClicked(Gleis gleis) {
    this.logger.debugf("gleisClicked: %s", gleis.getId());

    long now = System.currentTimeMillis();
    if (this.gleisClickMillis >= 0L && (now - this.gleisClickMillis) < 3000) {
      this.controlPanelEnabled = true;
      this.text.append(this.startGleis.getName());
      this.text.append("-");
      this.text.append(gleis.getName());
      this.text.append(" ");
    } else {
      this.gleisClickMillis = now;
      this.startGleis = gleis;
    }
  }

  private void weicheClicked(Weiche weiche) {
    this.logger.debugf("weicheClicked: %s", weiche.getId());
    this.controlPanelEnabled = true;
    this.text.append(weiche.getName());
    this.text.append(" ");
  }

  private void signalClicked(Signal signal) {
    this.logger.debugf("signalClicked: %s", signal.getId());
    this.controlPanelEnabled = true;
    this.text.append(signal.getName());
    this.text.append(" ");
  }

  @Getter
  private StringBuilder text = new StringBuilder();
}
