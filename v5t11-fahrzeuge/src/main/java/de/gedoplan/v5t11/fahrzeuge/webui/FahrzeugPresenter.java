package de.gedoplan.v5t11.fahrzeuge.webui;

import de.gedoplan.baselibs.utils.util.ResourceUtil;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.Fahrzeug;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.Fahrzeug.FahrzeugFunktion;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe;
import de.gedoplan.v5t11.fahrzeuge.gateway.StatusGateway;
import de.gedoplan.v5t11.fahrzeuge.persistence.FahrzeugRepository;
import de.gedoplan.v5t11.util.domain.attribute.FahrzeugId;
import de.gedoplan.v5t11.util.domain.attribute.SystemTyp;

import java.io.Serializable;
import java.text.Collator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.constraints.NotEmpty;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Named
@SessionScoped
public class FahrzeugPresenter implements Serializable {

  @Inject
  FahrzeugRepository fahrzeugRepository;

  @Inject
  @RestClient
  StatusGateway statusGateway;

  @Inject
  Logger log;

  @Getter
  private List<Fahrzeug> fahrzeuge;

  @Getter
  @Setter
  private Fahrzeug currentFahrzeug;

  @Getter
  @Setter
  @NotEmpty
  private FahrzeugId newId = new FahrzeugId(SystemTyp.DCC, 3);

  @PostConstruct
  void refreshFahrzeuge() {
    this.fahrzeuge = this.fahrzeugRepository.findAll();
  }

  public String create() {
    this.currentFahrzeug = new Fahrzeug(this.newId, null, null);
    this.fahrzeuge.add(this.currentFahrzeug);

    if (this.log.isDebugEnabled()) {
      this.log.debug("Create " + this.currentFahrzeug);
    }

    return "edit";
  }

  public String edit(Fahrzeug fahrzeug) {
    this.currentFahrzeug = fahrzeug;

    if (this.log.isDebugEnabled()) {
      this.log.debug("Edit " + this.currentFahrzeug);
    }

    return "edit";
  }

  public void addFunktion() {
    this.currentFahrzeug.getFunktionen().add(0, new FahrzeugFunktion(FahrzeugFunktionsGruppe.AF, 0, 0, false, false, false, ""));
  }

  public void removeFunktion(FahrzeugFunktion funktion) {
    Iterator<FahrzeugFunktion> iterator = this.currentFahrzeug.getFunktionen().iterator();
    while (iterator.hasNext()) {
      if (iterator.next() == funktion) {
        iterator.remove();
        break;
      }
    }
  }

  @Inject
  Validator validator;

  public String save() {
    Set<ConstraintViolation<Fahrzeug>> violations = this.validator.validate(this.currentFahrzeug);
    if (!violations.isEmpty()) {
      FacesContext facesContext = FacesContext.getCurrentInstance();
      violations.forEach(cv -> {
        FacesMessage facesMessage = new FacesMessage(cv.getMessage());
        facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
        facesContext.addMessage(null, facesMessage);
      });
      facesContext.validationFailed();
      return null;
    }

    Iterator<FahrzeugFunktion> iterator = this.currentFahrzeug.getFunktionen().iterator();
    while (iterator.hasNext()) {
      FahrzeugFunktion funktion = iterator.next();
      if (funktion.getMaske() == 0 || funktion.getBeschreibung() == null || funktion.getBeschreibung().strip().isEmpty()) {
        iterator.remove();
      }
    }
    this.fahrzeugRepository.merge(this.currentFahrzeug);

    return "finished";

  }

  public String remove() {
    // TODO Confirmation!

    this.fahrzeugRepository.removeById(this.currentFahrzeug.getId());
    refreshFahrzeuge();

    return "finished";
  }

  public String cancel() {
    refreshFahrzeuge();

    return "finished";
  }

  public String program(Fahrzeug fahrzeug) {
    this.currentFahrzeug = fahrzeug;

    if (this.log.isDebugEnabled()) {
      this.log.debug("Program " + fahrzeug);
    }

    return null;
  }

  public String getImage(Fahrzeug fahrzeug) {
    if (fahrzeug.getImage() != null) {
      throw new UnsupportedOperationException("not yet implemented");
    }

    if (fahrzeug.getBetriebsnummer() != null) {
      String name = fahrzeug.getBetriebsnummer().replaceAll("\\s+", "_");
      while (!name.isEmpty()) {
        String resourceName = "images/loks/" + name + ".png";
        if (ResourceUtil.getResource("META-INF/resources/" + resourceName) != null) {
          return resourceName;
        }

        name = name.substring(0, name.length() - 1);
      }
    }

    return "images/loks/none.png";

  }

  public SystemTyp[] getSystemTypen() {
    return SystemTyp.values();
  }

  public FahrzeugFunktionsGruppe[] getFunktionsGruppen() {
    return FahrzeugFunktionsGruppe.values();
  }

  public TriStateAdapter getTriStateAdapter(FahrzeugFunktion fahrzeugFunktion, int bitNr) {
    return new TriStateAdapter(fahrzeugFunktion, bitNr);
  }

  public static class TriStateAdapter {

    private FahrzeugFunktion fahrzeugFunktion;
    private int maske;

    public TriStateAdapter(FahrzeugFunktion fahrzeugFunktion, int bitNr) {
      this.fahrzeugFunktion = fahrzeugFunktion;
      this.maske = (1 << bitNr);
    }

