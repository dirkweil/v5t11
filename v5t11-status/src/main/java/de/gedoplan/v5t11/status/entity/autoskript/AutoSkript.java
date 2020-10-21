package de.gedoplan.v5t11.status.entity.autoskript;

import de.gedoplan.v5t11.status.entity.Steuerung;
import de.gedoplan.v5t11.util.domain.attribute.SchalterStellung;
import de.gedoplan.v5t11.util.domain.attribute.SignalStellung;
import de.gedoplan.v5t11.util.domain.attribute.WeichenStellung;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
      @XmlElement(name = "Gleisabschnitt", type = SkriptGleisabschnitt.class),
      @XmlElement(name = "Schalter", type = SkriptSchalter.class),
      @XmlElement(name = "Signal", type = SkriptSignal.class),
      @XmlElement(name = "Weiche", type = SkriptWeiche.class),
  })
  @Getter
  private List<SkriptObjekt> objekte = new ArrayList<>();

  @Getter
  private Set<Object> steuerungsObjekte = new HashSet<>();

  @XmlElement(name = "Groovy", required = true)
  @Getter
  private String skriptCode;

  private static final ScriptEngineManager ENGINE_MANAGER = new ScriptEngineManager();

  private ScriptEngine scriptEngine = ENGINE_MANAGER.getEngineByName("groovy");

  private static final Log LOG = LogFactory.getLog(AutoSkript.class);

  @Override
  public String toString() {
    return "AutoSkript{bereich=" + this.bereich + ", beschreibung=" + this.beschreibung + "}";
  }

  /*
   * Nachbearbeitung nach JAXB-Unmarshal.
   */
  protected void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
    StringBuilder builder = new StringBuilder();
    for (SchalterStellung stellung : SchalterStellung.values()) {
      builder.append("import static ");
      builder.append(SchalterStellung.class.getName());
      builder.append(".");
      builder.append(stellung.name());
      builder.append(";\n");
    }
    for (SignalStellung stellung : SignalStellung.values()) {
      builder.append("import static ");
      builder.append(SignalStellung.class.getName());
      builder.append(".");
      builder.append(stellung.name());
      builder.append(";\n");
    }
    for (WeichenStellung stellung : WeichenStellung.values()) {
      builder.append("import static ");
      builder.append(WeichenStellung.class.getName());
      builder.append(".");
      builder.append(stellung.name());
      builder.append(";\n");
    }
    builder.append(this.skriptCode);

    this.skriptCode = builder.toString();
  }

  public void linkSteuerungsObjekte(Steuerung steuerung) {
    this.objekte.forEach(o -> {
      o.linkSteuerungsObjekt(steuerung);
      this.steuerungsObjekte.add(o.getSteuerungsObjekt());
      this.scriptEngine.put(o.getVar(), o.getSteuerungsObjekt());
    });

    this.scriptEngine.put("log", LOG);
  }

  public void execute() {
    try {
      this.scriptEngine.eval(this.skriptCode);
    } catch (ScriptException e) {
      LOG.error("Fehler im Skript \"" + this.beschreibung + "\"", e);
    }
  }
}
