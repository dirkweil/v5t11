package de.gedoplan.v5t11.leitstand.webui;

import de.gedoplan.v5t11.leitstand.entity.fahrstrasse.Fahrstrasse;
import de.gedoplan.v5t11.leitstand.entity.fahrweg.Gleis;

import org.jboss.logging.Logger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@ApplicationScoped
public class StellwerkVorschlagService {

  @Inject
  Logger logger;

  @Inject
  PushService pushService;

  private Map<String, Fahrstrasse> vorschlag4Session = new ConcurrentHashMap<>();
  private Map<String, Collection<Fahrstrasse>> weitere4Session = new ConcurrentHashMap<>();

  public void clear(String sessionId) {
    Fahrstrasse vorschlag = this.vorschlag4Session.remove(sessionId);
    Collection<Fahrstrasse> weitere = this.weitere4Session.remove(sessionId);

    pushUpdates(vorschlag, weitere);

    this.logger.debugf("Vorschläge für Session %s gelöscht", sessionId);
  }

  public void set(String sessionId, Fahrstrasse vorschlag, Collection<Fahrstrasse> weitere) {
    this.vorschlag4Session.put(sessionId, vorschlag);

    weitere = weitere.stream().filter(fs -> !fs.equals(vorschlag)).toList();
    this.weitere4Session.put(sessionId, weitere);

    pushUpdates(vorschlag, weitere);

    this.logger.debugf("Vorschlag für Session %s: %s", sessionId, vorschlag.getShortName());
    this.logger.debugf("Weitere für Session %s: %s", sessionId, weitere.stream().map(Fahrstrasse::getShortName).collect(Collectors.joining(", ")));
  }

  private void pushUpdates(Fahrstrasse vorschlag, Collection<Fahrstrasse> weitere) {
    Set<Fahrstrasse> fahrstrassen = new HashSet<>();
    if (vorschlag != null) {
      fahrstrassen.add(vorschlag);
    }
    if (weitere != null) {
      fahrstrassen.addAll(weitere);
    }
    this.pushService.sendFahrstrassen(fahrstrassen);
  }

  public Fahrstrasse getVorgeschlageneFahrstrasse(Gleis gleis, String sessionId) {
    Fahrstrasse fahrstrasse = this.vorschlag4Session.get(sessionId);
    return fahrstrasse != null && fahrstrasse.getElement(gleis, false) != null ? fahrstrasse : null;
  }

  public Fahrstrasse getAlternativeFahrstrasse(Gleis gleis, String sessionId) {
    Collection<Fahrstrasse> alternativen = this.weitere4Session.get(sessionId);
    if (alternativen != null) {
      return alternativen
        .stream()
        .filter(fs -> fs.getElement(gleis, false) != null)
        .filter(fs -> !fs.equals(this.vorschlag4Session.get(sessionId)))
        .findFirst()
        .orElse(null);
    }

    return null;
  }
}
