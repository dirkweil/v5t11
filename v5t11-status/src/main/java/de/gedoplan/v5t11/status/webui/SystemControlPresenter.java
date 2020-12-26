package de.gedoplan.v5t11.status.webui;

import de.gedoplan.v5t11.status.entity.Steuerung;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Signal;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Weiche;
import de.gedoplan.v5t11.status.entity.fahrzeug.Fahrzeug;
import de.gedoplan.v5t11.util.domain.attribute.FahrzeugId;
import de.gedoplan.v5t11.util.domain.attribute.SignalStellung;
import de.gedoplan.v5t11.util.domain.attribute.WeichenStellung;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.logging.Logger;

import lombok.Getter;

@Named
@SessionScoped
public class SystemControlPresenter implements Serializable {

  @Inject
  Steuerung steuerung;

  @Inject
  Logger log;

  @Getter
  private String bereich;

  @Getter
  private String weichenName;
  private Weiche weiche;

  @Getter
  private String signalName;
  private Signal signal;

  @Getter
  private FahrzeugId lokId;
  private Fahrzeug lok;

  @PostConstruct
  void postConstruct() {
    resetBereich();
    resetWeiche();
    resetSignal();
    resetLok();
  }

  public boolean isGleisspannung() {
    return this.steuerung.getZentrale().isGleisspannung();
  }

  public void setGleisspannung(boolean on) {
    this.steuerung.getZentrale().setGleisspannung(on);
  }

  public Collection<String> getBereiche() {
    return this.steuerung.getBereiche();
  }

  public void setBereich(String bereich) {
    this.bereich = bereich;
    resetWeiche();
    resetSignal();
  }

  private void resetBereich() {
    Collection<String> bereiche = getBereiche();
    if (!bereiche.isEmpty()) {
      this.bereich = bereiche.iterator().next();
    }
  }

  public Collection<Weiche> getWeichen() {
    return this.bereich != null ? this.steuerung.getWeichen(this.bereich) : Collections.emptySet();
  }

  public void setWeichenName(String weichenName) {
    this.weichenName = weichenName;
    this.weiche = this.steuerung.getWeiche(this.bereich, weichenName);
    if (this.weiche == null) {
      FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("unbekannte Weiche: " + this.bereich + "/" + weichenName));
    }
  }

  private void resetWeiche() {
    this.weichenName = null;
    if (this.bereich != null) {
      Collection<Weiche> weichen = getWeichen();
      if (!weichen.isEmpty()) {
        this.weiche = weichen.iterator().next();
        this.weichenName = this.weiche.getName();
      }
    }
  }

  public WeichenStellung[] getWeichenStellungen() {
    return WeichenStellung.values();
  }

  public WeichenStellung getWeichenStellung() {
    return this.weiche != null ? this.weiche.getStellung() : WeichenStellung.GERADE;
  }

  public void setWeichenStellung(WeichenStellung stellung) {
    if (this.weiche != null) {
      this.weiche.setStellung(stellung);
    }
  }

  public Collection<Signal> getSignale() {
    return this.bereich != null ? this.steuerung.getSignale(this.bereich) : Collections.emptySet();
  }

  public void setSignalName(String signalName) {
    this.signalName = signalName;
    this.signal = this.steuerung.getSignal(this.bereich, this.signalName);
    if (this.signal == null) {
      FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("unbekanntes Signal: " + this.bereich + "/" + signalName));
    }
  }

  private void resetSignal() {
    this.weichenName = null;
    if (this.bereich != null) {
      Collection<Signal> signale = getSignale();
      if (!signale.isEmpty()) {
        this.signal = signale.iterator().next();
        this.signalName = this.signal.getName();
      }
    }
  }

  public Set<SignalStellung> getSignalStellungen() {
    return this.signal != null ? this.signal.getTyp().getErlaubteStellungen() : Collections.emptySet();
  }

  public SignalStellung getSignalStellung() {
    return this.signal != null ? this.signal.getStellung() : SignalStellung.HALT;
  }

  public void setSignalStellung(SignalStellung stellung) {
    if (this.signal != null) {
      this.signal.setStellung(stellung);
    }
  }

  public Collection<Fahrzeug> getLoks() {
    return this.steuerung.getFahrzeuge();
  }

  public void setLokId(FahrzeugId lokId) {
    if (this.log.isTraceEnabled()) {
      this.log.trace("setLokId(" + lokId + ")");
    }

    this.lokId = lokId;
    this.lok = this.steuerung.getFahrzeug(lokId);

    if (this.lok == null) {
      this.lok = new Fahrzeug(lokId);
      this.lok.injectFields();
      this.steuerung.addFahrzeug(this.lok);
    }

    if (this.log.isTraceEnabled()) {
      this.log.trace("setLokId: fertig");
    }
  }

  private void resetLok() {
    Collection<Fahrzeug> loks = getLoks();
    if (!loks.isEmpty()) {
      setLokId(loks.iterator().next().getId());
    }
  }

  public boolean isLokAktiv() {
    return this.lok != null ? this.lok.isAktiv() : false;
  }

  public void setLokAktiv(boolean on) {
    if (this.lok != null) {
      this.lok.setAktiv(on);
    }
  }

  public boolean isLokLicht() {
    return this.lok != null ? this.lok.isLicht() : false;
  }

  public void setLokLicht(boolean on) {
    if (this.lok != null) {
      this.lok.setLicht(on);
    }
  }

  public boolean isLokRueckwaerts() {
    return this.lok != null ? this.lok.isRueckwaerts() : false;
  }

  public void setLokRueckwaerts(boolean on) {
    if (this.lok != null) {
      this.lok.setRueckwaerts(on);
    }
  }

  public int getLokMaxFahrstufe() {
    return this.lok != null ? this.lok.getId().getSystemTyp().getMaxFahrstufe() : 31;
  }

  public int getLokFahrstufe() {
    return this.lok != null ? this.lok.getFahrstufe() : 0;
  }

  public void setLokFahrstufe(int fahrstufe) {
    if (this.lok != null) {
      if (fahrstufe >= 0 && fahrstufe <= this.lok.getId().getSystemTyp().getMaxFahrstufe()) {
        this.lok.setFahrstufe(fahrstufe);
      } else {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("ungültige Fahrstufe: " + fahrstufe));
      }
    }
  }

  @Getter
  private List<LokFunktion> lokFunktionen = IntStream.range(0, 16).mapToObj(LokFunktion::new).collect(Collectors.toList());

  public class LokFunktion {
    @Getter
    int nr;
    int mask;

    public LokFunktion(int nr) {
      this.nr = nr;
      this.mask = 1 << nr;
    }

    public boolean isOn() {
      return SystemControlPresenter.this.lok != null && (SystemControlPresenter.this.lok.getFktBits() & this.mask) != 0;
    }

    public void setOn(boolean on) {
      if (SystemControlPresenter.this.lok != null) {
        if (on) {
          SystemControlPresenter.this.lok.setFunktionStatus(SystemControlPresenter.this.lok.getFktBits() | this.mask);
        } else {
          SystemControlPresenter.this.lok.setFunktionStatus(SystemControlPresenter.this.lok.getFktBits() & ~this.mask);
        }
      }
    }
  }

}
