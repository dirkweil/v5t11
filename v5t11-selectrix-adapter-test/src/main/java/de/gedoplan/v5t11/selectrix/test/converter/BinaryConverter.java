package de.gedoplan.v5t11.selectrix.test.converter;

import javax.enterprise.context.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 * Converter für {@link Firma}.
 * 
 * @author dw
 */
@FacesConverter("de.gedoplan.BinaryConverter")
@RequestScoped
public class BinaryConverter implements Converter
{

  @Override
  public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String string)
  {
    if (string == null)
    {
      return null;
    }

    int value = 0;
    for (int i = 0; i < string.length(); ++i)
    {
      switch (string.charAt(i))
      {
      case '0':
        value <<= 1;
        break;

      case ' ':
        break;

      default:
        value = (value << 1) + 1;
        break;
      }
    }

    return value;
  }

  @Override
  public String getAsString(FacesContext arg0, UIComponent arg1, Object obj)
  {
    if (obj == null || !(obj instanceof Integer))
    {
      return null;
    }

    int value = (Integer) obj;
    StringBuilder buf = new StringBuilder();
    for (int mask = 0b10000000; mask > 0; mask = mask >> 1)
    {
      buf.append((value & mask) != 0 ? '1' : '0');
      if (mask == 0b10000)
      {
        buf.append(' ');
      }
    }
    return buf.toString();
  }
}
