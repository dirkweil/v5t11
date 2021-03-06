package de.gedoplan.v5t11.selectrix.impl;

import de.gedoplan.v5t11.selectrix.SelectrixConnection;
import de.gedoplan.v5t11.selectrix.SelectrixMessage;
import de.gedoplan.v5t11.selectrix.SelectrixMessageListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

public final class Selectrix implements SelectrixMessageListener {
  public static final int PORT_RECEIVE_TIMEOUT_MILLIS = 500;

  private static final Selectrix INSTANCE = new Selectrix();

  private AtomicIntegerArray cache = new AtomicIntegerArray(SelectrixConnection.MAX_ADDRESSE + 1);

  private static final Log LOGGER = LogFactory.getLog(Selectrix.class);

  private Collection<? extends SelectrixMessageListener> messageListeners = Collections.emptyList();

  // TODO Q&D-Hack: Device kann ein SerialPort oder ein Socket sein
  private Object device;

  private InputStream in;

  private OutputStream out;

  private SelectrixWorker selectrixWorker;

  private String serialPortName;

  private int serialPortSpeed;

  private String interfaceTyp;

  private Collection<Integer> adressen;

  public static Selectrix getInstance() {
    return INSTANCE;
  }

  private Selectrix() {
  }

  /**
   * Wert setzen: {@link #messageListeners}.
   *
   * @param messageListeners
   *          Wert
   */
  public void setMessageListeners(Collection<? extends SelectrixMessageListener> messageListeners) {
    this.messageListeners = messageListeners;
  }

  /**
   * Verbindung zum Selectrix-System aufnehmen und Lesethread starten.
   *
   * @param adressen
   * @param interfaceTyp
   * @param serialPortSpeed
   * @param serialPortName
   *
   * @throws IOException
   *           bei Fehlern
   */
  public void start(String serialPortName, int serialPortSpeed, String interfaceTyp, Collection<Integer> adressen) {
    this.serialPortName = serialPortName;
    this.serialPortSpeed = serialPortSpeed;
    this.interfaceTyp = interfaceTyp;
    this.adressen = adressen;

    restart();
  }

  public void restart() {
    while (true) {
      try {
        start();
        return;
      } catch (IOException e) {
        try {
          Thread.sleep(1000);
        } catch (InterruptedException ignore) {}
      }
    }
  }

  private void start() throws IOException {

    if (this.serialPortSpeed <= 0) {
      this.serialPortSpeed = "rautenhaus".equalsIgnoreCase(this.interfaceTyp) ? 19200 : 9600;
    }

    openPort(this.serialPortName, this.serialPortSpeed);

    if (this.device != null) {
      startReader(this.interfaceTyp);
    }

    for (int adr : this.adressen) {
      addWatchAddress(adr);
    }
  }

  private void startReader(String interfaceTyp) {
    if ("rautenhaus".equalsIgnoreCase(interfaceTyp)) {
      this.selectrixWorker = new RautenhausFormatWorker(this.in, this.out);
    } else {
      this.selectrixWorker = new TrixFormatWorker(this.in, this.out);
    }

    this.selectrixWorker.start();
  }

  /**
   * Lesethread stoppen und Verbindung zum Selectrix-System schliessen.
   */
  public void stop() {
    if (this.device != null) {
      stopReader();
      closePort();
    }

  }

  private void stopReader() {
    this.selectrixWorker.requestStop();
    try {
      this.selectrixWorker.join(5000);
    } catch (InterruptedException e) {
      LOGGER.warn("SelectrixWorker failed to stop within 5000 ms");
    }
  }

  /**
   * Wert abfragen.
   *
   * @param adresse
   *          Adresse im Bereich 0 .. 127
   * @return Wert
   */
  public int getValue(int adresse) {
    assert adresse >= 0 && adresse <= SelectrixConnection.MAX_ADDRESSE : "Ungueltige Adresse: " + adresse;

    return this.cache.get(adresse);
  }

  /**
   * Wert abfragen.
   *
   * @param adresse
   *          Adresse im Bereich 0 .. 127
   * @param refresh
   *          wenn <code>true</code>, Wert neu holen, sonst aus Cache liefern
   * @return Wert
   * @throws IOException
   */
  public int getValue(int adresse, boolean refresh) throws IOException {
    assert adresse >= 0 && adresse <= SelectrixConnection.MAX_ADDRESSE : "Ungueltige Adresse: " + adresse;

    if (refresh && this.selectrixWorker != null) {
      while (true) {
        try {
          return this.selectrixWorker.read(adresse);
        } catch (IOException e) {
          stop();
          restart();
        }
      }
    }

    return this.cache.get(adresse);
  }

  /**
   * Wert setzen.
   *
   * @param adresse
   *          Adresse im Bereich 0 .. 127
   * @param wert
   *          Wert
   * @throws IOException
   */
  public void setValue(int adresse, int wert) throws IOException {
    setValue(adresse, wert, true);
  }

