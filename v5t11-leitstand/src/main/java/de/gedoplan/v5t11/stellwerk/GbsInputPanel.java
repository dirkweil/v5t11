package de.gedoplan.v5t11.stellwerk;

import de.gedoplan.baselibs.utils.inject.InjectionUtil;
import de.gedoplan.v5t11.leitstand.entity.fahrstrasse.Fahrstrasse;
import de.gedoplan.v5t11.leitstand.entity.fahrweg.Gleisabschnitt;
import de.gedoplan.v5t11.leitstand.entity.fahrweg.Signal;
import de.gedoplan.v5t11.leitstand.entity.fahrweg.Weiche;
import de.gedoplan.v5t11.leitstand.gateway.FahrstrassenGateway;
import de.gedoplan.v5t11.leitstand.gateway.StatusGateway;
import de.gedoplan.v5t11.leitstand.service.FahrstrassenManager;
import de.gedoplan.v5t11.stellwerk.util.GridBagHelper;
import de.gedoplan.v5t11.stellwerk.util.IconUtil;
import de.gedoplan.v5t11.util.domain.attribute.FahrstrassenFilter;
import de.gedoplan.v5t11.util.domain.attribute.FahrstrassenReservierungsTyp;
import de.gedoplan.v5t11.util.domain.attribute.SignalStellung;
import de.gedoplan.v5t11.util.domain.attribute.WeichenStellung;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import javax.inject.Inject;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

public class GbsInputPanel extends JPanel {
  private static final long FAHRSTRASSEN_INPUT_MAXDELAY = 5000;

  private Gbs gbs;

  private Gleisabschnitt fahrstrassenBeginn = null;
  private long fahrstrassenBeginnStamp = 0;
  private List<Fahrstrasse> fahrstrassen;

  private JPanel fahrstrassenPanel = new JPanel();
  private JPanel geraetePanel = new JPanel();

  private int fahrstrassenIndex;

  private JLabel fahrstrassenLabel = new JLabel();
  private JButton fahrstrassenNextButton = new JButton(">");
  private JButton fahrstrassenZugfahrtButton = new JButton("Zugfahrt");
  private JButton fahrstrassenRangierfahrtButton = new JButton("Rangierfahrt");
  private JButton fahrstrassenFreigabeButton = new JButton("freigeben");
  private JButton abbrechenButton = new JButton("abbrechen");

  private Collection<Consumer<?>> actions = new ArrayList<>();

  @Inject
  FahrstrassenManager fahrstrassenManager;

  @Inject
  @RestClient
  FahrstrassenGateway fahrstrassenGateway;

  @Inject
  @RestClient
  StatusGateway statusGateway;

  @Inject
  StatusDispatcher statusDispatcher;

  private static final Logger LOG = Logger.getLogger(GbsInputPanel.class);

