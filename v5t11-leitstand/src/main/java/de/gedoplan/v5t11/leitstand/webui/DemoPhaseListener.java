package de.gedoplan.v5t11.leitstand.webui;

import org.jboss.logging.Logger;

import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

public class DemoPhaseListener implements PhaseListener {

  Logger logger = Logger.getLogger(DemoPhaseListener.class);

  @Override
  public void afterPhase(PhaseEvent phaseEvent) {
    this.logger.debugf("afterPhase %s", phaseEvent.getPhaseId());
  }

  @Override
  public void beforePhase(PhaseEvent phaseEvent) {
    this.logger.debugf("beforePhase %s", phaseEvent.getPhaseId());
  }

  @Override
  public PhaseId getPhaseId() {
    return PhaseId.RENDER_RESPONSE;
  }
}
