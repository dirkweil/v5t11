package de.gedoplan.v5t11.leitstand.webui;

import de.gedoplan.v5t11.leitstand.entity.Leitstand;
import de.gedoplan.v5t11.leitstand.entity.stellwerk.Stellwerk;
import lombok.Getter;
import org.jboss.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import java.io.Serializable;

@SessionScoped
public class StellwerkSessionHolder implements Serializable {

  @Inject
  Leitstand leitstand;

  @Inject
  Logger logger;

  @Getter
  Stellwerk stellwerk;

  @PostConstruct
  void postConstruct() {
    this.stellwerk = this.leitstand.getStellwerke().first();
  }

  public void changeBereich(String bereich) {
    if (bereich == null) {
      return;
    }

    if (this.stellwerk != null && this.stellwerk.getBereich().equals(bereich)) {
      return;
    }

    Stellwerk stellwerk = this.leitstand.getStellwerk(bereich);
    if (stellwerk == null) {
      return;
    }

    this.stellwerk = stellwerk;

    this.logger.debugf("Neuer Bereich: %s", this.stellwerk.getBereich());
  }
}
