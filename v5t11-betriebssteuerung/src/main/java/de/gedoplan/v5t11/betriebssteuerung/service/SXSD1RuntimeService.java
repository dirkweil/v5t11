package de.gedoplan.v5t11.betriebssteuerung.service;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SXSD1RuntimeService extends ConfigurationRuntimeService<SXSD1ConfigurationAdapter>
{
  @Override
  public void getRuntimeValues(SXSD1ConfigurationAdapter configuration)
  {
    configuration.setAdresseIst(this.selectrixGateway.getValue(0));
  }

  @Override
  public void setRuntimeValues(SXSD1ConfigurationAdapter configuration)
  {
    // Betriebsmodus "Output 16"
    this.selectrixGateway.setValue(5, 3);

    this.selectrixGateway.setValue(0, configuration.getAdresseIst());
    this.selectrixGateway.setValue(2, configuration.getAdresseIst() + 1);
  }

}
