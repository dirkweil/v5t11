package de.gedoplan.v5t11.stellwerk.util;

import de.gedoplan.baselibs.utils.util.ResourceUtil;

import java.awt.Image;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class IconUtil {
  private static Map<String, Icon> iconMap = new HashMap<String, Icon>();

  private static final Log LOG = LogFactory.getLog(IconUtil.class);

  public static Icon getIcon(String key) {
    return getIcon(key, 35, 35);
  }

  public static Icon getIcon(String key, int width, int height) {
    Icon icon = iconMap.get(key);
    if (icon == null) {
      try {
        URL iconUrl = ResourceUtil.getResource(key);
        if (iconUrl == null) {
          LOG.error("Kann Icon " + key + " nicht finden");
        } else {
          Image image = ImageIO.read(iconUrl);
          image = image.getScaledInstance(width, height, 0);
          icon = new ImageIcon(image);
          iconMap.put(key, icon);
        }
      } catch (Exception ex) {
        LOG.error("Kann Icon " + key + " nicht laden", ex);
      }
    }
    return icon;
  }

}
