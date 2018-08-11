package de.gedoplan.v5t11.status.entity.lok;

import de.gedoplan.v5t11.status.entity.baustein.Lokdecoder;
import de.gedoplan.v5t11.status.jsonb.JsonbInclude;
import de.gedoplan.v5t11.status.util.EventFirer;

import java.util.List;

import javax.json.bind.annotation.JsonbTransient;

import lombok.Getter;
import lombok.Setter;

public class Lok implements Comparable<Lok> {

  /**
   * Maximalwert für die Geschwindigkeit.
   */
  public static final int MAX_GESCHWINDIGKEIT = 31;

  @Getter(onMethod = @__(@JsonbInclude))
  private String id;

  @Getter(onMethod = @__(@JsonbInclude))
  private boolean licht;

  @Getter(onMethod = @__(@JsonbInclude))
  private boolean rueckwaerts;

  @Getter(onMethod = @__(@JsonbInclude))
  private int geschwindigkeit;

  @Getter
  @Setter
  @JsonbTransient
  private Lokdecoder lokdecoder;

  public Lok(String id) {
    this.id = id;
  }

  @Override
  public int compareTo(Lok other) {
    return this.id.compareTo(other.id);
  }

  @Override
  public String toString() {
    return "Lok{" + this.id + " @ " + this.lokdecoder.getAdresse() + "}";
  }

  public void adjustStatus() {
    boolean changed = false;

    if (this.licht != this.lokdecoder.isLicht()) {
      this.licht = this.lokdecoder.isLicht();
      changed = true;
    }

    if (this.rueckwaerts != this.lokdecoder.isRueckwaerts()) {
      this.rueckwaerts = this.lokdecoder.isRueckwaerts();
      changed = true;
    }

    if (this.geschwindigkeit != this.lokdecoder.getGeschwindigkeit()) {
      this.geschwindigkeit = this.lokdecoder.getGeschwindigkeit();
      changed = true;
    }

    if (changed) {
      EventFirer.fire(this);
    }

  }

  public void setLicht(boolean licht) {
    if (this.licht != licht) {
      this.licht = licht;
      this.lokdecoder.setLicht(licht);
      EventFirer.fire(this);
    }
  }

  public void setRueckwaerts(boolean rueckwaerts) {
    if (this.rueckwaerts != rueckwaerts) {
      this.rueckwaerts = rueckwaerts;
      this.lokdecoder.setRueckwaerts(rueckwaerts);
      EventFirer.fire(this);
    }
  }

  public void setGeschwindigkeit(int geschwindigkeit) {
    if (this.geschwindigkeit != geschwindigkeit) {
      this.geschwindigkeit = geschwindigkeit;
      this.lokdecoder.setGeschwindigkeit(geschwindigkeit);
      EventFirer.fire(this);
    }
  }

  @JsonbInclude(full = true)
  public String getLokdecoderTyp() {
    return this.lokdecoder.getClass().getSimpleName();
  }

  @JsonbInclude(full = true)
  public List<Integer> getLokdecoderAdressen() {
    return this.lokdecoder.getAdressen();
  }

}
