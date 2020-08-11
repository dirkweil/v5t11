package de.gedoplan.v5t11.stellwerk;

import de.gedoplan.v5t11.leitstand.entity.stellwerk.StellwerkElement;

import java.awt.Graphics2D;

/**
 * @author dw
 */
public class GbsLeer extends GbsElement
{
  /**
   * Konstruktor.
   * 
   * @param bereich
   * @param stellwerkElement
   */
  public GbsLeer(String bereich, StellwerkElement stellwerkElement)
  {
    super(bereich, stellwerkElement);
  }

  @Override
  protected void translate(Graphics2D g2d)
  {
    // REVIEW: Wozu dient das hier?
    g2d.translate(0, VIRTUAL_SIZE / 2 - VIRTUAL_STROKEWIDTH_GLEIS + 2);
  }
}
