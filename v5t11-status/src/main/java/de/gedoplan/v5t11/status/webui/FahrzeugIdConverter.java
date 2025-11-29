package de.gedoplan.v5t11.status.webui;

import de.gedoplan.v5t11.util.domain.attribute.DecoderAdr;
import de.gedoplan.v5t11.util.domain.attribute.SystemTyp;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;

@FacesConverter(forClass = DecoderAdr.class, value = "??")
public class FahrzeugIdConverter implements Converter<DecoderAdr> {

  @Override
  public DecoderAdr getAsObject(FacesContext context, UIComponent component, String value) {
    if (value == null) {
      return null;
    }

    String[] parts = value.split("@");
    int adresse = Integer.parseInt(parts[0]);
    SystemTyp systemTyp = SystemTyp.valueOf(parts[1]);
    return new DecoderAdr(systemTyp, adresse);
  }

  @Override
  public String getAsString(FacesContext context, UIComponent component, DecoderAdr value) {
    if (value == null) {
      return null;
    }

    return value.getAdresse() + "@" + value.getSystemTyp();
  }

}
