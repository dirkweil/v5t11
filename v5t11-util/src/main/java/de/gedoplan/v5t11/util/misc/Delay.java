package de.gedoplan.v5t11.util.misc;

public class Delay {

  public static void delay(long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      // ignore
    }
  }

}
