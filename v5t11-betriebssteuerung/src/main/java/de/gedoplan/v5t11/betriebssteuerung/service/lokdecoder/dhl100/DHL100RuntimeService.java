package de.gedoplan.v5t11.betriebssteuerung.service.lokdecoder.dhl100;

import de.gedoplan.v5t11.betriebssteuerung.service.ConfigurationRuntimeService;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DHL100RuntimeService extends ConfigurationRuntimeService<DHL100ConfigurationAdapter>
{
  @Override
  public void getRuntimeValues(DHL100ConfigurationAdapter configuration)
  {
    configuration.setAdresseIst(this.selectrixGateway.getValue(0));
  }

  @Override
  public void setRuntimeValues(DHL100ConfigurationAdapter configuration)
  {
    this.selectrixGateway.setValue(0, configuration.getAdresseIst());
  }

}
