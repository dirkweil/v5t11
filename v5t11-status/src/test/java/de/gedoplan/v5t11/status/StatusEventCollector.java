package de.gedoplan.v5t11.status;

import de.gedoplan.v5t11.status.entity.fahrzeug.Fahrzeug;
import de.gedoplan.v5t11.util.cdi.Changed;
import de.gedoplan.v5t11.util.domain.entity.Fahrwegelement;

import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedDeque;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;

import lombok.Getter;

@ApplicationScoped
public class StatusEventCollector {

  @Getter
  private Collection<Object> events = new ConcurrentLinkedDeque<>();

  public void clear() {
    this.events.clear();
  }

  void fahrwegelementChanged(@Observes @Changed Fahrwegelement fahrwegelement) {
    this.events.add(fahrwegelement);
  }

  void lokChanged(@Observes @Changed Fahrzeug fahrzeug) {
    this.events.add(fahrzeug);
  }

}
