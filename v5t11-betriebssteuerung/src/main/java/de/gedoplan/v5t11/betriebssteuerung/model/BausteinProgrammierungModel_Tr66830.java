package de.gedoplan.v5t11.betriebssteuerung.model;

import de.gedoplan.v5t11.betriebssteuerung.service.lokdecoder.tr66830.Tr66830ConfigurationAdapter;
import de.gedoplan.v5t11.betriebssteuerung.service.lokdecoder.tr66830.Tr66830RuntimeService;

import javax.enterprise.context.ConversationScoped;
import javax.enterprise.inject.Model;

/**
 * Presentation Model für die Programmierung von Funktionsdecodern des Typs Trix
 * 66830.
 *
 * @author dw
 */
@Model
@ConversationScoped
public class BausteinProgrammierungModel_Tr66830 extends BausteinProgrammierungModel_XXX<Tr66830ConfigurationAdapter, Tr66830RuntimeService> {
  public BausteinProgrammierungModel_Tr66830() {
    super(Tr66830ConfigurationAdapter.class, Tr66830RuntimeService.class);
  }
}
