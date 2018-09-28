package de.gedoplan.v5t11.stellwerk;

import de.gedoplan.v5t11.leitstand.entity.fahrweg.Weiche;
import de.gedoplan.v5t11.leitstand.entity.stellwerk.StellwerkElement;

public class GbsWeicheMit1Antrieb extends GbsWeiche {
  protected Weiche weiche;

  public GbsWeicheMit1Antrieb(String bereich, StellwerkElement stellwerkElement) {
    super(bereich, stellwerkElement);

    this.weiche = StellwerkMain.getLeitstand().getWeiche(stellwerkElement.getBereich(), stellwerkElement.getName());
    if (this.weiche != null) {
      StatusDispatcher.addListener(this.weiche, this);
    }
  }

  @Override
  protected void processMouseClick() {
    super.processMouseClick();

    if (this.inputPanel != null) {
      this.inputPanel.addWeiche(this.weiche);
    }
  }
}
