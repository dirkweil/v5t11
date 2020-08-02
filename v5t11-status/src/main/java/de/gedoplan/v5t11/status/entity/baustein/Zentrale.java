package de.gedoplan.v5t11.status.entity.baustein;

import de.gedoplan.baselibs.utils.inject.InjectionUtil;
import de.gedoplan.v5t11.status.entity.lok.Lok;
import de.gedoplan.v5t11.status.service.ConfigService;
import de.gedoplan.v5t11.util.cdi.EventFirer;
import de.gedoplan.v5t11.util.jsonb.JsonbInclude;
import de.gedoplan.v5t11.util.misc.V5t11Exception;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.concurrent.ExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Zentrale inkl. PC-Interface.
 *
 * @author dw
 */
@XmlAccessorType(XmlAccessType.NONE)
@NoArgsConstructor
public abstract class Zentrale implements Closeable {

  public static final int PORT_RECEIVE_TIMEOUT_MILLIS = 500;

  @XmlAttribute
  @Getter
  protected String portName;

  protected Log log = LogFactory.getLog(getClass());

  // Device kann ein SerialPort oder ein Socket sein
  private volatile Object device;

  protected volatile InputStream in;

  protected volatile OutputStream out;

  @Getter(onMethod_ = @JsonbInclude)
  protected boolean gleisspannung;

  @Getter(onMethod_ = @JsonbInclude)
  protected boolean kurzschluss;

  @Inject
  protected EventFirer eventFirer;

  @Inject
  ConfigService configService;

  public abstract void open(ExecutorService executorService);

  @Override
  public abstract void close();

  public abstract void setGleisspannung(boolean gleisspannung);

  /**
   * Auf Abschluss eines Synchronisationszyklus warten.
   * Wird von Subklassen überschrieben, wenn tatsächlich gewaret werden muss.
   */
  public void awaitSync() {
  }

  protected void openPort() {
    try {
      Pattern pattern = Pattern.compile("(?<host>\\S+):(?<port>\\d+)");
      Matcher matcher = pattern.matcher(this.portName);
      if (matcher.matches()) {
        openSocket(matcher.group("host"), Integer.parseInt(matcher.group("port")));
      } else {
        openSerialPort();
      }
    } catch (Exception e) {
      try {
        closePort();
      } catch (Exception ignore) {

      }

      throw new V5t11Exception("Kann Port " + this.portName + " nicht öffnen", e);
    }

  }

  private void openSocket(String host, int port) throws UnknownHostException, IOException {
    if (this.log.isDebugEnabled()) {
      this.log.debug("openSocket(" + host + "," + port + ")");
    }

    Socket socket = new Socket(host, port);
    this.device = socket;
    this.in = socket.getInputStream();
    this.out = socket.getOutputStream();
  }

  private void openSerialPort() throws IOException {

    if (this.log.isDebugEnabled()) {
      this.log.debug("openSerialPort(" + this.portName + ", " + getPortSpeed() + ")");
    }

    if (this.portName != null && !"none".equalsIgnoreCase(this.portName)) {
      CommPortIdentifier portId = null;
      try {
        portId = CommPortIdentifier.getPortIdentifier(this.portName);
      } catch (NoSuchPortException ex) {
        throw new IOException("Port " + this.portName + " ist nicht vorhanden");
      }

      if (portId.getPortType() != CommPortIdentifier.PORT_SERIAL) {
        throw new IOException("Port " + this.portName + " ist keine Serienschnittstelle");
      }

      SerialPort port = null;
      try {
        port = portId.open("SxInterface", 2000);
      } catch (PortInUseException ex) {
        throw new IOException("Port " + this.portName + " ist bereits belegt");
      }
      this.device = port;

      this.in = null;
      this.out = null;
      try {
        port.setSerialPortParams(getPortSpeed(), SerialPort.DATABITS_8, SerialPort.STOPBITS_2, SerialPort.PARITY_NONE);
        port.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
        port.enableReceiveTimeout(PORT_RECEIVE_TIMEOUT_MILLIS);
        port.enableReceiveThreshold(1);

        this.in = port.getInputStream();
        this.out = port.getOutputStream();
      } catch (UnsupportedCommOperationException ex) {
        closePort();
        throw new IOException("Port " + this.portName + " kann nicht initialisiert werden");
      } catch (IOException ex) {
        closePort();
        throw ex;
      }
    }
  }

  protected void closePort() {
    if (this.in != null) {
      try {
        this.in.close();
      } catch (Exception e) {
        // ignore
      }
    }
    this.in = null;

    if (this.out != null) {
      try {
        this.out.close();
      } catch (Exception e) {
        // ignore
      }
    }
    this.out = null;

    if (this.device != null) {
      try {
        if (this.log.isDebugEnabled()) {
          this.log.debug("close port " + this.portName);
        }

        // Device kann ein SerialPort oder ein Socket sein
        if (this.device instanceof Socket) {
          ((Socket) this.device).close();
        } else {
          ((SerialPort) this.device).close();
        }
      } catch (Exception e) {
        // ignore
      }
    }
    this.device = null;

  }

  protected abstract int getPortSpeed();

  @SuppressWarnings("unchecked")
  private static String selectFirstSerialPort() {
    Enumeration<CommPortIdentifier> portList = CommPortIdentifier.getPortIdentifiers();
    while (portList.hasMoreElements()) {
      CommPortIdentifier portId = portList.nextElement();
      if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
        return portId.getName();
      }
    }

    return "none";
  }

  /**
   * Anzahl SX-Busse liefern.
   *
   * @return Bus-Anzahl
   */
  public int getBusAnzahl() {
    return 2;
  }

  public abstract int getSX1Kanal(int adr);

  public abstract void setSX1Kanal(int adr, int wert);

  public void lokChanged(Lok lok) {
  }

  public void injectFields() {
    InjectionUtil.injectFields(this);
  }

  public void postConstruct() {
    // Falls Port in Config gesetzt, diesen nehmen
    String configPortName = System.getProperty(ConfigService.PROPERTY_PORT_NAME);
    if (configPortName != null) {
      this.portName = configPortName;
    }

    // Falls Port nicht gesetzt oder "auto", ersten Serienport nehmen
    if (this.portName == null || "auto".equalsIgnoreCase(this.portName)) {
      this.log.debug("select first serial port");
      this.portName = selectFirstSerialPort();
    }

    this.log.debug("port: " + this.portName);

  }

}
