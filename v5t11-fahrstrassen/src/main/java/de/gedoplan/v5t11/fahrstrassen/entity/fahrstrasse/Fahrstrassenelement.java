package de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse;

import de.gedoplan.baselibs.persistence.entity.UuidEntity;
import de.gedoplan.baselibs.utils.exception.BugException;
import de.gedoplan.v5t11.fahrstrassen.entity.Parcours;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.ReservierbaresFahrwegelement;
import de.gedoplan.v5t11.util.jsonb.JsonbInclude;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = Fahrstrassenelement.TABLE_NAME)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@XmlAccessorType(XmlAccessType.NONE)
@NoArgsConstructor
public abstract class Fahrstrassenelement extends UuidEntity implements Cloneable {

  public static final String TABLE_NAME = "FS_ELEMENT";

  @XmlAttribute
  @Getter(onMethod_ = @JsonbInclude)
  @Setter
  private String bereich;

  @XmlAttribute
  @Getter(onMethod_ = @JsonbInclude)
  @Setter
  private String name;

  @XmlAttribute
  protected Boolean zaehlrichtung;

  public Fahrstrassenelement(String bereich, String name, boolean zaehlrichtung) {
    this.bereich = bereich;
    this.name = name;
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
   * Benötigte Stellung.
   * 
   * @return Bei Geräten, die gestellt werden, die benötigte Stellung, sonst <code>null</code>
   */
  public Enum<?> getStellung() {
    return null;
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
   *        Parcours
   */
  public abstract void linkFahrwegelement(Parcours parcours);

  /**
   * Element für Fahrstrasse reservieren bzw. freigeben.
   *
   * @param fahrstrasse
   *        <code>null</code> zum Freigeben, sonst Fahrstrasse
   */
  public abstract void reservieren(Fahrstrasse fahrstrasse);

  public abstract String getTyp();

  public boolean isSame(Fahrstrassenelement other) {
    return getClass().equals(other.getClass()) && this.bereich.equals(other.bereich) && this.name.equals(other.name);
  }

  /**
   * Kopie erzeugen.
   *
   * Es wird eine Kopie des Elementes mit neuer ID erzeugt.
   *
   * @return umgekehrtes Element
   */
  public Fahrstrassenelement createKopie() {

    try {
      return (Fahrstrassenelement) this.clone();
    } catch (CloneNotSupportedException e) {
      throw new BugException(e);
    }
  }

  /**
   * Umgekehrtes Element erzeugen.
   *
   * Wie {@link #createKopie()}, aber mit umgekehrter Zählrichtung
   *
   * @return umgekehrtes Element
   */
  public Fahrstrassenelement createUmkehrung() {

    Fahrstrassenelement fahrstrassenelement = createKopie();
    fahrstrassenelement.zaehlrichtung = !fahrstrassenelement.isZaehlrichtung();
    return fahrstrassenelement;
  }

  @Override
  protected Object clone() throws CloneNotSupportedException {

    Fahrstrassenelement fahrstrassenelement = (Fahrstrassenelement) super.clone();
    // TODO Die UUID-Erzeugung sollte als Hilfsmethode in UUIDEntity sein
    fahrstrassenelement.id = UUID.randomUUID().toString();
    return fahrstrassenelement;
  }

  @Override
  public String toString() {
    return "Fahrstrassenelement{bereich=" + this.bereich + ", name=" + this.name + ", zaehlrichtung=" + this.zaehlrichtung + ", schutz=" + isSchutz() + "}";
  }

}
