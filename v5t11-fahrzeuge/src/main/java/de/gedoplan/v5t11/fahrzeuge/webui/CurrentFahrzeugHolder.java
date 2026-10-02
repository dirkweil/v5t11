package de.gedoplan.v5t11.fahrzeuge.webui;

import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.Fahrzeug;

import java.io.Serializable;

import jakarta.enterprise.context.SessionScoped;

import lombok.Getter;
import lombok.Setter;

/**
 * Hält das aktuell in der Fahrzeug-Liste ausgewählte Fahrzeug session-weit, damit die
 * Bearbeitungs-Views (FahrzeugControlView, FahrzeugFunctionView, FahrzeugTraktionView,
 * FahrzeugProgramView, FahrzeugMessungView, GleisMessungView) darauf zugreifen können, ohne es
 * selbst durch die Navigation zu reichen.
 */
@SessionScoped
public class CurrentFahrzeugHolder implements Serializable {

  @Getter
  @Setter
  private Fahrzeug currentFahrzeug;

}
