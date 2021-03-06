package de.gedoplan.v5t11.stellwerk.gui;

import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrstrasse.Fahrstrasse;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg.geraet.Weiche.Stellung;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.stellwerk.StellwerkElement;
import de.gedoplan.v5t11.betriebssteuerung.util.GbsFarben;

import java.awt.Color;
import java.awt.Graphics2D;

public class GbsDkw2 extends GbsWeicheMit2Antrieben
{
  private GbsRichtung[] geradePos  = new GbsRichtung[2];
  private GbsRichtung[] abzweigPos = new GbsRichtung[2];

  public GbsDkw2(String bereich, StellwerkElement stellwerkElement)
  {
    super(bereich, stellwerkElement);

    String[] segmente = stellwerkElement.getLage().split(",");
    if (segmente.length != 2)
    {
      throw new IllegalArgumentException("Lage muss bei Dkw2 2-teilig sein");
    }

    this.geradePos[0] = GbsRichtung.valueOf(segmente[0]);
    this.abzweigPos[0] = GbsRichtung.valueOf(segmente[1]);

    this.geradePos[1] = this.geradePos[0].getOpposite();
    this.abzweigPos[1] = this.abzweigPos[0].getOpposite();

    this.labelPos = findBestFreePosition(this.geradePos[0], this.geradePos[1], this.abzweigPos[0], this.abzweigPos[1]);
  }

  @Override
  public void doPaintGleis(Graphics2D g2d)
  {
    // Gleise zeichnen
    boolean[] gerade = new boolean[2];

    for (int i = 0; i < 2; ++i)
    {
      gerade[i] = this.weiche[i] == null || this.weiche[i].getStellung() == Stellung.GERADE;
      drawGleisSegment(g2d, GbsFarben.GLEIS_GESPERRT, gerade[i] ? this.abzweigPos[i] : this.geradePos[i]);
    }

    for (int i = 0; i < 2; ++i)
    {
      Color color = getGleisFarbe(this.gleisabschnitt);
      drawGleisSegment(g2d, color, gerade[i] ? this.geradePos[i] : this.abzweigPos[i]);
    }

    // Fahrstrasse drauf zeichen, wenn vorhanden
    for (int i = 0; i < 2; ++i)
    {
      Color color = null;
      if (this.weiche[i] != null)
      {
        Stellung stellungFuerFahrstrasse = null;
        Fahrstrasse fahrstrasse = this.weiche[i].getReservierteFahrstrasse();
        if (fahrstrasse != null)
        {
          color = fahrstrasse.getReservierungsTyp().getGbsFarbe();
          stellungFuerFahrstrasse = this.weiche[i].getStellung();
        }
        else
        {
          fahrstrasse = this.weiche[i].getVorgeschlageneneFahrstrasse();
          if (fahrstrasse != null)
          {
            color = GbsFarben.GLEIS_IN_VORGESCHLAGENER_FAHRSTRASSE;
            stellungFuerFahrstrasse = this.weiche[i].getStellungFuerFahrstrassenvorschlag();
          }
        }

        if (fahrstrasse != null)
        {
          boolean rueckwaerts = (i == 0) ^ this.weiche[i].isZaehlrichtung();
          drawFahrstrassenSegment(g2d, color, stellungFuerFahrstrasse == Stellung.GERADE ? this.geradePos[i] : this.abzweigPos[i], rueckwaerts);
        }
      }
    }

    // Basisklasse kümmert sich um Bezeichnung
    super.doPaintGleis(g2d);
  }
}
