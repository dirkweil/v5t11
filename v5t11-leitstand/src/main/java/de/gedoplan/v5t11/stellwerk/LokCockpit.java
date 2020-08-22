package de.gedoplan.v5t11.stellwerk;

import de.gedoplan.baselibs.utils.inject.InjectionUtil;
import de.gedoplan.v5t11.leitstand.entity.Leitstand;
import de.gedoplan.v5t11.leitstand.entity.baustein.LokController;
import de.gedoplan.v5t11.leitstand.entity.lok.Lok;
import de.gedoplan.v5t11.leitstand.gateway.StatusGateway;
import de.gedoplan.v5t11.stellwerk.util.IconUtil;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.swing.AbstractCellEditor;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.eclipse.microprofile.rest.client.inject.RestClient;

public class LokCockpit extends ApplicationPanel {
  private static final int COL_ICON = 0;
  private static final int COL_ID = 1;
  private static final int COL_FIRST_CONTROLLER = 2;

  // private static final Color COLOR_SELECTED = Color.BLACK;
  // private static final Color COLOR_UNSELECTED = Color.LIGHT_GRAY;

  private JTable table;
  private MyTableModel tableModel;

  @Inject
  Leitstand leitstand;

  @Inject
  @RestClient
  StatusGateway statusGateway;

  @Inject
  StatusDispatcher statusDispatcher;

  public LokCockpit() {
    InjectionUtil.injectFields(this);

    setLayout(new BorderLayout());

    this.statusDispatcher.addListener(LokController.class, () -> {
      if (this.tableModel != null) {
        this.tableModel.setAssignButtonSelections();
        this.tableModel.fireTableDataChanged();
      }
    });

  }

  @Override
  public void start() {
    Set<Lok> loks = this.leitstand.getLoks();
    SortedSet<LokController> lokController = this.leitstand.getLokController();
    this.tableModel = new MyTableModel(new ArrayList<>(loks), new ArrayList<>(lokController));
    this.table = new JTable(this.tableModel);
    this.table.getColumnModel().getColumn(COL_ICON).setCellRenderer(new IconCellRenderer());
    ButtonCellRenderer buttonCellRenderer = new ButtonCellRenderer();
    for (int i = 0; i < lokController.size(); ++i) {
      this.table.getColumnModel().getColumn(COL_FIRST_CONTROLLER + i).setCellRenderer(buttonCellRenderer);
      this.table.getColumnModel().getColumn(COL_FIRST_CONTROLLER + i).setCellEditor(buttonCellRenderer);
    }
    this.table.setRowHeight(100);
    this.table.setRowSelectionAllowed(false);
    add(new JScrollPane(this.table));

  }

  @Override
  public boolean stop() {
    removeAll();
    this.tableModel = null;
    this.table = null;

    return true;
  }

  @Override
  public String getName() {
    return "Handregler";
  }

  public void assignLokController(LokController lokController, Lok lok, boolean selected) {
    this.logger.debug("assignLokController: " + lokController + ", " + lok + ", " + selected);

    this.statusGateway.assignLokcontrollerLok(lokController.getId(), selected ? lok.getId() : "null");
  }

  private static Icon getIcon(Lok lok) {
    String name = lok.getId().replaceAll("\\s+", "_");
    while (!name.isEmpty()) {
      Icon icon = IconUtil.getIcon("images/loks/" + name + ".png", -1, 70);
      if (icon != null) {
        return icon;
      }

      name = name.substring(0, name.length() - 1);
    }

    return IconUtil.getIcon("images/loks/none.png", -1, 35);
  }

  private class MyTableModel extends AbstractTableModel {

    private List<Lok> lokListe;
    private List<Icon> lokImageListe;
    private List<LokController> lokcontrollerListe;

    private int lokCount;
    private int lokcontrollerCount;

    private JToggleButton[][] assignButton;

    public MyTableModel(List<Lok> lokListe, List<LokController> lokcontrollerListe) {
      this.lokListe = lokListe;
      this.lokImageListe = lokListe.stream().map(LokCockpit::getIcon).collect(Collectors.toList());
      this.lokcontrollerListe = lokcontrollerListe;

      this.lokCount = lokListe.size();
      this.lokcontrollerCount = lokcontrollerListe.size();

      this.assignButton = new JToggleButton[this.lokCount][this.lokcontrollerCount];
      for (int i = 0; i < this.lokCount; ++i) {
        for (int j = 0; j < this.lokcontrollerCount; ++j) {
          JToggleButton button = new JToggleButton(" ");
          this.assignButton[i][j] = button;

          final int row = i;
          final int col = j;
          button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ev) {
              assignButtonClicked(row, col);
            }
          });
        }
      }

      setAssignButtonSelections();

    }

    private void setAssignButtonSelections() {
      for (int row = 0; row < this.lokCount; ++row) {
        for (int col = 0; col < this.lokcontrollerCount; ++col) {
          Lok lok = this.lokListe.get(row);
          LokController lokController = this.lokcontrollerListe.get(col);

          boolean selected = lok.getId().equals(lokController.getLokId());
          this.assignButton[row][col].setSelected(selected);
          this.assignButton[row][col].setText(selected ? lok.getId() : " ");
        }
      }
    }

    protected void assignButtonClicked(int row, int col) {
      assignLokController(this.lokcontrollerListe.get(col), this.lokListe.get(row), this.assignButton[row][col].isSelected());
    }

    @Override
    public int getColumnCount() {
      return COL_FIRST_CONTROLLER + this.lokcontrollerCount;
    }

    @Override
    public int getRowCount() {
      return this.lokCount;
    }

    @Override
    public Object getValueAt(int row, int col) {
      Lok lok = this.lokListe.get(row);
      switch (col) {
      case -1:
        return lok;

      case COL_ICON:
        return this.lokImageListe.get(row);

      case COL_ID:
        return lok.getId();

      default:
        return this.assignButton[row][col - COL_FIRST_CONTROLLER];
      }
    }

    @Override
    public boolean isCellEditable(int row, int col) {
      return col >= COL_FIRST_CONTROLLER;
    }

    @Override
    public String getColumnName(int col) {
      switch (col) {
      case COL_ICON:
        return "Lok";

      case COL_ID:
        return "Name";

      default:
        return this.lokcontrollerListe.get(col - COL_FIRST_CONTROLLER).getId();
      }
    }
  }

  private static class IconCellRenderer extends JLabel implements TableCellRenderer {
    public IconCellRenderer() {
      setBackground(Color.BLUE.darker().darker());
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
      if (value instanceof Icon) {
        setText("");
        setIcon((Icon) value);
      } else {
        setText("" + value);
        setIcon(null);
      }

      setOpaque(isSelected);

      return this;
    }
  }

  private static class ButtonCellRenderer extends AbstractCellEditor implements TableCellRenderer, TableCellEditor {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
      return value instanceof Component ? (Component) value : null;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int col) {
      return getTableCellRendererComponent(table, value, isSelected, true, row, col);
    }

    @Override
    public Object getCellEditorValue() {
      return null;
    }
  }

}
