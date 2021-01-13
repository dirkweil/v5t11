package de.gedoplan.v5t11.stellwerk;

import de.gedoplan.v5t11.leitstand.entity.fahrweg.Weiche;
import de.gedoplan.v5t11.leitstand.entity.stellwerk.StellwerkElement;
import de.gedoplan.v5t11.leitstand.persistence.WeicheRepository;

import javax.inject.Inject;

public class GbsWeicheMit1Antrieb extends GbsWeiche {

  protected Weiche weiche;

  @Inject
  WeicheRepository weicheRepository;

  public GbsWeicheMit1Antrieb(String bereich, StellwerkElement stellwerkElement) {
    super(bereich, stellwerkElement);

    this.weiche = this.weicheRepository.findById(stellwerkElement.getId());
    if (this.weiche != null) {
      this.statusDispatcher.addListener(this.weiche, w -> {
        this.weiche = w;
        repaint();
      });
    }
  }

  @Override
  protected void processMouseClick() {
    super.processMouseClick();

    if (this.inputPanel != null) {
      this.inputPanel.addWeiche(this.weiche, this.gleis);
    }
  }
}
