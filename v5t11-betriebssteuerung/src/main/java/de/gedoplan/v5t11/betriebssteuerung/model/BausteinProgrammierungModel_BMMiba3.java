package de.gedoplan.v5t11.betriebssteuerung.model;

import de.gedoplan.v5t11.betriebssteuerung.service.besetztmelder.bmmiba3.BMMiba3ConfigurationAdapter;
import de.gedoplan.v5t11.betriebssteuerung.service.besetztmelder.bmmiba3.BMMiba3RuntimeService;

import javax.enterprise.context.ConversationScoped;
import javax.enterprise.inject.Model;

/**
 * Presentation Model für die Programmierung von Besetztmeldern des Typs Stärz BMMiba3.
 * 
 * @author dw
 */
@Model
@ConversationScoped
public class BausteinProgrammierungModel_BMMiba3 extends BausteinProgrammierungModel_XXX<BMMiba3ConfigurationAdapter, BMMiba3RuntimeService>
{
  public BausteinProgrammierungModel_BMMiba3()
  {
    super(BMMiba3ConfigurationAdapter.class, BMMiba3RuntimeService.class);
  }
}
