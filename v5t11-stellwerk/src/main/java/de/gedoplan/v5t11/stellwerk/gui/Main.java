package de.gedoplan.v5t11.stellwerk.gui;

import de.gedoplan.baselibs.naming.JNDIContextFactory;
import de.gedoplan.baselibs.utils.remote.ServiceLocator;
import de.gedoplan.baselibs.utils.util.ApplicationProperties;
import de.gedoplan.v5t11.betriebssteuerung.listener.ValueChangedEvent;
import de.gedoplan.v5t11.betriebssteuerung.listener.ValueChangedListener;
import de.gedoplan.v5t11.betriebssteuerung.messaging.FahrstrasseMessage;
import de.gedoplan.v5t11.betriebssteuerung.messaging.LokControllerMessage;
import de.gedoplan.v5t11.betriebssteuerung.remoteservice.SteuerungRemoteService;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.Steuerung;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.LokController;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrstrasse.Fahrstrasse;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.lok.Lok;
import de.gedoplan.v5t11.betriebssteuerung.util.IconUtil;
import de.gedoplan.v5t11.selectrix.SelectrixMessage;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Serializable;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Main extends JFrame
{
  private static final Icon ICON_SCHALTER_EIN = IconUtil.getIcon("images/schalter_ein.png", 67, 16);

  private static final Icon ICON_SCHALTER_AUS = IconUtil.getIcon("images/schalter_aus.png", 67, 16);

  private static final Icon ICON_KURZSCHLUSS  = IconUtil.getIcon("images/kurzschluss.png", 67, 16);

  private static Log        LOG               = LogFactory.getLog(Main.class);

  private static Steuerung  steuerung;

  private static JPanel     statusLine        = new JPanel(new BorderLayout());
  private static JLabel     statusLineText    = new JLabel(" ");
  private static JLabel     powerButton       = new JLabel(" ");

  public Main()
  {
    String applicationVersion = ApplicationProperties.getProperty("application.version");
    setTitle("V5T11 " + applicationVersion);

    // Maximale Fenstergröße ermitteln und setzen
    Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
    Dimension screenSize = defaultToolkit.getScreenSize();
    GraphicsConfiguration config = getGraphicsConfiguration();
    Insets screenInsets = defaultToolkit.getScreenInsets(config);
    int width = screenSize.width - screenInsets.left - screenInsets.right;
    int height = screenSize.height - screenInsets.top - screenInsets.bottom;
    setSize(width, height);

    addWindowListener(new WindowAdapter()
    {
      @Override
      public void windowClosing(WindowEvent ev)
      {
        terminate();
      }
    });

    statusLine.setBorder(BorderFactory.createLoweredBevelBorder());
    statusLine.add(statusLineText);

    statusLine.add(powerButton, BorderLayout.EAST);
    getContentPane().add(statusLine, BorderLayout.SOUTH);

    setVisible(true);

    LOG.info("Programm gestartet");

    try
    {
      steuerung = getSteuerungRemoteService().getSteuerung();
      steuerung.addValueChangedListeners();

      setupJmsSelectrixMessageListener();

      TabPanel mainPanel = new TabPanel();
      getContentPane().add(mainPanel);

      TabPanel gbsPanel = new TabPanel("Stellwerk");
      for (String bereich : steuerung.getBereiche())
      {
        gbsPanel.addApplicationPanel(new Gbs(bereich));
      }
      mainPanel.addApplicationPanel(gbsPanel);
      validate();

      mainPanel.addApplicationPanel(new LokCockpit());
      validate();

      // TabPanel monitorPanel = new TabPanel("Monitor");
      // monitorPanel.addApplicationPanel(new WeichenMonitor());
      // monitorPanel.addApplicationPanel(new SignalMonitor());
      // monitorPanel.addApplicationPanel(new GleisMonitor());
      // monitorPanel.addApplicationPanel(new SXMonitor());
      // mainPanel.addApplicationPanel(monitorPanel);
      // validate();

      // TabPanel progPanel = new TabPanel("Programmierung");
      // progPanel.addApplicationPanel(new BusGeraetProgrammer());
      // progPanel.addApplicationPanel(new LokDecoderProgrammer());
      // mainPanel.addApplicationPanel(progPanel);
      // validate();

      refreshPowerButton();

      steuerung.getZentrale().addValueChangedListener(new ValueChangedListener()
      {
        @Override
        public void valueChanged(ValueChangedEvent event)
        {
          refreshPowerButton();
        }
      });

      powerButton.addMouseListener(new MouseAdapter()
      {

        /**
         * {@inheritDoc}
         * 
         * @see java.awt.event.MouseAdapter#mousePressed(java.awt.event.MouseEvent)
         */
        @Override
        public void mousePressed(MouseEvent e)
        {
          powerButtonClicked();
        }
      });

      validate();

      // setVisible(false);
      // setVisible(true);
    }
    catch (Exception e)
    {
      LOG.error("Kann Anwendung nicht initialisieren", e);
      terminate();
    }

  }

  protected void powerButtonClicked()
  {
    getSteuerungRemoteService().setZentraleAktiv(!steuerung.getZentrale().isAktiv());
  }

  protected void refreshPowerButton()
  {
    boolean aktiv = steuerung.getZentrale().isAktiv();
    if (aktiv)
    {
      boolean kurzschluss = steuerung.getZentrale().isKurzschluss();
      powerButton.setIcon(kurzschluss ? ICON_KURZSCHLUSS : ICON_SCHALTER_EIN);
    }
    else
    {
      powerButton.setIcon(ICON_SCHALTER_AUS);
    }
  }

  /**
   * Wert liefern: {@link #steuerungRemoteService}.
   * 
   * @return Wert
   */
  public static SteuerungRemoteService getSteuerungRemoteService()
  {
    return new SteuerungRemoteServiceNPEIgnoreWrapper(ServiceLocator.getEjb(SteuerungRemoteService.class));
  }

  /**
   * Wert liefern: {@link #steuerung}.
   * 
   * @return Wert
   */
  public static Steuerung getSteuerung()
  {
    return steuerung;
  }

  public static void setStatusLineText(String statusLineText)
  {
    Main.statusLineText.setText(statusLineText);
  }

  public static void main(String[] args)
  {
    try
    {
      UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
    }
    catch (Exception e)
    {
      // Ignore
    }

    new Main();
  }

  private void terminate()
  {
    LOG.info("Bye");
    System.exit(0);
  }

  private void setupJmsSelectrixMessageListener() throws Exception
  {
    // TODO: ServiceLocator um JMS-CF ergänzen
    ConnectionFactory connectionFactory = ServiceLocator.getService(ConnectionFactory.class, "jms/RemoteConnectionFactory");

    Destination destination = ServiceLocator.getService(Destination.class, "jms/topic/V5T11SelectrixMessage");

    Connection connection = connectionFactory.createConnection(JNDIContextFactory.getGuestUserName(), JNDIContextFactory.getGuestUserPassword());
    connection.start();

    Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

    MessageConsumer consumer = session.createConsumer(destination);
    consumer.setMessageListener(new MessageListener()
    {
      @Override
      public void onMessage(Message message)
      {
        if (message instanceof ObjectMessage)
        {
          try
          {
            Serializable obj = ((ObjectMessage) message).getObject();

            if (LOG.isTraceEnabled())
            {
              LOG.trace("Message received: " + obj);
            }

            if (obj instanceof SelectrixMessage)
            {
              SelectrixMessage selectrixMessage = (SelectrixMessage) obj;
              steuerung.onMessage(selectrixMessage);
            }
            else if (obj instanceof FahrstrasseMessage)
            {
              FahrstrasseMessage fahrstrasseMessage = (FahrstrasseMessage) obj;
              Fahrstrasse fahrstrasse = steuerung.getFahrstrasse(fahrstrasseMessage.getBereich(), fahrstrasseMessage.getName());
              if (fahrstrasse != null)
              {
                if (fahrstrasse.getReservierungsTyp() != fahrstrasseMessage.getReservierungsTyp())
                {
                  if (fahrstrasseMessage.getReservierungsTyp() != null)
                  {
                    fahrstrasse.reservieren(fahrstrasseMessage.getReservierungsTyp());
                  }
                  else
                  {
                    fahrstrasse.freigeben(fahrstrasseMessage.getTeilFreigabeEnde());
                  }
                }
              }
            }
            else if (obj instanceof LokControllerMessage)
            {
              LokControllerMessage lokControllerMessage = (LokControllerMessage) obj;
              LokController lokController = steuerung.getLokController(lokControllerMessage.getLokControllerId());
              if (lokController != null)
              {
                int lokAdresse = lokControllerMessage.getLokAdresse();
                Lok lok = lokAdresse > 0 ? steuerung.getLok(lokAdresse) : null;
                lokController.setLok(lok);
              }
            }
          }
          catch (JMSException e)
          {
            LOG.warn("Error processing message", e);
          }
        }
        else
        {
          LOG.warn("Illegal message receiced: " + message);
        }
      }
    });
  }
}
