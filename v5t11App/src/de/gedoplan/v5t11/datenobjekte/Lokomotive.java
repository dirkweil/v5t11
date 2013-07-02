package de.gedoplan.v5t11.datenobjekte;

public class Lokomotive
{

  private int    adresse;

  private int    icon;

  private String bezeichnung;


  public Lokomotive(int adresse, int icon, String bezeichnung)
  {
    this.adresse = adresse;
    this.icon = icon;
    this.bezeichnung = bezeichnung;

  }

  public String getBezeichnung()
  {
    return bezeichnung;
  }

  public void setBezeichnung(String bezeichnung)
  {
    this.bezeichnung = bezeichnung;
  }

  public int getIcon()
  {
    return icon;
  }

  public void setIcon(int icon)
  {
    this.icon = icon;
  }

  public int getAdresse()
  {
    return adresse;
  }

  public void setAdresse(int adresse)
  {
    this.adresse = adresse;
  }

}
