package de.gedoplan.v5t11.stellwerk;

import de.gedoplan.v5t11.leitstand.entity.fahrstrasse.Fahrstrasse;
import de.gedoplan.v5t11.leitstand.entity.fahrstrasse.Fahrstrassenelement;
import de.gedoplan.v5t11.leitstand.entity.stellwerk.StellwerkElement;
import de.gedoplan.v5t11.stellwerk.util.GbsFarben;
import de.gedoplan.v5t11.util.domain.attribute.FahrstrassenReservierungsTyp;
import de.gedoplan.v5t11.util.domain.attribute.WeichenStellung;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GbsDkw2 extends GbsWeicheMit2Antrieben {
  private static final Pattern PATTERN = Pattern.compile("(\\w+)\\|(\\w+),(\\w+)\\|(\\w+)");

  private GbsRichtung[] geradePos = new GbsRichtung[2];
  private GbsRichtung[] abzweigPos = new GbsRichtung[2];

  public GbsDkw2(String bereich, StellwerkElement stellwerkElement) {
    super(bereich, stellwerkElement);

    Matcher matcher = PATTERN.matcher(stellwerkElement.getLage());
    if (matcher.matches()) {
      this.geradePos[0] = GbsRichtung.valueOf(matcher.group(1));
      this.abzweigPos[0] = GbsRichtung.valueOf(matcher.group(2));
      this.geradePos[1] = GbsRichtung.valueOf(matcher.group(3));
      this.abzweigPos[1] = GbsRichtung.valueOf(matcher.group(4));
    } else {
      throw new IllegalArgumentException("Lage muss bei Dkw das Format g|a,g|a haben");
    }

    this.labelPos = findBestFreePosition(this.geradePos[0], this.geradePos[1], this.abzweigPos[0], this.abzweigPos[1]);
  }

  @Override
  public void doPaintGleis(Graphics2D g2d) {
    // Gleise zeichnen
    boolean[] gerade = new boolean[2];

    for (int i = 0; i < 2; ++i) {
      gerade[i] = this.weiche[i] == null || this.weiche[i].getStellung() == WeichenStellung.GERADE;
      drawGleisSegment(g2d, GbsFarben.GLEIS_GESPERRT, gerade[i] ? this.abzweigPos[i] : this.geradePos[i]);
    }

    for (int i = 0; i < 2; ++i) {
      Color color = getGleisFarbe(this.gleisabschnitt);
      drawGleisSegment(g2d, color, gerade[i] ? this.geradePos[i] : this.abzweigPos[i]);
    }

    // Fahrstrasse drauf zeichen, wenn vorhanden
    for (int i = 0; i < 2; ++i) {
      Color color = null;
      if (this.weiche[i] != null) {
        Fahrstrassenelement fahrstrassenelementZuZeichnen = null;
        Fahrstrasse fahrstrasseZuZeichnen = this.fahrstrassenManager.getReservierteFahrstrasse(this.gleisabschnitt);
        if (fahrstrasseZuZeichnen != null) {
          fahrstrassenelementZuZeichnen = fahrstrasseZuZeichnen.getElement(this.weiche[i], false);

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
          if (fahrstrasseZuZeichnen != null && fahrstrasseZuZeichnen.getReservierungsTyp() == FahrstrassenReservierungsTyp.UNRESERVIERT) {
            fahrstrassenelementZuZeichnen = fahrstrasseZuZeichnen.getElement(this.weiche[i], false);
            if (fahrstrassenelementZuZeichnen != null) {
              color = GbsFarben.GLEIS_IN_VORGESCHLAGENER_FAHRSTRASSE;
            }
          }
        }

        if (fahrstrassenelementZuZeichnen != null) {
          gerade[i] = fahrstrassenelementZuZeichnen.getWeichenstellung() == WeichenStellung.GERADE;

          boolean rueckwaerts = (i == 0) ^ fahrstrassenelementZuZeichnen.isZaehlrichtung();
          drawFahrstrassenSegment(g2d, color, gerade[i] ? this.geradePos[i] : this.abzweigPos[i], rueckwaerts);
        }
      }
    }

    // Basisklasse kümmert sich um Bezeichnung
    super.doPaintGleis(g2d);
  }
}
