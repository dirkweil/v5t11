package de.gedoplan.v5t11.parcours;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;

@QuarkusMain
public class ParcoursMain {

  public static void main(String[] args) {
    try {
      Quarkus.run(args);
    } catch (Exception e) {
      Quarkus.asyncExit(1);
    }
  }
}
