package de.gedoplan.v5t11.stellwerk;

import de.gedoplan.v5t11.leitstand.entity.stellwerk.StellwerkElement;
import de.gedoplan.v5t11.stellwerk.util.GbsFarben;
import de.gedoplan.v5t11.util.domain.attribute.WeichenStellung;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GbsEinfachWeiche extends GbsWeicheMit1Antrieb {
  private GbsRichtung stammPos;
  private GbsRichtung geradePos;
  private GbsRichtung abzweigendPos;
  private boolean stammIstEinfahrt;

  private static final Pattern PATTERN_VORWAERTS = Pattern.compile("(\\w+),(\\w+)\\|(\\w+)");
  private static final Pattern PATTERN_RUECKWAERTS = Pattern.compile("(\\w+)\\|(\\w+),(\\w+)");

  public GbsEinfachWeiche(String bereich, StellwerkElement stellwerkElement) {
    super(bereich, stellwerkElement);

    String lage = stellwerkElement.getLage();
    Matcher matcher = PATTERN_VORWAERTS.matcher(lage);
    if (matcher.matches()) {
      this.stammPos = GbsRichtung.valueOf(matcher.group(1));
      this.geradePos = GbsRichtung.valueOf(matcher.group(2));
      this.abzweigendPos = GbsRichtung.valueOf(matcher.group(3));
      this.stammIstEinfahrt = true;
    } else {
      matcher = PATTERN_RUECKWAERTS.matcher(lage);
      if (matcher.matches()) {
        this.stammPos = GbsRichtung.valueOf(matcher.group(3));
        this.geradePos = GbsRichtung.valueOf(matcher.group(1));
        this.abzweigendPos = GbsRichtung.valueOf(matcher.group(2));
        this.stammIstEinfahrt = false;
      } else {
        throw new IllegalArgumentException("Lage muss bei Weichen das Format s,g|a oder g|a,s haben");
      }
    }

    if (this.label != null) {
      this.labelPos = findBestFreePosition(this.stammPos, this.geradePos, this.abzweigendPos);
    }
  }

  @Override
  public void doPaintGleis(Graphics2D g2d) {
    // Gleise zeichnen
    boolean gerade = this.weiche == null || this.weiche.getStellung() == WeichenStellung.GERADE;

    Color color = getGleisFarbe(this.gleisabschnitt);
    drawGleisSegment(g2d, GbsFarben.GLEIS_GESPERRT, gerade ? this.abzweigendPos : this.geradePos);
    drawGleisSegment(g2d, color, gerade ? this.geradePos : this.abzweigendPos);
    drawGleisSegment(g2d, color, this.stammPos);

    // Fahrstrasse drauf zeichen, wenn vorhanden
    if (this.weiche != null) {
      Fahrstrasse fahrstrasse = this.weiche.getReservierteFahrstrasse();
      if (fahrstrasse != null) {
        if (fahrstrasse.getReservierungsTyp() != null) {
          color = fahrstrasse.getReservierungsTyp().getGbsFarbe();
        } else {
          fahrstrasse = null;
        }
      }

      if (fahrstrasse == null) {
        fahrstrasse = this.weiche.getVorgeschlageneneFahrstrasse();
        if (fahrstrasse != null) {
          color = GbsFarben.GLEIS_IN_VORGESCHLAGENER_FAHRSTRASSE;
        }
      }

      if (fahrstrasse != null) {
        WeichenStellung stellungFuerFahrstrasse = this.weiche.getStellungFuerFahrstrassenvorschlag();
        if (stellungFuerFahrstrasse == null) {
          stellungFuerFahrstrasse = this.weiche.getStellung();
        }
        gerade = stellungFuerFahrstrasse == WeichenStellung.GERADE;

        boolean rueckwaerts = this.weiche.isZaehlrichtung() ^ this.stammIstEinfahrt;
        drawFahrstrassenSegment(g2d, color, gerade ? this.geradePos : this.abzweigendPos, !rueckwaerts);
        drawFahrstrassenSegment(g2d, color, this.stammPos, rueckwaerts);
      }
    }

    // Basisklasse kümmert sich um Bezeichnung
    super.doPaintGleis(g2d);
  }
}
