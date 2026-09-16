package de.gedoplan.v5t11.fahrzeuge.webui;

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
 * {@code v5t11-status/.../webui/VaadinChangePushBroadcaster.java} aus Phase 1a – eigene Klasse, da jeder Service ein
 * eigenes Deployment/CDI-Environment ist.
 * <p>
 * Bewusst {@code @Observes(during = AFTER_SUCCESS)} statt {@code @ObservesAsync}: {@code EventFirer.fire(...)} feuert
 * das Event noch innerhalb der Transaktion des Aufrufers (z. B. {@code StatusUpdater}), bevor sie committet ist. Ein
 * transaktionaler Observer wird garantiert erst nach erfolgreichem Commit benachrichtigt, ein
 * {@code @ObservesAsync}-Observer dagegen potenziell schon vorher (Race Condition bei nachfolgendem Neuladen aus der
 * DB).
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
