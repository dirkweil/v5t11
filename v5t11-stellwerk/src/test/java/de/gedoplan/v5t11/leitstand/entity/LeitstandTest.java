package de.gedoplan.v5t11.leitstand.entity;

import de.gedoplan.v5t11.leitstand.testenvironment.profile.V5T11Test;

import jakarta.inject.Inject;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import org.junit.jupiter.api.Test;

@QuarkusTest
@TestProfile(V5T11Test.class)
public class LeitstandTest {

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
