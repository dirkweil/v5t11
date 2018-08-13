package de.gedoplan.v5t11.status.entity.baustein.lokdecoder;

/**
 * Lokdecoder Trix 66825.
 *
 * Die Lokdecoder der ersten Generation wurden durch Bleistiftmarkierungen
 * programmiert, d. h. eine Programmierung mittels Software ist nicht möglich.
 *
 * @author dw
 */
public class Tr66825 extends SxLokdecoder {

  public Tr66825() {
    super(1);
  }

  @Override
  public Class<?> getProgrammierfamilie() {
    return null;
  }
}
