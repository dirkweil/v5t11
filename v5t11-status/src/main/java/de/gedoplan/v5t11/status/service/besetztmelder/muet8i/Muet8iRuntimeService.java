package de.gedoplan.v5t11.status.service.besetztmelder.muet8i;

import de.gedoplan.v5t11.status.entity.BausteinConfiguration;
import de.gedoplan.v5t11.status.entity.baustein.Baustein;
import de.gedoplan.v5t11.status.entity.baustein.besetztmelder.Muet8i;
import de.gedoplan.v5t11.status.service.BausteinConfigurationService;
import de.gedoplan.v5t11.status.service.ConfigurationRuntimeService;
import de.gedoplan.v5t11.status.service.Current;
import de.gedoplan.v5t11.status.service.Programmierfamilie;
import de.gedoplan.v5t11.status.service.besetztmelder.muet8i.Muet8iConfigurationAdapter.MeldungsModus;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;

import lombok.Getter;

/**
 * Service zum Lesen und Schreiben der Konfiguration eines Besetztmelders des Typs Muet 8i.
 *
 * @author dw
 */
@ConversationScoped
@Programmierfamilie(Muet8i.class)
public class Muet8iRuntimeService extends ConfigurationRuntimeService {
  private static final int WAIT_MILLIS = 1000;

  @Getter
  private Muet8iConfigurationAdapter configuration;

  @Inject
  public Muet8iRuntimeService(@Current Baustein baustein, BausteinConfigurationService bausteinConfigurationService) {
    BausteinConfiguration bausteinSollConfiguration = bausteinConfigurationService.getBausteinConfiguration(baustein);
    BausteinConfiguration bausteinIstConfiguration = new BausteinConfiguration(baustein.getId());
    this.configuration = new Muet8iConfigurationAdapter(bausteinIstConfiguration, bausteinSollConfiguration);
  }

  protected Muet8iRuntimeService() {
  }

  @Override
  public void getRuntimeValues() {
    this.configuration.setAdresseIst(getParameter(1));

    int options = getParameter(2);
    this.configuration.getAbfallVerzoegerung().setIst(options & 0b0000_0111);
    this.configuration.getMeldungBeiZeStopp().setIst(MeldungsModus.valueOf(options & 0b0100_0000));
    this.configuration.getMeldungsNegation().setIst((options & 0b1000_0000) != 0);
  }

  @Override
  public void setRuntimeValues() {
    setParameter(1, this.configuration.getAdresseIst());

    int options = this.configuration.getAbfallVerzoegerung().getIst();
    options |= this.configuration.getMeldungBeiZeStopp().getIst().getBits();
    if (this.configuration.getMeldungsNegation().getIst()) {
      options |= 0b1000_0000;
    }
    setParameter(2, options);
  }

  private int getParameter(int parameterNummer) {
    if (this.log.isDebugEnabled()) {
      this.log.debug("getParameter: set parameterNummer=" + parameterNummer);
    }

    this.steuerung.setSX1Kanal(1, parameterNummer);

    delay(WAIT_MILLIS);

    int value = this.steuerung.getSX1Kanal(2);

    if (this.log.isDebugEnabled()) {
      this.log.debug("getParameter: get value=" + value);
    }
    return value;
  }

  private void setParameter(int parameterNummer, int newValue) {
    if (this.log.isDebugEnabled()) {
      this.log.debug("setParameter: set parameterNummer=" + parameterNummer);
    }
    this.steuerung.setSX1Kanal(1, parameterNummer);

    delay(WAIT_MILLIS);

    int oldValue = this.steuerung.getSX1Kanal(2);
    if (this.log.isDebugEnabled()) {
      this.log.debug("setParameter: get old value=" + oldValue);
    }

    if (newValue != oldValue) {
      if (this.log.isDebugEnabled()) {
        this.log.debug("setParameter: set new value=" + newValue);
      }

      this.steuerung.setSX1Kanal(2, newValue);
      delay(WAIT_MILLIS);
    }
  }

  private static void delay(long millis) {
    try {
      Thread.sleep(millis);
    }
    catch (Exception e) {
      // ignore
    }
  }

}
