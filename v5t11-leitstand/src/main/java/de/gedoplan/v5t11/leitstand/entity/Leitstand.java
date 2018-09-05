package de.gedoplan.v5t11.leitstand.entity;

import de.gedoplan.v5t11.leitstand.entity.stellwerk.Stellwerk;
import de.gedoplan.v5t11.leitstand.entity.stellwerk.StellwerkElement;
import de.gedoplan.v5t11.leitstand.entity.stellwerk.StellwerkZeile;

import java.util.SortedSet;
import java.util.TreeSet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Leitstand.
 *
 * Diese Klasse fasst alle zum Leitstand gehörenden Elemente zusammen.
 *
 * @author dw
 */
@XmlRootElement(name = "Leitstand")
@XmlAccessorType(XmlAccessType.NONE)
public class Leitstand {

  @XmlElement(name = "Stellwerk")
  private SortedSet<Stellwerk> stellwerke = new TreeSet<>();

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
}
