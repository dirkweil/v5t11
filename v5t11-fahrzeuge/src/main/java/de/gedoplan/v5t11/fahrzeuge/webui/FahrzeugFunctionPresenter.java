package de.gedoplan.v5t11.fahrzeuge.webui;

import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.Fahrzeug;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.FahrzeugFunktion;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe;

import java.io.Serializable;
import java.util.Iterator;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named
@ViewScoped
public class FahrzeugFunctionPresenter implements Serializable {

  @Inject
  FahrzeugListPresenter fahrzeugListPresenter;

  //  @Inject
  //  Logger log;

  public Fahrzeug getCurrentFahrzeug() {
    return this.fahrzeugListPresenter.getCurrentFahrzeug();
  }

  public FahrzeugFunktionsGruppe[] getFunktionsGruppen() {
    return FahrzeugFunktionsGruppe.values();
  }

  public void addFunktion() {
    getCurrentFahrzeug().getFahrzeugdecoder().getFunktionen().add(0, new FahrzeugFunktion(FahrzeugFunktionsGruppe.AF, 0, 0, false, false, false, ""));
  }

  public void removeFunktion(FahrzeugFunktion funktion) {
    Iterator<FahrzeugFunktion> iterator = getCurrentFahrzeug().getFahrzeugdecoder().getFunktionen().iterator();
    while (iterator.hasNext()) {
      if (iterator.next() == funktion) {
        iterator.remove();
        break;
      }
    }
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

}
