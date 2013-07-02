/*
 * Created on 27.12.2005 by dw
 */
package de.gedoplan.v5t11.stellwerk.gui;

import de.gedoplan.v5t11.betriebssteuerung.listener.ConfigChangedEvent;
import de.gedoplan.v5t11.betriebssteuerung.listener.ConfigChangedListener;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.Steuerung;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.LokController;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.lok.Lok;
import de.gedoplan.v5t11.betriebssteuerung.util.IconUtil;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import javax.swing.AbstractCellEditor;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

public class LokCockpit extends ApplicationPanel
{
  private static final int COL_ICON             = 0;
  private static final int COL_NAME             = 1;
  private static final int COL_ADR              = 2;
  private static final int COL_FIRST_CONTROLLER = 3;

  // private static final Color COLOR_SELECTED = Color.BLACK;
  // private static final Color COLOR_UNSELECTED = Color.LIGHT_GRAY;

  private JTable           table;

  public LokCockpit()
  {
    setLayout(new BorderLayout());

    Steuerung steuerung = Main.getSteuerung();
    Set<Lok> loks = steuerung.getLoks();
    SortedSet<LokController> lokController = steuerung.getLokController();
    this.table = new JTable(new MyTableModel(new ArrayList<>(loks), new ArrayList<>(lokController)));
    this.table.getColumnModel().getColumn(COL_ICON).setCellRenderer(new IconCellRenderer());
    ButtonCellRenderer buttonCellRenderer = new ButtonCellRenderer();
    for (int i = 0; i < lokController.size(); ++i)
    {
      this.table.getColumnModel().getColumn(COL_FIRST_CONTROLLER + i).setCellRenderer(buttonCellRenderer);
      this.table.getColumnModel().getColumn(COL_FIRST_CONTROLLER + i).setCellEditor(buttonCellRenderer);
    }
    this.table.setRowHeight(35);
    this.table.setRowSelectionAllowed(false);
    add(new JScrollPane(this.table));
  }

  @Override
  public String getName()
  {
    return "Handregler";
  }

  public void assignLokController(LokController lokController, Lok lok, boolean selected)
  {
    this.logger.debug("assignLokController: " + lokController + ", " + lok + ", " + selected);
    Main.getSteuerungRemoteService().assignLokController(lokController.getId(), selected ? lok.getAdresse() : -1);
  }

  private class MyTableModel extends AbstractTableModel implements ConfigChangedListener
  {

    private List<Lok>           lokListe;
    private List<LokController> lokcontrollerListe;

    private int                 lokCount;
    private int                 lokcontrollerCount;

    private JToggleButton[][]   assignButton;

    public MyTableModel(List<Lok> lokListe, List<LokController> lokcontrollerListe)
    {
      this.lokListe = lokListe;
      this.lokcontrollerListe = lokcontrollerListe;

      this.lokCount = lokListe.size();
      this.lokcontrollerCount = lokcontrollerListe.size();

      this.assignButton = new JToggleButton[this.lokCount][this.lokcontrollerCount];
      for (int i = 0; i < this.lokCount; ++i)
      {
        for (int j = 0; j < this.lokcontrollerCount; ++j)
        {
          final JToggleButton button = new JToggleButton(" ");
          this.assignButton[i][j] = button;

          final int row = i;
          final int col = j;
          button.addActionListener(new ActionListener()
          {
            @Override
            public void actionPerformed(ActionEvent ev)
            {
              assignButtonClicked(row, col);
            }
          });
        }
      }

      setAssignButtonSelections();

      for (LokController lokController : lokcontrollerListe)
      {
        lokController.addConfigChangedListener(this);
      }
    }

    private void setAssignButtonSelections()
    {
      for (int row = 0; row < this.lokCount; ++row)
      {
        for (int col = 0; col < this.lokcontrollerCount; ++col)
        {
          Lok lok = this.lokListe.get(row);
          LokController lokController = this.lokcontrollerListe.get(col);

          this.assignButton[row][col].setSelected(lok.equals(lokController.getLok()));
        }
      }
    }

    @Override
    public void configChanged(ConfigChangedEvent event)
    {
      setAssignButtonSelections();
      fireTableDataChanged();
    }

    protected void assignButtonClicked(int row, int col)
    {
      assignLokController(this.lokcontrollerListe.get(col), this.lokListe.get(row), this.assignButton[row][col].isSelected());
    }

    @Override
    public int getColumnCount()
    {
      return COL_FIRST_CONTROLLER + this.lokcontrollerCount;
    }

    @Override
    public int getRowCount()
    {
      return this.lokCount;
    }

    @Override
    public Object getValueAt(int row, int col)
    {
      Lok lokDecoder = this.lokListe.get(row);
      switch (col)
      {
      case -1:
        return lokDecoder;

      case COL_ICON:
        return IconUtil.getIcon("images/loks/" + lokDecoder.getBildFileName(), -1, 35);

      case COL_NAME:
        return lokDecoder.getName();

      case COL_ADR:
        return lokDecoder.getAdresse();

      default:
        return this.assignButton[row][col - COL_FIRST_CONTROLLER];
      }
    }

    @Override
    public boolean isCellEditable(int row, int col)
    {
      return col >= COL_FIRST_CONTROLLER;
    }

    @Override
    public String getColumnName(int col)
    {
      switch (col)
      {
      case COL_ICON:
        return "Lok";

      case COL_NAME:
        return "Name";

      case COL_ADR:
        return "Adr.";

      default:
        return this.lokcontrollerListe.get(col - COL_FIRST_CONTROLLER).getId();
      }
    }
  }

  private static class IconCellRenderer extends JLabel implements TableCellRenderer
  {
    public IconCellRenderer()
    {
      setBackground(Color.BLUE.darker().darker());
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {
      if (value instanceof Icon)
      {
        setText("");
        setIcon((Icon) value);
      }
      else
      {
        setText("" + value);
        setIcon(null);
      }

      setOpaque(isSelected);

      return this;
    }
  }

  private static class ButtonCellRenderer extends AbstractCellEditor implements TableCellRenderer, TableCellEditor
  {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col)
    {
      return value instanceof Component ? (Component) value : null;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int col)
    {
      return getTableCellRendererComponent(table, value, isSelected, true, row, col);
    }

    @Override
    public Object getCellEditorValue()
    {
      return null;
    }
  }

}
