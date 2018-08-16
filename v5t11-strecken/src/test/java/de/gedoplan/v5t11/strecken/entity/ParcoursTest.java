package de.gedoplan.v5t11.strecken.entity;

import de.gedoplan.v5t11.strecken.CdiTestBase;

import javax.inject.Inject;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ParcoursTest extends CdiTestBase {

  @Inject
  Parcours parcours;

  /**
   * Test: Kann der Parcours produziert (= aus XML eingelesen) werden?
   */
  @Test
  public void test_01_produce() {
    System.out.println(this.parcours);
    this.parcours.getGleisabschnitte().forEach(g -> System.out.printf("%s isBesetzt=%b\n", g, g.isBesetzt()));
    this.parcours.getSignale().forEach(s -> System.out.printf("%s stellung=%s, erlaubteStellungen=%s, typ=%s\n", s, s.getStellung(), s.getErlaubteStellungen(), s.getTyp()));
    this.parcours.getWeichen().forEach(w -> System.out.printf("%s stellung=%s\n", w, w.getStellung()));
  }

}
