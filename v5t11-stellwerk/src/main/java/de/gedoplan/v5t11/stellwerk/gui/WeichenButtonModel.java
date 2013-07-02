package de.gedoplan.v5t11.stellwerk.gui;

import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg.geraet.Weiche;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg.geraet.Weiche.Stellung;

import javax.swing.JToggleButton;

public class WeichenButtonModel extends JToggleButton.ToggleButtonModel
{
  private Weiche weiche;

  // private static final Log LOG = LogFactory.getLog(WeichenButtonModel.class);

  public WeichenButtonModel(Weiche weiche)
  {
    this.weiche = weiche;
  }

  @Override
  public boolean isSelected()
  {
    return this.weiche.getStellung() == Weiche.Stellung.ABZWEIGEND;
  }

  @Override
  public void setSelected(boolean b)
  {
    Stellung stellung = b ? Weiche.Stellung.ABZWEIGEND : Weiche.Stellung.GERADE;

    Main.getSteuerungRemoteService().setWeicheStellung(this.weiche.getBereich(), this.weiche.getName(), stellung);
    Main.setStatusLineText(null);
  }

}
