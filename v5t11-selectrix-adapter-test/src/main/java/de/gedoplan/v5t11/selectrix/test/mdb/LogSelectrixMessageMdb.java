package de.gedoplan.v5t11.selectrix.test.mdb;

import de.gedoplan.v5t11.selectrix.SelectrixMessage;
import de.gedoplan.v5t11.selectrix.SelectrixMessageListener;

import javax.ejb.MessageDriven;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@MessageDriven(messageListenerInterface = SelectrixMessageListener.class)
public class LogSelectrixMessageMdb implements SelectrixMessageListener
{
  private static final Log LOG = LogFactory.getLog(LogSelectrixMessageMdb.class);

  @Override
  public void onMessage(SelectrixMessage message)
  {
    LOG.debug(message);
  }
}
