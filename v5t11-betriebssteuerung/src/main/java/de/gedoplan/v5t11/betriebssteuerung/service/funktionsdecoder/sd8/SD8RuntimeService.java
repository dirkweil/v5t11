package de.gedoplan.v5t11.betriebssteuerung.service.funktionsdecoder.sd8;

import de.gedoplan.v5t11.betriebssteuerung.service.ConfigurationRuntimeService;
import de.gedoplan.v5t11.betriebssteuerung.service.funktionsdecoder.sd8.SD8ConfigurationAdapter.ServoConfiguration;

import javax.enterprise.context.ApplicationScoped;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Service zum Lesen und Schreiben der Konfiguration eines Funktionsdecoders des Typs SD 8.
 *
 * @author dw
 */
@ApplicationScoped
public class SD8RuntimeService extends ConfigurationRuntimeService<SD8ConfigurationAdapter> {
  private static final int WAIT_MILLIS = 1000;

  private static Log LOG = LogFactory.getLog(SD8RuntimeService.class);

  @Override
  public void getRuntimeValues(SD8ConfigurationAdapter configuration) {
    configuration.setAdresseIst(getParameter(0));
    configuration.clearAdresseDirty();

    for (ServoConfiguration servo : configuration.getServoConfiguration()) {
      int parameterNummer = servo.getServoNummer() * 3 - 1;
      servo.getStart().setIst(getParameter(parameterNummer));
      servo.getStart().clearDirty();
      servo.getEnde().setIst(getParameter(parameterNummer + 1));
      servo.getEnde().clearDirty();
      servo.getGeschwindigkeit().setIst(getParameter(parameterNummer + 2));
      servo.getGeschwindigkeit().clearDirty();
    }

    configuration.getAbschaltZeit().setIst(getParameter(26));
    configuration.getAbschaltZeit().clearDirty();
  }

  @Override
  public void setRuntimeValues(SD8ConfigurationAdapter configuration) {
    setRuntimeValues(configuration, 0);
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
    if (LOG.isDebugEnabled()) {
      LOG.debug("getParameter: set parameterNummer=" + parameterNummer);
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
    if (LOG.isDebugEnabled()) {
      LOG.debug("getParameter: get value=" + value);
    }
    return value;
  }

  private void setParameter(int parameterNummer, int newValue) {
    if (LOG.isDebugEnabled()) {
      LOG.debug("setParameter: set parameterNummer=" + parameterNummer);
    }
    this.selectrixGateway.setValue(1, parameterNummer);

    delay(WAIT_MILLIS);

    int oldValue = this.selectrixGateway.getValue(2, true);
    if (LOG.isDebugEnabled()) {
      LOG.debug("setParameter: get old value=" + oldValue);
    }

    if (newValue != oldValue) {
      if (LOG.isDebugEnabled()) {
        LOG.debug("setParameter: set new value=" + newValue);
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

}
