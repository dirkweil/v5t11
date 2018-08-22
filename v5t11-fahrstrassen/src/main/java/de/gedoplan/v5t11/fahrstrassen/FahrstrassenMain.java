package de.gedoplan.v5t11.fahrstrassen;

import java.util.Scanner;

import org.apache.meecrowave.Meecrowave;

public class FahrstrassenMain {

  @SuppressWarnings("resource")
  public static void main(String[] args) {
    try (Meecrowave meecrowave = new Meecrowave().bake()) {
      // Wait for \n in stdin before terminating
      new Scanner(System.in).nextLine();
    }

  }

}
