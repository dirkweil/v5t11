package de.gedoplan.v5t11.fahrzeuge.webui;

import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.Fahrzeug;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.FahrzeugFunktion;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe;
import de.gedoplan.v5t11.fahrzeuge.gateway.StatusGateway;
import de.gedoplan.v5t11.fahrzeuge.persistence.FahrzeugRepository;
import de.gedoplan.v5t11.fahrzeuge.persistence.GleisRepository;
import de.gedoplan.v5t11.util.domain.attribute.BereichselementId;

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
  GleisRepository gleisRepository;

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
    return getRefreshedFahrzeug().getFahrzeugdecoder().isAktiv();
  }

  public void setLokAktiv(boolean aktiv) {
    this.statusGateway.changeFahrzeugdecoder(this.fahrzeugListPresenter.getCurrentFahrzeug().getFahrzeugdecoder().getDecoderAdr(), aktiv, null, null, null, null);
  }

  public boolean isLokRueckwaerts() {
    return getRefreshedFahrzeug().getFahrzeugdecoder().isRueckwaerts();
  }

  public void setLokRueckwaerts(boolean rueckwaerts) {
    this.statusGateway.changeFahrzeugdecoder(this.fahrzeugListPresenter.getCurrentFahrzeug().getFahrzeugdecoder().getDecoderAdr(), null, null, null, null, rueckwaerts);
  }

  public int getLokFahrstufe() {
    return getRefreshedFahrzeug().getFahrzeugdecoder().getFahrstufe();
  }

  public void setLokFahrstufe(int fahrstufe) {
    this.statusGateway.changeFahrzeugdecoder(this.fahrzeugListPresenter.getCurrentFahrzeug().getFahrzeugdecoder().getDecoderAdr(), null, fahrstufe, null, null, null);
  }

  public int getLokMaxFahrstufe() {
    return this.fahrzeugListPresenter.getCurrentFahrzeug().getFahrzeugdecoder().getDecoderAdr().getSystemTyp().getMaxFahrstufe();
  }

  public String getPositionsbeschreibung() {
    Fahrzeug fahrzeug = this.fahrzeugListPresenter.getCurrentFahrzeug();
    BereichselementId gleisId = fahrzeug.getGleisId();
    if (gleisId == null) {
      return "unbekannt";
    }

    return String.format("%s, %d mm, in Zählrichtung %s",
      gleisId,
      fahrzeug.getGleisPosition(),
      fahrzeug.isGleisZaehlrichtung() ? "vorwärts" : "rückwärts");
  }

  public List<FahrzeugFunktionsGruppe> getCurrentFunktionsGruppen() {
    return Stream.concat(
        Stream.of(FahrzeugFunktionsGruppe.FL),
        this.fahrzeugListPresenter.getCurrentFahrzeug().getFahrzeugdecoder().getFunktionen().stream().map(FahrzeugFunktion::getGruppe))
      .sorted()
      .distinct()
      .collect(Collectors.toList());
  }

  public List<FahrzeugFunktionWrapper> getLokFunktionen(FahrzeugFunktionsGruppe fahrzeugFunktionsGruppe) {
    Stream<FahrzeugFunktionWrapper> stream = this.fahrzeugListPresenter.getCurrentFahrzeug()
      .getFahrzeugdecoder()
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
      return (getRefreshedFahrzeug().getFahrzeugdecoder().getFktBits() & this.fahrzeugFunktion.getMaske()) == this.fahrzeugFunktion.getWert();
    }

    public void setAktiv(boolean aktiv) {
      int fktBits = getRefreshedFahrzeug().getFahrzeugdecoder().getFktBits() & (~this.fahrzeugFunktion.getMaske());
      if (aktiv) {
        fktBits |= this.fahrzeugFunktion.getWert();
      }

      FahrzeugControlPresenter.this.statusGateway.changeFahrzeugdecoder(FahrzeugControlPresenter.this.fahrzeugListPresenter.getCurrentFahrzeug().getFahrzeugdecoder().getDecoderAdr(), null, null,
        fktBits,
        null, null);
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
      return getRefreshedFahrzeug().getFahrzeugdecoder().isLicht();
    }

    @Override
    public void setAktiv(boolean aktiv) {
      FahrzeugControlPresenter.this.statusGateway.changeFahrzeugdecoder(FahrzeugControlPresenter.this.fahrzeugListPresenter.getCurrentFahrzeug().getFahrzeugdecoder().getDecoderAdr(), null, null, null,
        aktiv,
        null);
    }

  };

}
