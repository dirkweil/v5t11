package de.gedoplan.v5t11.status.entity;

import de.gedoplan.v5t11.util.jsonb.JsonbInclude;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Getter;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@AllArgsConstructor
public class Kanal {
  @Getter(onMethod_ = @JsonbInclude)
  private int adresse;

  @Getter(onMethod_ = @JsonbInclude)
  private int wert;
}
