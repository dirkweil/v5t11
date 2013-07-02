package de.gedoplan.v5t11.selectrix.impl;

import javax.resource.ResourceException;
import javax.resource.spi.ManagedConnectionMetaData;

/**
 * Managed Connection Meta Data zum Multichannel-Adapter.
 * 
 * @author dw
 */
public class SelectrixManagedConnectionMetaData implements ManagedConnectionMetaData
{
  /**
   * {@inheritDoc}
   * 
   * @see javax.resource.spi.ManagedConnectionMetaData#getEISProductName()
   */
  @Override
  public String getEISProductName() throws ResourceException
  {
    return "Selectrix Adapter";
  }

  /**
   * {@inheritDoc}
   * 
   * @see javax.resource.spi.ManagedConnectionMetaData#getEISProductVersion()
   */
  @Override
  public String getEISProductVersion() throws ResourceException
  {
    return "1.0.0";
  }

  /**
   * {@inheritDoc}
   * 
   * @see javax.resource.spi.ManagedConnectionMetaData#getMaxConnections()
   */
  @Override
  public int getMaxConnections() throws ResourceException
  {
    return 0;
  }

  /**
   * {@inheritDoc}
   * 
   * @see javax.resource.spi.ManagedConnectionMetaData#getUserName()
   */
  @Override
  public String getUserName() throws ResourceException
  {
    return null;
  }

}
