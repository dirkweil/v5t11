package de.gedoplan.v5t11.selectrix.impl;

import de.gedoplan.v5t11.selectrix.SelectrixConnection;
import de.gedoplan.v5t11.selectrix.SelectrixException;

import java.io.IOException;
import java.util.Collection;

/**
 * Connection Implementation zum Multichannel-Adapter.
 *
 * @author dw
 */
public class SelectrixConnectionImpl implements SelectrixConnection {
  // private static final Log LOG = LogFactory.getLog(MultichannelConnectionImpl.class);

  private SelectrixManagedConnection managedConnection;

  // private SelectrixManagedConnectionFactory managedConnectionFactory;

  /**
   * Konstruktor.
   *
   * @param managedConnection
   *          Managed Connection
   * @param managedConnectionFactory
   *          Managed Connection Factory
   */
  public SelectrixConnectionImpl(SelectrixManagedConnection managedConnection, SelectrixManagedConnectionFactory managedConnectionFactory) {
    this.managedConnection = managedConnection;
  }

  public void start(String serialPortName, int serialPortSpeed, String interfaceTyp, Collection<Integer> adressen) {
    try {
      Selectrix.getInstance().start(serialPortName, serialPortSpeed, interfaceTyp, adressen);
    } catch (IOException e) {
      throw new SelectrixException("Cannot start Selectrix", e);
    }
  }

  @Override
  public void addWatchAddress(int address) {
    Selectrix.getInstance().addWatchAddress(address);
  }

  public void stop() {
    Selectrix.getInstance().stop();
  }

  @Override
  public int getValue(int address) {
    return Selectrix.getInstance().getValue(address);
  }

  /**
   * {@inheritDoc}
   *
   * @see de.gedoplan.v5t11.selectrix.SelectrixConnection#getValue(int, boolean)
   */
  @Override
  public int getValue(int address, boolean refresh) {
    try {
      return Selectrix.getInstance().getValue(address, refresh);
    } catch (IOException e) {
      throw new SelectrixException("Cannot get value", e);
    }
  }

  @Override
  public void setValue(int address, int value) {
    try {
      Selectrix.getInstance().setValue(address, value);
    } catch (IOException e) {
      throw new SelectrixException("Cannot set value", e);
    }
  }

  @Override
  public void close() {
    this.managedConnection.closeHandle(this);
  }
}
