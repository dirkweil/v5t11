package de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse;

import de.gedoplan.baselibs.utils.exception.BugException;
import de.gedoplan.baselibs.utils.inject.InjectionUtil;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.ReservierbaresFahrwegelement;
import de.gedoplan.v5t11.util.domain.attribute.BereichselementId;
import de.gedoplan.v5t11.util.domain.attribute.FahrstrassenelementTyp;
import de.gedoplan.v5t11.util.domain.entity.Bereichselement;
import de.gedoplan.v5t11.util.jsonb.JsonbInclude;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import lombok.NoArgsConstructor;

@XmlAccessorType(XmlAccessType.NONE)
@NoArgsConstructor
public abstract class Fahrstrassenelement extends Bereichselement implements Cloneable {

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
   * Fahrwegelement anlegen.
   */
  public abstract void createFahrwegelement();

  /**
   * Rang für Anordnung von Fahrstrassenvorschlägen liefern.
   *
   * @return Rang der Fahrstrasse (kleiner = besser passend)
   */
  public int getRank() {
    return 0;
  }

  /**
   * Element für Fahrstrasse reservieren bzw. freigeben.
   *
   * @param reserviertefahrstrasseId <code>null</code> zum Freigeben, sonst Id der Fahrstrasse
   */
  public void reservieren(BereichselementId reserviertefahrstrasseId) {
    if (!isSchutz()) {
      getFahrwegelement().setReserviertefahrstrasseId(reserviertefahrstrasseId);
    }
  }

  public abstract FahrstrassenelementTyp getTyp();

  /**
   * Kurz-Kennung für Element liefern.
   * Wird nur für Logging/Debugging benötigt.
   * 
   * @return Code
   */
  public String getCode() {
    StringBuilder b = new StringBuilder();
    b.append(getTyp().getCode());
    b.append(isZaehlrichtung() ? '+' : '-');
    b.append(getName());
    String code = b.toString();
    return isSchutz() ? code.toLowerCase() + "~" : code;
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

  public void injectFields() {
    InjectionUtil.injectFields(this);
  }

}
