package de.gedoplan.v5t11.stellwerk;

import de.gedoplan.v5t11.leitstand.entity.fahrweg.OldWeiche;
import de.gedoplan.v5t11.leitstand.entity.stellwerk.StellwerkElement;

public class GbsWeicheMit1Antrieb extends GbsWeiche {
  protected OldWeiche weiche;

  public GbsWeicheMit1Antrieb(String bereich, StellwerkElement stellwerkElement) {
    super(bereich, stellwerkElement);

    this.weiche = this.leitstand.getWeiche(stellwerkElement.getBereich(), stellwerkElement.getName());
    if (this.weiche != null) {
      this.statusDispatcher.addListener(this.weiche, this::repaint);
    }
  }

  @Override
  protected void processMouseClick() {
    super.processMouseClick();

    if (this.inputPanel != null) {
      this.inputPanel.addWeiche(this.weiche, this.gleisabschnitt);
    }
  }
}
