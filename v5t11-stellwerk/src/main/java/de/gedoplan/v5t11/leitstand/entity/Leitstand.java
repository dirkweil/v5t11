package de.gedoplan.v5t11.leitstand.entity;

import de.gedoplan.baselibs.utils.inject.InjectionUtil;
import de.gedoplan.v5t11.leitstand.entity.baustein.Zentrale;
import de.gedoplan.v5t11.leitstand.entity.stellwerk.Stellwerk;
import de.gedoplan.v5t11.leitstand.entity.stellwerk.StellwerkElement;
import de.gedoplan.v5t11.leitstand.entity.stellwerk.StellwerkZeile;

import java.io.Serializable;
import java.util.SortedSet;
import java.util.TreeSet;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import lombok.Getter;

/**
 * Leitstand.
 * <p>
 * Diese Klasse fasst alle zum Leitstand gehörenden Elemente zusammen.
 *
 * @author dw
 */
@XmlRootElement(name = "Leitstand")
@XmlAccessorType(XmlAccessType.NONE)
public class Leitstand implements Serializable {

  @XmlElement(name = "Stellwerk")
  @Getter
  private SortedSet<Stellwerk> stellwerke = new TreeSet<>();

  @Getter
  private SortedSet<String> bereiche = new TreeSet<>();

  @Getter
  private Zentrale zentrale = new Zentrale();

  public Stellwerk getStellwerk(String bereich) {
    for (Stellwerk stellwerk : this.stellwerke) {
      if (stellwerk.getBereich().equals(bereich)) {
        return stellwerk;
      }
    }
    return null;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder();
    for (Stellwerk stellwerk : this.stellwerke) {
      buf.append("\n").append(stellwerk);
      for (StellwerkZeile zeile : stellwerk.getZeilen()) {
        buf.append("\n  Zeile");
        for (StellwerkElement element : zeile.getElemente()) {
          buf.append("\n    " + element);
        }
      }
    }

    return buf.toString();
  }

  public void injectFields() {
    InjectionUtil.injectFields(this);
    this.stellwerke.forEach(Stellwerk::injectFields);
  }

  public void addPersistentEntries() {
    this.stellwerke.forEach(Stellwerk::addPersistentEntries);
  }

}
