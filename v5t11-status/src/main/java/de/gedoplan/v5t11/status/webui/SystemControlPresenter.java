package de.gedoplan.v5t11.status.webui;

import de.gedoplan.v5t11.status.entity.Steuerung;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Signal;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Weiche;
import de.gedoplan.v5t11.status.entity.lok.Lok;
import de.gedoplan.v5t11.status.entity.lok.Lok.FunktionConfig;
import de.gedoplan.v5t11.util.domain.attribute.SignalStellung;
import de.gedoplan.v5t11.util.domain.attribute.WeichenStellung;

import java.io.Serializable;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import lombok.Getter;

@Named
@SessionScoped
public class SystemControlPresenter implements Serializable {

  @Inject
  Steuerung steuerung;

  @Getter
  private String bereich;

  @Getter
  private String weichenName;
  private Weiche weiche;

  @Getter
  private String signalName;
  private Signal signal;

  @Getter
  private String lokId;
  private Lok lok;

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
    return this.signal != null ? this.signal.getErlaubteStellungen() : Collections.emptySet();
  }

  public SignalStellung getSignalStellung() {
    return this.signal != null ? this.signal.getStellung() : SignalStellung.HALT;
  }

  public void setSignalStellung(SignalStellung stellung) {
    if (this.signal != null) {
      this.signal.setStellung(stellung);
    }
  }

  public Collection<Lok> getLoks() {
    return this.steuerung.getLoks();
  }

  public void setLokId(String lokId) {
    this.lokId = lokId;
    this.lok = this.steuerung.getLok(lokId);
    if (this.lok == null) {
      FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("unbekannte Lok: " + this.lokId));
    }
  }

  private void resetLok() {
    Collection<Lok> loks = getLoks();
    if (!loks.isEmpty()) {
      this.lok = loks.iterator().next();
      this.lokId = this.lok.getId();
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
    return this.lok != null ? this.lok.getMaxFahrstufe() : 31;
  }

  public int getLokFahrstufe() {
    return this.lok != null ? this.lok.getFahrstufe() : 0;
  }

  public void setLokFahrstufe(int fahrstufe) {
    if (this.lok != null) {
      if (fahrstufe >= 0 && fahrstufe <= this.lok.getMaxFahrstufe()) {
        this.lok.setFahrstufe(fahrstufe);
      } else {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("ungültige Fahrstufe: " + fahrstufe));
      }
    }
  }

  private static final Entry<Integer, FunktionConfig> LOK_LICHT_FUNKTION_CONFIG_ENTRY = new Entry<Integer, Lok.FunktionConfig>() {

    private final FunktionConfig value = new FunktionConfig("Licht", false);

    @Override
    public FunktionConfig setValue(FunktionConfig value) {
      return null;
    }

    @Override
    public FunktionConfig getValue() {
      return this.value;
    }

    @Override
    public Integer getKey() {
      return 0;
    }
  };

  public Set<Entry<Integer, FunktionConfig>> getLokFunktionConfigs() {
    // SortedSet<Entry<Integer, FunktionConfig>> result = new TreeSet<>((a, b) -> Integer.compare(a.getKey(), b.getKey()));
    SortedSet<Entry<Integer, FunktionConfig>> result = new TreeSet<>((a, b) -> Collator.getInstance().compare(a.getValue().getBeschreibung(), b.getValue().getBeschreibung()));
    if (this.lok != null) {
      result.add(LOK_LICHT_FUNKTION_CONFIG_ENTRY);
      result.addAll(this.lok.getFunktionConfigs().entrySet());
    }

    return result;
  }

  public List<Integer> getLokFunktionen() {
    List<Integer> funktionen = new ArrayList<>();
    if (this.lok != null) {
      if (this.lok.isLicht()) {
        funktionen.add(0);
      }

      for (int i = 1; i <= 16; ++i) {
        if (this.lok.getFunktionConfigs().containsKey(i) && (this.lok.getFunktionStatus() & (i - 1)) != 0) {
          funktionen.add(i);
        }
      }
    }
    return funktionen;
  }

  public void setLokFunktionen(List<Integer> funktionen) {
    if (this.lok != null) {
      this.lok.setLicht(funktionen.contains(0));

      for (int i = 1; i <= 16; ++i) {
        if (this.lok.getFunktionConfigs().containsKey(i)) {
          this.lok.setFunktion(i, funktionen.contains(i));
        }
      }
    }
  }
}
