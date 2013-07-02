package de.gedoplan.v5t11.listener;

import de.gedoplan.v5t11.LokomotiveBrowserActivity;
import de.gedoplan.v5t11.VerticalSlidebarExampleActivity;
import de.gedoplan.v5t11.adapter.LokomotiveAdapter;
import de.gedoplan.v5t11.datenobjekte.Lokomotive;
import de.gedoplan.v5t11.webserviceclient.RestWebserviceClient;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class LokomotiveListeListener implements OnItemClickListener
{

  private LokomotiveAdapter         adapter;
  private LokomotiveBrowserActivity ba;
  private RestWebserviceClient      client;

  public LokomotiveListeListener(LokomotiveBrowserActivity ba, LokomotiveAdapter adapter)
  {
    this.adapter = adapter;
    this.ba = ba;
    this.client = new RestWebserviceClient();
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id)
  {
    Lokomotive lok = (Lokomotive) this.adapter.getItem(position);
    System.out.println("Hole Lokdaten für Lok: " + lok.getAdresse());
    this.client.getAktiveLok(new Integer(lok.getAdresse()).toString());
    // eine Webseite anzeigen
    Intent viewIntent = new Intent(this.ba, VerticalSlidebarExampleActivity.class);

    this.ba.startActivity(viewIntent);
  }

}
