package de.gedoplan.v5t11.betriebssteuerung.service.lokdecoder.sxlokdecoder;

import de.gedoplan.v5t11.betriebssteuerung.service.ConfigurationRuntimeService;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SxLokdecoderRuntimeService extends ConfigurationRuntimeService<SxLokdecoderConfigurationAdapter>
{
  @Override
  public void getRuntimeValues(SxLokdecoderConfigurationAdapter configuration)
  {
    configuration.setAdresseIst(this.selectrixGateway.getValue(0));
  }

  @Override
  public void setRuntimeValues(SxLokdecoderConfigurationAdapter configuration)
  {
    this.selectrixGateway.setValue(0, configuration.getAdresseIst());
  }

}
