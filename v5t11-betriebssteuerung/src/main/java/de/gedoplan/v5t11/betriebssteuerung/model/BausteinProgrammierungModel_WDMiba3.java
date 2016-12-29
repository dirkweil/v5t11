package de.gedoplan.v5t11.betriebssteuerung.model;

import de.gedoplan.v5t11.betriebssteuerung.service.funktionsdecoder.wdmiba3.WDMiba3ConfigurationAdapter;
import de.gedoplan.v5t11.betriebssteuerung.service.funktionsdecoder.wdmiba3.WDMiba3RuntimeService;

import javax.enterprise.context.ConversationScoped;
import javax.enterprise.inject.Model;

/**
 * Presentation Model für die Programmierung von Funktionsdecodern des Typs Stärz WDMiba3.
 * 
 * @author dw
 */
@Model
@ConversationScoped
public class BausteinProgrammierungModel_WDMiba3 extends BausteinProgrammierungModel_XXX<WDMiba3ConfigurationAdapter, WDMiba3RuntimeService>
{
  public BausteinProgrammierungModel_WDMiba3()
  {
    super(WDMiba3ConfigurationAdapter.class, WDMiba3RuntimeService.class);
  }
}
