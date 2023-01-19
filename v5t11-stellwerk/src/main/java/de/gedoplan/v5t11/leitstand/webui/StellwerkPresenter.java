package de.gedoplan.v5t11.leitstand.webui;

import de.gedoplan.v5t11.leitstand.entity.fahrweg.Gleis;
import de.gedoplan.v5t11.leitstand.entity.fahrweg.Signal;
import de.gedoplan.v5t11.leitstand.entity.fahrweg.Weiche;
import de.gedoplan.v5t11.leitstand.entity.stellwerk.Stellwerk;
import de.gedoplan.v5t11.leitstand.entity.stellwerk.StellwerkDkw2;
import de.gedoplan.v5t11.leitstand.entity.stellwerk.StellwerkEinfachWeiche;
import de.gedoplan.v5t11.leitstand.entity.stellwerk.StellwerkElement;
import de.gedoplan.v5t11.leitstand.entity.stellwerk.StellwerkGleis;
import de.gedoplan.v5t11.util.domain.attribute.WeichenStellung;
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

  @Getter
  private Weiche weiche;

  @Getter
  private Weiche weiche2;

  @Getter
  private Signal signal;

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

      clearControlPanel();

      if (element instanceof StellwerkGleis stellwerkGleis) {
        gleisClicked(stellwerkGleis.findGleis());
      } else if (element instanceof StellwerkEinfachWeiche stellwerkGleis) {
        gleisClicked(stellwerkGleis.findGleis());
        weicheClicked(stellwerkGleis.findWeiche(), null);
      } else if (element instanceof StellwerkDkw2 stellwerkGleis) {
        gleisClicked(stellwerkGleis.findGleis());
        weicheClicked(stellwerkGleis.findWeicheA(), stellwerkGleis.findWeicheB());
      }

      Signal signal = element.findSignal();
      if (signal != null) {
        signalClicked(signal);
      }
    }
  }

  public void clearControlPanel() {
    this.controlPanelEnabled = false;
    this.weiche = null;
    this.weiche2 = null;
    this.signal = null;
    this.text = new StringBuilder();
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

  private void weicheClicked(Weiche weiche, Weiche weiche2) {
    this.logger.debugf("weicheClicked: %s", weiche.getId());

    this.weiche = weiche;
    this.weiche2 = weiche2;

    this.controlPanelEnabled = true;
  }

  public WeichenStellung[] getWeichenStellungen() {
    return WeichenStellung.values();
  }

  private void signalClicked(Signal signal) {
    this.logger.debugf("signalClicked: %s", signal.getId());

    this.signal = signal;

    this.controlPanelEnabled = true;
  }

  @Getter
  private StringBuilder text = new StringBuilder();
}
