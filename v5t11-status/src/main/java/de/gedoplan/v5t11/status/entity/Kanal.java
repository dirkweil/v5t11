package de.gedoplan.v5t11.status.entity;

import de.gedoplan.baselibs.persistence.entity.SingleIdEntity;
import de.gedoplan.v5t11.util.jsonb.JsonbInclude;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Table(name = Kanal.TABLE_NAME)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Kanal extends SingleIdEntity<Integer> {

  public static final String TABLE_NAME = "ST_KANAL";

  @Id
  @Getter(onMethod_ = @JsonbInclude)
  private int adresse;

  @Getter(onMethod_ = @JsonbInclude)
  private int wert;

  public Kanal(int adresse, int wert) {
    this.adresse = adresse;
    this.wert = wert & 0xff;
  }

  @Override
  public String toString() {
    return String.format("Kanal{adresse=%d, wert=0x%02x}", this.adresse, this.wert);
  }

  public static int toBusNr(int adr) {
    return adr / 1000;
  }

  public static int toLocalAdr(int adr) {
    return adr % 1000;
  }

  public static int toAdr(int busNr, int localAdr) {
    return busNr * 1000 + localAdr % 1000;
  }

  @Override
  public Integer getId() {
    return this.adresse;
  }
}
