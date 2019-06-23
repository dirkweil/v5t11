package de.gedoplan.v5t11.status.entity.lok;

import de.gedoplan.v5t11.util.cdi.EventFirer;
import de.gedoplan.v5t11.util.jsonb.JsonbInclude;

import java.util.List;

import lombok.Getter;

public class Lok implements Comparable<Lok> {

  /**
   * Maximalwert für die Geschwindigkeit.
   */
  public static final int MAX_GESCHWINDIGKEIT = 31;

  @Getter(onMethod_ = @JsonbInclude)
  private String id;

  @Getter(onMethod_ = @JsonbInclude)
  private boolean licht;

  @Getter(onMethod_ = @JsonbInclude)
  private boolean rueckwaerts;

  @Getter(onMethod_ = @JsonbInclude)
  private int geschwindigkeit;

  // ToDo FCC
  // @Getter
  // @Setter
  // @JsonbTransient
  // private Lokdecoder lokdecoder;

  public Lok(String id) {
    this.id = id;
  }

  @Override
  public int compareTo(Lok other) {
    return this.id.compareTo(other.id);
  }

  @Override
  public String toString() {
    // ToDo FCC
    // return "Lok{" + this.id + " @ " + this.lokdecoder.getAdresse() + "}";
    return "Lok{" + this.id + "}";
  }

  public void adjustStatus() {
    boolean changed = false;

    // ToDo FCC
    // if (this.licht != this.lokdecoder.isLicht()) {
    // this.licht = this.lokdecoder.isLicht();
    // changed = true;
    // }
    //
    // if (this.rueckwaerts != this.lokdecoder.isRueckwaerts()) {
    // this.rueckwaerts = this.lokdecoder.isRueckwaerts();
    // changed = true;
    // }
    //
    // if (this.geschwindigkeit != this.lokdecoder.getGeschwindigkeit()) {
    // this.geschwindigkeit = this.lokdecoder.getGeschwindigkeit();
    // changed = true;
    // }

    if (changed) {
      EventFirer.getInstance().fire(this);
    }

  }

  public void setLicht(boolean licht) {
    if (this.licht != licht) {
      this.licht = licht;
      // ToDo FCC
      // this.lokdecoder.setLicht(licht);
      EventFirer.getInstance().fire(this);
    }
  }

  public void setRueckwaerts(boolean rueckwaerts) {
    if (this.rueckwaerts != rueckwaerts) {
      this.rueckwaerts = rueckwaerts;
      // ToDo FCC
      // this.lokdecoder.setRueckwaerts(rueckwaerts);
      EventFirer.getInstance().fire(this);
    }
  }

  public void setGeschwindigkeit(int geschwindigkeit) {
    if (this.geschwindigkeit != geschwindigkeit) {
      this.geschwindigkeit = geschwindigkeit;
      // ToDo FCC
      // this.lokdecoder.setGeschwindigkeit(geschwindigkeit);
      EventFirer.getInstance().fire(this);
    }
  }

  @JsonbInclude(full = true)
  public String getLokdecoderTyp() {
    // ToDo FCC
    // return this.lokdecoder.getClass().getSimpleName();
    return null;
  }

  @JsonbInclude(full = true)
  public List<Integer> getLokdecoderAdressen() {
    // ToDo FCC
    // return this.lokdecoder.getAdressen();
    return null;
  }

}
