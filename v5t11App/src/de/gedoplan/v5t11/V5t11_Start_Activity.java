package de.gedoplan.v5t11;

import de.gedoplan.v5t11.datenobjekte.Anlagenstatus;
import de.gedoplan.v5t11.listener.LokomotiveButtonListener;
import de.gedoplan.v5t11.webserviceclient.RestWebserviceClient;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

public class V5t11_Start_Activity extends Activity
{
  /** Called when the activity is first created. */

  private Button startButton;

  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);

    Anlagenstatus.getInstance().setEingeschaltet(true);
    setContentView(R.layout.main);

    this.startButton = (Button) findViewById(R.id.kurse_button);
    this.startButton.setOnClickListener(new LokomotiveButtonListener(this));

    getPreferences();
  }

  private void getPreferences()
  {
    SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
    RestWebserviceClient.HOST = sharedPrefs.getString("v5t11_host", "192.168.10.53");
    RestWebserviceClient.PORT = sharedPrefs.getString("v5t11_port", "8080");

    TextView serverUrlTextView = (TextView) findViewById(R.id.serverUrl);
    serverUrlTextView.setText(RestWebserviceClient.HOST + ":" + RestWebserviceClient.PORT);
  }

  /**
   * {@inheritDoc}
   * 
   * @see android.app.Activity#onResume()
   */
  @Override
  protected void onResume()
  {
    super.onResume();
    getPreferences();
  }

  @Override
  public boolean onCreateOptionsMenu(android.view.Menu menu)
  {
    super.onCreateOptionsMenu(menu);
    MenuInflater menuInflater = getMenuInflater();
    menuInflater.inflate(R.menu.menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    switch (item.getItemId())
    {
    case R.id.preferences:
      Intent intent = new Intent(V5t11_Start_Activity.this, QuickPrefsActivity.class);
      startActivity(intent);
      break;
    case R.id.exit:
      finish();
      break;
    }
    return false;
  }
}
