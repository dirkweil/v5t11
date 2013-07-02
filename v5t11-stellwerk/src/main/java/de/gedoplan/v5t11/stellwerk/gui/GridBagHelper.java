package de.gedoplan.v5t11.stellwerk.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class GridBagHelper
{
  private Container          cont;
  private GridBagConstraints gbc    = new GridBagConstraints();
  private int                curRow = 0;
  private int                curCol = 0;

  public GridBagHelper(Container cont)
  {
    this.cont = cont;

    cont.setLayout(new GridBagLayout());

    this.gbc.insets = new Insets(0, 0, 0, 0);
  }

  public void add(Component comp, int row, int col, int height, int width, double vWeight, double hWeight, int fill, int anchor)
  {
    this.curRow = row;
    this.curCol = col;

    add(comp, height, width, vWeight, hWeight, fill, anchor);
  }

  public void add(Component comp, int height, int width, double vWeight, double hWeight, int fill, int anchor)
  {
    this.gbc.gridx = this.curCol;
    this.gbc.gridy = this.curRow;
    this.gbc.gridwidth = width;
    this.gbc.gridheight = height;
    this.gbc.weightx = hWeight;
    this.gbc.weighty = vWeight;
    this.gbc.fill = fill;
    this.gbc.anchor = anchor;

    this.cont.add(comp, this.gbc);

    this.curCol += width;
  }

  public void newRow()
  {
    ++this.curRow;
    this.curCol = 0;
  }

  public void skipCol()
  {
    ++this.curCol;
  }

  public void setInsets(int top, int left, int bottom, int right)
  {
    this.gbc.insets.top = top;
    this.gbc.insets.left = left;
    this.gbc.insets.bottom = bottom;
    this.gbc.insets.right = right;
  }

  public int getRow()
  {
    return this.curRow;
  }

  public int getCol()
  {
    return this.curCol;
  }
}
