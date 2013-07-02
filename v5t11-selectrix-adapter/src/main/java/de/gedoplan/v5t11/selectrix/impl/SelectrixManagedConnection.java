package de.gedoplan.v5t11.selectrix.impl;

import de.gedoplan.v5t11.selectrix.SelectrixConnection;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.resource.NotSupportedException;
import javax.resource.ResourceException;
import javax.resource.spi.ConnectionEvent;
import javax.resource.spi.ConnectionEventListener;
import javax.resource.spi.ConnectionRequestInfo;
import javax.resource.spi.LocalTransaction;
import javax.resource.spi.ManagedConnection;
import javax.resource.spi.ManagedConnectionMetaData;
import javax.security.auth.Subject;
import javax.transaction.xa.XAResource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Managed Connection zum Multichannel-Adapter.
 * 
 * @author dw
 */
public class SelectrixManagedConnection implements ManagedConnection
{
  private static final Log                          LOG = LogFactory.getLog(SelectrixManagedConnection.class);

  private PrintWriter                               logwriter;

  private SelectrixManagedConnectionFactory managedConnectionFactory;

  private List<ConnectionEventListener>             listeners;

  private Object                                    connection;

  /**
   * Konstruktor.
   * 
   * @param managedConnectionFactory Managed Connection Factory
   */
  public SelectrixManagedConnection(SelectrixManagedConnectionFactory managedConnectionFactory)
  {
    this.managedConnectionFactory = managedConnectionFactory;
    this.logwriter = null;
    this.listeners = Collections.synchronizedList(new ArrayList<ConnectionEventListener>(1));
    this.connection = null;
  }

  /**
   * {@inheritDoc}
   * 
   * @see javax.resource.spi.ManagedConnection#getConnection(javax.security.auth.Subject,
   *      javax.resource.spi.ConnectionRequestInfo)
   */
  @Override
  public Object getConnection(Subject subject, ConnectionRequestInfo connectionRequestInfo) throws ResourceException
  {
    if (LOG.isTraceEnabled())
    {
      LOG.trace("getConnection");
    }
    this.connection = new SelectrixConnectionImpl(this, this.managedConnectionFactory);
    return this.connection;
  }

  /**
   * {@inheritDoc}
   * 
   * @see javax.resource.spi.ManagedConnection#associateConnection(java.lang.Object)
   */
  @Override
  public void associateConnection(Object connection) throws ResourceException
  {
    if (LOG.isTraceEnabled())
    {
      LOG.trace("associateConnection");
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see javax.resource.spi.ManagedConnection#cleanup()
   */
  @Override
  public void cleanup() throws ResourceException

  {
    if (LOG.isTraceEnabled())
    {
      LOG.trace("cleanup");
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see javax.resource.spi.ManagedConnection#destroy()
   */
  @Override
  public void destroy() throws ResourceException
  {
    if (LOG.isTraceEnabled())
    {
      LOG.trace("destroy");
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see javax.resource.spi.ManagedConnection#addConnectionEventListener(javax.resource.spi.ConnectionEventListener)
   */
  @Override
  public void addConnectionEventListener(ConnectionEventListener listener)
  {
    if (LOG.isTraceEnabled())
    {
      LOG.trace("addConnectionEventListener");
    }
    if (listener == null)
    {
      throw new IllegalArgumentException("Listener is null");
    }
    this.listeners.add(listener);
  }

  /**
   * {@inheritDoc}
   * 
   * @see javax.resource.spi.ManagedConnection#removeConnectionEventListener(javax.resource.spi.ConnectionEventListener)
   */
  @Override
  public void removeConnectionEventListener(ConnectionEventListener listener)
  {
    if (LOG.isTraceEnabled())
    {
      LOG.trace("removeConnectionEventListener");
    }
    if (listener == null)
    {
      throw new IllegalArgumentException("Listener is null");
    }
    this.listeners.remove(listener);
  }

  /**
   * Handle schliessen.
   * 
   * @param handle Handle
   */
  public void closeHandle(SelectrixConnection handle)
  {
    ConnectionEvent event = new ConnectionEvent(this, ConnectionEvent.CONNECTION_CLOSED);
    event.setConnectionHandle(handle);
    for (ConnectionEventListener cel : this.listeners)
    {
      cel.connectionClosed(event);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see javax.resource.spi.ManagedConnection#getLogWriter()
   */
  @Override
  public PrintWriter getLogWriter() throws ResourceException
  {
    if (LOG.isTraceEnabled())
    {
      LOG.trace("getLogWriter");
    }
    return this.logwriter;
  }

  /**
   * {@inheritDoc}
   * 
   * @see javax.resource.spi.ManagedConnection#setLogWriter(java.io.PrintWriter)
   */
  @Override
  public void setLogWriter(PrintWriter out) throws ResourceException
  {
    if (LOG.isTraceEnabled())
    {
      LOG.trace("setLogWriter");
    }
    this.logwriter = out;
  }

  /**
   * {@inheritDoc}
   * 
   * @see javax.resource.spi.ManagedConnection#getLocalTransaction()
   */
  @Override
  public LocalTransaction getLocalTransaction() throws ResourceException
  {
    throw new NotSupportedException("LocalTransaction not supported");
  }

  /**
   * {@inheritDoc}
   * 
   * @see javax.resource.spi.ManagedConnection#getXAResource()
   */
  @Override
  public XAResource getXAResource() throws ResourceException
  {
    throw new NotSupportedException("GetXAResource not supported not supported");
  }

  /**
   * {@inheritDoc}
   * 
   * @see javax.resource.spi.ManagedConnection#getMetaData()
   */
  @Override
  public ManagedConnectionMetaData getMetaData() throws ResourceException
  {
    if (LOG.isTraceEnabled())
    {
      LOG.trace("getMetaData");
    }
    return new SelectrixManagedConnectionMetaData();
  }

}
