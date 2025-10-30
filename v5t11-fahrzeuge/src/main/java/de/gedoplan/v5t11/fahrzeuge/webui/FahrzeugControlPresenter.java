package de.gedoplan.v5t11.fahrzeuge.webui;

import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.Fahrzeug;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.FahrzeugFunktion;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe;
import de.gedoplan.v5t11.fahrzeuge.gateway.StatusGateway;
import de.gedoplan.v5t11.fahrzeuge.persistence.FahrzeugRepository;

import java.io.Serializable;
import java.text.Collator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import lombok.AllArgsConstructor;

@Named
@ViewScoped
public class FahrzeugControlPresenter implements Serializable {

  @Inject
  FahrzeugListPresenter fahrzeugListPresenter;

  @Inject
  FahrzeugRepository fahrzeugRepository;

  @Inject
  @RestClient
  StatusGateway statusGateway;

  @Inject
  Logger logger;

  private Fahrzeug getRefreshedFahrzeug() {
    if (!this.fahrzeugRepository.isAttached(this.fahrzeugListPresenter.getCurrentFahrzeug())) {
      this.fahrzeugListPresenter.setCurrentFahrzeug(this.fahrzeugRepository.findById(this.fahrzeugListPresenter.getCurrentFahrzeug().getId()).get());
    }
    return this.fahrzeugListPresenter.getCurrentFahrzeug();
  }

  public boolean isLokAktiv() {
    return getRefreshedFahrzeug().isAktiv();
  }

  public void setLokAktiv(boolean aktiv) {
    this.statusGateway.changeFahrzeug(this.fahrzeugListPresenter.getCurrentFahrzeug().getDecoderId(), aktiv, null, null, null, null);
  }

  public boolean isLokRueckwaerts() {
    return getRefreshedFahrzeug().isRueckwaerts();
  }

  public void setLokRueckwaerts(boolean rueckwaerts) {
    this.statusGateway.changeFahrzeug(this.fahrzeugListPresenter.getCurrentFahrzeug().getDecoderId(), null, null, null, null, rueckwaerts);
  }

  public int getLokFahrstufe() {
    return getRefreshedFahrzeug().getFahrstufe();
  }

  public void setLokFahrstufe(int fahrstufe) {
    this.statusGateway.changeFahrzeug(this.fahrzeugListPresenter.getCurrentFahrzeug().getDecoderId(), null, fahrstufe, null, null, null);
  }

  public int getLokMaxFahrstufe() {
    return this.fahrzeugListPresenter.getCurrentFahrzeug().getDecoderId().getSystemTyp().getMaxFahrstufe();
  }

  public List<FahrzeugFunktionsGruppe> getCurrentFunktionsGruppen() {
    return Stream.concat(
        Stream.of(FahrzeugFunktionsGruppe.FL),
        this.fahrzeugListPresenter.getCurrentFahrzeug().getFunktionen().stream().map(FahrzeugFunktion::getGruppe))
      .sorted()
      .distinct()
      .collect(Collectors.toList());
  }

  public List<FahrzeugFunktionWrapper> getLokFunktionen(FahrzeugFunktionsGruppe fahrzeugFunktionsGruppe) {
    Stream<FahrzeugFunktionWrapper> stream = this.fahrzeugListPresenter.getCurrentFahrzeug()
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

      FahrzeugControlPresenter.this.statusGateway.changeFahrzeug(FahrzeugControlPresenter.this.fahrzeugListPresenter.getCurrentFahrzeug().getDecoderId(), null, null, fktBits, null, null);
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
      FahrzeugControlPresenter.this.statusGateway.changeFahrzeug(FahrzeugControlPresenter.this.fahrzeugListPresenter.getCurrentFahrzeug().getDecoderId(), null, null, null, aktiv, null);
    }

  };

}
