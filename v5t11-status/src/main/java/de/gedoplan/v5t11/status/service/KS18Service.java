package de.gedoplan.v5t11.status.service;

import de.gedoplan.v5t11.status.entity.Steuerung;
import de.gedoplan.v5t11.status.entity.fahrweg.Gleisabschnitt;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Schalter;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.commons.logging.Log;

/**
 * Q&D-Hack: Kehrschleifensteuerung.
 * 
 * TODO Dieser Hack muss in eine saubere, konfigurierbare Lösung überführt werden!
 * 
 * @author dw
 *
 */
@ApplicationScoped
public class KS18Service {
  @Inject
  Log log;

  @Inject
  Steuerung steuerung;

  private Gleisabschnitt gleis18a;

  private Gleisabschnitt gleis18b;

  private Gleisabschnitt gleis18c;

  private Set<Gleisabschnitt> gleisabschnitte = new HashSet<>();

  private Schalter kS18;

  private ScriptEngine scriptEngine;

  @PostConstruct
  void postConstruct() {
    this.gleis18a = this.steuerung.getGleisabschnitt("SBf", "18a");
    this.gleisabschnitte.add(this.gleis18a);
    this.gleis18b = this.steuerung.getGleisabschnitt("SBf", "18b");
    this.gleisabschnitte.add(this.gleis18b);
    this.gleis18c = this.steuerung.getGleisabschnitt("SBf", "18c");
    this.gleisabschnitte.add(this.gleis18c);

    this.kS18 = this.steuerung.getSchalter("SBf", "KS18");

    ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
    this.scriptEngine = scriptEngineManager.getEngineByName("groovy");
  }

  void gleisChanged(@Observes Gleisabschnitt gleisabschnitt) {
    if (this.gleisabschnitte.contains(gleisabschnitt)) {

      this.scriptEngine.put("gleis18a", this.gleis18a);
      this.scriptEngine.put("gleis18b", this.gleis18b);
      this.scriptEngine.put("gleis18c", this.gleis18c);
      this.scriptEngine.put("kS18", this.kS18);
      this.scriptEngine.put("log", this.log);

      try {
        this.scriptEngine.eval(""
            + "import static de.gedoplan.v5t11.util.domain.attribute.SchalterStellung.EIN;\n"
            + "import static de.gedoplan.v5t11.util.domain.attribute.SchalterStellung.AUS;\n"
            + "if (gleis18c.isBesetzt()) {\n"
            + "  log.debug(\"Polung KS18: links\");\n"
            + "  kS18.setStellung(EIN);\n"
            + "} else if (gleis18a.isBesetzt()) {\n"
            + "  log.debug(\"Polung KS18: rechts\");\n"
            + "  kS18.setStellung(AUS);\n"
            + "}");
      } catch (ScriptException e) {
        this.log.error("Skript-Error", e);
      }
    }
  }
}
