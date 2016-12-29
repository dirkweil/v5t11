package de.gedoplan.v5t11.betriebssteuerung.model;

import de.gedoplan.v5t11.betriebssteuerung.service.besetztmelder.sxbm1.SXBM1ConfigurationAdapter;
import de.gedoplan.v5t11.betriebssteuerung.service.besetztmelder.sxbm1.SXBM1RuntimeService;

import javax.enterprise.context.ConversationScoped;
import javax.enterprise.inject.Model;

/**
 * Presentation Model für die Programmierung von Besetztmeldern des Typs Weil SX-BM-1.
 * 
 * @author dw
 */
@Model
@ConversationScoped
public class BausteinProgrammierungModel_SXBM1 extends BausteinProgrammierungModel_XXX<SXBM1ConfigurationAdapter, SXBM1RuntimeService>
{
  public BausteinProgrammierungModel_SXBM1()
  {
    super(SXBM1ConfigurationAdapter.class, SXBM1RuntimeService.class);
  }
}
