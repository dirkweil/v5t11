package de.gedoplan.v5t11.betriebssteuerung.steuerung;

import de.gedoplan.baselibs.utils.xml.XmlConverter;

import org.junit.Before;

public class DwAnlageTest extends XyzAnlageTest
{
  @Before
  public void before() throws Exception
  {
    this.steuerung = XmlConverter.fromXml(Steuerung.class, "DwAnlage.xml");
  }
}
