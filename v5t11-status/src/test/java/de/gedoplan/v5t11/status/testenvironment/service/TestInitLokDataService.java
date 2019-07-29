package de.gedoplan.v5t11.status.testenvironment.service;

import de.gedoplan.v5t11.status.service.init.InitLokDataService;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Specializes;

@ApplicationScoped
@Specializes
public class TestInitLokDataService extends InitLokDataService {

  @Override
  protected void createDemoData(Object event) {
  }

}
