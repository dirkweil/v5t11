package de.gedoplan.v5t11.betriebssteuerung.service.lokdecoder.tr66830;

import de.gedoplan.v5t11.betriebssteuerung.service.ConfigurationRuntimeService;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class Tr66830RuntimeService extends ConfigurationRuntimeService<Tr66830ConfigurationAdapter>
{
  @Override
  public void getRuntimeValues(Tr66830ConfigurationAdapter configuration)
  {
    configuration.setAdresseIst(this.selectrixGateway.getValue(0));
  }

  @Override
  public void setRuntimeValues(Tr66830ConfigurationAdapter configuration)
  {
    this.selectrixGateway.setValue(0, configuration.getAdresseIst());
  }

}
