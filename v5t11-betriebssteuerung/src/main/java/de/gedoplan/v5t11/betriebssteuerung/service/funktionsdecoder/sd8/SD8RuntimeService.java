package de.gedoplan.v5t11.betriebssteuerung.service.funktionsdecoder.sd8;

import de.gedoplan.v5t11.betriebssteuerung.entity.BausteinConfiguration;
import de.gedoplan.v5t11.betriebssteuerung.service.BausteinConfigurationService;
import de.gedoplan.v5t11.betriebssteuerung.service.ConfigurationRuntimeService;
import de.gedoplan.v5t11.betriebssteuerung.service.Current;
import de.gedoplan.v5t11.betriebssteuerung.service.Programmierfamilie;
import de.gedoplan.v5t11.betriebssteuerung.service.funktionsdecoder.sd8.SD8ConfigurationAdapter.ServoConfiguration;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.Baustein;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.funktionsdecoder.SD8;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;

import lombok.Getter;

/**
 * Service zum Lesen und Schreiben der Konfiguration eines Funktionsdecoders des Typs SD 8.
 *
 * @author dw
 */
@ConversationScoped
@Programmierfamilie(SD8.class)
public class SD8RuntimeService extends ConfigurationRuntimeService {
  // TODO Kann das verkürzt werden?
  private static final int WAIT_MILLIS = 1000;

  @Getter
  private SD8ConfigurationAdapter configuration;

  @Inject
  public SD8RuntimeService(@Current Baustein baustein, BausteinConfigurationService bausteinConfigurationService) {
    BausteinConfiguration bausteinSollConfiguration = bausteinConfigurationService.getBausteinConfiguration(baustein);
    BausteinConfiguration bausteinIstConfiguration = new BausteinConfiguration(baustein.getId());
    this.configuration = new SD8ConfigurationAdapter(bausteinIstConfiguration, bausteinSollConfiguration);
  }

  @Override
  public void getRuntimeValues() {
    this.configuration.setAdresseIst(getParameter(0));
    this.configuration.clearAdresseDirty();

    for (ServoConfiguration servo : this.configuration.getServoConfiguration()) {
      int parameterNummer = servo.getServoNummer() * 3 - 1;
      servo.getStart().setIst(getParameter(parameterNummer));
      servo.getStart().clearDirty();
      servo.getEnde().setIst(getParameter(parameterNummer + 1));
      servo.getEnde().clearDirty();
      servo.getGeschwindigkeit().setIst(getParameter(parameterNummer + 2));
      servo.getGeschwindigkeit().clearDirty();
    }

    this.configuration.getAbschaltZeit().setIst(getParameter(26));
    this.configuration.getAbschaltZeit().clearDirty();
  }

  @Override
  public void setRuntimeValues() {
    setRuntimeValues(this.configuration, 0);
  }

  public void setRuntimeValues(SD8ConfigurationAdapter configuration, int servoNummer) {
    if (servoNummer == 0) {
      setParameter(0, configuration.getAdresseIst());
      setParameter(1, 255);
      configuration.clearAdresseDirty();
    }

    for (ServoConfiguration servo : configuration.getServoConfiguration()) {
      if (servoNummer == 0 || servoNummer == servo.getServoNummer()) {
        int parameterNummer = servo.getServoNummer() * 3 - 1;
        if (servo.getStart().isDirty()) {
          setParameter(parameterNummer, servo.getStart().getIst());
          servo.getStart().clearDirty();
        }
        if (servo.getEnde().isDirty()) {
          setParameter(parameterNummer + 1, servo.getEnde().getIst());
          servo.getEnde().clearDirty();
        }
        if (servo.getGeschwindigkeit().isDirty()) {
          setParameter(parameterNummer + 2, servo.getGeschwindigkeit().getIst());
          servo.getGeschwindigkeit().clearDirty();
        }
      }
    }

    if (configuration.getAbschaltZeit().isDirty()) {
      setParameter(26, configuration.getAbschaltZeit().getIst());
      configuration.getAbschaltZeit().clearDirty();
    }
  }

  private int getParameter(int parameterNummer) {
    if (this.log.isDebugEnabled()) {
      this.log.debug("getParameter: set parameterNummer=" + parameterNummer);
    }
    int oldValue = this.selectrixGateway.getValue(2, true);
    this.selectrixGateway.setValue(1, parameterNummer);

    int value = 0;
    for (int i = 0; i < 10; ++i) {
      delay(WAIT_MILLIS / 10);

      value = this.selectrixGateway.getValue(2, true);
      if (value != oldValue) {
        break;
      }
    }
    if (this.log.isDebugEnabled()) {
      this.log.debug("getParameter: get value=" + value);
    }
    return value;
  }

  private void setParameter(int parameterNummer, int newValue) {
    if (this.log.isDebugEnabled()) {
      this.log.debug("setParameter: set parameterNummer=" + parameterNummer);
    }
    this.selectrixGateway.setValue(1, parameterNummer);

    delay(WAIT_MILLIS);

    int oldValue = this.selectrixGateway.getValue(2, true);
    if (this.log.isDebugEnabled()) {
      this.log.debug("setParameter: get old value=" + oldValue);
    }

    if (newValue != oldValue) {
      if (this.log.isDebugEnabled()) {
        this.log.debug("setParameter: set new value=" + newValue);
      }

      this.selectrixGateway.setValue(2, newValue);
      delay(WAIT_MILLIS);
    }
  }

  private static void delay(long millis) {
    try {
      Thread.sleep(millis);
    } catch (Exception e) {
      // ignore
    }
  }

  public void setServostellung(int servoNummer, boolean ende) {
    int mask = 1 << (servoNummer - 1);

    int value = this.selectrixGateway.getValue(0);
    if (ende) {
      value |= mask;
    } else {
      value &= (~mask);
    }
    this.selectrixGateway.setValue(0, value);
  }

  // TODO Aufruf der Testmethoden aus der View funktioniert nicht
  public void testStart(ServoConfiguration servoConfiguration) {
    test(servoConfiguration, false);
  }

  public void testEnde(ServoConfiguration servoConfiguration) {
    test(servoConfiguration, true);
  }

  private void test(ServoConfiguration servoConfiguration, boolean ende) {
    setRuntimeValues(getConfiguration(), servoConfiguration.getServoNummer());

    setServostellung(servoConfiguration.getServoNummer(), ende);
  }

}
