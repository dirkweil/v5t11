package de.gedoplan.v5t11.fahrzeuge.entity.fahrweg;

public interface StatusUpdateable<T> {
  void copyStatus(T other);
}
