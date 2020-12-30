package de.gedoplan.v5t11.leitstand.testenvironment;

import de.gedoplan.v5t11.stellwerk.StellwerkUIStarter;

import javax.annotation.Priority;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Alternative;

@Dependent
@Alternative
@Priority(1)
public class TestStellwerkUIStarter extends StellwerkUIStarter {

  @Override
  public void start() {
  }

}
