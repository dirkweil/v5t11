package de.gedoplan.v5t11.selectrix.impl;

import de.gedoplan.v5t11.selectrix.SelectrixConnection;
import de.gedoplan.v5t11.selectrix.SelectrixConnectionFactory;

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.resource.ResourceException;
import javax.resource.spi.ConnectionManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Connection Factory Implementation zum Multichannel-Adapter.
 * 
 * @author dw
 */
public class SelectrixConnectionFactoryImpl implements SelectrixConnectionFactory
{
  private static final Log                  LOG = LogFactory.getLog(SelectrixConnectionFactoryImpl.class);

  private Reference                         reference;

  private SelectrixManagedConnectionFactory managedConnectionFactory;

  private ConnectionManager                 connectionManager;

  /**
   * Konstructor.
   * 
   * @param managedConnectionFactory Managed Connection Factory
   * @param connectionManager Connection Manager
   */
  public SelectrixConnectionFactoryImpl(SelectrixManagedConnectionFactory managedConnectionFactory, ConnectionManager connectionManager)
  {
    this.managedConnectionFactory = managedConnectionFactory;
    this.connectionManager = connectionManager;
  }

  /**
   * {@inheritDoc}
   * 
   * @see de.gedoplan.v5t11.selectrix.SelectrixConnectionFactory#getConnection()
   */
  @Override
  public SelectrixConnection getConnection()
  {
    if (LOG.isTraceEnabled())
    {
      LOG.trace("getConnection");
    }
    try
    {
      return (SelectrixConnection) this.connectionManager.allocateConnection(this.managedConnectionFactory, null);
    }
    catch (ResourceException e)
    {
      throw new RuntimeException("Cannot allocate connection", e);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see javax.naming.Referenceable#getReference()
   */
  @Override
  public Reference getReference() throws NamingException
  {
    if (LOG.isTraceEnabled())
    {
      LOG.trace("getReference");
    }
    return this.reference;
  }

  /**
   * {@inheritDoc}
   * 
   * @see javax.resource.Referenceable#setReference(javax.naming.Reference)
   */
  @Override
  public void setReference(Reference reference)
  {
    if (LOG.isTraceEnabled())
    {
      LOG.trace("setReference");
    }
    this.reference = reference;
  }

}
