package de.gedoplan.v5t11.status.entity.fahrweg.geraet;

public abstract class Hauptsignal extends Signal
{

  /**
   * Konstruktor für JAXB.
   * 
   * Dieser Konstrktor ist für JAXB nötig, sollte aber dennoch nie aufgerufen werden.
   */
  protected Hauptsignal()
  {
    super();
  }

  /**
   * Konstruktor.
   * 
   * @param bitCount Anzahl genutzter Bits
   */
  protected Hauptsignal(int bitCount)
  {
    super(bitCount);
  }
}
