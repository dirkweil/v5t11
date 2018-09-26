package de.gedoplan.v5t11.stellwerk.util;

import java.awt.Image;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

public class IconUtil
{
  private static Map<String, Icon> iconMap = new HashMap<String, Icon>();

  public static Icon getIcon(String key)
  {
    return getIcon(key, 35, 35);
  }

  public static Icon getIcon(String key, int width, int height)
  {
    Icon icon = iconMap.get(key);
    if (icon == null)
    {
      try
      {
        Image image = ImageIO.read(ClassLoader.getSystemResource(key));
        image = image.getScaledInstance(width, height, 0);
        icon = new ImageIcon(image);
        iconMap.put(key, icon);
      }
      catch (Exception ex)
      {
        // ignore
      }
    }
    return icon;
  }

}
