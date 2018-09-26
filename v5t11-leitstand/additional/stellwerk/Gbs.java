package de.gedoplan.v5t11.stellwerk;

import de.gedoplan.v5t11.leitstand.entity.stellwerk.Stellwerk;
import de.gedoplan.v5t11.leitstand.entity.stellwerk.StellwerkElement;
import de.gedoplan.v5t11.leitstand.entity.stellwerk.StellwerkZeile;
import de.gedoplan.v5t11.stellwerk.util.GridBagHelper;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;

import javax.swing.JPanel;

public class Gbs extends ApplicationPanel {
  private String bereich;

  public Gbs(String bereich) {
    this.bereich = bereich;

    setLayout(new BorderLayout());

    JPanel gbsPanel = new JPanel();
    add(gbsPanel, BorderLayout.NORTH);

    // GbsInputPanel inputPanel = new GbsInputPanel(bereich);

    GridBagHelper gbHelper = new GridBagHelper(gbsPanel);

    Stellwerk stellwerk = StellwerkMain.getLeitstand().getStellwerk(bereich);
    for (StellwerkZeile stellwerkZeile : stellwerk.getZeilen()) {
      JPanel leftFiller = new JPanel();
      // leftFiller.setBackground(Color.GREEN);
      gbHelper.add(leftFiller, 1, 1, 0, 1, GridBagConstraints.BOTH, GridBagConstraints.CENTER);

      for (StellwerkElement stellwerkElement : stellwerkZeile.getElemente()) {
        for (int i = 0; i < stellwerkElement.getAnzahl(); ++i) {
          GbsElement gbsElement = GbsElement.createInstance(bereich, stellwerkElement);
          // gbsElement.setInputPanel(inputPanel);

          gbHelper.add(gbsElement, 1, 1, 0, 0, GridBagConstraints.BOTH, GridBagConstraints.CENTER);
        }
      }

      JPanel rightFiller = new JPanel();
      // rightFiller.setBackground(Color.GREEN);
      gbHelper.add(rightFiller, 1, GridBagConstraints.REMAINDER, 0, 1, GridBagConstraints.BOTH, GridBagConstraints.CENTER);

      gbHelper.newRow();
    }

    add(inputPanel, BorderLayout.CENTER);
  }

  @Override
  public String getName() {
    return this.bereich;
  }

  @Override
  public void start() {
    super.start();
  }

  @Override
  public boolean stop() {
    return super.stop();
  }
}
