package de.gedoplan.v5t11;

import de.gedoplan.v5t11.adapter.LokomotiveAdapter;
import de.gedoplan.v5t11.listener.LokomotiveListeListener;
import de.gedoplan.v5t11.webserviceclient.RestWebserviceClient;

import android.app.ListActivity;
import android.os.Bundle;

public class LokomotiveBrowserActivity extends ListActivity
{

  private LokomotiveAdapter lokadapter;

  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    // TODO Auto-generated method stub
    super.onCreate(savedInstanceState);

    // Hier initial den Webservice ansprechen
    RestWebserviceClient client = new RestWebserviceClient();
    client.updateAllLoks();

    this.lokadapter = new LokomotiveAdapter(this);

    setListAdapter(this.lokadapter);

    getListView().setOnItemClickListener(new LokomotiveListeListener(this, this.lokadapter));

  }

}
