package de.gedoplan.v5t11.fahrstrassen.entity;

import de.gedoplan.v5t11.util.test.V5t11TestConfigDirExtension;

import javax.inject.Inject;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;

import io.quarkus.test.junit.QuarkusTestExtension;

@ExtendWith({ V5t11TestConfigDirExtension.class, QuarkusTestExtension.class })
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
public class ParcoursTest {

  @Inject
  Parcours parcours;

  /**
   * Test: Kann der Parcours produziert (= aus XML eingelesen) werden?
   */
  @Test
  public void test_01_produce() {
    System.out.println(this.parcours);
    this.parcours.getGleisabschnitte().forEach(g -> System.out.printf("%s isBesetzt=%b\n", g, g.isBesetzt()));
    this.parcours.getSignale().forEach(System.out::println);
    this.parcours.getWeichen().forEach(System.out::println);
    this.parcours.getAutoFahrstrassen().forEach(System.out::println);
  }

}
