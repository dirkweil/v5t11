package de.gedoplan.v5t11.stellwerk.gui;

import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrstrasse.Fahrstrasse;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrstrasse.Fahrstrasse.ReservierungsTyp;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg.Gleisabschnitt;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg.geraet.Signal;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg.geraet.Weiche;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GbsInputPanel extends JPanel
{
  private static final long FAHRSTRASSEN_INPUT_MAXDELAY    = 5000;

  private Gleisabschnitt    fahrstrassenBeginn             = null;
  private long              fahrstrassenBeginnStamp        = 0;
  private List<Fahrstrasse> fahrstrassen;

  private JPanel            fahrstrassenPanel              = new JPanel();
  private JPanel            geraetePanel                   = new JPanel();

  private int               fahrstrassenIndex;

  private JLabel            fahrstrassenLabel              = new JLabel();
  private JButton           fahrstrassenNextButton         = new JButton(">");
  private JButton           fahrstrassenZugfahrtButton     = new JButton("Zugfahrt");
  private JButton           fahrstrassenRangierfahrtButton = new JButton("Rangierfahrt");
  private JButton           fahrstrassenFreigabeButton     = new JButton("freigeben");
  private JButton           abbrechenButton                = new JButton("abbrechen");

  private static final Log  LOG                            = LogFactory.getLog(GbsInputPanel.class);

  public GbsInputPanel(String bereich)
  {
    GridBagHelper gbHelper = new GridBagHelper(this);
    gbHelper.add(this.fahrstrassenPanel, 1, 1, 0, 1, GridBagConstraints.NONE, GridBagConstraints.CENTER);
    gbHelper.newRow();
    gbHelper.add(this.geraetePanel, 1, 1, 0, 1, GridBagConstraints.NONE, GridBagConstraints.CENTER);

    this.fahrstrassenNextButton.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        fahrstrassenNextButtonClicked();
      }
    });

    this.fahrstrassenZugfahrtButton.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        fahrstrassenReservierungsButtonClicked(ReservierungsTyp.ZUGFAHRT);
      }
    });

    this.fahrstrassenRangierfahrtButton.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        fahrstrassenReservierungsButtonClicked(ReservierungsTyp.RANGIERFAHRT);
      }
    });

    this.fahrstrassenFreigabeButton.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        fahrstrassenReservierungsButtonClicked(null);
      }
    });

    this.abbrechenButton.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        abbrechenButtonClicked();
      }
    });
  }

  public void reset()
  {
    if (this.fahrstrassen != null)
    {
      for (Fahrstrasse fahrstrasse : this.fahrstrassen)
      {
        fahrstrasse.vorschlagen(false);
      }
    }

    this.fahrstrassenPanel.removeAll();
    this.geraetePanel.removeAll();

    validate();
  }

  public void addSignal(final Signal signal)
  {
    if (signal != null)
    {
      if (LOG.isTraceEnabled())
      {
        LOG.trace("addSignal: " + signal);
      }

      this.geraetePanel.add(new JLabel(signal.getName()));

      ButtonGroup buttonGroup = new ButtonGroup();

      for (final Signal.Stellung stellung : signal.getErlaubteStellungen())
      {
        final JRadioButton rb = new JRadioButton();
        rb.setIcon(stellung.getUnselectedIcon());
        rb.setSelectedIcon(stellung.getSelectedIcon());
        buttonGroup.add(rb);

        rb.setSelected(stellung.equals(signal.getStellung()));

        rb.addItemListener(new ItemListener()
        {
          @Override
          public void itemStateChanged(ItemEvent e)
          {
            if (rb.isSelected())
            {
              Main.getSteuerungRemoteService().setSignalStellung(signal.getBereich(), signal.getName(), stellung);
              Main.setStatusLineText(null);
            }

          }
        });

        this.geraetePanel.add(rb);
      }

      validate();
    }
  }

  public void addWeiche(Weiche weiche)
  {
    if (weiche != null)
    {
      if (LOG.isTraceEnabled())
      {
        LOG.trace("addWeiche: " + weiche);
      }

      JCheckBox cb = new JCheckBox(weiche.getName());
      cb.setIcon(Weiche.Stellung.GERADE.getIcon());
      cb.setSelectedIcon(Weiche.Stellung.ABZWEIGEND.getIcon());
      cb.setHorizontalTextPosition(SwingConstants.LEFT);
      cb.setModel(new WeichenButtonModel(weiche));

      this.geraetePanel.add(cb);

      validate();

      Fahrstrasse aktiveFahrstrasse = weiche.getReservierteFahrstrasse();
      if (aktiveFahrstrasse != null)
      {
        showFahrstrasseZurDeaktivierung(aktiveFahrstrasse);
      }
    }
  }

  public void addGleisabschnitt(Gleisabschnitt gleisabschnitt)
  {

    if (gleisabschnitt != null)
    {
      if (LOG.isTraceEnabled())
      {
        LOG.trace("addGleisabschnitt: " + gleisabschnitt);
      }

      long now = System.currentTimeMillis();

      if (this.fahrstrassenBeginn != null && now - this.fahrstrassenBeginnStamp <= FAHRSTRASSEN_INPUT_MAXDELAY)
      {
        this.fahrstrassen = Main.getSteuerung().getFreieFahrstrassen(this.fahrstrassenBeginn, gleisabschnitt);
        int fahrstrassenAnzahl = this.fahrstrassen.size();

        String fahrstrassenAnzahlText;
        switch (fahrstrassenAnzahl)
        {
        case 0:
          fahrstrassenAnzahlText = "Keine Fahrstrasse";
          break;

        case 1:
          fahrstrassenAnzahlText = "1 Fahrstrasse";
          break;

        default:
          fahrstrassenAnzahlText = fahrstrassenAnzahl + " Fahrstrassen";
          break;
        }
        Main.setStatusLineText(this.fahrstrassenBeginn.toDisplayString() + " -> " + gleisabschnitt.toDisplayString() + ": " + fahrstrassenAnzahlText);

        if (fahrstrassenAnzahl != 0)
        {
          this.fahrstrassenIndex = 0;
          showFahrstrasseZurAktivierung();
          validate();
        }

        if (LOG.isDebugEnabled())
        {
          LOG.debug(fahrstrassenAnzahl + " Fahrstrassen von " + this.fahrstrassenBeginn + " nach " + gleisabschnitt + " gefunden");
        }

        this.fahrstrassenBeginnStamp = 0;
      }
      else if (this.fahrstrassenBeginn == null || now - this.fahrstrassenBeginnStamp > FAHRSTRASSEN_INPUT_MAXDELAY)
      {
        this.fahrstrassenBeginn = gleisabschnitt;

        Main.setStatusLineText(this.fahrstrassenBeginn.toDisplayString());

        if (LOG.isDebugEnabled())
        {
          LOG.debug(gleisabschnitt + " als moeglichen Fahrstrassenbeginn gemerkt");
        }

        this.fahrstrassenBeginnStamp = now;

        Fahrstrasse aktiveFahrstrasse = gleisabschnitt.getReservierteFahrstrasse();
        if (aktiveFahrstrasse != null)
        {
          this.fahrstrassenIndex = 0;
          showFahrstrasseZurDeaktivierung(aktiveFahrstrasse);
        }
      }
    }
  }

  private void showFahrstrasseZurDeaktivierung(Fahrstrasse aktiveFahrstrasse)
  {
    this.fahrstrassen = new ArrayList<Fahrstrasse>();
    this.fahrstrassen.add(aktiveFahrstrasse);
    this.fahrstrassenIndex = 0;

    showFahrstrasseZurDeaktivierung();

  }

  private void showFahrstrasseZurDeaktivierung()
  {
    this.fahrstrassenPanel.removeAll();
    this.fahrstrassenPanel.add(this.fahrstrassenLabel);
    this.fahrstrassenPanel.add(this.fahrstrassenFreigabeButton);
    this.fahrstrassenPanel.add(this.abbrechenButton);
    validate();

    showFahrstrasse(false);
  }

  private void showFahrstrasseZurAktivierung()
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("Freie Fahrstrassen:");
      for (Fahrstrasse fahrstrasse : this.fahrstrassen)
      {
        LOG.debug("  " + fahrstrasse.getName() + ", Rank=" + fahrstrasse.getRank());
      }
    }

    this.fahrstrassenPanel.removeAll();
    this.fahrstrassenPanel.add(this.fahrstrassenLabel);
    this.fahrstrassenPanel.add(this.fahrstrassenNextButton);
    this.fahrstrassenPanel.add(this.fahrstrassenZugfahrtButton);
    this.fahrstrassenPanel.add(this.fahrstrassenRangierfahrtButton);
    this.fahrstrassenPanel.add(this.abbrechenButton);
    validate();

    this.fahrstrassenNextButton.setEnabled(this.fahrstrassen.size() > 1);

    showFahrstrasse(true);
  }

  private void showFahrstrasse(boolean highlight)
  {
    Fahrstrasse fahrstrasse = this.fahrstrassen.get(this.fahrstrassenIndex);
    if (highlight)
    {
      fahrstrasse.vorschlagen(true);
    }
    this.fahrstrassenLabel.setText("Fahrstrasse " + fahrstrasse.getShortName());
  }

  protected void fahrstrassenNextButtonClicked()
  {
    Fahrstrasse fahrstrasse = this.fahrstrassen.get(this.fahrstrassenIndex);
    fahrstrasse.vorschlagen(false);

    ++this.fahrstrassenIndex;
    if (this.fahrstrassenIndex >= this.fahrstrassen.size())
    {
      this.fahrstrassenIndex = 0;
    }

    showFahrstrasse(true);
  }

  protected void fahrstrassenReservierungsButtonClicked(Fahrstrasse.ReservierungsTyp reservierungsTyp)
  {
    Fahrstrasse fahrstrasse = this.fahrstrassen.get(this.fahrstrassenIndex);

    reset();
    this.fahrstrassenBeginn = null;
    Main.setStatusLineText(null);

    Main.getSteuerungRemoteService().setFahrstrasseReserviert(fahrstrasse.getBereich(), fahrstrasse.getName(), reservierungsTyp);
  }

  protected void abbrechenButtonClicked()
  {
    reset();
    this.fahrstrassenBeginn = null;
    Main.setStatusLineText(null);
  }

}
