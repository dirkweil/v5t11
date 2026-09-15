package de.gedoplan.v5t11.status.service;

import de.gedoplan.v5t11.status.entity.baustein.Baustein;

import java.io.Serializable;

import jakarta.enterprise.context.SessionScoped;
import jakarta.enterprise.inject.Produces;

import lombok.Getter;
import lombok.Setter;

/**
 * Hält den aktuell zur Programmierung ausgewählten {@link Baustein} und stellt ihn via {@link Current} für CDI
 * bereit. Ausgelagert aus {@code BausteinProgrammierungPresenter} (JSF), damit sowohl das alte JSF-Menü als auch die
 * neue {@code BausteinProgrammierungView} (Vaadin) dieselbe, einzige CDI-Produktionsstelle für {@code @Current
 * Baustein} nutzen – zwei konkurrierende {@code @Produces}-Stellen für denselben Typ/Qualifier wären eine
 * CDI-Mehrdeutigkeit, die von den elf {@code XxxRuntimeService}-Konstruktoren (Parameter {@code @Current Baustein})
 * benötigte Injektion würde sonst fehlschlagen, sobald beide Präsentationsschichten koexistieren.
 */
@SessionScoped
public class CurrentBausteinHolder implements Serializable {
  @Getter
  @Setter
  @Produces
  @Current
  private Baustein currentBaustein;
}
