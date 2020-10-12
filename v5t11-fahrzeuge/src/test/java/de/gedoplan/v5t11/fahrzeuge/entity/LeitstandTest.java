package de.gedoplan.v5t11.fahrzeuge.entity;

import de.gedoplan.v5t11.fahrzeuge.entity.fahrweg.Fahrweg;
import de.gedoplan.v5t11.util.test.V5t11TestConfigDirExtension;

import javax.inject.Inject;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;

import io.quarkus.test.junit.QuarkusTestExtension;

@ExtendWith({ V5t11TestConfigDirExtension.class, QuarkusTestExtension.class })
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
public class LeitstandTest {

  @Inject
  Fahrweg leitstand;

  /**
   * Test: Kann der Leitstand produziert (= aus XML eingelesen) werden?
   */
  @Test
  public void test_01_produce() {
    System.out.println(this.leitstand);
  }

}
