package de.gedoplan.v5t11.leitstand.service;

import de.gedoplan.v5t11.leitstand.entity.Leitstand;
import de.gedoplan.v5t11.util.cdi.Created;
import de.gedoplan.v5t11.util.cdi.EventFirer;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class LeitstandProducer {

  @Produces
  @ApplicationScoped
  @Transactional(rollbackOn = Exception.class)
  Leitstand createLeitstand(ConfigService configService, EventFirer eventFirer) {

    Leitstand leitstand = configService.readXmlConfig("_leitstand.xml", Leitstand.class);
    leitstand.injectFields();
    leitstand.addPersistentEntries();

    eventFirer.fire(leitstand, Created.Literal.INSTANCE);

    return leitstand;
  }

}
