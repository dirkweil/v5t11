package de.gedoplan.v5t11.selectrix.impl;

import de.gedoplan.v5t11.selectrix.impl.inflow.SelectrixActivation;
import de.gedoplan.v5t11.selectrix.impl.inflow.SelectrixActivationSpec;

import java.util.concurrent.ConcurrentHashMap;

import javax.resource.ResourceException;
import javax.resource.spi.ActivationSpec;
import javax.resource.spi.BootstrapContext;
import javax.resource.spi.Connector;
import javax.resource.spi.ResourceAdapter;
import javax.resource.spi.ResourceAdapterInternalException;
import javax.resource.spi.TransactionSupport;
import javax.resource.spi.endpoint.MessageEndpointFactory;
import javax.transaction.xa.XAResource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Resource Adapter zum zum Multichannel-Adapter.
 *
 * @author dw
 */

@Connector(reauthenticationSupport = false, transactionSupport = TransactionSupport.TransactionSupportLevel.NoTransaction)
public class SelectrixResourceAdapter implements ResourceAdapter {
  private static Log log = LogFactory.getLog(SelectrixResourceAdapter.class);

  private ConcurrentHashMap<SelectrixActivationSpec, SelectrixActivation> activations;

  /**
   * Konstruktor.
   */
  public SelectrixResourceAdapter() {
    this.activations = new ConcurrentHashMap<SelectrixActivationSpec, SelectrixActivation>();
  }

  /**
   * {@inheritDoc}
   *
   * @see javax.resource.spi.ResourceAdapter#endpointActivation(javax.resource.spi.endpoint.MessageEndpointFactory,
   *      javax.resource.spi.ActivationSpec)
   */
  @Override
  public void endpointActivation(MessageEndpointFactory endpointFactory, ActivationSpec spec) throws ResourceException {
    if (log.isTraceEnabled()) {
      log.trace("endpointActivation");
    }

    try {
      SelectrixActivation activation = new SelectrixActivation(this, endpointFactory, (SelectrixActivationSpec) spec);
      this.activations.put((SelectrixActivationSpec) spec, activation);
      activation.start();
    } catch (Exception e) {
      log.error("Cannot activate endpoint", e);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @see javax.resource.spi.ResourceAdapter#endpointDeactivation(javax.resource.spi.endpoint.MessageEndpointFactory,
   *      javax.resource.spi.ActivationSpec)
   */
  @Override
  public void endpointDeactivation(MessageEndpointFactory endpointFactory, ActivationSpec spec) {
    if (log.isTraceEnabled()) {
      log.trace("endpointDeactivation");
    }

    SelectrixActivation activation = this.activations.remove(spec);
    if (activation != null) {
      activation.stop();
    }
  }

  /**
   * {@inheritDoc}
   *
   * @see javax.resource.spi.ResourceAdapter#start(javax.resource.spi.BootstrapContext)
   */
  @Override
  public void start(BootstrapContext bootstrapContext) throws ResourceAdapterInternalException {
    if (log.isTraceEnabled()) {
      log.trace("start");
    }

    Selectrix.getInstance().setMessageListeners(this.activations.values());
  }

  /**
   * {@inheritDoc}
   *
   * @see javax.resource.spi.ResourceAdapter#stop()
   */
  @Override
  public void stop() {
    if (log.isTraceEnabled()) {
      log.trace("stop");
    }

    Selectrix.getInstance().stop();
  }

  /**
   * {@inheritDoc}
   *
   * @see javax.resource.spi.ResourceAdapter#getXAResources(javax.resource.spi.ActivationSpec[])
   */
  @Override
  public XAResource[] getXAResources(ActivationSpec[] specs) throws ResourceException {
    if (log.isTraceEnabled()) {
      log.trace("getXAResources");
    }
    return null;
  }

  /**
   * {@inheritDoc}
   *
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    return 1;
  }

  /**
   * {@inheritDoc}
   *
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    return true;
  }
}
