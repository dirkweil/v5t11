package de.gedoplan.v5t11.status.entity.baustein;

import de.gedoplan.baselibs.utils.inject.InjectionUtil;
import de.gedoplan.v5t11.status.entity.fahrzeug.Fahrzeug;
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
import java.util.concurrent.ExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import org.jboss.logging.Logger;

import com.fazecast.jSerialComm.SerialPort;

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

  protected Logger log = Logger.getLogger(getClass());

  // Device kann ein SerialPort oder ein Socket sein
  private volatile Object device;

  protected volatile InputStream in;

  protected volatile OutputStream out;

  @Getter(onMethod_ = @JsonbInclude)
  protected boolean gleisspannung;

  @Getter(onMethod_ = @JsonbInclude)
  protected boolean kurzschluss;

  // Zentrale, Kanal, SX2Kanal
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
   * Wird von Subklassen überschrieben, wenn tatsächlich gewartet werden muss.
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

      this.eventFirer.fire(this, Connected.Literal.INSTANCE);
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

    this.log.debugf("openSerialPort(%s, %d)", this.portName, getPortSpeed());

    if (this.portName != null && !"none".equalsIgnoreCase(this.portName)) {
      SerialPort port = null;
      try {
        port = SerialPort.getCommPort(this.portName);
      } catch (Exception ex) {
        throw new IOException("SerialPort " + this.portName + " ist nicht vorhanden", ex);
      }

      port.setComPortParameters(getPortSpeed(), 8, SerialPort.TWO_STOP_BITS, SerialPort.NO_PARITY);
      port.setFlowControl(SerialPort.FLOW_CONTROL_DISABLED);
      port.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, PORT_RECEIVE_TIMEOUT_MILLIS, 0);
      if (!port.isOpen()) {
        if (!port.openPort()) {
          throw new IOException("SerialPort " + this.portName + " kann nicht geöffnet werden");
        }
      }

      this.device = port;

      this.log.infof("SerialPort %s geöffnet", this.portName);

      this.in = null;
      this.out = null;
      try {
        this.in = port.getInputStream();
        this.out = port.getOutputStream();
      } catch (Exception ex) {
        closePort();
        throw new IOException("SerialPort " + this.portName + " kann nicht initialisiert werden", ex);
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
          // ((SerialPort) this.device).close();
          ((SerialPort) this.device).closePort();
        }
      } catch (Exception e) {
        // ignore
      }
    }
    this.device = null;

  }

  protected abstract int getPortSpeed();

  private static String selectFirstSerialPort() {
    SerialPort[] commPorts = SerialPort.getCommPorts();
    if (commPorts.length > 0) {
      return commPorts[0].getSystemPortName();
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

  public void lokChanged(Fahrzeug lok) {
  }

  public void injectFields() {
    InjectionUtil.injectFields(this);
  }

  public void postConstruct() {
    // Falls Port in Config gesetzt, diesen nehmen
    String configPortName = this.configService.getPortName();
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

  /**
   * Gleisprotokoll einstellen.
   *
   * Derzeit wird stets mit SX1+SX2+DCC gefahren. Das könnte ggf. in Zukunft konfigurierbar gemacht werden.
   */
  public abstract void setGleisProtokoll();

}
