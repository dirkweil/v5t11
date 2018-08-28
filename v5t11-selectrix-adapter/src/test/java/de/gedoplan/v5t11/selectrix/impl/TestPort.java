package de.gedoplan.v5t11.selectrix.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

public class TestPort {
	public static void main(String[] args) throws Exception {

		String portName = "/dev/ttyUSB0";

		int portSpeed = 19200;

		System.out.println("Init: comPort=" + portName);

		SerialPort port = null;
		try {
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

				try {
					port = (SerialPort) portId.open("SxInterface", 2000);
				} catch (PortInUseException ex) {
					throw new IOException("Port " + portName + " ist bereits belegt");
				}

				InputStream in = null;
				OutputStream out = null;
				try {
					port.setSerialPortParams(portSpeed, SerialPort.DATABITS_8, SerialPort.STOPBITS_2,
							SerialPort.PARITY_NONE);
					port.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
					port.enableReceiveTimeout(500);
					port.enableReceiveThreshold(1);

					in = port.getInputStream();
					out = port.getOutputStream();
				} catch (UnsupportedCommOperationException ex) {
					throw new IOException("Port " + portName + " kann nicht initialisiert werden");
				} catch (IOException ex) {
					throw ex;
				}
			}
		} finally {
			port.close();
		}
	}

}
