package de.gedoplan.v5t11.fahrstrassen.entity;

import de.gedoplan.v5t11.fahrstrassen.TestBase;

import javax.inject.Inject;

import org.apache.meecrowave.junit.MonoMeecrowave;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

@RunWith(MonoMeecrowave.Runner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ParcoursTest extends TestBase {

  @Inject
  Parcours parcours;

  /**
   * Test: Kann der Parcours produziert (= aus XML eingelesen) werden?
   */
  @Test
  public void test_01_produce() {
    System.out.println(this.parcours);
    this.parcours.getGleisabschnitte().forEach(g -> System.out.printf("%s isBesetzt=%b\n", g, g.isBesetzt()));
    this.parcours.getSignale().forEach(s -> System.out.printf("%s stellung=%s\n", s, s.getStellung()));
    this.parcours.getWeichen().forEach(w -> System.out.printf("%s stellung=%s\n", w, w.getStellung()));
  }

}