  /**
   * Wert setzen.
   *
   * @param adresse
   *          Adresse im Bereich 0 .. 127
   * @param wert
   *          Wert
   * @param updateInterface
   *          wenn <code>true</code>, veränderten Wert an Interface schicken
   * @throws IOException
   */
  public void setValue(int adresse, int wert, boolean updateInterface) throws IOException {
    assert adresse >= 0 && adresse <= SelectrixConnection.MAX_ADDRESSE : "Ungueltige Adresse: " + adresse;
    assert wert >= 0 && wert < 256 : "Ungueltiger Wert: " + wert;

    if (this.cache.getAndSet(adresse, wert) != wert) {
      if (updateInterface && this.selectrixWorker != null) {
        while (true) {
          try {
            this.selectrixWorker.write(adresse, wert);
            return;
          } catch (IOException e) {
            stop();
            restart();
          }
        }
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
  public void onMessage(SelectrixMessage message) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("delivering " + message + " to " + this.messageListeners.size() + " listeners");
    }

    for (SelectrixMessageListener listener : this.messageListeners) {
      listener.onMessage(message);
    }
  }

  private void openPort(String portName, int portSpeed) throws IOException {

    if ("auto".equalsIgnoreCase(portName)) {
      portName = selectFirstSerialPort();
    }

    Pattern pattern = Pattern.compile("(?<host>\\S+):(?<port>\\d+)");
    Matcher matcher = pattern.matcher(portName);
    if (matcher.matches()) {
      openSocket(matcher.group("host"), Integer.parseInt(matcher.group("port")));
    } else {
      openSerialPort(portName, portSpeed);
    }
  }

  private void openSocket(String host, int port) throws UnknownHostException, IOException {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("openSocket(" + host + "," + port + ")");
    }

    Socket socket = new Socket(host, port);
    this.device = socket;
    this.in = socket.getInputStream();
    this.out = socket.getOutputStream();
  }

  private void openSerialPort(String portName, int portSpeed) throws IOException {

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("openSerialPort(" + portName + ", " + portSpeed + ")");
    }

    if (portName != null && !"none".equalsIgnoreCase(portName)) {
      CommPortIdentifier portId = null;
      try {
        portId = CommPortIdentifier.getPortIdentifier(portName);
      } catch (NoSuchPortException ex) {
        throw new IOException("Port " + portName + " ist nicht vorhanden");
      }

      if (portId.getPortType() != CommPortIdentifier.PORT_SERIAL) {
        throw new IOException("Port " + portName + " ist keine Serienschnittstelle");
      }

      SerialPort port = null;
      try {
        port = portId.open("SxInterface", 2000);
      } catch (PortInUseException ex) {
        throw new IOException("Port " + portName + " ist bereits belegt");
      }
      this.device = port;

      this.in = null;
      this.out = null;
      try {
        port.setSerialPortParams(portSpeed, SerialPort.DATABITS_8, SerialPort.STOPBITS_2, SerialPort.PARITY_NONE);
        port.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
        port.enableReceiveTimeout(PORT_RECEIVE_TIMEOUT_MILLIS);
        port.enableReceiveThreshold(1);

        this.in = port.getInputStream();
        this.out = port.getOutputStream();
      } catch (UnsupportedCommOperationException ex) {
        closePort();
        throw new IOException("Port " + portName + " kann nicht initialisiert werden");
      } catch (IOException ex) {
        closePort();
        throw ex;
      }
    }
  }

  private String selectFirstSerialPort() {
    @SuppressWarnings("unchecked")
    Enumeration<CommPortIdentifier> portList = CommPortIdentifier.getPortIdentifiers();
    while (portList.hasMoreElements()) {
      CommPortIdentifier portId = portList.nextElement();
      if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
        return portId.getName();
      }
    }

    return "none";
  }

  private void closePort() {
    try {
      this.in.close();
    } catch (Exception e) {
      // ignore
    }

    try {
      this.out.close();
    } catch (Exception e) {
      // ignore
    }

    try {
      // TODO Q&D-Hack: Device kann ein SerialPort oder ein Socket sein
      if (this.device instanceof Socket) {
        ((Socket) this.device).close();
      } else {
        ((SerialPort) this.device).close();
      }
    } catch (Exception e) {
      // ignore
    }
  }

  /**
   * Zu überwachende Adresse hinzufügen. Der Lesethread beobachtet ggf. - abhängig von der konkreten Impolementierung - nur die
   * hiermit angemeldeten Adressen.
   *
   * @param address
   *          Adresse
   */
  public void addWatchAddress(int address) {
    if (this.selectrixWorker != null) {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("addWatchAddress: " + address);
      }

      this.selectrixWorker.addWatchAddress(address);
    }
  }

}
