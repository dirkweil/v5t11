package de.gedoplan.v5t11.stellwerk.gui;

import de.gedoplan.v5t11.betriebssteuerung.listener.ValueChangedEvent;
import de.gedoplan.v5t11.betriebssteuerung.listener.ValueChangedListener;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg.geraet.Weiche;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.stellwerk.StellwerkElement;

public class GbsWeicheMit1Antrieb extends GbsWeiche
{
  protected Weiche weiche;

  public GbsWeicheMit1Antrieb(String bereich, StellwerkElement stellwerkElement)
  {
    super(bereich, stellwerkElement);

    this.weiche = Main.getSteuerung().getWeiche(stellwerkElement.getBereich(), stellwerkElement.getName());
    if (this.weiche != null)
    {
      this.weiche.addValueChangedListener(new ValueChangedListener()
      {
        @Override
        public void valueChanged(ValueChangedEvent event)
        {
          repaint();
        }
      });
    }
  }

  @Override
  protected void processMouseClick()
  {
    super.processMouseClick();

    if (this.inputPanel != null)
    {
      this.inputPanel.addWeiche(this.weiche);
    }
  }
}
