package de.gedoplan.v5t11;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import de.gedoplan.v5t11.datenobjekte.AktiveLok;
import de.gedoplan.v5t11.datenobjekte.Anlagenstatus;
import de.gedoplan.v5t11.listener.LichtButtonListener;
import de.gedoplan.v5t11.listener.NothaltButtonListener;
import de.gedoplan.v5t11.listener.OnSeekBarChangeListener;
import de.gedoplan.v5t11.listener.RichtungButtonListener;
import de.gedoplan.v5t11.verticalslidebar.VerticalSeekBar;

public class VerticalSlidebarExampleActivity extends Activity
{

  private Button    richtung;
  private Button    licht;
  private Button    nothalt;

  private ImageView image;

  private TextView  lokInfo1;
  private TextView  lokInfo2;

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.slidebar);

    VerticalSeekBar vSeekBar2 = (VerticalSeekBar) findViewById(R.id.SeekBar02);
    vSeekBar2.setMax(32);
    vSeekBar2.setProgress(AktiveLok.getInstance().getTempo());
    vSeekBar2.setOnSeekBarChangeListener(new OnSeekBarChangeListener());

    lokInfo1 = (TextView) findViewById(R.id.text1Slidebar);
    lokInfo2 = (TextView) findViewById(R.id.text2Slidebar);
    image = (ImageView) findViewById(R.id.iconSlidebar);

    lokInfo1.setText(AktiveLok.getInstance().getBezeichnung());
    image.setImageResource(AktiveLok.getInstance().getIcon());

    richtung = (Button) findViewById(R.id.richtung);

    richtung.setOnClickListener(new RichtungButtonListener(richtung));

    licht = (Button) findViewById(R.id.licht);

    if (AktiveLok.getInstance().isLicht())
    {
      licht.setText(R.string.button_licht_ausschalten);
    }
    else
    {
      licht.setText(R.string.button_licht_anschalten);
    }

    licht.setOnClickListener(new LichtButtonListener(licht));

    nothalt = (Button) findViewById(R.id.nothalt);
    if (Anlagenstatus.getInstance().isEingeschaltet())
    {
      nothalt.setText(R.string.button_nothalt);
    }
    else
    {
      nothalt.setText(R.string.button_anlagen_starten);
    }
    nothalt.setOnClickListener(new NothaltButtonListener(nothalt));
  }
}
