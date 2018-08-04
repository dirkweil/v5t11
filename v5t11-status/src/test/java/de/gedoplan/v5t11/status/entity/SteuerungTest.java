package de.gedoplan.v5t11.status.entity;

import de.gedoplan.v5t11.status.CdiTestBase;

import javax.inject.Inject;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SteuerungTest extends CdiTestBase {

  @Inject
  Steuerung steuerung;

  @Test
  public void test_01_produce() throws Exception {
    System.out.println(this.steuerung);
  }

}
