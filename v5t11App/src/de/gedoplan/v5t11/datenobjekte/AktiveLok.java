package de.gedoplan.v5t11.datenobjekte;

public class AktiveLok
{

  private int              icon;

  private String           bezeichnung      = "???";

  private int              adresse          = 999;

  private boolean          rueckwaerts      = false;

  private boolean          licht            = false;

  private int              tempo            = 0;

  private static AktiveLok aktiveLok        = null;

  public static AktiveLok getInstance()
  {
    if (aktiveLok == null)
    {
      aktiveLok = new AktiveLok();
    }

    return aktiveLok;
  }

  private AktiveLok()
  {

  }

  public void changeRichtung()
  {
    if (isRueckwaerts())
    {
      setRueckwaerts(false);
    }
    else
    {
      setRueckwaerts(true);
    }
  }

  public void changeLicht()
  {
    if (isLicht())
    {
      setLicht(false);
    }
    else
    {
      setLicht(true);
    }
  }

  public int getIcon()
  {
    return icon;
  }

  public void setIcon(int icon)
  {
    this.icon = icon;
  }

  public String getBezeichnung()
  {
    return bezeichnung;
  }

  public void setBezeichnung(String bezeichnung)
  {
    this.bezeichnung = bezeichnung;
  }

  public int getAdresse()
  {
    return adresse;
  }

  public void setAdresse(int adresse)
  {
    this.adresse = adresse;
  }

  public boolean isRueckwaerts()
  {
    return rueckwaerts;
  }

  public void setRueckwaerts(boolean rueckwaerts)
  {
    this.rueckwaerts = rueckwaerts;
  }

  public boolean isLicht()
  {
    return licht;
  }

  public void setLicht(boolean licht)
  {
    this.licht = licht;
  }

  public int getTempo()
  {
    return tempo;
  }

  public void setTempo(int tempo)
  {
    this.tempo = tempo;
  }

  @Override
  public String toString()
  {
    return "AktiveLok [icon=" + icon + ", bezeichnung=" + bezeichnung + ", adresse=" + adresse + ", rueckwaerts=" + rueckwaerts + ", licht=" + licht
        + ", tempo=" + tempo + "]";
  }

}
