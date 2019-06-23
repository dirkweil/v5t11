package de.gedoplan.v5t11.status.entity.baustein;

import de.gedoplan.v5t11.status.entity.lok.Lok;
import de.gedoplan.v5t11.util.jsonb.JsonbInclude;

import javax.json.bind.annotation.JsonbNillable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Lokcontroller.
 *
 * @author dw
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonbNillable
public abstract class Lokcontroller extends Baustein implements Encoder {

  /**
   * Zugewiesene Lok oder <code>null</code>.
   */
  @Getter
  protected Lok lok;

  protected Lokcontroller(int byteAnzahl) {
    super(byteAnzahl);
  }

  @JsonbInclude
  public String getLokId() {
    return this.lok != null ? this.lok.getId() : null;
  }

  /**
   * Lok zuweisen.
   *
   * @param lok
   *          Lok
   */
  public abstract void setLok(Lok lok);

  @Override
  public String getLabelPrefix() {
    return "Lokcontroller";
  }

  // TODO Ohne diese Methode wird die ID nicht ins JSON aufgenommen; warum nicht?
  @Override
  @JsonbInclude
  public String getId() {
    return super.getId();
  }

  @Override
  public void adjustStatus() {
  }
}
