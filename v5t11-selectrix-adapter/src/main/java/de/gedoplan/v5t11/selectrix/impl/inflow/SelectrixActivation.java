package de.gedoplan.v5t11.selectrix.impl.inflow;

import de.gedoplan.v5t11.selectrix.SelectrixMessage;
import de.gedoplan.v5t11.selectrix.SelectrixMessageListener;
import de.gedoplan.v5t11.selectrix.impl.SelectrixResourceAdapter;

import javax.resource.ResourceException;
import javax.resource.spi.endpoint.MessageEndpoint;
import javax.resource.spi.endpoint.MessageEndpointFactory;

/**
 * Activation zum zum Multichannel-Adapter.
 * 
 * @author dw
 */
public class SelectrixActivation implements SelectrixMessageListener
{
  // private DemoBidirectionalResourceAdapter resourceAdapter;

  // private MultichannelActivationSpec spec;

  // private MessageEndpointFactory endpointFactory;

  private SelectrixMessageListener messageListener;

  // private static final Log LOG = LogFactory.getLog(MultichannelActivation.class);

  /**
   * Konstruktor.
   * 
   * @param resourceAdapter Resource Adapter
   * @param endpointFactory Message Endpoint Factory
   * @param spec Activation Spec
   * @exception ResourceException bei Fehlern
   */
  public SelectrixActivation(SelectrixResourceAdapter resourceAdapter, MessageEndpointFactory endpointFactory, SelectrixActivationSpec spec) throws ResourceException

  {
    // this.resourceAdapter = resourceAdapter;
    // this.endpointFactory = endpointFactory;
    // this.spec = spec;

    MessageEndpoint endpoint = endpointFactory.createEndpoint(null);
    if (!(endpoint instanceof SelectrixMessageListener))
    {
      throw new IllegalArgumentException("Message endpoint " + endpoint + " does not implement ChannelMessageListener");
    }
    this.messageListener = (SelectrixMessageListener) endpoint;
  }

  /**
   * Activation starten.
   * 
   * @throws ResourceException bei Fehlern
   */
  public void start() throws ResourceException
  {
  }

  /**
   * Activation stoppen.
   */
  public void stop()
  {
  }

  @Override
  public void onMessage(SelectrixMessage message)
  {
    this.messageListener.onMessage(message);
  }
}
