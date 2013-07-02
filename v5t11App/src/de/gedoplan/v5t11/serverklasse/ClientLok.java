package de.gedoplan.v5t11.serverklasse;

/**
 * Lokomotive (Client-Sicht ohne serverseitige Logik).
 * 
 * @author dw
 */

public class ClientLok implements Comparable<ClientLok>
{
  protected static int MASK_HORN            = 0x80;
  protected static int MASK_LICHT           = 0x40;
  protected static int MASK_RICHTUNG        = 0x20;
  protected static int MASK_GESCHWINDIGKEIT = 0x1F;

  /**
   * Adresse der Lok am SX-Bus.
   * 
   * Die Adresse stellt auch die Id des Objektes dar.
   */

  protected int        adr;

  /**
   * Name.
   */
  protected String     name;

  /**
   * Bilddateiname.
   */

  protected String     bild;

  /**
   * Aktueller Wert.
   */
  protected int        wert;

  /**
   * Konstruktor.
   * 
   * @param adresse Adresse
   * @param name Name
   * @param bildFileName Name der Bilddatei
   */
  public ClientLok(int adresse, String name, String bildFileName)
  {
    this.adr = adresse;
    this.name = name;
    this.bild = bildFileName;
  }

  /**
   * Wert liefern: {@link #adr}.
   * 
   * @return Wert
   */
  public int getAdr()
  {
    return this.adr;
  }

  /**
   * Wert liefern: {@link #name}.
   * 
   * @return Wert
   */
  public String getName()
  {
    return this.name;
  }

  /**
   * Wert liefern: {@link #bild}.
   * 
   * @return Wert
   */
  public String getBild()
  {
    return this.bild;
  }

  /**
   * Wert liefern: {@link #wert}.
   * 
   * @return Wert
   */
  public int getWert()
  {
    return this.wert;
  }

  /**
   * Horn abfragen.
   * 
   * @return boolean Einschaltzustand des Lokhorns
   */
  public boolean isHorn()
  {
    return (this.wert & MASK_HORN) != 0;
  }

  /**
   * Horn ein/ausschalten.
   * 
   * @param on Einschaltzustand des Lokhorns
   */
  public void setHorn(boolean on)
  {
    if (on)
    {
      setWert(this.wert | MASK_HORN);
    }
    else
    {
      setWert(this.wert & ~MASK_HORN);
    }
  }

  /**
   * Licht abfragen.
   * 
   * @return boolean Einschaltzustand der Stirn/Schlußbeleuchtung
   */
  public boolean isLicht()
  {
    return (this.wert & MASK_LICHT) != 0;
  }

  /**
   * Licht ein/ausschalten.
   * 
   * @param on Einschaltzustand der Stirn/Schlußbeleuchtung
   */
  public void setLicht(boolean on)
  {
    if (on)
    {
      setWert(this.wert | MASK_LICHT);
    }
    else
    {
      setWert(this.wert & ~MASK_LICHT);
    }
  }

  /**
   * Fahrrichtung abfragen.
   * 
   * @return boolean <code>true</code>=rückwärts, <code>false</code>=vorwärts
   */
  public boolean isRueckwaerts()
  {
    return (this.wert & MASK_RICHTUNG) != 0;
  }

  /**
   * Fahrrichtung setzen.
   * 
   * @param on <code>true</code>=rückwärts, <code>false</code>=vorwärts
   */
  public void setRueckwaerts(boolean on)
  {
    if (on)
    {
      setWert(this.wert | MASK_RICHTUNG);
    }
    else
    {
      setWert(this.wert & ~MASK_RICHTUNG);
    }
  }

  /**
   * Geschwindigkeit abfragen.
   * 
   * @return Fahrstufe (0..31)
   */
  public int getGeschwindigkeit()
  {
    return this.wert & MASK_GESCHWINDIGKEIT;
  }

  /**
   * Geschwindigkeit setzen.
   * 
   * @param geschwindigkeit Fahrstufe (0..31)
   */
  public void setGeschwindigkeit(int geschwindigkeit)
  {
    setWert((this.wert & ~MASK_GESCHWINDIGKEIT) | (geschwindigkeit & MASK_GESCHWINDIGKEIT));
  }

  /**
   * Wert setzen: {@link #wert}.
   * 
   * @param wert Wert
   */
  public void setWert(int wert)
  {
    this.wert = wert;
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  @Override
  public int compareTo(ClientLok other)
  {
    return this.adr - other.adr;
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + this.adr;
    return result;
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj)
  {
    if (this == obj)
    {
      return true;
    }
    if (obj == null)
    {
      return false;
    }
    if (getClass() != obj.getClass())
    {
      return false;
    }
    ClientLok other = (ClientLok) obj;
    if (this.adr != other.adr)
    {
      return false;
    }
    return true;
  }

  @Override
  public String toString()
  {
    return "ClientLok [adresse=" + adr + ", name=" + name + ", bildFileName=" + bild + ", wert=" + wert + "]";
  }

  /**
   * Konstruktor für interne Zwecke.
   */
  protected ClientLok()
  {
  }

}
