package de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug;

import de.gedoplan.v5t11.util.jsonb.JsonbInclude;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Embeddable
@Getter(onMethod_ = @JsonbInclude(full = true))
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@XmlAccessorType(XmlAccessType.FIELD)
public class FahrzeugFunktion {
  @NotNull
  @Enumerated(EnumType.STRING)
  private FahrzeugFunktionsGruppe gruppe;
  @NotEmpty
  private String beschreibung;
  @XmlAttribute
  private int maske;
  @XmlAttribute
  private int wert;
  private boolean impuls;
  private boolean horn;
  private boolean fader;

  public FahrzeugFunktion(FahrzeugFunktionsGruppe gruppe, int maske, int wert, boolean impuls, boolean horn, boolean fader, String beschreibung) {
    this.gruppe = gruppe;
    this.impuls = impuls;
    this.horn = horn;
    this.maske = maske;
    this.wert = wert;
    this.fader = fader;
    this.beschreibung = beschreibung;
  }

  public FahrzeugFunktion(FahrzeugFunktionsGruppe gruppe, int nr, boolean impuls, boolean horn, boolean fader, String beschreibung) {
    this(gruppe, 1 << (nr - 1), 1 << (nr - 1), impuls, horn, fader, beschreibung);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + this.maske;
    result = prime * result + this.wert;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    FahrzeugFunktion other = (FahrzeugFunktion) obj;
    if (this.maske != other.maske) {
      return false;
    }
    if (this.wert != other.wert) {
      return false;
    }
    return true;
  }

  @AllArgsConstructor
  @Getter
  public static enum FahrzeugFunktionsGruppe {
    FL("Fahrlicht"),
    FG("Fahrgeräusch"),
    BG("Betriebsgeräusch"),
    BA("Bahnsteigansage"),
    AF("Andere Funktion");

    private String name;
  }
}
