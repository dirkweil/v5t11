package de.gedoplan.v5t11.listener;

import de.gedoplan.v5t11.datenobjekte.AktiveLok;
import de.gedoplan.v5t11.verticalslidebar.VerticalSeekBar;
import de.gedoplan.v5t11.webserviceclient.RestWebserviceClient;

/**
 * A callback that notifies clients when the progress level has been changed. This includes changes that were initiated by the
 * user through a touch gesture or arrow key/trackball as well as changes that were initiated programmatically.
 */
public class OnSeekBarChangeListener
{

  private RestWebserviceClient client;

  public OnSeekBarChangeListener()
  {

    client = new RestWebserviceClient();
  }

  public void onStopTrackingTouch(VerticalSeekBar seekBar)
  {
  }

  public void onStartTrackingTouch(VerticalSeekBar seekBar)
  {

  }

  public void onProgressChanged(VerticalSeekBar seekBar, int progress, boolean fromUser)
  {

    client.setAktiveLokTempo(progress);

    System.out.println("Position: " + progress + " Tempo: " + AktiveLok.getInstance().getTempo());
  }

}
