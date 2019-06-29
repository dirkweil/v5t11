package de.gedoplan.v5t11.status.entity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class SX2Kanal {

  @Getter
  private Format format;

  @Getter
  private int adresse;

  @Getter
  private boolean licht;

  @Getter
  private boolean rueckwaerts;

  @Getter
  private int fahrstufe;

  @Getter
  private int funktionStatus;

  public SX2Kanal(byte formatCode, byte adrHigh, byte adrLowLicht, byte rueckwaertsFahrstufe, byte funktion1_8, byte funktion9_16) {
    this.format = Format.valueOf((byte) (formatCode & 0x0f));
    this.adresse = decodeAdresse(this.format, adrHigh, adrLowLicht);
    this.licht = (adrLowLicht & 0b0000_0010) != 0;
    this.rueckwaerts = (rueckwaertsFahrstufe & 0b1000_0000) != 0;
    this.fahrstufe = decodeFahrstufe(this.format, rueckwaertsFahrstufe);
    this.funktionStatus = Byte.toUnsignedInt(funktion9_16) << 8 | Byte.toUnsignedInt(funktion1_8);

  }

  private static int decodeAdresse(Format format, byte adrHigh, byte adrLow) {
    int adr = Byte.toUnsignedInt(adrHigh) << 6 | (Byte.toUnsignedInt(adrLow) & 0b1111_1100) >>> 2;
    // System.out.printf("adrHigh: 0x%02x %s\n", adrHigh, toBinary(adrHigh, 8));
    // System.out.printf("adrLow: 0x%02x %s\n", adrHigh, toBinary(adrLow, 8));
    // System.out.printf("adr: 0x%04x %s\n", adr, toBinary(adr, 16));
    switch (format) {
    case FREI:
      return 0;

    case SX2:
      return ((adr & 0b0011_1111_1000_0000) >>> 7) * 100 + (adr & 0b0011_1111);

    case DCC:
      return adr;

    default:
      throw new IllegalArgumentException("Ungültiges Format: " + format);
    }
  }

  private static int decodeFahrstufe(Format format, byte rueckwaertsFahrstufe) {
    int fahrstufe = rueckwaertsFahrstufe & 0b0111_1111;
    switch (format) {
    case FREI:
      return 0;

    case SX2:
      return fahrstufe;

    case DCC:
      return fahrstufe < 2 ? 0 : fahrstufe - 1;

    default:
      throw new IllegalArgumentException("Ungültiges Format: " + format);
    }
  }

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
    return String.format("SX2Kanal [format=%s, adresse=%d, licht=%b, rueckwaerts=%b, fahrstufe=%d, funktionStatus=0x%04x]", this.format, this.adresse, this.licht, this.rueckwaerts, this.fahrstufe,
        this.funktionStatus);
  }

  public static enum Format {
    FREI((byte) 0x00), // frei
    SX2((byte) 0x04), // SX2
    DCC((byte) 0x07); // DCC

    @Getter
    private byte formatCode;

    private Format(byte formatCode) {
      this.formatCode = formatCode;

    }

    public static Format valueOf(byte formatCode) {
      for (Format format : Format.values()) {
        if (format.formatCode == formatCode) {
          return format;
        }
      }
      throw new IllegalArgumentException(String.format("Ungültiger Formatcode: 0x%0x", formatCode));
    }
  }
}
