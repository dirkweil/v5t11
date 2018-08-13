/*
 * Created on 09.04.2006 by dw
 */
package de.gedoplan.v5t11.status.util;

import java.text.Collator;
import java.text.NumberFormat;
import java.text.ParseException;

public class NameComparator
{
  private static NumberFormat formatter = NumberFormat.getIntegerInstance();
  private static Collator     collator  = Collator.getInstance();

  public static int compare(String name1, String name2)
  {
    if (name1 == null)
    {
      return name2 == null ? 0 : 1;
    }
    if (name2 == null)
    {
      return -1;
    }

    try
    {
      long diff = formatter.parse(name1).longValue() - formatter.parse(name2).longValue();
      if (diff < 0)
      {
        return -1;
      }
      if (diff > 0)
      {
        return 1;
      }
    }
    catch (ParseException e)
    {
      // ignore
    }

    return collator.compare(name1, name2);
  }
}
