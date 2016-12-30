package de.gedoplan.v5t11.betriebssteuerung.model;

import de.gedoplan.v5t11.betriebssteuerung.service.lokdecoder.dhlokdecoder.DHLokdecoderConfigurationAdapter;
import de.gedoplan.v5t11.betriebssteuerung.service.lokdecoder.dhlokdecoder.DHLokdecoderRuntimeService;

import javax.enterprise.context.ConversationScoped;
import javax.enterprise.inject.Model;

/**
 * Presentation Model für die Programmierung von Funktionsdecodern des Typs D&H DHL100.
 *
 * @author dw
 */
@Model
@ConversationScoped
public class BausteinProgrammierungModel_DHLokdecoder extends BausteinProgrammierungModel_XXX<DHLokdecoderConfigurationAdapter, DHLokdecoderRuntimeService>
{
  public BausteinProgrammierungModel_DHLokdecoder()
  {
    super(DHLokdecoderConfigurationAdapter.class, DHLokdecoderRuntimeService.class);
  }
}
