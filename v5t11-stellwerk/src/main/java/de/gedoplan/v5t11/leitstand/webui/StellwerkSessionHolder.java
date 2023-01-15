package de.gedoplan.v5t11.leitstand.webui;

import de.gedoplan.v5t11.leitstand.entity.Leitstand;
import de.gedoplan.v5t11.leitstand.entity.stellwerk.Stellwerk;
import de.gedoplan.v5t11.leitstand.entity.stellwerk.StellwerkElement;
import lombok.Getter;
import org.jboss.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.Map;
import java.util.stream.Collectors;

@SessionScoped
public class StellwerkSessionHolder implements Serializable {

  @Inject
  Leitstand leitstand;

  @Inject
  Logger logger;

  @Getter
  Stellwerk stellwerk;

  private Map<String, StellwerkElement> uiId2StellwerkelementMap = Map.of();

  @PostConstruct
  void postConstruct() {
    this.stellwerk = this.leitstand.getStellwerke().first();
  }

  public void changeBereich(String bereich) {

    if (bereich == null) {
      return;
    }

    //    if (this.stellwerk != null && this.stellwerk.getBereich().equals(bereich)) {
    //      return;
    //    }

    Stellwerk stellwerk = this.leitstand.getStellwerk(bereich);
    if (stellwerk == null) {
      return;
    }

    this.stellwerk = stellwerk;

    this.logger.debugf("Bereich: %s", this.stellwerk.getBereich());

    this.uiId2StellwerkelementMap =
      stellwerk
        .getZeilen()
        .stream()
        .flatMap(x -> x.getElemente().stream())
        .collect(Collectors.toMap(x -> x.getUiId(), x -> x));
  }

  public StellwerkElement getElementByUiUd(String uiId) {
    return this.uiId2StellwerkelementMap.get(uiId);
  }
}
