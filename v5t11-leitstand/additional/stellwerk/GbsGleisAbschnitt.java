package de.gedoplan.v5t11.stellwerk;

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

    this.gleisabschnitt = StellwerkMain.getLeitstand().getGleisabschnitt(stellwerkElement.getBereich(), stellwerkElement.getName());
    if (this.gleisabschnitt != null) {
      this.gleisabschnitt.addValueChangedListener(new ValueChangedListener() {
        @Override
        public void valueChanged(ValueChangedEvent event) {
          repaint();
        }
      });
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
      Fahrstrasse fahrstrasse = this.gleisabschnitt.getReservierteFahrstrasse();
      if (fahrstrasse != null) {
        if (fahrstrasse.getReservierungsTyp() != null) {
          color = fahrstrasse.getReservierungsTyp().getGbsFarbe();
        } else {
          fahrstrasse = null;
        }
      }

      if (fahrstrasse == null) {
        fahrstrasse = this.gleisabschnitt.getVorgeschlageneneFahrstrasse();
        if (fahrstrasse != null) {
          color = GbsFarben.GLEIS_IN_VORGESCHLAGENER_FAHRSTRASSE;
        }
      }

      if (fahrstrasse != null) {
        if ("Schattenbahnhof1".equals(this.gleisabschnitt.getBereich()) && "1".equals(this.gleisabschnitt.getName())) {
          System.out.println("S1/1.zaehlrichtung: " + this.gleisabschnitt.isZaehlrichtung());
        }
        boolean zaehlrichtung = this.gleisabschnitt.isZaehlrichtung();
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
