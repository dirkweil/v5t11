package de.gedoplan.v5t11.stellwerk;

import de.gedoplan.v5t11.leitstand.entity.fahrweg.Weiche;
import de.gedoplan.v5t11.util.domain.attribute.WeichenStellung;

import javax.swing.JToggleButton;

public class WeichenButtonModel extends JToggleButton.ToggleButtonModel {
  private Weiche weiche;

  // private static final Log LOG = LogFactory.getLog(WeichenButtonModel.class);

  public WeichenButtonModel(Weiche weiche) {
    this.weiche = weiche;
  }

  @Override
  public boolean isSelected() {
    return this.weiche.getStellung() == WeichenStellung.ABZWEIGEND;
  }

  @Override
  public void setSelected(boolean b) {
    WeichenStellung stellung = b ? WeichenStellung.ABZWEIGEND : WeichenStellung.GERADE;

    // TODO Action
    // StellwerkMain.getSteuerungRemoteService().setWeicheStellung(this.weiche.getBereich(), this.weiche.getName(), stellung);
    StellwerkMain.setStatusLineText(null);
  }

}
