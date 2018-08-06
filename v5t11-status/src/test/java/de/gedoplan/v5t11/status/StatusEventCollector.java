package de.gedoplan.v5t11.status;

import de.gedoplan.v5t11.status.entity.fahrweg.Fahrwegelement;

import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedDeque;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import lombok.Getter;

@ApplicationScoped
public class StatusEventCollector {

  @Getter
  private Collection<Fahrwegelement> events = new ConcurrentLinkedDeque<Fahrwegelement>();

  public void clear() {
    this.events.clear();
  }

  void fahrwegelementChanged(@Observes Fahrwegelement fahrwegelement) {
    this.events.add(fahrwegelement);
  }

}
