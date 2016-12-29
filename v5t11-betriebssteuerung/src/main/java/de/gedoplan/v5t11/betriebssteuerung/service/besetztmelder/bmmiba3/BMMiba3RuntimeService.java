package de.gedoplan.v5t11.betriebssteuerung.service.besetztmelder.bmmiba3;

import de.gedoplan.v5t11.betriebssteuerung.service.ConfigurationRuntimeService;
import de.gedoplan.v5t11.betriebssteuerung.service.besetztmelder.bmmiba3.BMMiba3ConfigurationAdapter.MeldungsModus;
import de.gedoplan.v5t11.betriebssteuerung.service.besetztmelder.bmmiba3.BMMiba3ConfigurationAdapter.MeldungsSpeicherung;
import de.gedoplan.v5t11.betriebssteuerung.service.besetztmelder.bmmiba3.BMMiba3ConfigurationAdapter.Zeittakt;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class BMMiba3RuntimeService extends ConfigurationRuntimeService<BMMiba3ConfigurationAdapter> {
  @Override
  public void getRuntimeValues(BMMiba3ConfigurationAdapter configuration) {
    configuration.setAdresseIst(this.selectrixGateway.getValue(0));

    configuration.getAnsprechVerzoegerung().setIst(this.selectrixGateway.getValue(1));

    configuration.getAbfallVerzoegerung().setIst(this.selectrixGateway.getValue(2));

    int options = this.selectrixGateway.getValue(3);
    configuration.getMeldungBeiZeStopp().setIst(MeldungsModus.valueOf(options & 0b00000011));
    configuration.getMeldungBeiFehlendemFahrstrom().setIst(MeldungsModus.valueOf((options & 0b00001100) >> 2));
    configuration.getMeldungsNegation().setIst((options & 0b00010000) != 0);
    configuration.getZeittakt().setIst(Zeittakt.valueof((options & 0b01100000) >> 5));
    configuration.getMeldungsSpeicherung().setIst(MeldungsSpeicherung.valueof((options & 0b10000000) >> 7));
  }

  @Override
  public void setRuntimeValues(BMMiba3ConfigurationAdapter configuration) {
    this.selectrixGateway.setValue(0, configuration.getAdresseIst());
    this.selectrixGateway.setValue(1, configuration.getAnsprechVerzoegerung().getIst());
    this.selectrixGateway.setValue(2, configuration.getAbfallVerzoegerung().getIst());
    int options = configuration.getMeldungBeiZeStopp().getIst().getBits();
    options |= configuration.getMeldungBeiFehlendemFahrstrom().getIst().getBits() << 2;
    if (configuration.getMeldungsNegation().getIst()) {
      options |= 0b00010000;
    }
    options |= configuration.getZeittakt().getIst().getBits() << 5;
    options |= configuration.getMeldungsSpeicherung().getIst().getBits() << 7;
    this.selectrixGateway.setValue(3, options);
  }

}
