package de.gedoplan.v5t11.stellwerk;

import de.gedoplan.baselibs.utils.inject.InjectionUtil;
import de.gedoplan.v5t11.leitstand.entity.Leitstand;
import de.gedoplan.v5t11.leitstand.gateway.StatusGateway;
import de.gedoplan.v5t11.leitstand.service.ConfigService;
import de.gedoplan.v5t11.stellwerk.util.IconUtil;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.inject.Inject;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import lombok.Getter;

public class StellwerkUI extends JFrame {
  private static final Icon ICON_SCHALTER_EIN = IconUtil.getIcon("images/schalter_ein.png", 67, 16);

  private static final Icon ICON_SCHALTER_AUS = IconUtil.getIcon("images/schalter_aus.png", 67, 16);

  private static final Icon ICON_KURZSCHLUSS = IconUtil.getIcon("images/kurzschluss.png", 67, 16);

  private static JPanel statusLine = new JPanel(new BorderLayout());
  private static JLabel statusLineText = new JLabel(" ");
  private static JLabel powerButton = new JLabel(" ");

  @Inject
  Leitstand leitstand;

  @Inject
  Logger log;

  @Inject
  StatusDispatcher statusDispatcher;

  @Inject
  @RestClient
  StatusGateway statusGateway;

  @Inject
  ConfigService configService;

  @Getter
  private static final StellwerkUI INSTANCE = new StellwerkUI();

  public static void start() {
    INSTANCE.init();
  }

  private void init() {
    InjectionUtil.injectFields(this);

    setTitle(this.configService.getArtifactId() + ":" + this.configService.getVersion());

    // Maximale Fenstergröße ermitteln und setzen
    Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
    Dimension screenSize = defaultToolkit.getScreenSize();
    GraphicsConfiguration config = getGraphicsConfiguration();
    Insets screenInsets = defaultToolkit.getScreenInsets(config);
    int width = screenSize.width - screenInsets.left - screenInsets.right;
    int height = screenSize.height - screenInsets.top - screenInsets.bottom;
    // width = 1600;
    // height = 900;
    setSize(width, height);

    GbsElement.setDimensions(width, height);

    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent ev) {
        terminate();
      }
    });

    statusLine.setBorder(BorderFactory.createLoweredBevelBorder());
    statusLine.add(statusLineText);

    statusLine.add(powerButton, BorderLayout.EAST);
    getContentPane().add(statusLine, BorderLayout.SOUTH);

    setVisible(true);

    TabPanel mainPanel = new TabPanel();
    getContentPane().add(mainPanel);

    TabPanel gbsPanel = new TabPanel("Stellwerk");
    for (String bereich : this.leitstand.getBereiche()) {
      gbsPanel.addApplicationPanel(new Gbs(bereich));
    }
    mainPanel.addApplicationPanel(gbsPanel);
    validate();

    mainPanel.addApplicationPanel(new LokCockpit());
    validate();

    refreshPowerButton();

    this.statusDispatcher.addListener(this.leitstand.getZentrale(), this::refreshPowerButton);

    powerButton.addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        powerButtonClicked();
      }
    });

    validate();

  }

  private void refreshPowerButton() {
    boolean aktiv = this.leitstand.getZentrale().isGleisspannung();
    if (aktiv) {
      boolean kurzschluss = this.leitstand.getZentrale().isKurzschluss();
      powerButton.setIcon(kurzschluss ? ICON_KURZSCHLUSS : ICON_SCHALTER_EIN);
    } else {
      powerButton.setIcon(ICON_SCHALTER_AUS);
    }
  }

  private void powerButtonClicked() {
    this.statusGateway.putGleisspannung("" + (!this.leitstand.getZentrale().isGleisspannung()));
  }

  private void terminate() {
    this.log.info("Bye");
    System.exit(0);
  }

  public static void setStatusLineText(String statusLineText) {
    StellwerkUI.statusLineText.setText(statusLineText);
  }

}
