package de.gedoplan.v5t11.betriebssteuerung.model;

import de.gedoplan.v5t11.betriebssteuerung.service.SXSD1ConfigurationAdapter;
import de.gedoplan.v5t11.betriebssteuerung.service.SXSD1RuntimeService;

import javax.enterprise.context.ConversationScoped;
import javax.enterprise.inject.Model;

/**
 * Presentation Model für die Programmierung von Funktionsdecodern des Typs Weil SX-SD-1.
 * 
 * @author dw
 */
@Model
@ConversationScoped
public class BausteinProgrammierungModel_SXSD1 extends BausteinProgrammierungModel_XXX<SXSD1ConfigurationAdapter, SXSD1RuntimeService>
{
  public BausteinProgrammierungModel_SXSD1()
  {
    super(SXSD1ConfigurationAdapter.class, SXSD1RuntimeService.class);
  }
}
