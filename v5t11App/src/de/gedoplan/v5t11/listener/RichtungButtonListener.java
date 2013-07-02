package de.gedoplan.v5t11.listener;

import de.gedoplan.v5t11.R;
import de.gedoplan.v5t11.datenobjekte.AktiveLok;
import de.gedoplan.v5t11.webserviceclient.RestWebserviceClient;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class RichtungButtonListener implements OnClickListener
{

  private Button               button;
  private RestWebserviceClient client;

  public RichtungButtonListener(Button button)
  {

    this.button = button;
    client = new RestWebserviceClient();
  }

  @Override
  public void onClick(View v)
  {

    client.setAktiveLokRichtung();

    if (AktiveLok.getInstance().isRueckwaerts())
    {
      button.setText(R.string.button_richtung_aendern_vorwaerts);
    }
    else
    {
      button.setText(R.string.button_richtung_aendern_rueckwaerts);
    }
  }

}
