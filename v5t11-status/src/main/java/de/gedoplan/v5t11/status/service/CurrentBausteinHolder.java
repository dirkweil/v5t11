package de.gedoplan.v5t11.status.service;

import de.gedoplan.v5t11.status.entity.baustein.Baustein;

import java.io.Serializable;

import jakarta.enterprise.context.SessionScoped;
import jakarta.enterprise.inject.Produces;

import lombok.Getter;
import lombok.Setter;

/**
 * Hält den aktuell zur Programmierung ausgewählten {@link Baustein} und stellt ihn via {@link Current} für CDI
 * bereit. Alleinige CDI-Produktionsstelle für {@code @Current Baustein}, genutzt von
 * {@code BausteinProgrammierungView} (Vaadin) sowie den elf {@code XxxRuntimeService}-Konstruktoren (Parameter
 * {@code @Current Baustein}).
 */
@SessionScoped
public class CurrentBausteinHolder implements Serializable {
  @Getter
  @Setter
  @Produces
  @Current
  private Baustein currentBaustein;
}
