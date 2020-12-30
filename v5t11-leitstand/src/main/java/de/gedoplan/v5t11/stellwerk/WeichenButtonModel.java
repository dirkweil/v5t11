package de.gedoplan.v5t11.stellwerk;

import de.gedoplan.baselibs.utils.inject.InjectionUtil;
import de.gedoplan.v5t11.leitstand.entity.fahrweg.Weiche;
import de.gedoplan.v5t11.leitstand.gateway.StatusGateway;
import de.gedoplan.v5t11.util.domain.attribute.WeichenStellung;

import javax.inject.Inject;
import javax.swing.JToggleButton;

import org.eclipse.microprofile.rest.client.inject.RestClient;

public class WeichenButtonModel extends JToggleButton.ToggleButtonModel {
  private Weiche weiche;

  @Inject
  @RestClient
  StatusGateway statusGateway;

  // private static final Log LOG = LogFactory.getLog(WeichenButtonModel.class);

  public WeichenButtonModel(Weiche weiche) {
    InjectionUtil.injectFields(this);

    this.weiche = weiche;
  }

  @Override
  public boolean isSelected() {
    return this.weiche.getStellung() == WeichenStellung.ABZWEIGEND;
  }

  @Override
  public void setSelected(boolean b) {
    WeichenStellung stellung = b ? WeichenStellung.ABZWEIGEND : WeichenStellung.GERADE;

    this.statusGateway.weicheStellen(this.weiche.getBereich(), this.weiche.getName(), stellung);
    StellwerkUI.setStatusLineText(null);
  }

  public void setWeiche(Weiche weiche) {
    this.weiche = weiche;
    fireStateChanged();
  }

}
