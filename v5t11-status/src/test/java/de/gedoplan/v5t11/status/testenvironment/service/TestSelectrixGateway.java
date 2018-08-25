package de.gedoplan.v5t11.status.testenvironment.service;

import de.gedoplan.v5t11.status.service.SelectrixGateway;

import java.util.Collection;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Specializes;

@ApplicationScoped
@Specializes
public class TestSelectrixGateway extends SelectrixGateway {

  @Override
  protected void postConstruct() {
  }

  @Override
  protected void preDestroy() {
  }

  @Override
  public void start(String serialPortName, int serialPortSpeed, String interfaceName, Collection<Integer> adressen) {
  }

  @Override
  public void stop() {
  }

  @Override
  public int getValue(int address) {
    throw new UnsupportedOperationException();
  }

  @Override
  public int getValue(int address, boolean refresh) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setValue(int address, int value) {
  }

}
