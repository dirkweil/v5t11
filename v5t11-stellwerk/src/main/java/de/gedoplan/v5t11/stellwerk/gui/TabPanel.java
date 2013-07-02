package de.gedoplan.v5t11.stellwerk.gui;

import java.awt.BorderLayout;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class TabPanel extends ApplicationPanel
{
  private JTabbedPane tabPane    = new JTabbedPane();
  private int         currentTab = -1;
  private String      name;

  public TabPanel(String name)
  {
    this.name = name;

    setLayout(new BorderLayout());
    add(this.tabPane, BorderLayout.CENTER);
    this.tabPane.addChangeListener(new ChangeListener()
    {
      @Override
      public void stateChanged(ChangeEvent e)
      {
        activateTab();
      }
    });
  }

  public TabPanel()
  {
    this("");
  }

  @Override
  public String getName()
  {
    return this.name;
  }

  @Override
  public void start()
  {
    if (this.currentTab >= 0 && this.currentTab < this.tabPane.getTabCount())
    {
      ApplicationPanel currentPanel = (ApplicationPanel) this.tabPane.getComponentAt(this.currentTab);
      currentPanel.start();
    }
  }

  @Override
  public boolean stop()
  {
    if (this.currentTab >= 0 && this.currentTab < this.tabPane.getTabCount())
    {
      ApplicationPanel currentPanel = (ApplicationPanel) this.tabPane.getComponentAt(this.currentTab);
      return currentPanel.stop();
    }
    else
    {
      return true;
    }
  }

  public void addApplicationPanel(ApplicationPanel applicationPanel)
  {
    this.tabPane.add(applicationPanel.getName(), applicationPanel);
  }

  private void activateTab()
  {
    int newTab = this.tabPane.getSelectedIndex();
    if (newTab == -1 || this.currentTab == newTab)
    {
      return;
    }

    ApplicationPanel currentPanel;
    if (this.currentTab >= 0 && this.currentTab < this.tabPane.getTabCount())
    {
      currentPanel = (ApplicationPanel) this.tabPane.getComponentAt(this.currentTab);
    }
    else
    {
      currentPanel = null;
    }

    try
    {
      if (currentPanel == null || currentPanel.stop())
      {
        ApplicationPanel newPanel = (ApplicationPanel) this.tabPane.getComponentAt(newTab);
        newPanel.start();
        this.currentTab = newTab;
      }
      else
      {
        this.tabPane.setSelectedIndex(this.currentTab);
      }
    }
    catch (Exception ex)
    {
      this.tabPane.setSelectedIndex(this.currentTab);
    }
  }
}
