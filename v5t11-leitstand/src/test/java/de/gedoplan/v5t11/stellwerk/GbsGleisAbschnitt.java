package de.gedoplan.v5t11.stellwerk;

import de.gedoplan.v5t11.leitstand.entity.fahrstrasse.Fahrstrasse;
import de.gedoplan.v5t11.leitstand.entity.fahrweg.Gleisabschnitt;
import de.gedoplan.v5t11.leitstand.entity.stellwerk.StellwerkElement;
import de.gedoplan.v5t11.stellwerk.util.GbsFarben;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * Gleisabschnitt im Stellwerk.
 *
 * @author dw
 */
public class GbsGleisAbschnitt extends GbsElement {
  private Gleisabschnitt gleisabschnitt = null;
  private boolean label;
  private GbsRichtung[] segmentPos;

  /**
   * Konstruktor.
   *
   * @param bereich
   *          Stellwerksbereich
   * @param stellwerkElement
   *          zugehöriges Stellwerkselement
   */
  public GbsGleisAbschnitt(String bereich, StellwerkElement stellwerkElement) {
    super(bereich, stellwerkElement);

    this.gleisabschnitt = this.leitstand.getGleisabschnitt(stellwerkElement.getBereich(), stellwerkElement.getName());
    if (this.gleisabschnitt != null) {
      this.statusDispatcher.addListener(this.gleisabschnitt, this);
    }

    this.label = stellwerkElement.isLabel();

    String[] segmente = stellwerkElement.getLage().split(",");
    assert segmente.length == 2 : "GBS-Gleisabschnitt muss 2 Segmente haben";
    this.segmentPos = new GbsRichtung[segmente.length];
    for (int i = 0; i < segmente.length; ++i) {
      this.segmentPos[i] = GbsRichtung.valueOf(segmente[i]);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @see de.gedoplan.v5t11.stellwerk.gui.GbsElement#doPaintGleis(java.awt.Graphics2D)
   */
  @Override
  public void doPaintGleis(Graphics2D g2d) {
    // Gleis zeichnen (dicker Strich)
    Color color = getGleisFarbe(this.gleisabschnitt);
    for (int i = 0; i < this.segmentPos.length; ++i) {
      drawGleisSegment(g2d, color, this.segmentPos[i]);
    }

    // Falls relevant, Fahrstrasse zeichnen (dünner Strich in der Gleismitte)
    if (this.gleisabschnitt != null) {
      boolean zeichneFahrstrasse = false;

      switch (this.fahrstrassenManager.getGleisabschnittReservierung(this.gleisabschnitt)) {
      case ZUGFAHRT:
        color = GbsFarben.GLEIS_IN_ZUGFAHRSTRASSE;
        zeichneFahrstrasse = true;
        break;

      case RANGIERFAHRT:
        color = GbsFarben.GLEIS_IN_RANGIERFAHRSTRASSE;
        zeichneFahrstrasse = true;
        break;

      default:
        Fahrstrasse vorgeschlageneFahrstrasse = this.inputPanel.getVorgeschlageneFahrstrasse();
        if (vorgeschlageneFahrstrasse != null) {
          if (vorgeschlageneFahrstrasse.contains(this.gleisabschnitt, false)) {
            color = GbsFarben.GLEIS_IN_VORGESCHLAGENER_FAHRSTRASSE;
            zeichneFahrstrasse = true;
          }
        }
      }

      if (zeichneFahrstrasse) {
        // TODO FS
        // boolean zaehlrichtung = this.gleisabschnitt.isZaehlrichtung();
        boolean zaehlrichtung = false;
        drawFahrstrassenSegment(g2d, color, this.segmentPos[0], !zaehlrichtung);
        drawFahrstrassenSegment(g2d, color, this.segmentPos[1], zaehlrichtung);
      }
    }

    // Bezeichnung zeichnen, falls vorhanden
    if (this.label) {
      g2d.setColor(Color.BLACK);
      int labelSize = VIRTUAL_SIZE / 3;
      g2d.fillRect(VIRTUAL_SIZE / 2 - labelSize / 2, VIRTUAL_SIZE / 2 - labelSize / 2, labelSize, labelSize);

      drawString(g2d, Color.WHITE, VIRTUAL_FONTSIZE_GLEIS, this.name, VIRTUAL_SIZE / 2, VIRTUAL_SIZE / 2);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @see de.gedoplan.v5t11.stellwerk.gui.GbsElement#processMouseClick()
   */
  @Override
  protected void processMouseClick() {
    super.processMouseClick();

    if (this.inputPanel != null) {
      this.inputPanel.addGleisabschnitt(this.gleisabschnitt);
    }
  }
}
