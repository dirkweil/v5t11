package de.gedoplan.v5t11.leitstand.entity;

import de.gedoplan.v5t11.leitstand.TestBase;

import javax.inject.Inject;

import org.apache.meecrowave.junit.MonoMeecrowave;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

@RunWith(MonoMeecrowave.Runner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LeitstandTest extends TestBase {

  @Inject
  Leitstand leitstand;

  /**
   * Test: Kann der Leitstand produziert (= aus XML eingelesen) werden?
   */
  @Test
  public void test_01_produce() {
    System.out.println(this.leitstand);
  }

}
