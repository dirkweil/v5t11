package de.gedoplan.v5t11.stellwerk.gui;

import de.gedoplan.v5t11.betriebssteuerung.listener.ValueChangedEvent;
import de.gedoplan.v5t11.betriebssteuerung.listener.ValueChangedListener;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg.Gleisabschnitt;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.stellwerk.StellwerkElement;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

public abstract class GbsWeiche extends GbsElement
{
  protected GbsRichtung    labelPos = null;
  protected String         label    = null;
  protected Gleisabschnitt gleisabschnitt;

  public GbsWeiche(String bereich, StellwerkElement stellwerkElement)
  {
    super(bereich, stellwerkElement);

    this.gleisabschnitt = Main.getSteuerung().getGleisabschnitt(stellwerkElement.getBereich(), "W" + stellwerkElement.getName());
    if (this.gleisabschnitt != null)
    {
      this.gleisabschnitt.addValueChangedListener(new ValueChangedListener()
      {
        @Override
        public void valueChanged(ValueChangedEvent event)
        {
          repaint();
        }
      });
    }

    this.label = this.name;
  }

  @Override
  public void doPaintGleis(Graphics2D g2d)
  {
    if (this.label != null && this.labelPos != null)
    {
      AffineTransform oldTransform = g2d.getTransform();

      // Koordinatensystem so drehen, dass das Label oben ist
      g2d.rotate(Math.toRadians(this.labelPos.getWinkel()), VIRTUAL_SIZE / 2, VIRTUAL_SIZE / 2);

      // Koordinatensystem zur Schreibposition verschieben
      g2d.translate(VIRTUAL_SIZE / 2, VIRTUAL_SIZE / 6);

      // Koordinatensystem an dieser Stelle wieder zurückdrehen, damit der Text waagerecht liegt
      g2d.rotate(-Math.toRadians(this.labelPos.getWinkel()));

      // Bezeichnung schreiben
      drawString(g2d, Color.BLACK, VIRTUAL_FONTSIZE_WEICHE, this.label, 0, 0);

      g2d.setTransform(oldTransform);
    }
  }
}
