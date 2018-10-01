package de.gedoplan.v5t11.stellwerk;

import de.gedoplan.v5t11.leitstand.entity.fahrstrasse.Fahrstrasse;
import de.gedoplan.v5t11.leitstand.entity.stellwerk.StellwerkElement;
import de.gedoplan.v5t11.stellwerk.util.GbsFarben;
import de.gedoplan.v5t11.util.domain.attribute.WeichenStellung;

import java.awt.Color;
import java.awt.Graphics2D;

public class GbsDkw1 extends GbsWeicheMit1Antrieb {
  private GbsRichtung einfahrtPos1;
  private GbsRichtung einfahrtPos2;
  private GbsRichtung ausfahrtPos1;
  private GbsRichtung ausfahrtPos2;

  public GbsDkw1(String bereich, StellwerkElement stellwerkElement) {
    super(bereich, stellwerkElement);

    String[] segmente = stellwerkElement.getLage().split(",");
    if (segmente.length != 2) {
      throw new IllegalArgumentException("Lage muss bei Dkw1 2-teilig sein");
    }

    this.einfahrtPos1 = GbsRichtung.valueOf(segmente[0]);
    this.einfahrtPos2 = GbsRichtung.valueOf(segmente[1]);

    this.ausfahrtPos1 = this.einfahrtPos1.getOpposite();
    this.ausfahrtPos2 = this.einfahrtPos2.getOpposite();

    if (this.label != null) {
      this.labelPos = findBestFreePosition(this.einfahrtPos1, this.einfahrtPos2, this.ausfahrtPos1, this.ausfahrtPos2);
    }
  }

  @Override
  public void doPaintGleis(Graphics2D g2d) {
    // Gleise zeichnen
    boolean gerade = this.weiche == null || this.weiche.getStellung().equals(WeichenStellung.GERADE);

    Color color = getGleisFarbe(this.gleisabschnitt);

    drawGleisSegment(g2d, GbsFarben.GLEIS_GESPERRT, gerade ? this.ausfahrtPos2 : this.ausfahrtPos1);
    drawGleisSegment(g2d, GbsFarben.GLEIS_GESPERRT, gerade ? this.ausfahrtPos1 : this.ausfahrtPos2);

    drawGleisSegment(g2d, color, gerade ? this.ausfahrtPos1 : this.ausfahrtPos2);
    drawGleisSegment(g2d, color, gerade ? this.ausfahrtPos2 : this.ausfahrtPos1);

    // Fahrstrasse drauf zeichen, wenn vorhanden
    if (this.weiche != null) {
      Fahrstrasse fahrstrasseZuZeichnen = this.fahrstrassenManager.getReservierteFahrstrasse(this.gleisabschnitt);
      if (fahrstrasseZuZeichnen != null) {
        switch (fahrstrasseZuZeichnen.getReservierungsTyp()) {
        case ZUGFAHRT:
          color = GbsFarben.GLEIS_IN_ZUGFAHRSTRASSE;
          break;

        case RANGIERFAHRT:
          color = GbsFarben.GLEIS_IN_RANGIERFAHRSTRASSE;
          break;

        default:
        }
      } else {
        fahrstrasseZuZeichnen = this.inputPanel.getVorgeschlageneFahrstrasse();
        if (fahrstrasseZuZeichnen != null && fahrstrasseZuZeichnen.getElement(this.weiche, false) != null) {
          color = GbsFarben.GLEIS_IN_VORGESCHLAGENER_FAHRSTRASSE;
        } else {
          fahrstrasseZuZeichnen = null;
        }
      }

      if (fahrstrasseZuZeichnen != null) {
        WeichenStellung stellungFuerFahrstrasse = fahrstrasseZuZeichnen.getElement(this.weiche, false).getWeichenstellung();
        gerade = stellungFuerFahrstrasse == WeichenStellung.GERADE;

        // TODO Zählrichtung
        drawFahrstrassenSegment(g2d, color, gerade ? this.ausfahrtPos1 : this.ausfahrtPos2, false);
        drawFahrstrassenSegment(g2d, color, gerade ? this.ausfahrtPos2 : this.ausfahrtPos1, false);
      }
    }

    // Basisklasse kümmert sich um Bezeichnung
    super.doPaintGleis(g2d);
  }
}
