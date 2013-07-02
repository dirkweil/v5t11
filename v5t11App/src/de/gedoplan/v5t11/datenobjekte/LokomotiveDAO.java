package de.gedoplan.v5t11.datenobjekte;

import de.gedoplan.v5t11.R;
import de.gedoplan.v5t11.serverklasse.ClientLok;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LokomotiveDAO
{

  private static List<Lokomotive> lokList = new ArrayList<Lokomotive>();

  public static void fuelleLokListeViaWebservice(Iterator<ClientLok> loks)
  {
    lokList.clear();
    
    Map<String, Integer> lokIconMap = getLokIcons();

    while (loks.hasNext())
    {
      ClientLok lok = loks.next();
      lokList.add(new Lokomotive(lok.getAdr(), lokIconMap.get(lok.getName()), lok.getName()));
    }
    
    /*
    Set<String> set = lokIconMap.keySet();
    Iterator<String> it = set.iterator();

    while (it.hasNext())
    {
      String value = it.next();
      if (!lokList.contains(value))
      {
        lokList.add(new Lokomotive(999, lokIconMap.get(value), value, "nicht vorhanden"));
      }
    }
    */
  }

  public static List<Lokomotive> getList()
  {
    return lokList;
  }

  private static Map<String, Integer> getLokIcons()
  {
    Map<String, Integer> map = new HashMap<String, Integer>();

    map.put("221 137-3", R.drawable.weinrot221);
    map.put("212 216-6", R.drawable.weinrot212);
    map.put("VT 98.9731", R.drawable.vt98);
    map.put("103 118-6", R.drawable.beigerot103);
    map.put("120 002-1", R.drawable.beigerot120);
    map.put("110 222-7", R.drawable.blau110);
    map.put("216 xxx-x", R.drawable.blaubeige216);
    map.put("151 032-0", R.drawable.gruen151);
    map.put("332 262-5", R.drawable.koefiiiblaubeige);
    map.put("323 673-4", R.drawable.koefiirot);
    map.put("14283", R.drawable.krokodilgruen);
    map.put("111 205-1", R.drawable.ozeanblaubeige111);
    map.put("614 083-4", R.drawable.ozeanblaubeige614);
    map.put("VT 08 509", R.drawable.vt08);
    map.put("VT 11.5019", R.drawable.vt115);

    return map;

  }
}
