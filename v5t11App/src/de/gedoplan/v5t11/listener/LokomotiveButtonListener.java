package de.gedoplan.v5t11.listener;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import de.gedoplan.v5t11.LokomotiveBrowserActivity;

public class LokomotiveButtonListener implements OnClickListener
{

  private Context context;

  public LokomotiveButtonListener(Context context)
  {
    this.context = context;
  }

  @Override
  public void onClick(View v)
  {
    Intent kursact = new Intent(context, LokomotiveBrowserActivity.class);
    context.startActivity(kursact);
  }

}
