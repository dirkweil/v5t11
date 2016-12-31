package de.gedoplan.v5t11.betriebssteuerung.background;

import de.gedoplan.v5t11.betriebssteuerung.event.EventFirer;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Singleton
@Startup
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class InitService {
  @Inject
  Instance<InitializableService> initializableServices;

  private static final Log LOG = LogFactory.getLog(InitService.class);

  @Inject
  BeanManager beanManager;

  @PostConstruct
  private void init() {
    if (!System.getProperty("v5t11.portName", "none").equalsIgnoreCase("none")) {
      for (InitializableService initializable : this.initializableServices) {
        if (LOG.isDebugEnabled()) {
          LOG.debug("Initialisierung von " + initializable.getClass());
        }

        initializable.init();
      }
    }

    EventFirer.setBeanManager(this.beanManager);
  }
}
