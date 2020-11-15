package de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse;

import de.gedoplan.v5t11.util.domain.entity.Bereichselement;
import de.gedoplan.v5t11.util.jsonb.JsonbInclude;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@Entity
@Table(name = Fahrstrasse.TABLE_NAME)
@Access(AccessType.FIELD)
public class Fahrstrasse extends Bereichselement {

  public static final String TABLE_NAME = "FS_FAHRSTRASSE";

  /**
   * In Zählrichtung orientiert?
   * Dieses Attribut dient nur als Default für die zugehörigen Fahrstrassenelemente.
   */
  @XmlAttribute
  @Getter(onMethod_ = @JsonbInclude(full = true))
  private boolean zaehlrichtung;

  /**
   * Kann diese Fahrstrasse umgekehrt genutzt werden?
   */
  @XmlAttribute
  @Getter
  private boolean umkehrbar;

  /**
   * Aus anderen Fahrstrassen kombiniert?
   */
  @Getter
  private boolean combi;

  /**
   * Ranking (für Auswahl aus Alternativen).
   */
  @Getter(onMethod_ = @JsonbInclude(full = true))
  private int rank;

  /**
   * Liste der Fahrstrassenelemente. Beginnt und endet immer mit einem Gleisabschnitt.
   */
  @OneToMany
  @JoinColumn(name = "FAHRSTRASSE_BEREICH", referencedColumnName = "BEREICH")
  @JoinColumn(name = "FAHRSTRASSE_NAME", referencedColumnName = "NAME")
  private List<Fahrstrassenelement> fahrstrassenelemente = new ArrayList<>();
}
