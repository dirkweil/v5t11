package de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrstrasse;

import de.gedoplan.baselibs.utils.xml.XmlConverter;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.Steuerung;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class FahrstrassenTest
{
  private Steuerung steuerung;

  @Before
  public void before() throws Exception
  {
    this.steuerung = XmlConverter.fromXml(Steuerung.class, "DwAnlage.xml");
  }

  @Test
  public void testBereichGesetzt() throws Exception
  {
    for (Fahrstrasse fahrstrasse : this.steuerung.getFahrstrassen())
    {
      for (FahrstrassenElement element : fahrstrasse.getElemente())
      {
        Assert.assertNotNull("Bereich nicht gesetzt (" + fahrstrasse + ", " + element + ")", element.getBereich());
      }
    }
  }

  @Test
  public void testZaehlrichtungGesetzt() throws Exception
  {
    for (Fahrstrasse fahrstrasse : this.steuerung.getFahrstrassen())
    {
      for (FahrstrassenElement element : fahrstrasse.getElemente())
      {
        Assert.assertNotNull("Zaehlrichtung nicht gesetzt (" + fahrstrasse + ", " + element + ")", element.zaehlrichtung);
      }
    }
  }

  @Test
  @Ignore
  public void printFahrstrassenFensterhausen() throws Exception
  {
    printFahrstrassen("Fensterhausen", false);
  }

  @Test
  @Ignore
  public void printFahrstrassenSchattenbahnhof1() throws Exception
  {
    printFahrstrassen("Schattenbahnhof1", true);
  }

  @Test
  @Ignore
  public void printFahrstrassenSchattenbahnhof2() throws Exception
  {
    printFahrstrassen("Schattenbahnhof2", true);
  }

  @Test
  @Ignore
  public void printFahrstrassen7_36() throws Exception
  {
    Fahrstrasse fahrstrasse = this.steuerung.getFahrstrasse("Fensterhausen", "7-36");
    printFahrstrasse(fahrstrasse, true);
  }

  @Test
  @Ignore
  public void printFahrstrassen36_7() throws Exception
  {
    Fahrstrasse fahrstrasse = this.steuerung.getFahrstrasse("Fensterhausen", "36-7");
    printFahrstrasse(fahrstrasse, true);
  }

  private void printFahrstrassen(String bereich, boolean showElements) throws Exception
  {
    for (Fahrstrasse fahrstrasse : this.steuerung.getFahrstrassen())
    {
      if (bereich.equals(fahrstrasse.getBereich()))
      {
        printFahrstrasse(fahrstrasse, showElements);
      }
    }
  }

  private void printFahrstrasse(Fahrstrasse fahrstrasse, boolean showElements)
  {
    System.out.println(fahrstrasse);
    if (showElements)
    {
      for (FahrstrassenElement element : fahrstrasse.getElemente())
      {
        System.out.println("  " + element);
      }
    }
  }
}
