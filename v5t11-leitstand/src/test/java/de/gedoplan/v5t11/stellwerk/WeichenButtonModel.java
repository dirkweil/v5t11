package de.gedoplan.v5t11.stellwerk;

import de.gedoplan.baselibs.utils.inject.InjectionUtil;
import de.gedoplan.v5t11.leitstand.entity.fahrweg.Weiche;
import de.gedoplan.v5t11.leitstand.gateway.WeicheResourceClient;
import de.gedoplan.v5t11.util.domain.attribute.WeichenStellung;

import javax.inject.Inject;
import javax.swing.JToggleButton;

public class WeichenButtonModel extends JToggleButton.ToggleButtonModel {
  private Weiche weiche;

  @Inject
  WeicheResourceClient weicheResourceClient;

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

    this.weicheResourceClient.weicheStellen(this.weiche, stellung);
    StellwerkMain.setStatusLineText(null);
  }

}
