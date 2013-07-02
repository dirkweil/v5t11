package de.gedoplan.v5t11.betriebssteuerung.service;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class STRFD1RuntimeService extends ConfigurationRuntimeService<STRFD1ConfigurationAdapter>
{
  @Override
  public void getRuntimeValues(STRFD1ConfigurationAdapter configuration)
  {
    configuration.setAdresseIst(this.selectrixGateway.getValue(0));

    int betriebsArt = this.selectrixGateway.getValue(1);
    for (int i = 0, bit = 1; i < 8; ++i, bit <<= 1)
    {
      configuration.getDauer()[i].setIst((betriebsArt & bit) != 0);
    }

    configuration.getImpulsDauer().setIst(this.selectrixGateway.getValue(3) * 80);
  }

  @Override
  public void setRuntimeValues(STRFD1ConfigurationAdapter configuration)
  {
    // Betriebsmodus "Output 8"
    this.selectrixGateway.setValue(5, 1);

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

    int verz = configuration.getImpulsDauer().getIst();
    if (verz < 0)
    {
      verz = 0;
    }
    if (verz > 255)
    {
      verz = 255;
    }
    this.selectrixGateway.setValue(3, verz);
  }

}
