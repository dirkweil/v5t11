package de.gedoplan.v5t11.status.webui;

import de.gedoplan.v5t11.status.entity.fahrzeug.FahrzeugId;
import de.gedoplan.v5t11.util.domain.attribute.SystemTyp;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter(forClass = FahrzeugId.class, value = "??")
public class FahrzeugIdConverter implements Converter<FahrzeugId> {

  @Override
  public FahrzeugId getAsObject(FacesContext context, UIComponent component, String value) {
    if (value == null) {
      return null;
    }

    String[] parts = value.split("@");
    int adresse = Integer.parseInt(parts[0]);
    SystemTyp systemTyp = SystemTyp.valueOf(parts[1]);
    return new FahrzeugId(systemTyp, adresse);
  }

  @Override
  public String getAsString(FacesContext context, UIComponent component, FahrzeugId value) {
    if (value == null) {
      return null;
    }

    return value.getAdresse() + "@" + value.getSystemTyp();
  }

}
