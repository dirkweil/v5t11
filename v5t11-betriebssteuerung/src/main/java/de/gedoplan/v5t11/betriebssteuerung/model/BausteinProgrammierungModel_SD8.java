package de.gedoplan.v5t11.betriebssteuerung.model;

import de.gedoplan.v5t11.betriebssteuerung.service.SD8ConfigurationAdapter;
import de.gedoplan.v5t11.betriebssteuerung.service.SD8ConfigurationAdapter.ServoConfiguration;
import de.gedoplan.v5t11.betriebssteuerung.service.SD8RuntimeService;

import javax.enterprise.context.ConversationScoped;
import javax.enterprise.inject.Model;

/**
 * Presentation Model für die Programmierung von Funktionsdecodern des Typs Martsch SD-8.
 * 
 * @author dw
 */
@Model
@ConversationScoped
public class BausteinProgrammierungModel_SD8 extends BausteinProgrammierungModel_XXX<SD8ConfigurationAdapter, SD8RuntimeService>
{
  public BausteinProgrammierungModel_SD8()
  {
    super(SD8ConfigurationAdapter.class, SD8RuntimeService.class);
  }

  public void testStart(ServoConfiguration servoConfiguration)
  {
    test(servoConfiguration, false);
  }

  public void testEnde(ServoConfiguration servoConfiguration)
  {
    test(servoConfiguration, true);
  }

  private void test(ServoConfiguration servoConfiguration, boolean ende)
  {
    this.configurationRuntimeService.setRuntimeValues(getConfiguration(), servoConfiguration.getServoNummer());

    this.configurationRuntimeService.setServostellung(servoConfiguration.getServoNummer(), ende);
  }

}
