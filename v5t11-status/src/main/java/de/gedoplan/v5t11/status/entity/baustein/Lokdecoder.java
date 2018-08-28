package de.gedoplan.v5t11.status.entity.baustein;

import de.gedoplan.v5t11.status.entity.lok.Lok;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Lokdecoder.
 *
 * @author dw
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Lokdecoder extends Baustein implements Decoder {

  @XmlAttribute
  @XmlJavaTypeAdapter(XmlLokAdapter.class)
  @Getter
  protected Lok lok;

  protected Lokdecoder(int byteAnzahl) {
    super(byteAnzahl);
  }

  @Override
  public String getLabelPrefix() {
    return "Lokdecoder";
  }

  @Override
  public boolean isBusBaustein() {
    return false;
  }

  public static final class XmlLokAdapter extends XmlAdapter<String, Lok> {

    @Override
    public Lok unmarshal(String lokId) throws Exception {
      return new Lok(lokId);
    }

    @Override
    public String marshal(Lok lok) throws Exception {
      return lok.getId();
    }
  }

  /**
   * Nachbearbeitung nach JAXB-Unmarshal.
   *
   * @param unmarshaller
   *          Unmarshaller
   * @param parent
   *          Parent
   */
  @Override
  public void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
    super.afterUnmarshal(unmarshaller, parent);

    if (this.lok != null) {
      this.lok.setLokdecoder(this);
    }
  }

  public abstract boolean isHorn();

  public abstract void setHorn(boolean horn);

  public abstract boolean isLicht();

  public abstract void setLicht(boolean licht);

  public abstract boolean isRueckwaerts();

  public abstract void setRueckwaerts(boolean rueckwaerts);

  public abstract int getGeschwindigkeit();

  public abstract void setGeschwindigkeit(int geschwindigkeit);
}
