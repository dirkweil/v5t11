package de.gedoplan.v5t11.listener;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import de.gedoplan.v5t11.R;
import de.gedoplan.v5t11.datenobjekte.AktiveLok;
import de.gedoplan.v5t11.webserviceclient.RestWebserviceClient;

public class LichtButtonListener implements OnClickListener
{

  private Button               button;
  private RestWebserviceClient client;

  public LichtButtonListener(Button button)
  {

    this.button = button;
    this.client = new RestWebserviceClient();
  }

  @Override
  public void onClick(View v)
  {

    client.setAktiveLokLicht();

    if (AktiveLok.getInstance().isLicht())
    {
      button.setText(R.string.button_licht_ausschalten);
      System.out.println("LICHT AUS");
    }
    else
    {
      button.setText(R.string.button_licht_anschalten);
      System.out.println("LICHT AN");
    }
  }

}
