package de.gedoplan.v5t11.strecken.entity.strecke;

import de.gedoplan.v5t11.strecken.entity.Bereichselement;
import de.gedoplan.v5t11.strecken.entity.Parcours;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class Strecke extends Bereichselement {
  /**
   * In Zählrichtung orientiert?
   * Dieses Attribut dient nur als Default für die zugehörigen Streckenelemente.
   */
  @XmlAttribute
  @Getter
  private boolean zaehlrichtung;

  /**
   * Liste der Fahrstrassenelemente. Beginnt und endet immer mit einem Gleisabschnitt.
   */
  @XmlElements({
      @XmlElement(name = "Gleisabschnitt", type = StreckenGleisabschnitt.class),
      @XmlElement(name = "Signal", type = StreckenSignal.class),
      @XmlElement(name = "Weiche", type = StreckenWeiche.class) })
  @Getter
  private List<Streckenelement> elemente = new ArrayList<>();

  @Getter
  private StreckenGleisabschnitt start;

  @Getter
  private StreckenGleisabschnitt ende;

  /**
   * Nachbearbeitung nach JAXB-Unmarshal.
   *
   * @param unmarshaller
   *          Unmarshaller
   * @param parent
   *          Parent
   */
  @SuppressWarnings("unused")
  private void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
    if (!(parent instanceof Parcours)) {
      throw new IllegalArgumentException("Parent von Strecke muss Parcours sein");
    }

    Parcours parcours = (Parcours) parent;

    // Fuer alle Elemente Defaults übernehmen wenn nötig
    this.elemente.forEach(element -> {
      if (element.getBereich() == null) {
        element.setBereich(this.bereich);
      }

      if (element.zaehlrichtung == null) {
        element.zaehlrichtung = this.zaehlrichtung;
      }
    });

    // Zu Weichen die entsprechenden Gleisabschitte ergänzen
    ListIterator<Streckenelement> iterator = this.elemente.listIterator();
    while (iterator.hasNext()) {
      Streckenelement streckenelement = iterator.next();
      if (streckenelement instanceof StreckenWeiche) {
        StreckenGleisabschnitt streckenGleisabschnitt = ((StreckenWeiche) streckenelement).createStreckenGleisabschnitt();
        if (!this.elemente.contains(streckenGleisabschnitt)) {
          iterator.add(streckenGleisabschnitt);
        }
      }
    }

    // Für alle Elemente die zugehörigen Fahrwegelemente erzeugen bzw. zuordnen
    this.elemente.forEach(element -> element.linkFahrwegelement(parcours));
  }
}
