package de.gedoplan.v5t11.betriebssteuerung.steuerung;

import de.gedoplan.baselibs.utils.xml.XmlConverter;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.Besetztmelder;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.Funktionsdecoder;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrstrasse.Fahrstrasse;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrstrasse.FahrstrassenElement;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg.Geraet;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg.Gleisabschnitt;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.konfiguration.AutoFahrstrassenKonfiguration;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.konfiguration.BahnuebergangKonfiguration;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.konfiguration.BlockstellenKonfiguration;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.konfiguration.VorsignalKonfiguration;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.lok.Lok;

import java.util.Map.Entry;

import org.junit.Ignore;
import org.junit.Test;

public abstract class XyzAnlageTest
{
  protected Steuerung steuerung;

  @Test
  @Ignore
  public void showSteuerung()
  {
    System.out.println(this.steuerung.toDebugString(false));
  }

  @Test
  @Ignore
  public void testXmlMarshall() throws Exception
  {
    System.out.println(XmlConverter.toXml(this.steuerung));
  }

  @Test
  @Ignore
  public void showGleiseOhneBesetztmelder()
  {
    for (Gleisabschnitt gleisabschnitt : this.steuerung.getGleisabschnitte())
    {
      Besetztmelder besetztmelder = gleisabschnitt.getBesetztmelder();
      if (besetztmelder == null || (besetztmelder.getAdresse() >= 50 && besetztmelder.getAdresse() < 60))
      {
        System.out.println(gleisabschnitt + " hat keinen Besetztmelder");
      }
    }
  }

  @Test
  @Ignore
  public void showFreieBesetztmelder()
  {
    for (Besetztmelder besetztmelder : this.steuerung.getBesetztmelder())
    {
      if (besetztmelder.getAdresse() >= 60)
      {
        int benutztFlags = 0;
        for (Gleisabschnitt gleisabschnitt : besetztmelder.getGleisabschnitte())
        {
          benutztFlags |= (1 << gleisabschnitt.getAnschluss());
        }

        for (int anschluss = 0; anschluss < besetztmelder.getByteAnzahl() * 8; ++anschluss)
        {
          if ((benutztFlags & (1 << anschluss)) == 0)
          {
            System.out.println(besetztmelder + ": Anschluss " + anschluss + " ist frei");
          }
        }
      }
    }
  }

  @Test
  @Ignore
  public void showFreieFunktionsdekoder()
  {
    for (Funktionsdecoder funktionsdecoder : this.steuerung.getFunktionsdecoder())
    {
      if (funktionsdecoder.getAdresse() >= 70)
      {
        int benutztFlags = 0;
        for (Geraet geraet : funktionsdecoder.getGeraete())
        {
          int mask = ~(0b11111111 << geraet.getBitCount());
          benutztFlags |= (mask << geraet.getAnschluss());
        }

        for (int anschluss = 0; anschluss < funktionsdecoder.getByteAnzahl() * 8; ++anschluss)
        {
          if ((benutztFlags & (1 << anschluss)) == 0)
          {
            System.out.println(funktionsdecoder + ": Anschluss " + anschluss + " ist frei");
          }
        }
      }
    }
  }

  @Test
  @Ignore
  public void showFunktionsdekoder()
  {
    for (Funktionsdecoder funktionsdecoder : this.steuerung.getFunktionsdecoder())
    {
      if (funktionsdecoder.getAdresse() >= 70)
      {
        System.out.println(funktionsdecoder + ": " + funktionsdecoder.getEinbauOrt());
      }
    }
  }

  @Test
  @Ignore
  public void showBlockstellen()
  {
    for (BlockstellenKonfiguration blockstellenKonfiguration : this.steuerung.getBlockstellenKonfigurationen())
    {
      System.out.println(blockstellenKonfiguration.getSignal() + " sichert " + blockstellenKonfiguration.getGleisabschnitt());
    }
  }

  @Test
  @Ignore
  public void showBahnuebergaenge()
  {
    for (BahnuebergangKonfiguration blockstellenKonfiguration : this.steuerung.getBahnuebergangKonfigurationen())
    {
      System.out.println(blockstellenKonfiguration.getBahnuebergang() + " sichert " + blockstellenKonfiguration.getGleisabschnitte());
    }
  }

  @Test
  @Ignore
  public void showVorsignale()
  {
    for (VorsignalKonfiguration vorsignalKonfiguration : this.steuerung.getVorsignalKonfigurationen())
    {
      System.out.println("Vorsignal=" + vorsignalKonfiguration.getVorsignal() + ", HauptsignalAmGleichenMast=" + vorsignalKonfiguration.getHauptsignalAmGleichenMast() + ", autodunkel="
          + vorsignalKonfiguration.isAutodunkel() + ", Hauptsignale=" + vorsignalKonfiguration.getHauptsignale());
    }
  }

  @Test
  @Ignore
  public void showAutofahrstrassen()
  {
    for (AutoFahrstrassenKonfiguration konfiguration : this.steuerung.getAutoFahrstrassenKonfigurationen())
    {
      System.out.println(konfiguration.getGleisabschnitt() + " triggert " + konfiguration.getZielGleisabschnitte());
    }
  }

  @Test
  @Ignore
  public void showFahrstrassen()
  {
    for (Fahrstrasse fahrstrasse : this.steuerung.getFahrstrassen())
    {
      switch (fahrstrasse.getName())
      {
      case "4-W3-W2-LE":
      case "5-W5-W3-W2-LE":
        break;

      default:
        continue;
      }

      System.out.println(fahrstrasse);
      for (FahrstrassenElement element : fahrstrasse.getElemente())
      {
        System.out.println("  " + element);
      }
    }
  }

  @Test
  // @Ignore
  public void showFahrstrassenAnzahl()
  {
    System.out.println("Anzahl Fahrstrassen: " + this.steuerung.getFahrstrassen().size());
  }

  @Test
  @Ignore
  public void showLoks() throws Exception
  {
    for (Lok lok : this.steuerung.getLoks())
    {
      System.out.println(lok.toDebugString());
      System.out.println("  " + lok.getDecoder());
      if (lok.getProperties() != null)
      {
        for (Entry<String, String> entry : lok.getProperties().entrySet())
        {
          System.out.println("  " + entry.getKey() + " --> " + entry.getValue());
        }
      }
    }

  }
}
