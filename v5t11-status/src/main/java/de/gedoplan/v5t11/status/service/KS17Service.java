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
public class KS17Service {
  @Inject
  Log log;

  @Inject
  Steuerung steuerung;

  private Gleisabschnitt gleis109;

  private Gleisabschnitt gleis17a;

  private Gleisabschnitt gleis17b;

  private Gleisabschnitt gleis17c;

  private Gleisabschnitt gleis17d;

  private Set<Gleisabschnitt> gleisabschnitte = new HashSet<>();

  private Schalter kS17;

  private ScriptEngine scriptEngine;

  @PostConstruct
  void postConstruct() {
    this.gleis109 = this.steuerung.getGleisabschnitt("SBf", "109");
    this.gleisabschnitte.add(this.gleis109);
    this.gleis17a = this.steuerung.getGleisabschnitt("SBf", "17a");
    this.gleisabschnitte.add(this.gleis17a);
    this.gleis17b = this.steuerung.getGleisabschnitt("SBf", "17b");
    this.gleisabschnitte.add(this.gleis17b);
    this.gleis17c = this.steuerung.getGleisabschnitt("SBf", "17c");
    this.gleisabschnitte.add(this.gleis17c);
    this.gleis17d = this.steuerung.getGleisabschnitt("SBf", "17d");
    this.gleisabschnitte.add(this.gleis17d);

    this.kS17 = this.steuerung.getSchalter("SBf", "KS17");

    ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
    this.scriptEngine = scriptEngineManager.getEngineByName("groovy");
  }

  void gleisChanged(@Observes Gleisabschnitt gleisabschnitt) {
    if (this.gleisabschnitte.contains(gleisabschnitt)) {

      this.scriptEngine.put("gleis109", this.gleis109);
      this.scriptEngine.put("gleis17a", this.gleis17a);
      this.scriptEngine.put("gleis17b", this.gleis17b);
      this.scriptEngine.put("gleis17c", this.gleis17c);
      this.scriptEngine.put("gleis17d", this.gleis17d);
      this.scriptEngine.put("kS17", this.kS17);
      this.scriptEngine.put("log", this.log);

      try {
        this.scriptEngine.eval(""
            + "import static de.gedoplan.v5t11.util.domain.attribute.SchalterStellung.EIN;\n"
            + "import static de.gedoplan.v5t11.util.domain.attribute.SchalterStellung.AUS;\n"
            + "if (gleis17c.isBesetzt() || gleis17d.isBesetzt()) {\n"
            + "  log.debug(\"Polung KS17: links\");\n"
            + "  kS17.setStellung(EIN);\n"
            + "} else if (gleis109.isBesetzt() || gleis17a.isBesetzt()) {\n"
            + "  log.debug(\"Polung KS17: rechts\");\n"
            + "  kS17.setStellung(AUS);\n"
            + "}");
      } catch (ScriptException e) {
        this.log.error("Skript-Error", e);
      }
    }
  }
}
