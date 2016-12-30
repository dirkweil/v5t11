package de.gedoplan.v5t11.betriebssteuerung.model;

import de.gedoplan.v5t11.betriebssteuerung.service.lokdecoder.sxlokdecoder.SxLokdecoderConfigurationAdapter;
import de.gedoplan.v5t11.betriebssteuerung.service.lokdecoder.sxlokdecoder.SxLokdecoderRuntimeService;

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
public class BausteinProgrammierungModel_SxLokdecoder extends BausteinProgrammierungModel_XXX<SxLokdecoderConfigurationAdapter, SxLokdecoderRuntimeService> {
  public BausteinProgrammierungModel_SxLokdecoder() {
    super(SxLokdecoderConfigurationAdapter.class, SxLokdecoderRuntimeService.class);
  }
}
