package de.gedoplan.v5t11.fahrzeuge.entity.fahrweg;

import de.gedoplan.v5t11.fahrzeuge.entity.baustein.Zentrale;
import de.gedoplan.v5t11.util.domain.entity.Bereichselement;

import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.BiFunction;

import javax.enterprise.context.ApplicationScoped;

import lombok.Getter;

/**
 * Cache der Gleisbelegungen und Weichenstellungen.
 *
 * @author dw
 */
@ApplicationScoped
public class Fahrweg {

  @Getter
  private SortedSet<String> bereiche = new TreeSet<>();

  @Getter
  private SortedSet<Gleis> gleise = new TreeSet<>();

  @Getter
  private Zentrale zentrale = new Zentrale();

  @Getter
  private SortedSet<Weiche> weichen = new TreeSet<>();

  /**
   * Gleis liefern.
   *
   * @param bereich
   *          Bereich
   * @param name
   *          Name
   * @return gefundener Gleis oder <code>null</code>
   */
  public Gleis getGleis(String bereich, String name) {
    return getBereichselement(bereich, name, this.gleise);
  }

  /**
   * Gleis liefern oder bei Bedarf neu anlegen.
   *
   * @param bereich
   *          Bereich
   * @param name
   *          Name
   * @return gefundener oder erzeugter Gleis
   */
  public Gleis getOrCreateGleis(String bereich, String name) {
    return getOrCreateBereichselement(bereich, name, this.gleise, Gleis::new);
  }

  /**
   * Weiche liefern.
   *
   * @param bereich
   *          Bereich
   * @param name
   *          Name
   * @return gefundene Weiche oder <code>null</code>
   */
  public Weiche getWeiche(String bereich, String name) {
    return getBereichselement(bereich, name, this.weichen);
  }

  /**
   * Weiche liefern oder bei Bedarf neu anlegen.
   *
   * @param bereich
   *          Bereich
   * @param name
   *          Name
   * @return gefundene oder erzeugte Weiche
   */
  public Weiche getOrCreateWeiche(String bereich, String name) {
    return getOrCreateBereichselement(bereich, name, this.weichen, Weiche::new);
  }

  private static <T extends Bereichselement> T getBereichselement(String bereich, String name, Collection<T> set) {
    for (T element : set) {
      if (element.getBereich().equals(bereich) && element.getName().equals(name)) {
        return element;
      }
    }

    return null;
  }

  private static synchronized <T extends Bereichselement> T getOrCreateBereichselement(String bereich, String name, Collection<T> set, BiFunction<String, String, T> creator) {
    T element = getBereichselement(bereich, name, set);
    if (element == null) {
      element = creator.apply(bereich, name);
      set.add(element);
    }
    return element;
  }

}
