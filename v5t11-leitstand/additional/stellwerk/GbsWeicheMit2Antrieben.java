package de.gedoplan.v5t11.stellwerk;

import de.gedoplan.v5t11.leitstand.entity.fahrweg.Weiche;
import de.gedoplan.v5t11.leitstand.entity.stellwerk.StellwerkElement;

public class GbsWeicheMit2Antrieben extends GbsWeiche {
  protected Weiche[] weiche = new Weiche[2];

  public GbsWeicheMit2Antrieben(String bereich, StellwerkElement stellwerkElement) {
    super(bereich, stellwerkElement);

    String[] namen = this.name.split(",");
    if (namen.length == 1) {
      namen = new String[] { this.name + "a", this.name + "b" };
    }
    if (namen.length != 2) {
      throw new IllegalArgumentException("Weiche mit 2 Antrieben benötigt zwei Namen: " + this.name);
    }

    for (int i = 0; i < 2; ++i) {
      this.weiche[i] = StellwerkMain.getSteuerung().getWeiche(stellwerkElement.getBereich(), namen[i]);
      if (this.weiche[i] != null) {
        this.weiche[i].addValueChangedListener(new ValueChangedListener() {
          @Override
          public void valueChanged(ValueChangedEvent event) {
            repaint();
          }
        });
      }
    }
  }

  @Override
  protected void processMouseClick() {
    super.processMouseClick();

    if (this.inputPanel != null) {
      this.inputPanel.addWeiche(this.weiche[0]);
      this.inputPanel.addWeiche(this.weiche[1]);
    }
  }
}
