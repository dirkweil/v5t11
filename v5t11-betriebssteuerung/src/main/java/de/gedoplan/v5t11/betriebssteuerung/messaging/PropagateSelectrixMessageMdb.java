package de.gedoplan.v5t11.betriebssteuerung.messaging;

import de.gedoplan.v5t11.betriebssteuerung.steuerung.Steuerung;
import de.gedoplan.v5t11.selectrix.SelectrixMessage;
import de.gedoplan.v5t11.selectrix.SelectrixMessageListener;

import javax.ejb.MessageDriven;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@MessageDriven(messageListenerInterface = SelectrixMessageListener.class)
public class PropagateSelectrixMessageMdb implements SelectrixMessageListener
{
  @Inject
  private Steuerung        steuerung;

  @Inject
  private MessagePusher    messagePusher;

  private static final Log LOG = LogFactory.getLog(PropagateSelectrixMessageMdb.class);

  @Override
  public void onMessage(SelectrixMessage message)
  {
    try
    {
      this.steuerung.onMessage(message);
    }
    catch (Exception e) // CHECKSTYLE:IGNORE
    {
      LOG.error("Cannot propagate message to Steuerung", e);
    }

    this.messagePusher.pushMessage(message);
  }
}
