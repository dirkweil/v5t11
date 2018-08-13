package de.gedoplan.v5t11.status.entity;

import de.gedoplan.v5t11.status.jsonb.JsonbInclude;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Getter;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@AllArgsConstructor
public class Kanal {
  @Getter(onMethod = @__(@JsonbInclude))
  private int adresse;

  @Getter(onMethod = @__(@JsonbInclude))
  private int wert;
}
