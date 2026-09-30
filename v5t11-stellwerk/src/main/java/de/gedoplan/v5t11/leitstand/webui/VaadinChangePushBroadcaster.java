package de.gedoplan.v5t11.leitstand.webui;

import de.gedoplan.v5t11.util.cdi.Changed;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.TransactionPhase;

/**
 * Verteilt dieselben {@code @Changed}-CDI-Events (gefeuert über {@link de.gedoplan.v5t11.util.cdi.EventFirer},
 * bisher konsumiert von {@link PushService} für den rohen JSF-Websocket-Push) an Vaadin-Views, damit diese gezielt
 * (nicht per Full-Page-Reload) über {@code UI.access(...)} aktualisieren können. Pendant zu
 * {@code v5t11-status/.../webui/VaadinChangePushBroadcaster.java} (Phase 1a) bzw.
 * {@code v5t11-fahrzeuge/.../webui/VaadinChangePushBroadcaster.java} (Phase 2) – eigene Klasse, da jeder Service ein
 * eigenes Deployment/CDI-Environment ist.
 * <p>
 * Bewusst {@code @Observes(during = AFTER_SUCCESS)} statt des von {@code PushService} verwendeten
 * {@code AFTER_COMPLETION} (feuert auch nach fehlgeschlagenem Rollback) oder {@code @ObservesAsync}: {@code
 * EventFirer.fire(...)} feuert das Event noch innerhalb der Transaktion des Aufrufers, bevor sie committet ist. Ein
 * {@code AFTER_SUCCESS}-Observer wird garantiert erst nach erfolgreichem Commit benachrichtigt (gleiche Konvention
 * wie in Phase 1a/2 etabliert).
 */
@ApplicationScoped
public class VaadinChangePushBroadcaster {

  private final List<Consumer<Object>> listeners = new CopyOnWriteArrayList<>();

  void onChanged(@Observes(during = TransactionPhase.AFTER_SUCCESS) @Changed Object changed) {
    this.listeners.forEach(listener -> listener.accept(changed));
  }

  public void addListener(Consumer<Object> listener) {
    this.listeners.add(listener);
  }

  public void removeListener(Consumer<Object> listener) {
    this.listeners.remove(listener);
  }
}
