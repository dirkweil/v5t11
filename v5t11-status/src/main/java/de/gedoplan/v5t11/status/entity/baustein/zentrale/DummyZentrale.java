package de.gedoplan.v5t11.status.entity.baustein.zentrale;

import de.gedoplan.v5t11.status.entity.Kanal;
import de.gedoplan.v5t11.status.entity.baustein.Zentrale;
import de.gedoplan.v5t11.util.cdi.Changed;
import de.gedoplan.v5t11.util.domain.attribute.SystemTyp;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;

import lombok.NoArgsConstructor;

/**
 * Future Central Control von D&H (oder Mini FCC von Joachim Havekost).
 *
 * @author dw
 */
@XmlAccessorType(XmlAccessType.NONE)
@NoArgsConstructor
public class DummyZentrale extends Zentrale {

  private ConcurrentMap<Integer, Byte> kanalwerte = new ConcurrentHashMap<>();

  @Override
  public String toString() {
    return "DummyZentrale{gleisspannung=" + this.gleisspannung + ", kurzschluss=" + this.kurzschluss + "}";
  }

  @Override
  protected int getPortSpeed() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void open(ExecutorService executorService) {
  }

  @Override
  public void close() {
  }

  @Override
  public void setGleisspannung(boolean gleisspannung) {
    boolean gleisspannungAlt = this.gleisspannung;
    this.gleisspannung = gleisspannung;
    if (this.gleisspannung != gleisspannungAlt) {
      this.eventFirer.fire(this, Changed.Literal.INSTANCE);
    }
  }

  @Override
  public int getSX1Kanal(int adr) {
    Byte wert = this.kanalwerte.get(adr);
    if (wert != null) {
      return wert.byteValue() & 0xff;
    }

    return 0;
  }

  @Override
  public void setSX1Kanal(int adr, int wert) {
    byte neu = (byte) wert;
    Byte alt = this.kanalwerte.put(adr, neu);
    if (alt == null || alt.byteValue() != neu) {
      this.eventFirer.fire(new Kanal(adr, neu), Changed.Literal.INSTANCE);
    }
  }

  @Override
  public void setGleisProtokoll() {
  }

  @Override
  public Map<Integer, Integer> readFahrzeugConfig(SystemTyp systemTyp, Collection<Integer> fahrzeugConfigParameterKeys) {
    return fahrzeugConfigParameterKeys
      .stream()
      .collect(Collectors.toMap(key -> key, key -> null));
  }

  @Override
  public void writeFahrzeugConfig(SystemTyp systemTyp, Map<Integer, Integer> fahrzeugConfigParameters) {
  }

}
