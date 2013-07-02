package de.gedoplan.v5t11.betriebssteuerung.background;

import de.gedoplan.v5t11.betriebssteuerung.messaging.LokControllerMessage;
import de.gedoplan.v5t11.betriebssteuerung.messaging.MessagePusher;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.Steuerung;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.LokController;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.lok.Lok;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class LokControllerSteuerung
{
  @Inject
  Steuerung     steuerung;

  @Inject
  MessagePusher messagePusher;

  public void assignLokController(String lokControllerId, int lokAdresse)
  {
    LokController lokController = this.steuerung.getLokController(lokControllerId);
    if (lokController != null)
    {
      Lok lok = lokAdresse > 0 ? this.steuerung.getLok(lokAdresse) : null;

      if (lok != null)
      {
        for (LokController lokController2 : this.steuerung.getLokController())
        {
          if (!lokController2.equals(lokController) && lok.equals(lokController2.getLok()))
          {
            lokController2.setLok(null);
            if (this.messagePusher != null)
            {
              this.messagePusher.pushMessage(new LokControllerMessage(lokController2.getId(), -1));
            }
          }
        }
      }

      lokController.setLok(lok);
      if (this.messagePusher != null)
      {
        this.messagePusher.pushMessage(new LokControllerMessage(lokController.getId(), lok != null ? lok.getAdresse() : -1));
      }
    }
  }

}
