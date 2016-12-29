package de.gedoplan.v5t11.betriebssteuerung.model;

import de.gedoplan.v5t11.betriebssteuerung.service.funktionsdecoder.wdmiba.WDMibaConfigurationAdapter;
import de.gedoplan.v5t11.betriebssteuerung.service.funktionsdecoder.wdmiba.WDMibaRuntimeService;

import javax.enterprise.context.ConversationScoped;
import javax.enterprise.inject.Model;

/**
 * Presentation Model für die Programmierung von Funktionsdecodern des Typs D&H WDMiba.
 * 
 * @author dw
 */
@Model
@ConversationScoped
public class BausteinProgrammierungModel_WDMiba extends BausteinProgrammierungModel_XXX<WDMibaConfigurationAdapter, WDMibaRuntimeService>
{
  public BausteinProgrammierungModel_WDMiba()
  {
    super(WDMibaConfigurationAdapter.class, WDMibaRuntimeService.class);
  }
}
