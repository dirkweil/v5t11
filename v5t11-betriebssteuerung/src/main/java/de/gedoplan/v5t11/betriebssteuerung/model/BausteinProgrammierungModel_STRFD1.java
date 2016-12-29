package de.gedoplan.v5t11.betriebssteuerung.model;

import de.gedoplan.v5t11.betriebssteuerung.service.funktionsdecoder.strfd1.STRFD1ConfigurationAdapter;
import de.gedoplan.v5t11.betriebssteuerung.service.funktionsdecoder.strfd1.STRFD1RuntimeService;

import javax.enterprise.context.ConversationScoped;
import javax.enterprise.inject.Model;

/**
 * Presentation Model für die Programmierung von Funktionsdecodern des Typs Reinhard FD-1.
 * 
 * @author dw
 */
@Model
@ConversationScoped
public class BausteinProgrammierungModel_STRFD1 extends BausteinProgrammierungModel_XXX<STRFD1ConfigurationAdapter, STRFD1RuntimeService>
{
  public BausteinProgrammierungModel_STRFD1()
  {
    super(STRFD1ConfigurationAdapter.class, STRFD1RuntimeService.class);
  }
}
