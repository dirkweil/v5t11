package de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg;

import de.gedoplan.baselibs.utils.xml.XmlConverter;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.Steuerung;

import org.junit.Before;
import org.junit.Test;

public class GleisabschnittTest
{
  private Steuerung steuerung;

  @Before
  public void before() throws Exception
  {
    this.steuerung = XmlConverter.fromXml(Steuerung.class, "DwAnlage.xml");
  }

  @Test
  public void showGleisabschniteOhneRouting() throws Exception
  {
    this.steuerung.getGleisabschnitte()
        .stream()
        .map(g -> g.toString())
        .filter(s -> s.contains("null") || s.contains("unknown"))
        .forEach(System.out::println);
  }
}
