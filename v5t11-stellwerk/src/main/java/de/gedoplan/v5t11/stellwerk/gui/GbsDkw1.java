package de.gedoplan.v5t11.stellwerk.gui;

import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrstrasse.Fahrstrasse;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg.geraet.Weiche.Stellung;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.stellwerk.StellwerkElement;
import de.gedoplan.v5t11.betriebssteuerung.util.GbsFarben;

import java.awt.Color;
import java.awt.Graphics2D;

public class GbsDkw1 extends GbsWeicheMit1Antrieb
{
  private GbsRichtung einfahrtPos1;
  private GbsRichtung einfahrtPos2;
  private GbsRichtung ausfahrtPos1;
  private GbsRichtung ausfahrtPos2;

  public GbsDkw1(String bereich, StellwerkElement stellwerkElement)
  {
    super(bereich, stellwerkElement);

    String[] segmente = stellwerkElement.getLage().split(",");
    if (segmente.length != 2)
    {
      throw new IllegalArgumentException("Lage muss bei Dkw1 2-teilig sein");
    }

    this.einfahrtPos1 = GbsRichtung.valueOf(segmente[0]);
    this.einfahrtPos2 = GbsRichtung.valueOf(segmente[1]);

    this.ausfahrtPos1 = this.einfahrtPos1.getOpposite();
    this.ausfahrtPos2 = this.einfahrtPos2.getOpposite();

    if (this.label != null)
    {
      this.labelPos = findBestFreePosition(this.einfahrtPos1, this.einfahrtPos2, this.ausfahrtPos1, this.ausfahrtPos2);
    }
  }

  @Override
  public void doPaintGleis(Graphics2D g2d)
  {
    // Gleise zeichnen
    boolean gerade = this.weiche == null || this.weiche.getStellung().equals(Stellung.GERADE);

    Color color = getGleisFarbe(this.gleisabschnitt);

    drawGleisSegment(g2d, GbsFarben.GLEIS_GESPERRT, gerade ? this.ausfahrtPos2 : this.ausfahrtPos1);
    drawGleisSegment(g2d, GbsFarben.GLEIS_GESPERRT, gerade ? this.ausfahrtPos1 : this.ausfahrtPos2);

    drawGleisSegment(g2d, color, gerade ? this.ausfahrtPos1 : this.ausfahrtPos2);
    drawGleisSegment(g2d, color, gerade ? this.ausfahrtPos2 : this.ausfahrtPos1);

    // Fahrstrasse drauf zeichen, wenn vorhanden
    if (this.weiche != null)
    {
      Fahrstrasse fahrstrasse = this.weiche.getReservierteFahrstrasse();
      if (fahrstrasse != null)
      {
        color = fahrstrasse.getReservierungsTyp().getGbsFarbe();
      }
      else
      {
        fahrstrasse = this.weiche.getVorgeschlageneneFahrstrasse();
        if (fahrstrasse != null)
        {
          color = GbsFarben.GLEIS_IN_VORGESCHLAGENER_FAHRSTRASSE;
        }
      }

      if (fahrstrasse != null)
      {
        Stellung stellungFuerFahrstrasse = this.weiche.getStellungFuerFahrstrassenvorschlag();
        if (stellungFuerFahrstrasse == null)
        {
          stellungFuerFahrstrasse = this.weiche.getStellung();
        }
        gerade = stellungFuerFahrstrasse == Stellung.GERADE;

        // TODO zählrichtung
        drawFahrstrassenSegment(g2d, color, gerade ? this.ausfahrtPos1 : this.ausfahrtPos2, false);
        drawFahrstrassenSegment(g2d, color, gerade ? this.ausfahrtPos2 : this.ausfahrtPos1, false);
      }
    }

    // Basisklasse kümmert sich um Bezeichnung
    super.doPaintGleis(g2d);
  }
}
