package de.gedoplan.v5t11.betriebssteuerung.steuerung;

import de.gedoplan.baselibs.utils.xml.XmlConverter;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrstrasse.Fahrstrasse;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrstrasse.FahrstrassenElement;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrstrasse.element.FahrstrassenGleisabschnitt;

import org.junit.Before;
import org.junit.Test;

public class FolgeGleisTest
{
  private Steuerung steuerung;

  /**
   * Initialisierung vor jedem Test.
   *
   * @throws Exception
   */
  @Before
  public void before() throws Exception // CHECKSTYLE:IGNORE
  {
    this.steuerung = XmlConverter.fromXml(Steuerung.class, "DwAnlage.xml");
  }

  @Test
  public void testDeriveFolgeGleisabschnitte() throws Exception
  {
    Fahrstrasse fahrstrasse = this.steuerung.getFahrstrasse("Fensterhausen", "LE-4");
    this.steuerung.deriveFolgeGleisabschnitte(fahrstrasse);

    fahrstrasse = this.steuerung.getFahrstrasse("Fensterhausen", "LE-5");
    this.steuerung.deriveFolgeGleisabschnitte(fahrstrasse);

    fahrstrasse = this.steuerung.getFahrstrasse("Fensterhausen", "LE-7");
    this.steuerung.deriveFolgeGleisabschnitte(fahrstrasse);

    fahrstrasse = this.steuerung.getFahrstrasse("Fensterhausen", "LE-18");
    this.steuerung.deriveFolgeGleisabschnitte(fahrstrasse);

    fahrstrasse = this.steuerung.getFahrstrasse("Fensterhausen", "LE-19");
    this.steuerung.deriveFolgeGleisabschnitte(fahrstrasse);

    fahrstrasse = this.steuerung.getFahrstrasse("Fensterhausen", "LE-10");
    this.steuerung.deriveFolgeGleisabschnitte(fahrstrasse);

    fahrstrasse = this.steuerung.getFahrstrasse("Fensterhausen", "LA-2");
    this.steuerung.deriveFolgeGleisabschnitte(fahrstrasse);

    fahrstrasse = this.steuerung.getFahrstrasse("Fensterhausen", "LA-3");
    this.steuerung.deriveFolgeGleisabschnitte(fahrstrasse);

    for (FahrstrassenElement fahrstrassenElement : fahrstrasse.getElemente())
    {
      if (fahrstrassenElement instanceof FahrstrassenGleisabschnitt)
      {
        System.out.println(fahrstrassenElement.getFahrwegelement());
      }
    }
  }
}
