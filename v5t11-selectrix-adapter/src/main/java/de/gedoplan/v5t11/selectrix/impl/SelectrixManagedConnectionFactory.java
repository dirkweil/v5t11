package de.gedoplan.v5t11.selectrix.impl;

import de.gedoplan.v5t11.selectrix.SelectrixConnection;
import de.gedoplan.v5t11.selectrix.SelectrixConnectionFactory;

import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Set;

import javax.resource.ResourceException;
import javax.resource.spi.ConnectionDefinition;
import javax.resource.spi.ConnectionManager;
import javax.resource.spi.ConnectionRequestInfo;
import javax.resource.spi.ManagedConnection;
import javax.resource.spi.ManagedConnectionFactory;
import javax.resource.spi.ResourceAdapter;
import javax.resource.spi.ResourceAdapterAssociation;
import javax.security.auth.Subject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Managed Connection Factory zum Multichannel-Adapter.
 * 
 * @author dw
 */
@ConnectionDefinition(connectionFactory = SelectrixConnectionFactory.class, connectionFactoryImpl = SelectrixConnectionFactoryImpl.class, connection = SelectrixConnection.class, connectionImpl = SelectrixConnectionImpl.class)
public class SelectrixManagedConnectionFactory implements ManagedConnectionFactory, ResourceAdapterAssociation
{
  private static final Log LOG = LogFactory.getLog(SelectrixManagedConnectionFactory.class);

  private ResourceAdapter  resourceAdapter;

  private PrintWriter      logwriter;

  /**
   * {@inheritDoc}
   * 
   * @see javax.resource.spi.ManagedConnectionFactory#createConnectionFactory(javax.resource.spi.ConnectionManager)
   */
  @Override
  public Object createConnectionFactory(ConnectionManager connectionManager) throws ResourceException
  {
    if (LOG.isTraceEnabled())
    {
      LOG.trace("createConnectionFactory");
    }
    return new SelectrixConnectionFactoryImpl(this, connectionManager);
  }

  /**
   * {@inheritDoc}
   * 
   * @see javax.resource.spi.ManagedConnectionFactory#createConnectionFactory()
   */
  @Override
  public Object createConnectionFactory() throws ResourceException
  {
    throw new ResourceException("This resource adapter doesn't support non-managed environments");
  }

  /**
   * {@inheritDoc}
   * 
   * @see javax.resource.spi.ManagedConnectionFactory#createManagedConnection(javax.security.auth.Subject,
   *      javax.resource.spi.ConnectionRequestInfo)
   */
  @Override
  public ManagedConnection createManagedConnection(Subject subject, ConnectionRequestInfo connectionRequestInfo) throws ResourceException
  {
    if (LOG.isTraceEnabled())
    {
      LOG.trace("createManagedConnection");
    }
    return new SelectrixManagedConnection(this);
  }

  /**
   * {@inheritDoc}
   * 
   * @see javax.resource.spi.ManagedConnectionFactory#matchManagedConnections(java.util.Set, javax.security.auth.Subject,
   *      javax.resource.spi.ConnectionRequestInfo)
   */
  @SuppressWarnings("rawtypes")
  @Override
  public ManagedConnection matchManagedConnections(Set connectionSet, Subject subject, ConnectionRequestInfo connectionRequestInfo) throws ResourceException
  {
    if (LOG.isTraceEnabled())
    {
      LOG.trace("matchManagedConnections");
    }
    ManagedConnection result = null;
    Iterator it = connectionSet.iterator();
    while (result == null && it.hasNext())
    {
      ManagedConnection mc = (ManagedConnection) it.next();
      if (mc instanceof SelectrixManagedConnection)
      {
        result = mc;
      }

    }
    return result;
  }

  /**
   * {@inheritDoc}
   * 
   * @see javax.resource.spi.ManagedConnectionFactory#getLogWriter()
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
   * @see javax.resource.spi.ManagedConnectionFactory#setLogWriter(java.io.PrintWriter)
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
   * @see javax.resource.spi.ResourceAdapterAssociation#getResourceAdapter()
   */
  @Override
  public ResourceAdapter getResourceAdapter()
  {
    if (LOG.isTraceEnabled())
    {
      LOG.trace("getResourceAdapter");
    }
    return this.resourceAdapter;
  }

  /**
   * {@inheritDoc}
   * 
   * @see javax.resource.spi.ResourceAdapterAssociation#setResourceAdapter(javax.resource.spi.ResourceAdapter)
   */
  @Override
  public void setResourceAdapter(ResourceAdapter resourceAdapter)
  {
    if (LOG.isTraceEnabled())
    {
      LOG.trace("setResourceAdapter");
    }
    this.resourceAdapter = resourceAdapter;
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode()
  {
    return 1;
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj)
  {
    if (this == obj)
    {
      return true;
    }
    if (obj == null)
    {
      return false;
    }
    if (getClass() != obj.getClass())
    {
      return false;
    }
    return true;
  }

}
