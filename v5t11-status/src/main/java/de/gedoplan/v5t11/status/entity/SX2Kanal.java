package de.gedoplan.v5t11.status.entity;

import de.gedoplan.v5t11.util.domain.attribute.SystemTyp;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Getter;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@AllArgsConstructor
public class SX2Kanal {

  private int index;

  private SystemTyp systemTyp;

  private int adresse;

  private boolean licht;

  private boolean rueckwaerts;

  private int fahrstufe;

  private int funktionStatus;

  // public SX2Kanal(int index, SystemTyp systemTyp, byte adrHigh, byte adrLowLicht, byte rueckwaertsFahrstufe, byte funktion1_8, byte funktion9_16) {
  // this.index = index;
  // this.systemTyp = systemTyp;
  // if (this.systemTyp != SystemTyp.SX1) {
  // this.adresse = decodeAdresse(this.systemTyp, adrHigh, adrLowLicht);
  // this.licht = (adrLowLicht & 0b0000_0010) != 0;
  // this.rueckwaerts = (rueckwaertsFahrstufe & 0b1000_0000) != 0;
  // this.fahrstufe = decodeFahrstufe(this.systemTyp, rueckwaertsFahrstufe);
  // this.funktionStatus = Byte.toUnsignedInt(funktion9_16) << 8 | Byte.toUnsignedInt(funktion1_8);
  // }
  // }
  //
  // public static int decodeAdresse(SystemTyp systemTyp, byte adrHigh, byte adrLow) {
  // if (systemTyp != null) {
  // int adr = Byte.toUnsignedInt(adrHigh) << 6 | (Byte.toUnsignedInt(adrLow) & 0b1111_1100) >>> 2;
  // // System.out.printf("adrHigh: 0x%02x %s\n", adrHigh, toBinary(adrHigh, 8));
  // // System.out.printf("adrLow: 0x%02x %s\n", adrHigh, toBinary(adrLow, 8));
  // // System.out.printf("adr: 0x%04x %s\n", adr, toBinary(adr, 16));
  // switch (systemTyp) {
  // case SX2:
  // return ((adr & 0b0011_1111_1000_0000) >>> 7) * 100 + (adr & 0b0011_1111);
  //
  // case DCC_L_126:
  // return adr;
  //
  // default:
  // break;
  // }
  // }
  // throw new IllegalArgumentException("Ungültiger Systemtyp: " + systemTyp);
  // }
  //
  // public static byte[] encodeAdresse(SystemTyp systemTyp, int adresse) {
  // if (systemTyp != null) {
  // switch (systemTyp) {
  // case SX2:
  // int hunderter = adresse / 100;
  // int einer = adresse % 100;
  // int sx2Wert = (hunderter << 9) | (einer << 2);
  // return new byte[] { (byte) (sx2Wert & 0xff), (byte) ((sx2Wert >> 8) & 0xff) };
  //
  // case DCC_L_126:
  // int dccWert = adresse << 2;
  // return new byte[] { (byte) (dccWert & 0xff), (byte) ((dccWert >> 8) & 0xff) };
  //
  // default:
  // break;
  // }
  // }
  // throw new IllegalArgumentException("Ungültiger Systemtyp: " + systemTyp);
  // }
  //
  // public static int decodeFahrstufe(SystemTyp systemTyp, byte rueckwaertsFahrstufe) {
  // if (systemTyp != null) {
  // int fahrstufe = rueckwaertsFahrstufe & 0b0111_1111;
  // switch (systemTyp) {
  // case SX1:
  // case SX2:
  // return fahrstufe;
  //
  // case DCC_L_126:
  // return fahrstufe < 2 ? 0 : fahrstufe - 1;
  //
  // default:
  // }
  // }
  //
  // throw new IllegalArgumentException("Ungültiger Systemtyp: " + systemTyp);
  // }
  //
  // public static int encodeFahrstufeUndRückwaerts(SystemTyp systemTyp, int fahrstufe, boolean rueckwaerts) {
  // if (systemTyp != null) {
  // int result = fahrstufe;
  // switch (systemTyp) {
  // case SX1:
  // case SX2:
  // break;
  //
  // case DCC_L_126:
  // if (fahrstufe > 0) {
  // result++;
  // }
  // break;
  //
  // default:
  // throw new IllegalArgumentException("Ungültiger Systemtyp: " + systemTyp);
  // }
  //
  // if (rueckwaerts) {
  // result |= 0b1000_0000;
  // }
  //
  // return result;
  // }
  //
  // throw new IllegalArgumentException("Ungültiger Systemtyp: " + systemTyp);
  // }

  // private static String toBinary(int i, int len) {
  // StringBuilder s = new StringBuilder(Integer.toBinaryString(i));
  // int fill = len - s.length();
  // while (fill-- > 0) {
  // s.insert(0, '0');
  // }
  // return s.toString();
  // }

  @Override
  public String toString() {
    return String.format("SX2Kanal [index=%d, systemTyp=%s, adresse=%d, licht=%b, rueckwaerts=%b, fahrstufe=%d, funktionStatus=0x%04x]", this.index, this.systemTyp, this.adresse, this.licht,
        this.rueckwaerts,
        this.fahrstufe,
        this.funktionStatus);
  }
}
