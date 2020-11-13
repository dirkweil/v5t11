package de.gedoplan.v5t11.status.testenvironment.service;

import de.gedoplan.v5t11.status.service.init.InitLokDataService;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;

import io.quarkus.runtime.StartupEvent;

@ApplicationScoped
@Alternative
@Priority(1)
public class TestInitLokDataService extends InitLokDataService {

  @Override
  protected void createDemoData(StartupEvent event) {
  }

}
