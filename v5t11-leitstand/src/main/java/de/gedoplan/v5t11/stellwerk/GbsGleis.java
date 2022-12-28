package de.gedoplan.v5t11.stellwerk;

import de.gedoplan.v5t11.leitstand.entity.fahrstrasse.Fahrstrasse;
import de.gedoplan.v5t11.leitstand.entity.fahrstrasse.Fahrstrassenelement;
import de.gedoplan.v5t11.leitstand.entity.fahrweg.Gleis;
import de.gedoplan.v5t11.leitstand.entity.stellwerk.StellwerkElement;
import de.gedoplan.v5t11.leitstand.entity.stellwerk.StellwerkRichtung;
import de.gedoplan.v5t11.leitstand.persistence.GleisRepository;
import de.gedoplan.v5t11.stellwerk.util.GbsFarben;
import de.gedoplan.v5t11.util.domain.attribute.FahrstrassenReservierungsTyp;

import javax.inject.Inject;
import java.awt.*;

/**
 * Gleis im Stellwerk.
 *
 * @author dw
 */
public class GbsGleis extends GbsElement {

  private Gleis gleis = null;
  private boolean label;
  private StellwerkRichtung[] segmentPos;

  @Inject
  GleisRepository gleisRepository;

  /**
   * Konstruktor.
   *
   * @param bereich
   *   Stellwerksbereich
   * @param stellwerkElement
   *   zugehöriges Stellwerkselement
   */
  public GbsGleis(String bereich, StellwerkElement stellwerkElement) {
    super(bereich, stellwerkElement);

    this.gleis = this.gleisRepository.findById(stellwerkElement.getId());
    if (this.gleis != null) {
      this.statusDispatcher.addListener(this.gleis, g -> {
        this.gleis = g;
        repaint();
      });
    }

    this.label = stellwerkElement.isLabel();

    String[] segmente = stellwerkElement.getLage().split(",");
    assert segmente.length == 2 : "GBS-Gleis muss 2 Segmente haben";
    this.segmentPos = new StellwerkRichtung[segmente.length];
    for (int i = 0; i < segmente.length; ++i) {
      this.segmentPos[i] = StellwerkRichtung.valueOf(segmente[i]);
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
    Color color = getGleisFarbe(this.gleis);
    for (int i = 0; i < this.segmentPos.length; ++i) {
      drawGleisSegment(g2d, color, this.segmentPos[i]);
    }

    // Falls relevant, Fahrstrasse zeichnen (dünner Strich in der Gleismitte)
    if (this.gleis != null) {
      Fahrstrassenelement fahrstrassenelementZuZeichnen = null;
      Fahrstrasse fahrstrasseZuZeichnen = this.fahrstrassenManager.getReservierteFahrstrasse(this.gleis);
      if (fahrstrasseZuZeichnen != null) {
        fahrstrassenelementZuZeichnen = fahrstrasseZuZeichnen.getElement(this.gleis, true);

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
          fahrstrassenelementZuZeichnen = fahrstrasseZuZeichnen.getElement(this.gleis, false);
          if (fahrstrassenelementZuZeichnen != null) {
            color = GbsFarben.GLEIS_IN_VORGESCHLAGENER_FAHRSTRASSE;
          }
        }
      }

      if (fahrstrassenelementZuZeichnen != null) {
        boolean zaehlrichtung = fahrstrassenelementZuZeichnen.isZaehlrichtung();
        drawFahrstrassenSegment(g2d, color, this.segmentPos[0], !zaehlrichtung);
        drawFahrstrassenSegment(g2d, color, this.segmentPos[1], zaehlrichtung);
      }
    }

    // Bezeichnung zeichnen, falls vorhanden
    if (this.label) {
      g2d.setColor(Color.BLACK);
      int labelSize = VIRTUAL_SIZE / 2;
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
      this.inputPanel.addGleis(this.gleis);
    }
  }
}