  public GbsInputPanel(String bereich, Gbs gbs) {
    InjectionUtil.injectFields(this);

    this.gbs = gbs;

    Dimension dim = new Dimension(400, 200);
    setPreferredSize(dim);
    setMaximumSize(dim);
    setMinimumSize(dim);

    GridBagHelper gbHelper = new GridBagHelper(this);
    gbHelper.add(this.fahrstrassenLabel, 1, 1, 0, 1, GridBagConstraints.BOTH, GridBagConstraints.WEST);
    gbHelper.newRow();
    gbHelper.add(this.fahrstrassenPanel, 1, 1, 0, 1, GridBagConstraints.NONE, GridBagConstraints.WEST);
    gbHelper.newRow();
    gbHelper.add(this.geraetePanel, 1, 1, 0, 1, GridBagConstraints.NONE, GridBagConstraints.WEST);

    this.fahrstrassenNextButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        fahrstrassenNextButtonClicked();
      }
    });

    this.fahrstrassenZugfahrtButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        fahrstrassenReservierungsButtonClicked(FahrstrassenReservierungsTyp.ZUGFAHRT);
      }
    });

    this.fahrstrassenRangierfahrtButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        fahrstrassenReservierungsButtonClicked(FahrstrassenReservierungsTyp.RANGIERFAHRT);
      }
    });

    this.fahrstrassenFreigabeButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        fahrstrassenReservierungsButtonClicked(FahrstrassenReservierungsTyp.UNRESERVIERT);
      }
    });

    this.abbrechenButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        abbrechenButtonClicked();
      }
    });

    this.validate();
  }

  public void reset() {
    this.fahrstrassen = null;

    this.fahrstrassenLabel.setText(null);
    this.fahrstrassenPanel.removeAll();
    this.geraetePanel.removeAll();

    clearListeners();

    validate();

    this.gbs.repaint();
  }

  public void addSignal(final Signal signal) {
    if (signal != null) {
      if (LOG.isTraceEnabled()) {
        LOG.trace("addSignal: " + signal);
      }

      this.geraetePanel.add(new JLabel(signal.getName()));

      var buttonGroup = new ButtonGroup();

      for (SignalStellung stellung : SignalStellung.values()) {
        var erlaubteStellungen = signal.getTyp().getErlaubteStellungen();
        if (erlaubteStellungen == null || erlaubteStellungen.isEmpty() || erlaubteStellungen.contains(stellung)) {
          var rb = new JRadioButton();
          rb.setIcon(getUnselectedIcon(stellung));
          rb.setSelectedIcon(getSelectedIcon(stellung));
          buttonGroup.add(rb);

          final SignalStellung rbStellung = stellung;
          rb.setSelected(rbStellung.equals(signal.getStellung()));

          addListener(signal, s -> rb.setSelected(rbStellung.equals(s.getStellung())));

          rb.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
              if (rb.isSelected()) {
                GbsInputPanel.this.statusGateway.signalStellen(signal.getBereich(), signal.getName(), stellung);
                StellwerkUI.setStatusLineText(null);
              }

            }
          });

          this.geraetePanel.add(rb);
        }
      }

      validate();
    }
  }

  public void addWeiche(Weiche weiche, Gleisabschnitt gleisabschnitt) {
    if (weiche != null) {
      if (LOG.isTraceEnabled()) {
        LOG.trace("addWeiche: " + weiche);
      }

      var cb = new JCheckBox(weiche.getName());
      cb.setIcon(getIcon(WeichenStellung.GERADE));
      cb.setSelectedIcon(getIcon(WeichenStellung.ABZWEIGEND));
      cb.setHorizontalTextPosition(SwingConstants.LEFT);
      WeichenButtonModel model = new WeichenButtonModel(weiche);
      cb.setModel(model);

      addListener(weiche, model::setWeiche);

      this.geraetePanel.add(cb);

      validate();

      Fahrstrasse aktiveFahrstrasse = this.fahrstrassenManager.getReservierteFahrstrasse(gleisabschnitt);
      if (aktiveFahrstrasse != null) {
        showFahrstrasseZurDeaktivierung(aktiveFahrstrasse);
      }
    }
  }

  public void addGleisabschnitt(Gleisabschnitt gleisabschnitt) {

    if (gleisabschnitt != null) {
      if (LOG.isTraceEnabled()) {
        LOG.trace("addGleisabschnitt: " + gleisabschnitt);
      }

      long now = System.currentTimeMillis();

      if (this.fahrstrassenBeginn != null && now - this.fahrstrassenBeginnStamp <= FAHRSTRASSEN_INPUT_MAXDELAY) {
        this.fahrstrassen = this.fahrstrassenGateway.getFahrstrassen(
            this.fahrstrassenBeginn.getBereich(), this.fahrstrassenBeginn.getName(),
            gleisabschnitt.getBereich(), gleisabschnitt.getName(),
            FahrstrassenFilter.FREI);
        int fahrstrassenAnzahl = this.fahrstrassen.size();

        String fahrstrassenAnzahlText;
        switch (fahrstrassenAnzahl) {
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
        StellwerkUI.setStatusLineText(this.fahrstrassenBeginn.toDisplayString() + " -> " + gleisabschnitt.toDisplayString() + ": " + fahrstrassenAnzahlText);

        if (fahrstrassenAnzahl != 0) {
          this.fahrstrassenIndex = 0;
          showFahrstrasseZurAktivierung();
          validate();
        }

        if (LOG.isDebugEnabled()) {
          LOG.debug(fahrstrassenAnzahl + " Fahrstrassen von " + this.fahrstrassenBeginn + " nach " + gleisabschnitt + " gefunden");
        }

        this.fahrstrassenBeginnStamp = 0;
      } else if (this.fahrstrassenBeginn == null || now - this.fahrstrassenBeginnStamp > FAHRSTRASSEN_INPUT_MAXDELAY) {
        this.fahrstrassenBeginn = gleisabschnitt;

        StellwerkUI.setStatusLineText(this.fahrstrassenBeginn.toDisplayString());

        if (LOG.isDebugEnabled()) {
          LOG.debug(gleisabschnitt + " als moeglichen Fahrstrassenbeginn gemerkt");
        }

        this.fahrstrassenBeginnStamp = now;

        Fahrstrasse aktiveFahrstrasse = this.fahrstrassenManager.getReservierteFahrstrasse(gleisabschnitt);
        if (aktiveFahrstrasse != null) {
          this.fahrstrassenIndex = 0;
          showFahrstrasseZurDeaktivierung(aktiveFahrstrasse);
        }
      }
    }
  }

  private <T> void addListener(T observed, Consumer<T> action) {
    this.statusDispatcher.addListener(observed, action);

    this.actions.add(action);
  }

  private void clearListeners() {
    this.actions.forEach(a -> this.statusDispatcher.removeListener(a));
    this.actions.clear();
  }

  private void showFahrstrasseZurDeaktivierung(Fahrstrasse aktiveFahrstrasse) {
    this.fahrstrassen = new ArrayList<Fahrstrasse>();
    this.fahrstrassen.add(aktiveFahrstrasse);
    this.fahrstrassenIndex = 0;

    showFahrstrasseZurDeaktivierung();

  }

  private void showFahrstrasseZurDeaktivierung() {
    this.fahrstrassenPanel.removeAll();
    this.fahrstrassenPanel.add(this.fahrstrassenFreigabeButton);
    this.fahrstrassenPanel.add(this.abbrechenButton);
    validate();

    showFahrstrasse(false);
  }

  private void showFahrstrasseZurAktivierung() {
    if (LOG.isDebugEnabled()) {
      LOG.debug("Freie Fahrstrassen:");
      for (Fahrstrasse fahrstrasse : this.fahrstrassen) {
        LOG.debug(" " + fahrstrasse.getName());
      }
    }

    this.fahrstrassenPanel.removeAll();
    this.fahrstrassenPanel.add(this.fahrstrassenNextButton);
    this.fahrstrassenPanel.add(this.fahrstrassenZugfahrtButton);
    this.fahrstrassenPanel.add(this.fahrstrassenRangierfahrtButton);
    this.fahrstrassenPanel.add(this.abbrechenButton);
    validate();

    this.fahrstrassenNextButton.setEnabled(this.fahrstrassen.size() > 1);

    showFahrstrasse(true);
  }

  private void showFahrstrasse(boolean highlight) {
    Fahrstrasse fahrstrasse = this.fahrstrassen.get(this.fahrstrassenIndex);
    this.fahrstrassenLabel.setText("Fahrstrasse " + fahrstrasse.getShortName());

    this.gbs.repaint();
  }

  protected void fahrstrassenNextButtonClicked() {
    ++this.fahrstrassenIndex;
    if (this.fahrstrassenIndex >= this.fahrstrassen.size()) {
      this.fahrstrassenIndex = 0;
    }

    showFahrstrasse(true);

    this.gbs.repaint();
  }

  protected void fahrstrassenReservierungsButtonClicked(FahrstrassenReservierungsTyp reservierungsTyp) {
    Fahrstrasse fahrstrasse = this.fahrstrassen.get(this.fahrstrassenIndex);

    reset();
    this.fahrstrassenBeginn = null;
    StellwerkUI.setStatusLineText(null);

    this.fahrstrassenGateway.reserviereFahrstrasse(fahrstrasse.getBereich(), fahrstrasse.getName(), reservierungsTyp);

    this.gbs.repaint();
  }

  protected void abbrechenButtonClicked() {
    reset();
    this.fahrstrassenBeginn = null;
    StellwerkUI.setStatusLineText(null);
  }

  public Fahrstrasse getVorgeschlageneFahrstrasse() {
    if (this.fahrstrassen == null || this.fahrstrassenIndex >= this.fahrstrassen.size()) {
      return null;
    }

    return this.fahrstrassen.get(this.fahrstrassenIndex);
  }

  private static Icon getSelectedIcon(SignalStellung stellung) {
    switch (stellung) {
    case DUNKEL:
      return IconUtil.getIcon("images/signal_dunkel_selektiert.gif");
    case FAHRT:
      return IconUtil.getIcon("images/signal_gruen_selektiert.gif");
    case HALT:
      return IconUtil.getIcon("images/signal_rot_selektiert.gif");
    case LANGSAMFAHRT:
      return IconUtil.getIcon("images/signal_gelb_selektiert.gif");
    case RANGIERFAHRT:
      return IconUtil.getIcon("images/signal_weiss_selektiert.gif");
    }
    return null;
  }

  private static Icon getUnselectedIcon(SignalStellung stellung) {
    switch (stellung) {
    case DUNKEL:
      return IconUtil.getIcon("images/signal_dunkel.gif");
    case FAHRT:
      return IconUtil.getIcon("images/signal_gruen.gif");
    case HALT:
      return IconUtil.getIcon("images/signal_rot.gif");
    case LANGSAMFAHRT:
      return IconUtil.getIcon("images/signal_gelb.gif");
    case RANGIERFAHRT:
      return IconUtil.getIcon("images/signal_weiss.gif");
    }
    return null;
  }

  private static Icon getIcon(WeichenStellung stellung) {
    switch (stellung) {
    case ABZWEIGEND:
      return IconUtil.getIcon("images/weiche_abzweigend.gif");
    case GERADE:
      return IconUtil.getIcon("images/weiche_gerade.gif");
    }
    return null;
  }

}
