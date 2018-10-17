package de.gedoplan.v5t11.leitstand.entity.fahrweg;

public interface StatusUpdateable<T> {
  void copyStatus(T other);
}
