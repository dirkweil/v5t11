package de.gedoplan.v5t11.status.service;

import de.gedoplan.v5t11.selectrix.SelectrixMessage;
import de.gedoplan.v5t11.selectrix.SelectrixMessageListener;
import de.gedoplan.v5t11.status.entity.Steuerung;

import javax.ejb.MessageDriven;
import javax.inject.Inject;

import org.apache.commons.logging.Log;

@MessageDriven(messageListenerInterface = SelectrixMessageListener.class)
public class PropagateSelectrixMessageMdb implements SelectrixMessageListener {
  @Inject
  Steuerung steuerung;

  @Inject
  Log log;

  @Override
  public void onMessage(SelectrixMessage message) {
    try {
      this.steuerung.onMessage(message);
    } catch (Exception e) // CHECKSTYLE:IGNORE
    {
      this.log.error("Cannot propagate message to Steuerung", e);
    }
  }
}