    public String getValue() {
      if ((this.fahrzeugFunktion.getMaske() & this.maske) == 0) {
        return "0";
      }
      if ((this.fahrzeugFunktion.getWert() & this.maske) == 0) {
        return "2";
      }
      return "1";
    }

    public void setValue(String s) {
      switch (s) {
      default:
      case "0":
        this.fahrzeugFunktion.setMaske(this.fahrzeugFunktion.getMaske() & (~this.maske));
        this.fahrzeugFunktion.setWert(this.fahrzeugFunktion.getWert() & (~this.maske));
        break;

      case "1":
        this.fahrzeugFunktion.setMaske(this.fahrzeugFunktion.getMaske() | this.maske);
        this.fahrzeugFunktion.setWert(this.fahrzeugFunktion.getWert() | this.maske);
        break;

      case "2":
        this.fahrzeugFunktion.setMaske(this.fahrzeugFunktion.getMaske() | this.maske);
        this.fahrzeugFunktion.setWert(this.fahrzeugFunktion.getWert() & (~this.maske));
        break;
      }
    }
  }

  public String control(Fahrzeug fahrzeug) {
    this.currentFahrzeug = fahrzeug;
    return "control";
  }

  public Fahrzeug getRefreshedFahrzeug() {
    if (!this.fahrzeugRepository.isAttached(this.currentFahrzeug)) {
      this.currentFahrzeug = this.fahrzeugRepository.findById(this.currentFahrzeug.getId());
    }
    return this.currentFahrzeug;
  }

  public boolean isLokAktiv() {
    return getRefreshedFahrzeug().isAktiv();
  }

  public void setLokAktiv(boolean aktiv) {
    this.statusGateway.changeFahrzeug(this.currentFahrzeug.getId(), aktiv, null, null, null, null);
  }

  public boolean isLokRueckwaerts() {
    return getRefreshedFahrzeug().isRueckwaerts();
  }

  public void setLokRueckwaerts(boolean rueckwaerts) {
    this.statusGateway.changeFahrzeug(this.currentFahrzeug.getId(), null, null, null, null, rueckwaerts);
  }

  public int getLokFahrstufe() {
    return getRefreshedFahrzeug().getFahrstufe();
  }

  public void setLokFahrstufe(int fahrstufe) {
    this.statusGateway.changeFahrzeug(this.currentFahrzeug.getId(), null, fahrstufe, null, null, null);
  }

  public int getLokMaxFahrstufe() {
    return this.currentFahrzeug.getId().getSystemTyp().getMaxFahrstufe();
  }

  public List<FahrzeugFunktionsGruppe> getCurrentFunktionsGruppen() {
    return this.currentFahrzeug.getFunktionen()
        .stream()
        .map(FahrzeugFunktion::getGruppe)
        .sorted()
        .distinct()
        .collect(Collectors.toList());
  }

  public List<FahrzeugFunktionWrapper> getLokFunktionen(FahrzeugFunktionsGruppe fahrzeugFunktionsGruppe) {
    Stream<FahrzeugFunktionWrapper> stream = this.currentFahrzeug
        .getFunktionen()
        .stream()
        .filter(f -> f.getGruppe() == fahrzeugFunktionsGruppe)
        .map(FahrzeugFunktionWrapper::new)
        .sorted();
    if (fahrzeugFunktionsGruppe == FahrzeugFunktionsGruppe.FL) {
      stream = Stream.concat(Stream.of(this.lichtWrapper), stream);
    }
    return stream
        .collect(Collectors.toList());
  }

  public FahrzeugFunktionWrapper getFahrzeugFunktionWrapper(FahrzeugFunktion fahrzeugFunktion) {
    return new FahrzeugFunktionWrapper(fahrzeugFunktion);
  }

  @AllArgsConstructor
  public class FahrzeugFunktionWrapper implements Comparable<FahrzeugFunktionWrapper> {
    private FahrzeugFunktion fahrzeugFunktion;

    public String getOnLabel() {
      return this.fahrzeugFunktion.getBeschreibung();
    }

    public String getOffLabel() {
      return this.fahrzeugFunktion.getBeschreibung();
    }

    public boolean isAktiv() {
      return (getRefreshedFahrzeug().getFktBits() & this.fahrzeugFunktion.getMaske()) == this.fahrzeugFunktion.getWert();
    }

    public void setAktiv(boolean aktiv) {
      int fktBits = getRefreshedFahrzeug().getFktBits() & (~this.fahrzeugFunktion.getMaske());
      if (aktiv) {
        fktBits |= this.fahrzeugFunktion.getWert();
      }

      FahrzeugPresenter.this.statusGateway.changeFahrzeug(FahrzeugPresenter.this.currentFahrzeug.getId(), null, null, fktBits, null, null);
    }

    @Override
    public int compareTo(FahrzeugFunktionWrapper other) {
      return Collator.getInstance().compare(this.fahrzeugFunktion.getBeschreibung(), other.fahrzeugFunktion.getBeschreibung());
    }
  }

  private FahrzeugFunktionWrapper lichtWrapper = new FahrzeugFunktionWrapper(null) {

    @Override
    public String getOnLabel() {
      return "an";
    }

    @Override
    public String getOffLabel() {
      return "aus";
    }

    @Override
    public boolean isAktiv() {
      return getRefreshedFahrzeug().isLicht();
    }

    @Override
    public void setAktiv(boolean aktiv) {
      FahrzeugPresenter.this.statusGateway.changeFahrzeug(FahrzeugPresenter.this.currentFahrzeug.getId(), null, null, null, aktiv, null);
    }

  };

}
