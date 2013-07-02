package de.gedoplan.v5t11.selectrix.impl;

import de.gedoplan.v5t11.selectrix.SelectrixConnection;
import de.gedoplan.v5t11.selectrix.SelectrixMessage;
import de.gedoplan.v5t11.selectrix.SelectrixMessageListener;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicIntegerArray;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class Selectrix implements SelectrixMessageListener
{
  public static final int                                PORT_RECEIVE_TIMEOUT_MILLIS = 500;

  private static final Selectrix                         INSTANCE                    = new Selectrix();

  private AtomicIntegerArray                             cache                       = new AtomicIntegerArray(SelectrixConnection.MAX_ADDRESSE + 1);

  private static final Log                               LOGGER                      = LogFactory.getLog(Selectrix.class);

  private Collection<? extends SelectrixMessageListener> messageListeners            = Collections.emptyList();

  private SerialPort                                     port;

  private InputStream                                    in;

  private OutputStream                                   out;

  private SelectrixWorker                                selectrixWorker;

  public static Selectrix getInstance()
  {
    return INSTANCE;
  }

  private Selectrix()
  {
  }

  /**
   * Wert setzen: {@link #messageListeners}.
   * 
   * @param messageListeners Wert
   */
  public void setMessageListeners(Collection<? extends SelectrixMessageListener> messageListeners)
  {
    this.messageListeners = messageListeners;
  }

  /**
   * Verbindung zum Selectrix-System aufnehmen und Lesethread starten.
   * 
   * @throws IOException bei Fehlern
   */
  public void start() throws IOException
  {
    openPort();

    if (this.port != null)
    {
      startReader();
    }
  }

  private void startReader()
  {
    String interfaceTyp = System.getProperty("v5t11.ifTyp");
    if ("rautenhaus".equalsIgnoreCase(interfaceTyp))
    {
      this.selectrixWorker = new RautenhausFormatWorker(this.in, this.out);
    }
    else
    {
      this.selectrixWorker = new TrixFormatWorker(this.in, this.out);
    }

    this.selectrixWorker.start();
  }

  /**
   * Lesethread stoppen und Verbindung zum Selectrix-System schliessen.
   */
  public void stop()
  {
    if (this.port != null)
    {
      stopReader();
      closePort();
    }

  }

  private void stopReader()
  {
    this.selectrixWorker.requestStop();
    try
    {
      this.selectrixWorker.join(5000);
    }
    catch (InterruptedException e)
    {
      LOGGER.warn("SelectrixWorker failed to stop within 5000 ms");
    }
  }

  /**
   * Wert abfragen.
   * 
   * @param adresse Adresse im Bereich 0 .. 127
   * @return Wert
   */
  public int getValue(int adresse)
  {
    assert adresse >= 0 && adresse <= SelectrixConnection.MAX_ADDRESSE : "Ungueltige Adresse: " + adresse;

    return this.cache.get(adresse);
  }

  /**
   * Wert abfragen.
   * 
   * @param adresse Adresse im Bereich 0 .. 127
   * @param refresh wenn <code>true</code>, Wert neu holen, sonst aus Cache liefern
   * @return Wert
   * @throws IOException
   */
  public int getValue(int adresse, boolean refresh) throws IOException
  {
    assert adresse >= 0 && adresse <= SelectrixConnection.MAX_ADDRESSE : "Ungueltige Adresse: " + adresse;

    if (refresh && this.selectrixWorker != null)
    {
      return this.selectrixWorker.read(adresse);
    }

    return this.cache.get(adresse);
  }

  /**
   * Wert setzen.
   * 
   * @param adresse Adresse im Bereich 0 .. 127
   * @param wert Wert
   * @throws IOException
   */
  public void setValue(int adresse, int wert) throws IOException
  {
    setValue(adresse, wert, true);
  }

  /**
   * Wert setzen.
   * 
   * @param adresse Adresse im Bereich 0 .. 127
   * @param wert Wert
   * @param updateInterface wenn <code>true</code>, veränderten Wert an Interface schicken
   * @throws IOException
   */
  public void setValue(int adresse, int wert, boolean updateInterface) throws IOException
  {
    assert adresse >= 0 && adresse <= SelectrixConnection.MAX_ADDRESSE : "Ungueltige Adresse: " + adresse;
    assert wert >= 0 && wert < 256 : "Ungueltiger Wert: " + wert;

    if (this.cache.getAndSet(adresse, wert) != wert)
    {
      if (updateInterface && this.selectrixWorker != null)
      {
        this.selectrixWorker.write(adresse, wert);
      }

      // Send message
      onMessage(new SelectrixMessage(adresse, wert));
    }

  }

  /**
   * {@inheritDoc}
   * 
   * @see de.gedoplan.v5t11.selectrix.SelectrixMessageListener#onMessage(de.gedoplan.v5t11.selectrix.SelectrixMessage)
   */
  @Override
  public void onMessage(SelectrixMessage message)
  {
    if (LOGGER.isDebugEnabled())
    {
      LOGGER.debug("delivering " + message + " to " + this.messageListeners.size() + " listeners");
    }

    for (SelectrixMessageListener listener : this.messageListeners)
    {
      listener.onMessage(message);
    }
  }

  private void openPort() throws IOException
  {

    String portName = System.getProperty("v5t11.portName");

    int portSpeed;
    switch (System.getProperty("v5t11.portSpeed", "9600"))
    {
    case "19200":
      portSpeed = 19200;
      break;
    case "4800":
      portSpeed = 4800;
      break;
    case "2400":
      portSpeed = 2400;
      break;
    default:
      portSpeed = 9600;
      break;
    }

    if (LOGGER.isDebugEnabled())
    {
      LOGGER.debug("Init: comPort=" + portName);
    }

    if (portName != null && !"none".equalsIgnoreCase(portName))
    {
      CommPortIdentifier portId = null;
      try
      {
        portId = CommPortIdentifier.getPortIdentifier(portName);
      }
      catch (NoSuchPortException ex)
      {
        throw new IOException("Port " + portName + " ist nicht vorhanden");
      }

      if (portId.getPortType() != CommPortIdentifier.PORT_SERIAL)
      {
        throw new IOException("Port " + portName + " ist keine Serienschnittstelle");
      }

      this.port = null;
      try
      {
        this.port = (SerialPort) portId.open("SxInterface", 2000);
      }
      catch (PortInUseException ex)
      {
        throw new IOException("Port " + portName + " ist bereits belegt");
      }

      this.in = null;
      this.out = null;
      try
      {
        this.port.setSerialPortParams(portSpeed, SerialPort.DATABITS_8, SerialPort.STOPBITS_2, SerialPort.PARITY_NONE);
        this.port.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
        this.port.enableReceiveTimeout(PORT_RECEIVE_TIMEOUT_MILLIS);
        this.port.enableReceiveThreshold(1);

        this.in = this.port.getInputStream();
        this.out = this.port.getOutputStream();
      }
      catch (UnsupportedCommOperationException ex)
      {
        closePort();
        throw new IOException("Port " + portName + " kann nicht initialisiert werden");
      }
      catch (IOException ex)
      {
        closePort();
        throw ex;
      }
    }
  }

  private void closePort()
  {
    try
    {
      this.in.close();
    }
    catch (Exception e)
    {
      // ignore
    }

    try
    {
      this.out.close();
    }
    catch (Exception e)
    {
      // ignore
    }

    try
    {
      this.port.close();
    }
    catch (Exception e)
    {
      // ignore
    }
  }

  /**
   * Zu überwachende Adresse hinzufügen. Der Lesethread beobachtet ggf. - abhängig von der konkreten Impolementierung - nur die
   * hiermit angemeldeten Adressen.
   * 
   * @param address Adresse
   */
  public void addWatchAddress(int address)
  {
    if (this.selectrixWorker != null)
    {
      if (LOGGER.isDebugEnabled())
      {
        LOGGER.debug("addWatchAddress: " + address);
      }

      this.selectrixWorker.addWatchAddress(address);
    }
  }

}
