package de.gedoplan.v5t11.comserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

public class ComServer {

  private static Log log = LogFactory.getLog(ComServer.class);

  private static int port = 30079;
  private static String serialDevName = "auto";
  private static int speed = 9600;

  private static SerialPort serialDev;

  private static Socket socket;

  private static ExecutorService threadPool = Executors.newCachedThreadPool();

  public static void main(String[] args) {
    try {
      for (String arg : args) {
        if (arg.startsWith("--port=")) {
          port = Integer.parseUnsignedInt(arg.substring(7));
        } else if (arg.startsWith("--dev=")) {
          serialDevName = arg.substring(6);
        } else if (arg.startsWith("--speed=")) {
          speed = Integer.parseUnsignedInt(arg.substring(8));
        } else {
          throw new IllegalArgumentException("Unknown option: " + arg);
        }
      }
    } catch (Exception e) {
      System.err.println(e);
      System.err.println("Usage: java ComServer [--port=PortNo] [--dev=Device] [--speed=BaudRate]");
      System.exit(2);
    }

    if ("auto".equalsIgnoreCase(serialDevName)) {
      serialDevName = selectFirstSerialPort();
    }

    if (log.isDebugEnabled()) {
      log.debug("port: " + port);
      log.debug("dev: " + serialDevName);
      log.debug("speed: " + speed);
    }

    if (!"none".equals(serialDevName)) {
      try {
        serialDev = openSerialDevice();
      } catch (NoSuchPortException e) {
        log.error("Device " + serialDevName + " not found");
      } catch (PortInUseException e) {
        log.error("Device " + serialDevName + " is busy");
      } catch (Exception e) {
        log.error(e);
      }

      if (log.isDebugEnabled()) {
        log.debug("Opened serial device " + serialDevName);
      }
    }

    try (ServerSocket serverSocket = new ServerSocket(port)) {

      while (true) {
        if (log.isDebugEnabled()) {
          log.debug("Waiting for incoming connection on port " + port);
        }

        socket = serverSocket.accept();

        if (!"none".equals(serialDevName)) {
          connect();
        } else {
          simulate();
        }
      }
    } catch (Exception e) {
      log.error(e);

      try {
        serialDev.close();
      } catch (Exception ignore) {}
    }
  }

  private static String selectFirstSerialPort() {
    @SuppressWarnings("unchecked")
    Enumeration<CommPortIdentifier> portList = CommPortIdentifier.getPortIdentifiers();
    while (portList.hasMoreElements()) {
      CommPortIdentifier portId = portList.nextElement();
      if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
        return portId.getName();
      }
    }

    // throw new NoSuchElementException("No serial port found");
    return "none";
  }

  private static SerialPort openSerialDevice() throws NoSuchPortException, PortInUseException, UnsupportedCommOperationException {
    CommPortIdentifier commPortId = CommPortIdentifier.getPortIdentifier(serialDevName);
    if (commPortId.getPortType() != CommPortIdentifier.PORT_SERIAL) {
      throw new IllegalArgumentException("Device " + serialDevName + " is no serial device");
    }
    SerialPort comPort = commPortId.open("SxInterface", 2000);

    comPort.setSerialPortParams(speed, SerialPort.DATABITS_8, SerialPort.STOPBITS_2, SerialPort.PARITY_NONE);
    comPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
    comPort.enableReceiveTimeout(500);
    comPort.enableReceiveThreshold(1);

    return comPort;
  }

  private static void connect() throws IOException {
    if (log.isDebugEnabled()) {
      log.debug(String.format("Starting data transfer %s <-> %s", socket.getInetAddress(), serialDevName));
    }

    InputStream serialDevInputStream = serialDev.getInputStream();
    OutputStream serialDevOutputStream = serialDev.getOutputStream();

    InputStream socketInputStream = socket.getInputStream();
    OutputStream socketOutputStream = socket.getOutputStream();

    threadPool.execute(() -> transfer(String.format("from %s", serialDevName), serialDevInputStream, socketOutputStream));

    transfer(String.format("to   %s", serialDevName), socketInputStream, serialDevOutputStream);
  }

  private static void transfer(String name, InputStream inputStream, OutputStream outputStream) {
    try {
      while (true) {
        int b = inputStream.read();
        if (b < 0) {
          break;
        }

        if (log.isTraceEnabled()) {
          log.trace(name + ": " + to8BitString(b) + " (" + b + ")");
        }

        outputStream.write(b);
        outputStream.flush();
      }
    } catch (IOException e) {
      log.error(e);
    }

  }

  private static void simulate() throws IOException {
    if (log.isDebugEnabled()) {
      log.debug(String.format("Starting SX simulation %s <-> SX", socket.getInetAddress()));
    }

    InputStream socketInputStream = socket.getInputStream();
    OutputStream socketOutputStream = socket.getOutputStream();

    byte[] values = new byte[128];

    while (true) {
      int i = socketInputStream.read();
      if (i < 0) {
        break;
      }

      boolean writeCmd = (i & 0x80) != 0;
      int adr = (i & 0x7f);

      i = socketInputStream.read();
      if (i < 0) {
        break;
      }

      if (writeCmd) {
        byte value = (byte) i;
        values[adr] = value;

        if (log.isTraceEnabled()) {
          log.trace(String.format("write(%3d): %s", adr, to8BitString(value)));
        }
      } else {
        byte value = values[adr];
        if (log.isTraceEnabled()) {
          log.trace(String.format("read(%3d): %s", adr, to8BitString(value)));
        }
        socketOutputStream.write(value);
      }
    }
  }

  private static CharSequence to8BitString(int b) {
    StringBuilder sb = new StringBuilder();
    for (int mask = 0x80; mask != 0; mask >>= 1) {
      sb.append((b & mask) != 0 ? '1' : '0');
    }
    return sb;
  }

}
