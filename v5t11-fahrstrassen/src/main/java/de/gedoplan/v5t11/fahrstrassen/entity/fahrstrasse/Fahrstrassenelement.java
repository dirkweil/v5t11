package de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse;

import de.gedoplan.baselibs.utils.exception.BugException;
import de.gedoplan.v5t11.fahrstrassen.entity.Parcours;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.ReservierbaresFahrwegelement;
import de.gedoplan.v5t11.util.domain.entity.Bereichselement;
import de.gedoplan.v5t11.util.jsonb.JsonbInclude;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import lombok.NoArgsConstructor;

@Entity
@Table(name = Fahrstrassenelement.TABLE_NAME)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@XmlAccessorType(XmlAccessType.NONE)
@NoArgsConstructor
public abstract class Fahrstrassenelement extends Bereichselement implements Cloneable {

  public static final String TABLE_NAME = "FS_ELEMENT";

  @XmlAttribute
  protected Boolean zaehlrichtung;

  public Fahrstrassenelement(String bereich, String name, boolean zaehlrichtung) {
    super(bereich, name);
    this.zaehlrichtung = zaehlrichtung;
  }

  /**
   * In Zählrichtung orientiert?
   *
   * @return <code>true</code>, wenn in Zählrichtung
   */
  @JsonbInclude(full = true)
  public boolean isZaehlrichtung() {
    return this.zaehlrichtung != null ? this.zaehlrichtung : false;
  }

  /**
   * Schutzfunktion?
   * Siehe {@link FahrstrassenGeraet#schutz}.
   *
   * @return <code>true</code>, wenn Schutzfunktion
   */
  public boolean isSchutz() {
    return false;
  }

  /**
   * Ist dies ein Hauptsignal?
   *
   * @return <code>true</code>, wenn Hauptsignal
   */
  public boolean isHauptsignal() {
    return false;
  }

  /**
   * Ist dies ein Vorsignal?
   *
   * @return <code>true</code>, wenn Vorsignal
   */
  public boolean isVorsignal() {
    return false;
  }

  /**
   * Ist dies ein Sperrsignal?
   *
   * @return <code>true</code>, wenn Vorsignal
   */
  public boolean isSperrsignal() {
    return false;
  }

  /**
   * Ist dies ein Blocksignal?
   *
   * @return <code>true</code>, wenn Blocksignal
   */
  public boolean isBlocksignal() {
    return false;
  }

  /**
   * Zugehöriges Fahrwegelement liefern.
   *
   * @return Fahrwegelement
   */
  public abstract ReservierbaresFahrwegelement getFahrwegelement();

  /**
   * Rang für Anordnung von Fahrstrassenvorschlägen liefern.
   *
   * @return Rang der Fahrstrasse (kleiner = besser passend)
   */
  public int getRank() {
    return 0;
  }

  /**
   * Zugehöriges Fahrwegelement suchen und eintragen.
   *
   * @param parcours
   *          Parcours
   */
  public abstract void linkFahrwegelement(Parcours parcours);

  @Override
  public String toString() {
    return getFahrwegelement() + ", zaehlrichtung=" + this.zaehlrichtung;
  }

  /**
   * Element für Fahrstrasse reservieren bzw. freigeben.
   *
   * @param fahrstrasse
   *          <code>null</code> zum Freigeben, sonst Fahrstrasse
   */
  public abstract void reservieren(Fahrstrasse fahrstrasse);

  public abstract String getTyp();

  /**
   * Umgekehrtes Element erzeugen.
   *
   * Es wird eine Kopie des Elemtes erzeugt und die gegenteilige Richtung eingetragen.
   *
   * @return umgekehrtes Element
   */
  public Fahrstrassenelement createUmkehrung() {

    try {
      Fahrstrassenelement fahrstrassenelement = (Fahrstrassenelement) this.clone();

      fahrstrassenelement.zaehlrichtung = !fahrstrassenelement.isZaehlrichtung();

      return fahrstrassenelement;
    } catch (CloneNotSupportedException e) {
      throw new BugException(e);
    }
  }

}
