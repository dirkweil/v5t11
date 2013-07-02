package de.gedoplan.v5t11.betriebssteuerung.entity;

import de.gedoplan.baselibs.persistence.entity.SingleIdEntity;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * Bausteinkonfiguration.
 * 
 * @author dw
 */
@Entity
@Access(AccessType.FIELD)
public class BausteinConfiguration extends SingleIdEntity<String>
{
  /**
   * Id des Bausteins.
   */
  @Id
  private String              id;

  /**
   * Adresse des Bausteins am SX-Bus.
   */
  @Min(0)
  @Max(127)
  private int                 adresse;

  /**
   * Konfigurationswerte.
   */
  @ElementCollection(fetch = FetchType.EAGER)
  private Map<String, String> properties;

  /**
   * Konstruktor.
   * 
   * @param id Id des Bausteins
   */
  public BausteinConfiguration(String id)
  {
    this.id = id;
    this.properties = new HashMap<String, String>();
  }

  /**
   * Konstruktor (nur für JPA).
   */
  protected BausteinConfiguration()
  {
  }

  /**
   * Wert liefern: {@link #id}.
   * 
   * @return Wert
   */
  @Override
  public String getId()
  {
    return this.id;
  }

  /**
   * Wert liefern: {@link #adresse}.
   * 
   * @return Wert
   */
  public int getAdresse()
  {
    return this.adresse;
  }

  /**
   * Wert setzen: {@link #adresse}.
   * 
   * @param adresse Wert
   */
  public void setAdresse(int adresse)
  {
    this.adresse = adresse;
  }

  /**
   * Wert liefern: {@link #properties}.
   * 
   * @return Wert
   */
  public Map<String, String> getProperties()
  {
    return this.properties;
  }

}
