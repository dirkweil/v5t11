package de.gedoplan.v5t11.status.entity.autoskript;

import de.gedoplan.v5t11.status.entity.Steuerung;
import de.gedoplan.v5t11.util.domain.attribute.SchalterStellung;
import de.gedoplan.v5t11.util.domain.attribute.SignalStellung;
import de.gedoplan.v5t11.util.domain.attribute.WeichenStellung;

import java.rmi.UnmarshalException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlValue;

import org.jboss.logging.Logger;

import lombok.Getter;

@XmlAccessorType(XmlAccessType.NONE)
public class AutoSkript {
  @XmlAttribute
  @Getter
  private String bereich;

  @XmlAttribute
  @Getter
  private String beschreibung;

  @XmlElements({
      @XmlElement(name = "Gleis", type = SkriptGleis.class),
      @XmlElement(name = "Schalter", type = SkriptSchalter.class),
      @XmlElement(name = "Signal", type = SkriptSignal.class),
      @XmlElement(name = "Weiche", type = SkriptWeiche.class),
  })
  @Getter
  private List<SkriptObjekt> objekte = new ArrayList<>();

  @Getter
  private Set<Object> steuerungsObjekte = new HashSet<>();

  @XmlElement(name = "Skript", required = true)
  @Getter
  private Skript skript;

  private static final Logger LOGGER = Logger.getLogger(AutoSkript.class);

  @Override
  public String toString() {
    return "AutoSkript{bereich=" + this.bereich + ", beschreibung=" + this.beschreibung + "}";
  }

  public void linkSteuerungsObjekte(Steuerung steuerung) {
    boolean hasSchalter = false;
    boolean hasSignal = false;
    boolean hasWeiche = false;
    for (SkriptObjekt skriptObjekt : this.objekte) {
      skriptObjekt.linkSteuerungsObjekt(steuerung);
      this.steuerungsObjekte.add(skriptObjekt.getSteuerungsObjekt());
      this.skript.getEngine().put(skriptObjekt.getVar(), skriptObjekt.getSteuerungsObjekt());
      hasSchalter |= skriptObjekt.uses(SchalterStellung.class);
      hasSignal |= skriptObjekt.uses(SignalStellung.class);
      hasWeiche |= skriptObjekt.uses(WeichenStellung.class);
    }

    this.skript.getEngine().put("log", LOGGER);
    if (hasSchalter) {
      for (SchalterStellung stellung : SchalterStellung.values()) {
        this.skript.getEngine().put(stellung.name(), stellung);
      }
    }
    if (hasSignal) {
      for (SignalStellung stellung : SignalStellung.values()) {
        this.skript.getEngine().put(stellung.name(), stellung);
      }
    }
    if (hasWeiche) {
      for (WeichenStellung stellung : WeichenStellung.values()) {
        this.skript.getEngine().put(stellung.name(), stellung);
      }
    }
  }

  public void execute() {
    try {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debugf("Skript-Start: %s (vars: %s)",
            this.beschreibung,
            this.skript.getEngine().getBindings(ScriptContext.ENGINE_SCOPE)
                .keySet()
                .stream()
                .collect(Collectors.joining(",")));
      }

      this.skript.getEngine().eval(this.skript.getCode());

      LOGGER.debugf("Skript-Ende: %s", this.beschreibung);
    } catch (ScriptException e) {
      LOGGER.error("Fehler im Skript: " + this.beschreibung, e);
    }
  }

  @XmlAccessorType(XmlAccessType.NONE)
  public static class Skript {
    @XmlAttribute
    @Getter
    private String sprache;

    @XmlValue
    @Getter
    private String code;

    private static final ScriptEngineManager ENGINE_MANAGER = new ScriptEngineManager();

    @Getter
    private ScriptEngine engine;

    /*
     * Nachbearbeitung nach JAXB-Unmarshal.
     */
    protected void afterUnmarshal(Unmarshaller unmarshaller, Object parent) throws UnmarshalException {
      this.engine = ENGINE_MANAGER.getEngineByName(this.sprache);
      if (this.engine == null) {
        String names = ENGINE_MANAGER.getEngineFactories().stream().flatMap(sef -> sef.getNames().stream()).collect(Collectors.joining(","));
        String message = "Unbekannte Skript-Sprache: " + this.sprache + " (Verfügbar: " + names + ")";
        LOGGER.error(message);
        throw new UnmarshalException(message);
      }

      // Dummy-Code ausführen, um Engine zu initialisieren
      try {
        this.engine.eval("");
      } catch (ScriptException e) {
        LOGGER.errorf("Kann %s-Engine nicht starten: %s", this.sprache, e.toString());
      }
    }

  }
}
