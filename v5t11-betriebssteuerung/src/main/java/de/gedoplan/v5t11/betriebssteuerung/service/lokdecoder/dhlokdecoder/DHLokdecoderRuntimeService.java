package de.gedoplan.v5t11.betriebssteuerung.service.lokdecoder.dhlokdecoder;

import de.gedoplan.v5t11.betriebssteuerung.service.ConfigurationRuntimeService;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DHLokdecoderRuntimeService extends ConfigurationRuntimeService<DHLokdecoderConfigurationAdapter>
{
  @Override
  public void getRuntimeValues(DHLokdecoderConfigurationAdapter configuration)
  {
    configuration.setAdresseIst(this.selectrixGateway.getValue(0));
  }

  @Override
  public void setRuntimeValues(DHLokdecoderConfigurationAdapter configuration)
  {
    this.selectrixGateway.setValue(0, configuration.getAdresseIst());
  }

}
