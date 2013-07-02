package de.gedoplan.v5t11.selectrix.impl;

import javax.resource.cci.ResourceAdapterMetaData;

/**
 * Resource Adapter Meta Data zum Multichannel-Adapter.
 * 
 * @author dw
 */
public class SelectrixResourceAdapterMetaData implements ResourceAdapterMetaData
{
  /**
   * {@inheritDoc}
   * 
   * @see javax.resource.cci.ResourceAdapterMetaData#getAdapterVersion()
   */
  @Override
  public String getAdapterVersion()
  {
    return "1.0.0";
  }

  /**
   * {@inheritDoc}
   * 
   * @see javax.resource.cci.ResourceAdapterMetaData#getAdapterVendorName()
   */
  @Override
  public String getAdapterVendorName()
  {
    return "GEDOPLAN";
  }

  /**
   * {@inheritDoc}
   * 
   * @see javax.resource.cci.ResourceAdapterMetaData#getAdapterName()
   */
  @Override
  public String getAdapterName()
  {
    return "Selectrix Adapter";
  }

  /**
   * {@inheritDoc}
   * 
   * @see javax.resource.cci.ResourceAdapterMetaData#getAdapterShortDescription()
   */
  @Override
  public String getAdapterShortDescription()
  {
    return "Multichannel Adapter";
  }

  /**
   * {@inheritDoc}
   * 
   * @see javax.resource.cci.ResourceAdapterMetaData#getSpecVersion()
   */
  @Override
  public String getSpecVersion()
  {
    return "1.6";
  }

  /**
   * {@inheritDoc}
   * 
   * @see javax.resource.cci.ResourceAdapterMetaData#getInteractionSpecsSupported()
   */
  @Override
  public String[] getInteractionSpecsSupported()
  {
    return new String[0];
  }

  /**
   * {@inheritDoc}
   * 
   * @see javax.resource.cci.ResourceAdapterMetaData#supportsExecuteWithInputAndOutputRecord()
   */
  @Override
  public boolean supportsExecuteWithInputAndOutputRecord()
  {
    return false;
  }

  /**
   * {@inheritDoc}
   * 
   * @see javax.resource.cci.ResourceAdapterMetaData#supportsExecuteWithInputRecordOnly()
   */
  @Override
  public boolean supportsExecuteWithInputRecordOnly()
  {
    return false;
  }

  /**
   * {@inheritDoc}
   * 
   * @see javax.resource.cci.ResourceAdapterMetaData#supportsLocalTransactionDemarcation()
   */
  @Override
  public boolean supportsLocalTransactionDemarcation()
  {
    return false;
  }

}
