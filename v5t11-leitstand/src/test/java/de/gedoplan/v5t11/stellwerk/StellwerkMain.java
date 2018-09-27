package de.gedoplan.v5t11.stellwerk;

import de.gedoplan.baselibs.utils.util.ApplicationProperties;
import de.gedoplan.v5t11.leitstand.LeitstandMain;
import de.gedoplan.v5t11.leitstand.entity.Leitstand;
import de.gedoplan.v5t11.stellwerk.util.IconUtil;
import de.gedoplan.v5t11.util.config.ConfigBase;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.enterprise.inject.spi.CDI;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.meecrowave.Meecrowave;

import lombok.Getter;

public class StellwerkMain extends JFrame {
  private static final Icon ICON_SCHALTER_EIN = IconUtil.getIcon("images/schalter_ein.png", 67, 16);

  private static final Icon ICON_SCHALTER_AUS = IconUtil.getIcon("images/schalter_aus.png", 67, 16);

  private static final Icon ICON_KURZSCHLUSS = IconUtil.getIcon("images/kurzschluss.png", 67, 16);

  private static JPanel statusLine = new JPanel(new BorderLayout());
  private static JLabel statusLineText = new JLabel(" ");
  private static JLabel powerButton = new JLabel(" ");

  private static Log log;

  private static Meecrowave container;

  @Getter
  private static Leitstand leitstand;

  private StellwerkMain() {

    String applicationVersion = ApplicationProperties.getProperty("application.version");
    setTitle("v5t11 " + applicationVersion);

    // Maximale Fenstergröße ermitteln und setzen
    Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
    Dimension screenSize = defaultToolkit.getScreenSize();
    GraphicsConfiguration config = getGraphicsConfiguration();
    Insets screenInsets = defaultToolkit.getScreenInsets(config);
    int width = screenSize.width - screenInsets.left - screenInsets.right;
    int height = screenSize.height - screenInsets.top - screenInsets.bottom;
    setSize(width, height);

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

    try {
      // TabPanel mainPanel = new TabPanel();
      // getContentPane().add(mainPanel);
      //
      // TabPanel gbsPanel = new TabPanel("Stellwerk");
      // for (String bereich : leitstand.getBereiche()) {
      // gbsPanel.addApplicationPanel(new Gbs(bereich));
      // }
      // mainPanel.addApplicationPanel(gbsPanel);
      // validate();

      // mainPanel.addApplicationPanel(new LokCockpit());
      // validate();

      // refreshPowerButton();
      //
      // steuerung.getZentrale().addValueChangedListener(new ValueChangedListener() {
      // @Override
      // public void valueChanged(ValueChangedEvent event) {
      // refreshPowerButton();
      // }
      // });
      //
      // powerButton.addMouseListener(new MouseAdapter() {
      //
      // /**
      // * {@inheritDoc}
      // *
      // * @see java.awt.event.MouseAdapter#mousePressed(java.awt.event.MouseEvent)
      // */
      // @Override
      // public void mousePressed(MouseEvent e) {
      // powerButtonClicked();
      // }
      // });

      validate();

    } catch (Exception e) {
      log.error("Kann Anwendung nicht initialisieren", e);
      terminate();
    }

  }

  private void terminate() {
    if (container != null) {
      try {
        container.close();
      } catch (Exception e) {
        // ignore
      }
    }

    log.info("Bye");
    System.exit(0);
  }

  public static void setStatusLineText(String statusLineText) {
    StellwerkMain.statusLineText.setText(statusLineText);
  }

  @SuppressWarnings("resource")
  public static void main(String[] args) {

    // JUL in Log4j2 leiten; Achtung: Property muss vor jeglicher Log-Aktivität gesetzt werden!
    System.setProperty("java.util.logging.manager", "org.apache.logging.log4j.jul.LogManager");
    log = LogFactory.getLog(LeitstandMain.class);

    if (System.getProperty(ConfigBase.PROPERTY_ANLAGE) == null) {
      System.setProperty(ConfigBase.PROPERTY_ANLAGE, "show");
    }
    System.setProperty("UNITTEST", "false");

    try {
      UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
    } catch (Exception e) {
      // Ignore
    }

    try {
      container = new Meecrowave().bake();

      leitstand = CDI.current().select(Leitstand.class).get();
      log.info("Stellwerk gestartet für Bereiche " + leitstand.getBereiche());

      new StellwerkMain();
    } catch (Exception e) {

      // TODO Meecrowave scheint die Logger beim Shutdown zu schliessen
      if (log.isErrorEnabled()) {
        log.error("Kann Stellwerk nicht starten", e);
      } else {
        System.err.println("Kann Stellwerk nicht starten: " + e);
      }

      System.exit(0);
    }

  }
}
