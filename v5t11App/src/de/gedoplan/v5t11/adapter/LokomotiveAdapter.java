package de.gedoplan.v5t11.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import de.gedoplan.v5t11.R;
import de.gedoplan.v5t11.R.id;
import de.gedoplan.v5t11.R.layout;
import de.gedoplan.v5t11.datenobjekte.Lokomotive;
import de.gedoplan.v5t11.datenobjekte.LokomotiveDAO;

public class LokomotiveAdapter extends BaseAdapter
{

  private final LayoutInflater inflator;
  private List<Lokomotive>     list;

  public LokomotiveAdapter(Context context)
  {
    inflator = LayoutInflater.from(context);
    // LokomotiveDAO.fuelleLokListeStatisch();
    list = LokomotiveDAO.getList();
  }

  @Override
  public int getCount()
  {
    return list.size();
  }

  @Override
  public Object getItem(int position)
  {
    return list.get(position);
  }

  @Override
  public long getItemId(int position)
  {
    return position;
  }

  public View getView(int position, View convertView, ViewGroup parent)
  {
    ViewHolder holder;

    // falls n�tig, convertView bauen
    if (convertView == null)
    {
      // Layoutdatei entfalten
      convertView = inflator.inflate(R.layout.icon_text_text, parent, false);

      // Holder erzeugen
      holder = new ViewHolder();
      holder.name = (TextView) convertView.findViewById(R.id.text1);
      //holder.beschreibung = (TextView) convertView.findViewById(R.id.text2);
      holder.icon = (ImageView) convertView.findViewById(R.id.icon);

      convertView.setTag(holder);
    }
    else
    {
      // Holder bereits vorhanden
      holder = (ViewHolder) convertView.getTag();
    }

    Lokomotive kurs = (Lokomotive) getItem(position);
    holder.name.setText(kurs.getBezeichnung());
    holder.icon.setImageResource(kurs.getIcon());
    //holder.beschreibung.setText(kurs.getKurzbeschreibung());

    return convertView;
  }

  static class ViewHolder
  {
    TextView  name;
    ImageView icon;
  }

}
