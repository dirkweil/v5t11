package de.gedoplan.v5t11.betriebssteuerung.model;

import de.gedoplan.v5t11.betriebssteuerung.service.DHL100ConfigurationAdapter;
import de.gedoplan.v5t11.betriebssteuerung.service.DHL100RuntimeService;

import javax.enterprise.context.ConversationScoped;
import javax.enterprise.inject.Model;

/**
 * Presentation Model für die Programmierung von Funktionsdecodern des Typs D&H DHL100.
 *
 * @author dw
 */
@Model
@ConversationScoped
public class BausteinProgrammierungModel_DHL100 extends BausteinProgrammierungModel_XXX<DHL100ConfigurationAdapter, DHL100RuntimeService>
{
  public BausteinProgrammierungModel_DHL100()
  {
    super(DHL100ConfigurationAdapter.class, DHL100RuntimeService.class);
  }
}
