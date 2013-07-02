package de.gedoplan.v5t11.listener;

import de.gedoplan.v5t11.R;
import de.gedoplan.v5t11.datenobjekte.Anlagenstatus;
import de.gedoplan.v5t11.webserviceclient.RestWebserviceClient;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class NothaltButtonListener implements OnClickListener
{

  private Button               button;
  private RestWebserviceClient client;

  public NothaltButtonListener(Button button)
  {

    this.button = button;
    this.client = new RestWebserviceClient();
  }

  @Override
  public void onClick(View v)
  {

    this.client.setAnlagenstatus();

    if (Anlagenstatus.getInstance().isEingeschaltet())
    {
      this.button.setText(R.string.button_nothalt);

      System.out.println("STOP");
    }
    else
    {
      this.button.setText(R.string.button_anlagen_starten);

      System.out.println("START");
    }
  }

}
