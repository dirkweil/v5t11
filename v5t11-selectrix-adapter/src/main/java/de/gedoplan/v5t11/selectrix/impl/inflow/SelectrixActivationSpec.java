package de.gedoplan.v5t11.selectrix.impl.inflow;

import de.gedoplan.v5t11.selectrix.SelectrixMessageListener;

import javax.resource.spi.Activation;
import javax.resource.spi.ActivationSpec;
import javax.resource.spi.InvalidPropertyException;
import javax.resource.spi.ResourceAdapter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Activation Spec zum Multichannel-Adapter.
 * 
 * @author dw
 */
@Activation(messageListeners = { SelectrixMessageListener.class })
public class SelectrixActivationSpec implements ActivationSpec
{
  private static Log      log = LogFactory.getLog(SelectrixActivationSpec.class);

  private ResourceAdapter resourceAdapter;

  /**
   * {@inheritDoc}
   * 
   * @see javax.resource.spi.ActivationSpec#validate()
   */
  @Override
  public void validate() throws InvalidPropertyException
  {
    if (log.isTraceEnabled())
    {
      log.trace("validate");
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see javax.resource.spi.ResourceAdapterAssociation#getResourceAdapter()
   */
  @Override
  public ResourceAdapter getResourceAdapter()
  {
    if (log.isTraceEnabled())
    {
      log.trace("getResourceAdapter");
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
    if (log.isTraceEnabled())
    {
      log.trace("setResourceAdapter");
    }
    this.resourceAdapter = resourceAdapter;
  }

}
