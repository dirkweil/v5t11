package de.gedoplan.v5t11.selectrix;

import java.io.Serializable;

import javax.resource.Referenceable;

/**
 * Connection Factory zum Multichannel-Adapter.
 * 
 * @author dw
 */
public interface SelectrixConnectionFactory extends Serializable, Referenceable
{
  /**
   * Connection öffnen.
   * 
   * @return Connection
   */
  public SelectrixConnection getConnection();

}
