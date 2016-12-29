package de.gedoplan.v5t11.betriebssteuerung.service.funktionsdecoder.wdmiba;

import de.gedoplan.v5t11.betriebssteuerung.service.ConfigurationRuntimeService;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class WDMibaRuntimeService extends ConfigurationRuntimeService<WDMibaConfigurationAdapter>
{
  @Override
  public void getRuntimeValues(WDMibaConfigurationAdapter configuration)
  {
    configuration.setAdresseIst(this.selectrixGateway.getValue(0));

    int betriebsArt = this.selectrixGateway.getValue(1);
    for (int i = 0, bit = 1; i < 8; ++i, bit <<= 1)
    {
      configuration.getDauer()[i].setIst((betriebsArt & bit) != 0);
    }
  }

  @Override
  public void setRuntimeValues(WDMibaConfigurationAdapter configuration)
  {
    this.selectrixGateway.setValue(0, configuration.getAdresseIst());

    int betriebsArt = 0;
    for (int i = 0, bit = 1; i < 8; ++i, bit <<= 1)
    {
      if (configuration.getDauer()[i].isIst())
      {
        betriebsArt |= bit;
      }
    }
    this.selectrixGateway.setValue(1, betriebsArt);
  }

}
