package de.gedoplan.v5t11.stellwerk;

import de.gedoplan.v5t11.leitstand.entity.fahrweg.Gleis;
import de.gedoplan.v5t11.leitstand.entity.stellwerk.StellwerkElement;
import de.gedoplan.v5t11.leitstand.entity.stellwerk.StellwerkRichtung;
import de.gedoplan.v5t11.leitstand.persistence.GleisRepository;
import de.gedoplan.v5t11.util.domain.attribute.BereichselementId;

import javax.inject.Inject;
import java.awt.*;
import java.awt.geom.AffineTransform;

public abstract class GbsWeiche extends GbsElement {

  protected StellwerkRichtung labelPos = null;
  protected String label = null;
  protected Gleis gleis;

  @Inject
  GleisRepository gleisRepository;

  public GbsWeiche(String bereich, StellwerkElement stellwerkElement) {
    super(bereich, stellwerkElement);

    this.gleis = this.gleisRepository.findById(new BereichselementId(stellwerkElement.getBereich(), "W" + stellwerkElement.getName()));
    if (this.gleis != null) {
      this.statusDispatcher.addListener(this.gleis, g -> {
        this.gleis = g;
        repaint();
      });
    }

    this.label = this.name;
  }

  @Override
  public void doPaintGleis(Graphics2D g2d) {
    if (this.label != null && this.labelPos != null) {
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
