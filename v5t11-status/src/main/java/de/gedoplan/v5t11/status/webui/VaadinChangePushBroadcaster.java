package de.gedoplan.v5t11.status.webui;

import de.gedoplan.v5t11.util.cdi.Changed;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.ObservesAsync;

/**
 * Verteilt dieselben {@code @Changed}-CDI-Events (gefeuert über {@link de.gedoplan.v5t11.util.cdi.EventFirer}, bisher
 * konsumiert von {@link PushService} für den rohen JSF-Websocket-Push) an Vaadin-Views, damit diese gezielt (nicht
 * per Full-Page-Reload) über {@code UI.access(...)} aktualisieren können.
 * <p>
 * Registrierung/Deregistrierung folgt demselben Muster wie
 * {@code NavigationPresenter#addMenuChangeListener}/{@code VaadinNavigationMenu}.
 */
@ApplicationScoped
public class VaadinChangePushBroadcaster {

  private final List<Consumer<Object>> listeners = new CopyOnWriteArrayList<>();

  void onChanged(@ObservesAsync @Changed Object changed) {
    this.listeners.forEach(listener -> listener.accept(changed));
  }

  public void addListener(Consumer<Object> listener) {
    this.listeners.add(listener);
  }

  public void removeListener(Consumer<Object> listener) {
    this.listeners.remove(listener);
  }
}
