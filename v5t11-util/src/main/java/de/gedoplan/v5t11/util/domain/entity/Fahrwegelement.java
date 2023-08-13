package de.gedoplan.v5t11.util.domain.entity;

import de.gedoplan.v5t11.util.domain.attribute.BereichselementId;
import de.gedoplan.v5t11.util.jsonb.JsonbInclude;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Element eines Fahrwegs.
 * <p>
 * Ein solches Element kann ein Gerät sein (Signal, Weiche etc.) oder ein Gleis.
 *
 * @author dw
 */
@XmlAccessorType(XmlAccessType.NONE)
@MappedSuperclass
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Fahrwegelement extends Bereichselement {

  @Getter(onMethod_ = @JsonbInclude)
  @Setter(onMethod_ = @JsonbInclude)
  @Column(name = "LAST_CHANGE_MS")
  protected long lastChangeMillis;

  protected Fahrwegelement(String bereich, String name) {
    super(bereich, name);
  }

  protected Fahrwegelement(BereichselementId id) {
    super(id);
  }

  /**
   * Status kopieren.
   * <p>
   * Diese Methode muss von den abgeleiteten Klassen implementiert werden, wenn der Status aus einem anderen Objekt
   * übernommen werden können soll. Dies wird bei Objekten benötigt, die zwische den Teilservices transportiert
   * werden.
   *
   * @param other Quellobjekt
   * @return <code>true</code>, falls Status verändert wurde
   */
  public boolean copyStatus(Fahrwegelement other) {
    return false;
  }
}
